package comp6521.lab.com;

import java.util.Set;

/**
 * 
 * @author Julien
 *
 */
public class MemoryManager 
{
	public enum RecordType 
	{ 
		eCustomerPage(0),
		eLineItemPage(1),
		eNationPage(2),
		eOrdersPage(3),
		ePartPage(4),
		ePartSuppPage(5),
		eRegionPage(6),
		eSupplierPage(7);
		
		private final int index;
		RecordType(int i){index = i;}		
	}
	
	
	// Records keeper
	private class RecordKeeper
	{
		String m_type;
		String m_filename;
		int    m_recordSize;
		Set< Integer > m_pagesTaken;
		
		RecordKeeper( String type, int recordSize )
		{
			m_type = type;
			m_recordSize = recordSize;
		}
	}
	
	RecordKeeper[] m_records;
	
	// Constructor
	private MemoryManager()
	{
		m_ActualMemory = 0;
		m_MaxMemory = 10240; // 10k = 10 * 2^10 in bytes
		
		m_records = new RecordKeeper[8];
		// fill in some data... like the size of the records
		m_records[0] = new RecordKeeper( String.valueOf("Customer"), 0 );
		m_records[1] = new RecordKeeper( String.valueOf("LineItem"), 0 );
		m_records[2] = new RecordKeeper( String.valueOf("Nation"),   0 );
		m_records[3] = new RecordKeeper( String.valueOf("Orders"),   0 );
		m_records[4] = new RecordKeeper( String.valueOf("Part"),     0 );
		m_records[5] = new RecordKeeper( String.valueOf("PartSupp"), 0 );
		m_records[6] = new RecordKeeper( String.valueOf("Region"),   0 );
		m_records[7] = new RecordKeeper( String.valueOf("Supplier"), 0 );
	}
	
	// Singleton
	private static final MemoryManager ms_Instance = new MemoryManager();
	public MemoryManager getInstance() { return ms_Instance; }
	
	// Memory management variables
	int m_ActualMemory;
	int m_MaxMemory;
     
	// Memory manager interface to the page manager
    public char[] getPage( RecordType pageType, int pageNumber )
    {
    	 int i = pageType.index;
    	 // Check if we have enough memory left, if not, return null
    	 int NeededMemory = m_records[i].m_recordSize * PageManagerSingleton.getInstance().getNumberOfRecordsPerPage();
    	 
    	 if( m_ActualMemory + NeededMemory > m_MaxMemory )
    	 {
    		 return null;
    	 }
    	 else
    	 {
    		 m_ActualMemory += NeededMemory;
    		 char[] rawData = PageManagerSingleton.getInstance().getPage( m_records[i].m_filename, m_records[i].m_recordSize, pageNumber );
    		 
    		 // Add memory entry
    		 m_records[i].m_pagesTaken.add( Integer.valueOf(pageNumber) );
    		 
    		 return rawData;
    	 }   	 
    }
     
    public int RemainingMemory() { return m_MaxMemory - m_ActualMemory; }

    public void freePage( RecordType pageType, int pageNumber )
    {
    	 int i = pageType.index;
    	 
    	 // Make sure we're not cheating by checking the memory entry
    	 if( m_records[i].m_pagesTaken.contains( Integer.valueOf(pageNumber) ) )
    	 {
    		 // Restore available memory
    		 int FreedMemory = m_records[i].m_recordSize * PageManagerSingleton.getInstance().getNumberOfRecordsPerPage();
    		 m_ActualMemory -= FreedMemory;
    	 
    		 // Remove from memory entry 
    		 m_records[i].m_pagesTaken.remove( Integer.valueOf(pageNumber));    		 
    	 }
    	 else
    	 {
    		 // Explode
    	 }
    }
    
    public void ReportMemoryUse()
    {
    	System.out.println("------------------");
    	System.out.println("-- Memory usage --");
    	for( int i = 0; i < m_records.length; i++ )
    	{
    		if( !m_records[i].m_pagesTaken.isEmpty() )
    		{
    			System.out.println( m_records[i].m_type + " pages used : " + m_records[i].m_pagesTaken.size() + " == " + m_records[i].m_pagesTaken.size() * m_records[i].m_recordSize + " bytes " );
    		}
    	}
    	
    	System.out.println("------------------");
    	System.out.println("-- Free memory: " + RemainingMemory() + " bytes ");
    	System.out.println("------------------");    	
    }
	
	public void SetPageFile ( RecordType recType, String filename ) { m_records[recType.index].m_filename   = filename; }
	public void SetPageRecordSize( RecordType recType, int size )   { m_records[recType.index].m_recordSize = size;     }
}
