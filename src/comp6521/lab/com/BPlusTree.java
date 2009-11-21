package comp6521.lab.com;

import comp6521.lab.com.Pages.Page;
import comp6521.lab.com.Records.*;

public class BPlusTree<T extends Page<?> > {
	boolean m_treeCreated;
	
	Class<T> m_pageType;
	String   m_filename;

	String   m_key;	
	int      m_n;

	public BPlusTree()
	{
		m_treeCreated = false;
	}
	
	public void CreateBPlusTree( Class<T> pageClass, String filename, String key )
	{
		m_pageType = pageClass;
		m_filename = filename;
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
	BPlusTreePage   m_page;
	RecordElement[] m_elements;
	int[]           m_records;
	
	BPlusTreeNode()
	{
		m_page     = null;
		m_elements = null;
		m_records  = null;
	}
	
	public void Load(int node, int n, String filename)
	{
		m_elements = new RecordElement[n];
		m_records  = new int[n+1];
		
		m_page = MemoryManager.getInstance().getPage( BPlusTreePage.class, node, filename );
		
		Parse( m_page.m_records[0].toString() );
	}
	
	public void Clear()
	{
		if( m_page != null )
			MemoryManager.getInstance().freePage( m_page );
		
		m_elements = null;
		m_records  = null;
	}
	
	void Parse(String data)
	{
		int elementSize = m_elements[0].Size();
		int pointerSize = 4;
		
		assert( data.length() - pointerSize % (elementSize + pointerSize) == 0);
		int n = (data.length() - pointerSize) / (elementSize + pointerSize);
		
		for( int i = 0; i < n; i++ )
		{
			// Pointer first
			// element second
		}
		// Pointer to leaf
	}
}
