package comp6521.lab.com;

import java.util.HashSet;
import java.util.Set;

import comp6521.lab.com.Pages.CustomerPage;
import comp6521.lab.com.Pages.LineItemPage;
import comp6521.lab.com.Pages.NationPage;
import comp6521.lab.com.Pages.OrdersPage;
import comp6521.lab.com.Pages.Page;
import comp6521.lab.com.Pages.PartPage;
import comp6521.lab.com.Pages.PartSuppPage;
import comp6521.lab.com.Pages.RegionPage;
import comp6521.lab.com.Pages.SupplierPage;
import comp6521.lab.com.Records.CustomerRecord;
import comp6521.lab.com.Records.LineItemRecord;
import comp6521.lab.com.Records.NationRecord;
import comp6521.lab.com.Records.OrdersRecord;
import comp6521.lab.com.Records.PartRecord;
import comp6521.lab.com.Records.PartSuppRecord;
import comp6521.lab.com.Records.RegionRecord;
import comp6521.lab.com.Records.SupplierRecord;

/**
 * 
 * @author Julien L'Heureux
 *
 */
public class MemoryManager 
{
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
			m_pagesTaken = new HashSet< Integer >();
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
		m_records[0] = new RecordKeeper( CustomerPage.class.getName(), CustomerRecord.GetRecordSize() );
		m_records[1] = new RecordKeeper( LineItemPage.class.getName(), LineItemRecord.GetRecordSize() );
		m_records[2] = new RecordKeeper( NationPage.class.getName(),   NationRecord.GetRecordSize()   );
		m_records[3] = new RecordKeeper( OrdersPage.class.getName(),   OrdersRecord.GetRecordSize()   );
		m_records[4] = new RecordKeeper( PartPage.class.getName(),     PartRecord.GetRecordSize()     );
		m_records[5] = new RecordKeeper( PartSuppPage.class.getName(), PartSuppRecord.GetRecordSize() );
		m_records[6] = new RecordKeeper( RegionPage.class.getName(),   RegionRecord.GetRecordSize()   );
		m_records[7] = new RecordKeeper( SupplierPage.class.getName(), SupplierRecord.GetRecordSize() );
	}
	
	// Singleton
	private static final MemoryManager ms_Instance = new MemoryManager();
	public static MemoryManager getInstance() { return ms_Instance; }
	
	// Memory management variables
	int m_ActualMemory;
	int m_MaxMemory;
	
    // Simpler interface
    public <T extends Page<?> > T getPage( Class<T> c, int pageNumber )
    {
    	int i = getPageIndex( c );

	   	// First, check if we already have the page in memory.
	   	// In the case we do, output a log message to the console,
	   	// but read the page anyway and do not consume memory
	   	if( m_records[i].m_pagesTaken.contains( Integer.valueOf(pageNumber) ) )
		{
			System.out.println("Warning: asking for a page already in memory!");
			System.out.println("-- " + m_records[i].m_type + " page n." + pageNumber );
			return CreatePage( c, PageManagerSingleton.getInstance().getPage( m_records[i].m_filename, m_records[i].m_recordSize, pageNumber ) );
		}
    	
	   	// Check if we have enough memory left, if not, return null
	   	int NeededMemory = m_records[i].m_recordSize * PageManagerSingleton.getInstance().getNumberOfRecordsPerPage();
 
		if( m_ActualMemory + NeededMemory > m_MaxMemory )
		{
			return null;
		}
		else
		{
			T page = CreatePage( c, PageManagerSingleton.getInstance().getPage( m_records[i].m_filename, m_records[i].m_recordSize, pageNumber ) );
			
			if( page != null )
			{
				m_ActualMemory += NeededMemory;
				m_records[i].m_pagesTaken.add( Integer.valueOf(pageNumber) );				
			}
			
			return page;
		}  
    }
    
    private <T> int getPageIndex( Class<T> c )
    {
    	for( int i = 0; i < m_records.length; i++ )
    	{
    		if( m_records[i].m_type == c.getName())
    			return i;
    	}
    	return -1;
    }
    
    private <T extends Page<?> > T CreatePage( Class<T> c, char[] rawData )
    {
    	T page = null;
    	
    	// If the file couldn't be read, we shouldn't create a page.
    	if( rawData == null )
    		return page;
    	
    	try {
			page = c.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		
		if( page != null )
			page.Construct( rawData );
    	
		return page;
    }
         
    public int RemainingMemory() { return m_MaxMemory - m_ActualMemory; }

    public <T extends Page<?> > void freePage( T page, int pageNumber )
    {
    	 int i = getPageIndex( page.getClass() );
    	 
    	 // Make sure we're not cheating by checking the memory entry
    	 if( m_records[i].m_pagesTaken.contains( Integer.valueOf(pageNumber) ) )
    	 {
    		 // Restore available memory
    		 int FreedMemory = m_records[i].m_recordSize * PageManagerSingleton.getInstance().getNumberOfRecordsPerPage();
    		 m_ActualMemory -= FreedMemory;
    	 
    		 // Remove from memory entry 
    		 m_records[i].m_pagesTaken.remove( Integer.valueOf(pageNumber));    
    		 page = null;
    	 }
    	 else
    	 {
    		 // Explode
    		 System.out.println("Trying to free a page that wasn't loaded or that was freed already!");
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
	
	public <T extends Page<?> > void SetPageFile      ( Class<T> c, String filename ) { m_records[getPageIndex(c)].m_filename = filename; }
	public <T extends Page<?> > void SetPageRecordSize( Class<T> c, int size )        { m_records[getPageIndex(c)].m_recordSize = size;   }
}
