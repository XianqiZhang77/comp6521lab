package comp6521.lab.com;

import java.util.ArrayList;

import comp6521.lab.com.Pages.Page;
import comp6521.lab.com.Records.*;

// Generic B+ tree implementation, for great justice!
public class BPlusTree<T extends Page<?>, S extends RecordElement > {
	boolean m_treeCreated;
	BPlusTreeNode<S> m_root;
	
	Class<T> m_pageType;
	Class<S> m_recordElementClass;
	String   m_filename;

	String   m_key;	
	int      m_n;
	int      m_recordElementLength; // Parameter needed when initializing keys (for StringRecordElement)
	
	BPlusTreeCache<S> cache;
	boolean caching;
	boolean rootCached;

	public BPlusTree()
	{
		m_treeCreated = false;
		caching = false;
		rootCached = false;
	}
	
	public void CacheRoot()
	{
		if(m_root.IsLoaded())
			return;
		
		m_root.Load(0);
		rootCached = true;
	}
	
	public void UncacheRoot()
	{
		if(!m_root.IsLoaded())
			return;
		
		m_root.Clear();
		rootCached = false;
	}
	
	public void CreateBPlusTree( Class<T> pageClass, Class<S> recordElementClass, String pageFilename, String treeFilename, String key )
	{
		CreateBPlusTree( pageClass, recordElementClass, -1, pageFilename, treeFilename, key );
	}
	
	public void CreateBPlusTree( Class<T> pageClass, Class<S> recordElementClass, int recordElementLength, String pageFilename, String treeFilename, String key )
	{
		m_pageType = pageClass;
		m_recordElementClass = recordElementClass;
		m_recordElementLength = recordElementLength;
		m_filename = treeFilename;
		m_key      = key;
		
		// Consider size of the key field
		T dummyPage = null;
		try { dummyPage = pageClass.newInstance();} catch (InstantiationException e) {e.printStackTrace();} catch (IllegalAccessException e) {e.printStackTrace();}
		int nbRecordsPerPage = dummyPage.m_nbRecordsPerPage;
		Record dummyRecord = dummyPage.CreateElement();
		int keySize = dummyRecord.get(key).Size();
		
		// "pointer" will be record number
		int pointerSize = 8;
		
		// Block size is arbitrarily 1K
		// 1 parent pointer, n elements, n+1 child pointers
		m_n = (1024 - 2 * pointerSize - 1) / (keySize + pointerSize);
		
		// Consider number of levels we'll need.
		int nbRecords = MemoryManager.getInstance().GetNumberOfRecords( pageClass, pageFilename );
		int nbLevels = 1 + (int)(Math.log((double)nbRecords) / Math.log((double)m_n));
		
		Log.LogSomething("Nb records to index: " + nbRecords );
		Log.LogSomething("Nb levels needed: " + nbLevels );
		Log.LogSomething("n : " + m_n );
		
		// Add a custom page type
		MemoryManager.getInstance().AddPageType( BPlusTreePage.class, m_filename );
		
		// Compute our cache size..
		// We must hold in memory 1 page of the relation
		// Plus any number of cached nodes
		int CacheMemory = MemoryManager.getInstance().RemainingMemory() - MemoryManager.getInstance().GetPageSize( m_pageType );
		int NodeSize    = MemoryManager.getInstance().GetPageSize( BPlusTreePage.class );
		
		int maxCacheSize = CacheMemory / NodeSize;
		cache = new BPlusTreeCache<S>(maxCacheSize, m_recordElementClass, m_recordElementLength, m_n, m_filename);
		caching = true;				
		
		// Get the root and keep it in memory at this time.
		m_root = LoadNode(-1);
		
		// Parse the whole table
		int p = 0;

		T page = null;
		while ( (page = MemoryManager.getInstance().getPage( m_pageType , p, pageFilename)) != null )
		{
			for(int i = 0; i < page.m_records.length; i++)
			{
				InsertLeaf( page.m_records[i], nbRecordsPerPage * p + i );
			}
			
			MemoryManager.getInstance().freePage(page);
			p++;
		}
		
		cache.ClearCache();
		cache = null;
		caching = false;
		
		m_treeCreated = true;
	}
	
	public void InsertLeaf( Record rec, int recordNumber )
	{
		KeyPointerPair pair = new KeyPointerPair( rec.get(m_key), recordNumber );
		// Load the root
		BPlusTreeNode<S> root = LoadNode(0);
		
		// We try to find a place for the new key in the appropriate leaf
		BPlusTreeNode<S> target = getLeafNode( root, pair.el );

		Insert( target, pair );		
		
		// Clear the root if it wasn't previously loaded
		ClearNode(root);
	}
	
	private void Insert( BPlusTreeNode<S> node, KeyPointerPair pair)
	{
		assert(node.IsLoaded());
		
		// Special case for "null" nodes .. we must find the appropriate node first
		if( node.m_IsNullNode )
		{
			while( node.m_elements[node.m_nbElements-1].CompareTo( pair.el ) < 0 && node.m_IsNullNode )
			{
				int nextLeaf = node.m_records[node.m_nbElements];
				ClearNode(node);
				node = LoadNode(nextLeaf);
			}
		}
		
		if( node.m_nbElements == m_n )
		{
			// If there is no room in the proper leaf, we split the leaf into two
			// and divide the keys between the two new nodes, so each is half full
			
			// Start by creating new sorted arrays
			// Create a new split node
			BPlusTreeNode<S> splitNode = LoadNode(-1);
			
			KeyPointerPair nodeToAdd = SplitNode( node, splitNode, pair );
					
			if( node == m_root )
			{
				// In the case of the root, the root becomes a leaf or interior node
				// And we create a new root
				BPlusTreeNode<S> newroot = LoadNode(-1);
				
				// Now, perform a big hack
				assert(newroot.m_node == newroot.m_page.m_pageNumber);
				assert(node.m_node == node.m_page.m_pageNumber);
				int tempPageNumber = newroot.m_node;
				newroot.m_node = node.m_node;
				newroot.m_page.m_pageNumber = node.m_page.m_pageNumber;
				node.m_node = tempPageNumber;
				node.m_page.m_pageNumber = tempPageNumber;
				
				// Setup new parent pointers
				node.m_parent = newroot.m_node;
				splitNode.m_parent = newroot.m_node;

				// We just swapped the page numbers, which will sort itself out when writing back the data.
				// Now setup the new root data correctly!
				m_root = newroot;
				
				m_root.m_elements[0] = nodeToAdd.el;
				m_root.m_records[0] = node.m_node;
				m_root.m_records[1] = splitNode.m_node;
				m_root.m_nbElements = 1;
				m_root.m_dirty = true;
				m_root.m_isLeaf = false;
				
				splitNode.m_isLeaf = true;
				node.m_isLeaf = true;
				
				ClearNode(m_root);
				ClearNode(splitNode);
				ClearNode(node);
				// No recursive calls in this case			
			}
			else
			{
				splitNode.m_parent = node.m_parent;
				ClearNode(splitNode);
				
				int parentptr = node.m_parent;
				ClearNode(node);
				
				BPlusTreeNode<S> parent = null;
				
				if( parentptr == 0 )
					parent = m_root;
				else
					parent = LoadNode(parentptr);

				// parent is cleared inside the Insert.
				Insert( parent, nodeToAdd );
			}
		}
		else
		{
			node.Insert( pair );
			ClearNode(node);
		}
	}
	
	public ArrayList<Integer> GetList( RecordElement el ){ return GetList(el, el);	}
	public int[] Get( RecordElement el ) { return Get(el, el); }
	
	// Use this for range queries
	public ArrayList<Integer> GetList( RecordElement sel, RecordElement eel )
	{
		ArrayList<Integer> recs = new ArrayList<Integer>();
		m_root = LoadNode(0);
		
		// Get first leaf where we have a value <= to our start element
		BPlusTreeNode<S> leaf = getLeafNode( m_root, sel );
		
		boolean ContinueToNextLeaf = true;
		
		while( ContinueToNextLeaf )
		{
			for( int i = 0; i < leaf.m_nbElements; i++ )
			{
				if( leaf.m_elements[i].CompareTo( sel ) >= 0 && 
					leaf.m_elements[i].CompareTo( eel ) <= 0    )
				{
					recs.add(new Integer(leaf.m_records[i]));
				}
			}
			
			ContinueToNextLeaf = ( leaf.m_elements[leaf.m_nbElements-1].CompareTo(eel) <= 0 );
			
			// If the last element was matched, we have to look in the next leaf
			if( ContinueToNextLeaf )
			{
				int nextLeaf = leaf.getNextLeafPtr();
				
				if( nextLeaf == 0 )
				{
					// We're done
					ContinueToNextLeaf = false;
				}
				else
				{			
					ClearNode(leaf);
					leaf = LoadNode(nextLeaf);
				}
			}
		}
		
		ClearNode(leaf);			
		ClearNode(m_root);
		return recs;
	}
	
	public int[] Get( RecordElement sel, RecordElement eel )
	{
		ArrayList<Integer> list = GetList( sel, eel );
		
		int[] array = new int[list.size()];
		for( int i = 0; i < array.length; i++ )
			array[i] = list.get(i).intValue();
		
		return array;
	}
	
	// ---- Implementation methods ----
	BPlusTreeNode<S> LoadNode( int nodeNb )
	{
		BPlusTreeNode<S> node = null;
		if( caching )
		{
			node = cache.getNode(nodeNb);
			
			if( node == null )
				node = cache.AddToCache(nodeNb);
		}
		else if( rootCached && nodeNb == 0 )
		{
			node = m_root;
		}
		else
		{
			// not using cache
			node = _getNode(nodeNb);
		}
	
		return node;		
	}
	
	void ClearNode( BPlusTreeNode<S> node )
	{
		if( caching )
		{
			// Don't free yet
		}
		else if( rootCached && node.m_node == 0 )
		{
			// Don't free yet
		}
		else
		{
			node.Clear();
		}
	}
	
	BPlusTreeNode<S> _createNode()
	{
		return new BPlusTreeNode<S>(m_recordElementClass, m_recordElementLength, m_n, m_filename);
	}
		
	BPlusTreeNode<S> _getNode( int nb )
	{
		BPlusTreeNode<S> node = _createNode();
		node.Load(nb);
		return node;
	}
	
	BPlusTreeNode<S> getLeafNode( BPlusTreeNode<S> parent, RecordElement el )
	{
		if( parent.IsLeaf() )
			return parent;
		
		int firstNull = -1;
		
		for( int i = 0; i <= parent.m_nbElements; i++ )
		{
			int childnode = -1;
			
			// First condition: we're at the end
			if( i == parent.m_nbElements )
			{
				// Either we select the last pointer, or the first pointer that was null -
				if( firstNull >= 0 )
					childnode = firstNull;
				else
					childnode = parent.m_records[i];
			}
			else if( parent.m_elements[i] == null )
			{
				// Set the first null only if it isn't set yet
				if( firstNull == -1 )
					firstNull = parent.m_records[i];
			}
			else
			{
				// We have an element, then we must compare with it.
				if( el.CompareTo( parent.m_elements[i] ) < 0 )
				{
					// In the case we had previously seen nulls, we must branch there
					if( firstNull >= 0 )
						childnode = firstNull;
					else
						childnode = parent.m_records[i];						
				}
				
				// Reset the last null seen
				if( childnode < 0 )
					firstNull = -1;
			}
			
			// If we can branch to a child in this iteration
			if( childnode >= 0 )
			{
				ClearNode(parent);
				
				BPlusTreeNode<S> child = LoadNode(childnode);
				
				if( childnode == firstNull )
					child.m_IsNullNode = true;
				
				return getLeafNode( child, el );
			}				
		}

		// Not supposed to get here
		assert(false);
		return null;
	}
	
	KeyPointerPair SplitNode(BPlusTreeNode<S> node, BPlusTreeNode<S> splitNode, KeyPointerPair keyAdded )
	{
		RecordElement[] els = new RecordElement[m_n+1];
		int[]           ptrs = new int[m_n+2];
		boolean inserted = false;
		
		int e = 0;
		for( int i = 0; i < m_n+1; i++ )
		{
			if( inserted || ( e < m_n && node.m_elements[e].CompareTo(keyAdded.el) < 0 ) )
			{
				els[i]  = node.m_elements[e];
				ptrs[i] = node.m_records[e];
				e++;						
			}
			else
			{
				els[i] = keyAdded.el;
				ptrs[i] = keyAdded.ptr;
				inserted = true;
			}
		}
		
		node.m_dirty = true;
		splitNode.m_dirty = true;
		
		splitNode.m_isLeaf = node.m_isLeaf;
		
		if( node.IsLeaf() )
		{
			// Last pointer points to split node..
			int rightPointer = node.m_records[m_n];
		
			// If we're splitting a leaf node:
			// The first ceil( (n+1)/2 ) key-pointer pairs stay with N
			// The remaining move to M.
			int kept  = (int) Math.ceil( (double)(m_n+1) / 2.0 );
			
			for( int i = 0; i < kept; i++ )
			{
				node.m_elements[i] = els[i];
				node.m_records[i]  = ptrs[i];
			}
			node.m_records[kept] = splitNode.m_node;
			node.m_nbElements = kept;
						
			for( int i = kept; i < m_n+1; i++)
			{
				splitNode.m_elements[i-kept] = els[i];
				splitNode.m_records[i-kept] = ptrs[i];
			}		
			splitNode.m_nbElements = m_n+1 - kept;
			splitNode.m_records[splitNode.m_nbElements] = rightPointer;
			
			// Find the first "different" element that will be used as the representing value
			RecordElement rel = null;
			int k = kept;
			while( k < m_n+1 )
			{
				if( els[k].CompareTo(els[k-1]) != 0)
				{
					rel = els[k];
					break;
				}
				k++;				
			}
			
			boolean isNull = false;
			if( rel == null )
			{
				rel = els[m_n];
				isNull = true;
			}
			
			KeyPointerPair kp = new KeyPointerPair(rel, splitNode.m_node);
			kp.isNull = isNull;			
			splitNode.m_IsNullNode = isNull;
			
			// Insert new key-pointer pair into the parent.
			return kp;
		}
		else
		{
			// ABSOLUTELY NOT SURE ABOUT THAT
			// I THINK IT'S MORE THE FIRST POINTER THAT NEVER CHANGES
			ptrs[m_n+1] = node.m_records[m_n];
			
			// If we're splitting an interior node:
			// Leave at N the first ceil( (n+2) / 2) pointers
			//  and move to M the remaining floor( (n+2) / 2) pointers
			// The first ceil(n/2) keys stay with N, 
			// while the last floor(n/2) keys move to M.
			// There's always a key left out: K
			// It must be inserted into the parent.
			int keptPtrs = (int)Math.ceil( (double)(m_n+2) / 2.0 );
			int movedPtrs = m_n+2 - keptPtrs;
			
			int keptKeys = (int)Math.ceil( (double)m_n / 2.0f );
			int movedKeys = (int)Math.floor( (double)m_n / 2.0f );
			
			assert( keptPtrs == keptKeys+1);
			assert( movedPtrs == movedKeys+1);
			assert( m_n + 1 == 1 + keptKeys + movedKeys );
			
			for(int i = 0; i < keptPtrs; i++)
				node.m_records[i] = ptrs[i];
			for(int i = m_n+2 - movedPtrs; i < m_n+2; i++)
				splitNode.m_records[i - (m_n+2)] = ptrs[i];
			for(int i = 0; i < keptKeys; i++)
				node.m_elements[i] = els[i];
			for(int i = m_n+1 - movedKeys; i < m_n+1; i++ )
				splitNode.m_elements[i - (m_n+1)] = els[i];
			
			node.m_nbElements      = keptKeys;
			splitNode.m_nbElements = movedKeys;
		
			// Insert new key-pointer pair into parent
			// i.e. [left out] - [split node]
			return new KeyPointerPair(els[keptKeys+1], splitNode.m_node);
		}
	}
}

//////////////////////////////////////
// Subclasses ////////////////////////
//////////////////////////////////////
class BPlusTreeCache<S extends RecordElement>
{
	// info needed to be able to create nodes
	Class<S> m_recordElementClass;
	int      m_recordElementLength;
	int      m_n;
	String   m_filename;
	
	// Real cache data
	int maxCacheSize;
	ArrayList< BPlusTreeNode<S> > cache;
	ArrayList< Long >          cacheHits;
	long cacheCount;
	
	BPlusTreeCache( int cacheSize, Class<S> classRec, int RecLength, int n, String filename )
	{
		cacheCount = 0;
		maxCacheSize = cacheSize;
		cache = new ArrayList< BPlusTreeNode<S> >();
		cacheHits = new ArrayList< Long >();
		
		m_recordElementClass = classRec;
		m_recordElementLength = RecLength;
		m_n = n;
		m_filename = filename;
	}
	
	BPlusTreeNode<S> AddToCache( int nodeNb )
	{
		BPlusTreeNode<S> node = new BPlusTreeNode<S>(m_recordElementClass, m_recordElementLength, m_n, m_filename);
		// If cache is full, remove oldest one (except the root)
		if( cache.size() == maxCacheSize )
		{
			int idx = -1;
			long minHit = Long.MAX_VALUE;
			
			for( int i = 0; i < cache.size(); i++ )
			{
				if( cache.get(i) == null )
				{
					idx = i;
					break;
				}
				else
				{
					// Skip root
					if( cacheHits.get(i) < minHit && cache.get(i).m_node != 0 )
					{
						minHit = cacheHits.get(i).longValue();
						idx = i;
					}
				}
			}
			
			cache.get(idx).Clear();
			
			// Load new node			
			node.Load(nodeNb);
			
			cache.set(idx, node);
			cacheHits.set(idx, new Long(cacheCount++));			
		}
		// Add the node to the cache and give it a number	
		else
		{
			// Load new node
			node.Load(nodeNb);
			
			cache.add(node);
			cacheHits.add(new Long(cacheCount++));
		}
		
		return node;
	}
	
	BPlusTreeNode<S> getNode( int node )
	{
		for(int i = 0; i < cache.size(); i++)
		{
			BPlusTreeNode<S> cachedNode = cache.get(i);
			
			if( cachedNode != null && cachedNode.m_node == node )
			{
				// We found the good node
				// Update the cache hit
				cacheHits.set(i, new Long(cacheCount++));
				return cachedNode;
			}
		}
		
		return null;
	}
	
	public void ClearCache()
	{
		for(int i = 0; i < cache.size(); i++)
		{
			BPlusTreeNode<S> cachedNode = cache.get(i);
			if( cachedNode != null )
				cachedNode.Clear();
		}
		
		cache.clear();
		cacheHits.clear();
	}
}


class BPlusTreeRecord extends Record
{
	public BPlusTreeRecord()
	{
		AddElement( "data", new StringRecordElement(1024) );
	}
}

class BPlusTreePage extends Page< BPlusTreeRecord >
{
	public BPlusTreePage()
	{
		super();
		m_nbRecordsPerPage = 1;
	}
	public BPlusTreeRecord[] CreateArray(int n){ assert(n==1); return new BPlusTreeRecord[n]; }
	public BPlusTreeRecord   CreateElement()   { return new BPlusTreeRecord(); } 
}

class BPlusTreeNode<S extends RecordElement>
{
	// --- Runtime generated data ---
	Class<S>        m_class;
	boolean         m_loaded;
	BPlusTreePage   m_page;
	
	int             m_nbElements;
	
	int             m_node;
	boolean         m_dirty;
	
	// --- Flag for "null" type nodes ---
	boolean         m_IsNullNode;

	// --- Data used for simpler calls ---
	int             m_RecordLength;
	int             m_n;
	String          m_filename;
	
	// --- Saved data ---
	RecordElement[] m_elements;
	int[]           m_records;
	boolean         m_isLeaf;
	int             m_parent;
	
	BPlusTreeNode(Class<S> classRec, int RecLength, int n, String filename)
	{
		m_class      = classRec;
		m_dirty      = false;
		m_page       = null;
		m_elements   = null;
		m_records    = null;
		m_loaded     = false;
		m_nbElements = 0;
		m_isLeaf     = false;
		m_parent     = 0;
		m_node       = -1;
		
		m_RecordLength = RecLength;
		m_n            = n;
		m_filename     = filename;
		
		m_IsNullNode  = false;
	}
	
	public void Load(int node)
	{
		if(m_loaded)
			Clear();
		
		m_elements = new RecordElement[m_n];
		m_records  = new int[m_n+1];
		
		m_page = MemoryManager.getInstance().getRWPage( BPlusTreePage.class, node, m_filename );
		
		m_node = m_page.m_pageNumber;
		
		if( m_node != node && node >= 0 )
		{
			System.out.println("Error found.. node doesn't match page number");
		}
		
		if( m_page.m_records[0] == null )
		{
			m_page.setRecord( 0, m_page.CreateElement() );
			m_nbElements = 0;
			m_isLeaf = true;
		}
		else
		{
			Parse( m_page.m_records[0].get("data").getString() );
		}	
		
		m_loaded = true;
	}
	
	public void Clear()
	{
		if( m_page != null )
		{
			if(m_dirty)
			{
				Write( m_page.m_records[0].get("data") );
				m_page.m_cleanupToDo = true;
			}
			
			MemoryManager.getInstance().freePage( m_page );
			m_page = null;
		}
		
		m_elements = null;
		m_records  = null;
		m_loaded   = false;
		
		m_dirty  = false;
		m_parent = 0;
		m_node   = -1;
	}
	
	void Parse(String data)
	{
		int elementSize = CreateRecordElement().Size();
		int pointerSize = 8;
		
		if( ((data.length() - 2 * pointerSize - 1) % (elementSize + pointerSize)) != 0 )
			System.out.println("Bad data string");
		
		// String is :
		// [0|1] + parent ptr + {n els, n+1 ptrs}
		m_nbElements = (data.length() - 2 * pointerSize - 1) / (elementSize + pointerSize);
		
		int p = 0;
		// Very first, is it a leaf or not?
		m_isLeaf = (data.substring(0, 1).charAt(0) == '1' || data.substring(0, 1).charAt(0) == '2' );
		m_IsNullNode = ( data.substring(0, 1).charAt(0) == '2' );
		p += 1;
		// First, parent pointer		
		m_parent = Integer.parseInt(data.substring(p, p+8).trim(), 16);
		p += 8;
		
		int lel = CreateRecordElement().Size();

		for( int i = 0; i < m_nbElements; i++ )
		{
			// Pointer first
			m_records[i] = Integer.parseInt(data.substring(p, p+8).trim(), 16);
			p += 8;
			// element second
			m_elements[i] = CreateRecordElement();
			m_elements[i].Parse(data.substring(p, p+lel));
			p += lel;
		}
		
		// Last pointer (possibly to another leaf or to another child)
		m_records[m_nbElements] = Integer.parseInt(data.substring(p).trim(), 16);
	}
	
	void Write(RecordElement el)
	{
		int pointerSize = 8;
		String data = "";
		
		// Very very first, 0 if interior node, 1 if leaf
		if( m_isLeaf && m_IsNullNode )
			data += "2";
		else if( m_isLeaf )
			data += "1";
		else
			data += "0";
		
		// First, parent pointer
		data += String.format("%1$8s", Integer.toHexString(m_parent));
		
		// Pointer/Key pairs
		for( int i = 0; i < m_nbElements; i++ )
		{
			String ptrdata = String.format("%1$8s", Integer.toHexString(m_records[i]));
			
			if(ptrdata.length() != pointerSize)
				System.out.println("Not supposed to happen either");
			
			data += ptrdata;
			
			if( m_elements[i] == null )
				System.out.println("Bad pointer?");
			
			String eldata = m_elements[i].Write();
			if(eldata.length() != m_elements[i].Size())
				System.out.println("not supposed to happen");
			
			data += eldata;
		}
		
		// Last pointer
		data += String.format("%1$8s", Integer.toHexString(m_records[m_nbElements]));
		
		// Size verification
		if( m_nbElements > 0 )
		{
			int elementSize = m_elements[0].Size();
			
			if( ((data.length() - (1 + 2 * pointerSize)) % (pointerSize + elementSize)) != 0 )
			{
				System.out.println("Bad write!!!");
			}
		}
		
		el.setString(data);
	}
	
	void Insert( KeyPointerPair pair )
	{
		assert(m_nbElements != m_elements.length);
		
		// Find index we'll insert the value in
		int insertionIdx = -1;
		for( int i = 0; i < m_nbElements && insertionIdx < 0; i++ )
			if( pair.el.CompareTo( m_elements[i] ) < 0 )
				insertionIdx = i;
		
		if( insertionIdx == -1 )
			insertionIdx = m_nbElements;
		
		m_nbElements++;
		
		// Insertion is slightly different depending on whether this is a leaf or not;
		// If we're in a leaf, the last pointer will ALWAYS move to the last position, since it points to the next block
		// In an interior node, we don't have that
		
		if( IsLeaf() )
		{
			m_records[m_nbElements] = m_records[m_nbElements-1];
				
			for(int i = m_nbElements - 1; i > insertionIdx; i-- )
			{
				m_elements[i] = m_elements[i-1];
				m_records[i]  = m_records[i-1];
			}
			
			if(pair.isNull)
				m_elements[insertionIdx] = null;
			else
				m_elements[insertionIdx] = pair.el;
			m_records[insertionIdx]  = pair.ptr;
		}
		else
		{
			// The inserted pair [key, ptr] will be in positions [n, n+1], whatever the key is
			for( int i = m_nbElements - 1; i > insertionIdx; i-- )
			{
				m_elements[i]  = m_elements[i-1];
				m_records[i+1] = m_records[i];
			}
			
			if(pair.isNull)
				m_elements[insertionIdx] = null;
			else
				m_elements[insertionIdx]  = pair.el;
			m_records[insertionIdx+1] = pair.ptr;
		}
		
		m_dirty = true;
	}
	
	boolean IsLoaded() { return m_loaded; }
	boolean IsLeaf()   { return m_isLeaf; }
	
	int getNextLeafPtr() { if(!IsLeaf()) return -1; else return m_records[m_nbElements]; }
	
	private S CreateRecordElement()
	{
		S el = null;
		try {
			el = m_class.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		
		// Set size if needed
		if( m_RecordLength > 0 && el != null)
			el.setSize(m_RecordLength);		
		
		return el;
	}
}

class KeyPointerPair
{
	RecordElement el;
	int           ptr;
	public boolean isNull;
	
	KeyPointerPair()
	{
		el = null;
		ptr = 0;
		isNull = false;
	}
	
	KeyPointerPair(RecordElement rel, int pointer)
	{
		el = rel;
		ptr = pointer;
	}
}
