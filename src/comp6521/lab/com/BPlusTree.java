package comp6521.lab.com;

import comp6521.lab.com.Pages.Page;
import comp6521.lab.com.Records.*;

public class BPlusTree<T extends Page<?> > {
	boolean m_treeCreated;
	BPlusTreeNode m_root;
	
	Class<T> m_pageType;
	String   m_filename;

	String   m_key;	
	int      m_n;

	public BPlusTree()
	{
		m_treeCreated = false;
	}
	
	public void CreateBPlusTree( Class<T> pageClass, String pageFilename, String treeFilename, String key )
	{
		m_pageType = pageClass;
		m_filename = treeFilename;
		m_key      = key;
		
		// Consider size of the key field
		T dummyPage = null;
		try { dummyPage = pageClass.newInstance();} catch (InstantiationException e) {e.printStackTrace();} catch (IllegalAccessException e) {e.printStackTrace();}
		int nbRecordsPerPage = dummyPage.m_nbRecordsPerPage;
		Record dummyRecord = dummyPage.CreateElement();
		int keySize = dummyRecord.get(key).Size();
		
		// "pointer" will be record number
		int pointerSize = 4;
		
		// Block size is arbitrarily 1K
		m_n = (1024 - pointerSize) / (keySize + pointerSize);
		
		// Consider number of levels we'll need.
		int nbRecords = MemoryManager.getInstance().GetNumberOfRecords( pageClass, pageFilename );
		int nbLevels = (int)(Math.log((double)nbRecords) / Math.log((double)m_n));
		
		// Get the root and keep it in memory at this time.
		m_root = new BPlusTreeNode();
		m_root.Load(0, m_n, m_filename);
		
		// Parse the whole table
		int p = 0;

		T page = null;
		while ( (page = MemoryManager.getInstance().getPage( m_pageType , p++, pageFilename)) != null )
		{
			/*for(int i = 0; i < page.m_records.length; i++)
				Insert( page.m_records[i], nbRecordsPerPage * p + i );*/
			
			MemoryManager.getInstance().freePage(page);
		}
		
		m_treeCreated = true;
	}
	
	public void InsertLeaf( Record rec, int recordNumber )
	{
		// Load the root
		boolean RootLoaded = (m_root.IsLoaded());
		if( !RootLoaded )
			m_root.Load(0, m_n, m_filename);
		
		// We try to find a place for the new key in the appropriate leaf
		BPlusTreeNode target = getLeafNode( m_root, rec.get(m_key) );

		Insert( target, rec.get(m_key), recordNumber );		
		
		// Clear the root if it wasn't previously loaded
		if( !RootLoaded )
			m_root.Clear();		
	}
	
	public void Insert( BPlusTreeNode node, RecordElement el, int pointer )
	{
		assert(node.IsLoaded());
		
		if( node.m_nbElements == m_n )
		{
			// If there is no room in the proper leaf, we split the leaf into two
			// and divide the keys between the two new nodes, so each is half full
			RecordElement[] els = new RecordElement[m_n+1];
			int[]           ptrs = new int[m_n+2];
			boolean inserted = false;
			
			int e = 0;
			for( int i = 0; i < m_n+1; i++ )
			{
				if( inserted || node.m_elements[e].CompareTo(el) < 0 )
				{
					els[i]  = node.m_elements[e];
					ptrs[i] = node.m_records[e];
					e++;						
				}
				else
				{
					els[i] = el;
					ptrs[i] = pointer;
					inserted = true;
				}
			}
			
			// Start by creating new sorted arrays
			// Create a new split node
			BPlusTreeNode splitNode = new BPlusTreeNode();
			splitNode.Load(-1, m_n, m_filename);
			
			if( node == m_root )
			{
				
			}
			else if( node.IsLeaf() )
			{
				// Last pointer points to split node..
				splitNode.m_records[m_n] = node.m_records[m_n];
				node.m_records[m_n] = splitNode.m_node;				
				
				// If we're splitting a leaf node:
				// The first ceil( (n+1)/2 ) key-pointer pairs stay with N
				// The remaining move to M.
				int kept  = (int) Math.ceil( (double)(m_n+1) / 2.0 );
				
				for( int i = 0; i < kept; i++ )
				{
					node.m_elements[i] = els[i];
					node.m_records[i]  = ptrs[i];
				}
				node.m_nbElements = kept;
				
				for( int i = kept; i < m_n+1; i++)
				{
					splitNode.m_elements[i-kept] = els[i];
					splitNode.m_records[i-kept] = ptrs[i];
				}		
				splitNode.m_nbElements = m_n+1 - kept;
				
				RecordElement keyEl  = splitNode.m_elements[0];
				int           keyPtr = splitNode.m_node;
								
				// Insert new key-pointer pair into the parent.
				BPlusTreeNode parent = getParent(node);
				node.Clear();
				Insert(parent, keyEl, keyPtr);
				parent.Clear();
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
			
				RecordElement LeftOut    = els[keptKeys+1];
				int           LeftOutPtr = splitNode.m_node;
				
				splitNode.Clear();
				
				// Insert new key-pointer pair into parent
				// i.e. [left out] - [split node]
				BPlusTreeNode parent = getParent(node);
				node.Clear();
				Insert(parent, LeftOut, LeftOutPtr);
				parent.Clear();				
			}
		}
		else
		{
			node.Insert( el, pointer );
		}
	}
	
	BPlusTreeNode getNode( int nb )
	{
		BPlusTreeNode node = new BPlusTreeNode();
		node.Load(nb, m_n, m_filename);
		return node;
	}
	
	BPlusTreeNode getParent( BPlusTreeNode node )
	{
		assert(node.IsLoaded());
		if( node.m_parent > 0)
			return getNode( node.m_parent );
		else
		{
			return null;
		}
	}
	
	BPlusTreeNode getLeafNode( BPlusTreeNode parent, RecordElement el )
	{
		if( parent.IsLeaf() )
			return parent;
		
		for( int i = 0; i <= parent.m_nbElements; i++ )
		{
			if( (i == parent.m_nbElements) ||                 // Last element
				(el.CompareTo( parent.m_elements[i] ) < 0) )  // Standard comparison
			{
				int parentnode = parent.m_node;
				int childnode  = parent.m_records[i];
				
				// for the moment, free the parent				
				if( parent != m_root )
					parent.Clear();
				
				// Recurse down one level
				BPlusTreeNode child = getNode( childnode );
				child.m_parent = parentnode;
				
				return getLeafNode( child, el );
			}
		}
		// Not supposed to get here
		assert(false);
		return null;
	}
}

//////////////////////////////////////
// Subclasses ////////////////////////
//////////////////////////////////////
class BPlusTreeRecord extends Record
{
	public BPlusTreeRecord()
	{
		AddElement( "data", new StringRecordElement(1024) );
	}
}

class BPlusTreePage extends Page< BPlusTreeRecord >
{
	public BPlusTreeRecord[] CreateArray(int n){ assert(n==1); return new BPlusTreeRecord[n]; }
	public BPlusTreeRecord   CreateElement()   { return new BPlusTreeRecord(); } 
}

class BPlusTreeNode
{
	boolean         m_loaded;
	BPlusTreePage   m_page;
	RecordElement[] m_elements;
	int[]           m_records;
	int             m_nbElements;
	boolean         m_isLeaf;
	int             m_parent;
	int             m_node;
	boolean         m_dirty;
	
	BPlusTreeNode()
	{
		m_dirty      = false;
		m_page       = null;
		m_elements   = null;
		m_records    = null;
		m_loaded     = false;
		m_nbElements = 0;
		m_isLeaf     = false;
		m_parent     = -1;
		m_node       = -1;
	}
	
	public void Load(int node, int n, String filename)
	{
		if(m_loaded)
			Clear();
		
		m_elements = new RecordElement[n];
		m_records  = new int[n+1];
		
		m_page = MemoryManager.getInstance().getRWPage( BPlusTreePage.class, node, filename );
		
		if( node < 0 )
			m_node = m_page.m_pageNumber;
		else
			m_node = node;
		
		Parse( m_page.m_records[0].toString() );
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
		}
		
		m_elements = null;
		m_records  = null;
		m_loaded   = false;
		
		m_dirty  = false;
		m_parent = -1;
		m_node   = -1;
	}
	
	void Parse(String data)
	{
		int elementSize = m_elements[0].Size();
		int pointerSize = 4;
		
		assert( data.length() - pointerSize % (elementSize + pointerSize) == 0);
		m_nbElements = (data.length() - pointerSize) / (elementSize + pointerSize);
		
		for( int i = 0; i < m_nbElements; i++ )
		{
			// Pointer first
			// element second
		}
		// Pointer to leaf
	}
	
	void Write(RecordElement el)
	{
		// ...
	}
	
	void Insert( RecordElement el, int pointer )
	{
		assert(m_nbElements != m_elements.length);
		
		// Find index we'll insert the value in
		int insertionIdx = -1;
		for( int i = 0; i < m_nbElements && insertionIdx < 0; i++ )
			if( el.CompareTo( m_elements[i] ) < 0 )
				insertionIdx = i;
		
		if( insertionIdx == -1 )
			insertionIdx = m_nbElements;
		
		m_nbElements++;

		RecordElement keyValue = el;
		RecordElement keyTemp  = null;
		int ptrValue = pointer;
		int ptrTemp  = 0;
		
		for(int i = insertionIdx; i < m_nbElements; i++ )
		{
			keyTemp = m_elements[i];
			ptrTemp = m_records[i];
			
			m_elements[i] = keyValue;
			m_records[i]  = ptrValue;
			
			keyValue = keyTemp;
			ptrValue = ptrTemp;
		}		
		
		m_dirty = true;
	}
	
	void Split( BPlusTreeNode other )
	{
		// If we're splitting a leaf node:
		// The first ceil( (n+1)/2 ) key-pointer pairs stay with N
		// The remaining move to M.
		
		// If we're splitting an interior node:
		// Leave at N the first ceil( (n+2) / 2) pointers
		//  and move to M the remaining floor( (n+2) / 2) pointers
		// The first ceil(n/2) keys stay with N, 
		// while the last floor(n/2) keys move to M.
		// There's always a key left out: K
		// It must be inserted into the parent.
		
		
		/*int kept = (m_nbElements / 2);
		int copied = m_nbElements - kept;
		
		for( int i = kept; i < m_nbElements; i++ )
		{
			other.m_elements[i - kept] = m_elements[i];
			other.m_records [i - kept] = m_records [i];  
		}
		
		m_nbElements = kept + 1;
		other.m_nbElements = copied;
		
		// If current is leaf, split node is leaf also
		other.m_isLeaf = m_isLeaf;
		other.m_parent = m_parent;
		// Furthermore, fix up the last pointer for range queries
		other.m_records[other.m_records.length-1] = m_records[m_records.length-1];
		m_records[m_records.length-1] = other.m_node;*/
		
		m_dirty = true;
	}
	
	boolean IsLoaded() { return m_loaded; }
	boolean IsLeaf()   { return m_isLeaf; }
}
