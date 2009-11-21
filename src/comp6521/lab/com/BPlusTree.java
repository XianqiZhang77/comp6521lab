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
			for(int i = 0; i < page.m_records.length; i++)
				Insert( page.m_records[i] );
			
			MemoryManager.getInstance().freePage(page);
		}
		
		m_treeCreated = true;
	}
	
	public void Insert( Record rec )
	{
		// Load the root
		boolean RootLoaded = (m_root.IsLoaded());
		if( !RootLoaded )
			m_root.Load(0, m_n, m_filename);
		
		// We try to find a place for the new key in the appropriate leaf
 		//BPlusTreeNode target = getLeafNode( m_root, rec.get(m_key) );

 		// And we put it there if there is room
 		
		
		// If there is no room in the proper leaf, we split the leaf into two
		// and divide the keys between the two new nodes, so each is half full
		// Exception: the root.		
		// The splitting of nodes at one level appears to the level above as if a new key-pointer
		// pair needs to be inserted at that higher level. [[ apply recursively ]]
		
		// Clear the root if it wasn't previously loaded
		if( !RootLoaded )
			m_root.Clear();
	}
	
	BPlusTreeNode getNode( int nb )
	{
		BPlusTreeNode node = new BPlusTreeNode();
		node.Load(nb, m_n, m_filename);
		return node;
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
	
	BPlusTreeNode()
	{
		m_page       = null;
		m_elements   = null;
		m_records    = null;
		m_loaded     = false;
		m_nbElements = 0;
		m_isLeaf     = false;
	}
	
	public void Load(int node, int n, String filename)
	{
		if(m_loaded)
			Clear();
		
		m_elements = new RecordElement[n];
		m_records  = new int[n+1];
		
		m_page = MemoryManager.getInstance().getPage( BPlusTreePage.class, node, filename );
		
		Parse( m_page.m_records[0].toString() );
		m_loaded = true;
	}
	
	public void Clear()
	{
		if( m_page != null )
			MemoryManager.getInstance().freePage( m_page );
		
		m_elements = null;
		m_records  = null;
		m_loaded   = false;
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
	
	boolean IsLoaded() { return m_loaded; }
	boolean IsLeaf()   { return m_isLeaf; }
}
