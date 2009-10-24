package comp6521.lab.com;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import comp6521.lab.com.Pages.*;
import comp6521.lab.com.Records.*;

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
		int    m_recordLength;
		Set< Integer > m_pagesTaken;
		
		RecordKeeper( String type, int recordSize, int recordLength )
		{
			m_type = type;
			m_recordSize = recordSize;
			m_recordLength = recordLength;
			m_pagesTaken = new HashSet< Integer >();
		}
	}
	
	ArrayList<RecordKeeper> m_records;
	
	// Constructor
	private MemoryManager()
	{
		m_ActualMemory = 0;
		m_MaxMemory = 10240; // 10k = 10 * 2^10 in bytes
		
		m_records = new ArrayList<RecordKeeper>();
		// fill in some data... like the size of the records
		m_records.add(new RecordKeeper( CustomerPage.class.getName(), CustomerRecord.GetRecordSize(), CustomerRecord.GetRecordLength() ) );
		m_records.add(new RecordKeeper( LineItemPage.class.getName(), LineItemRecord.GetRecordSize(), LineItemRecord.GetRecordLength() ) );
		m_records.add(new RecordKeeper( NationPage.class.getName(),   NationRecord.GetRecordSize(),   NationRecord.GetRecordLength()   ) );
		m_records.add(new RecordKeeper( OrdersPage.class.getName(),   OrdersRecord.GetRecordSize(),   OrdersRecord.GetRecordLength()   ) );
		m_records.add(new RecordKeeper( PartPage.class.getName(),     PartRecord.GetRecordSize(),     PartRecord.GetRecordLength()     ) );
		m_records.add(new RecordKeeper( PartSuppPage.class.getName(), PartSuppRecord.GetRecordSize(), PartSuppRecord.GetRecordLength() ) );
		m_records.add(new RecordKeeper( RegionPage.class.getName(),   RegionRecord.GetRecordSize(),   RegionRecord.GetRecordLength()   ) );
		m_records.add(new RecordKeeper( SupplierPage.class.getName(), SupplierRecord.GetRecordSize(), SupplierRecord.GetRecordLength() ) );
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
	   	if( m_records.get(i).m_pagesTaken.contains( Integer.valueOf(pageNumber) ) )
		{
			System.out.println("Warning: asking for a page already in memory!");
			System.out.println("-- " + m_records.get(i).m_type + " page n." + pageNumber );
			return CreatePage( c, PageManagerSingleton.getInstance().getRawPage( m_records.get(i).m_filename, m_records.get(i).m_recordLength, pageNumber ) );
		}
    	
	   	// Check if we have enough memory left, if not, return null
	   	int NeededMemory = m_records.get(i).m_recordSize * PageManagerSingleton.getInstance().getNumberOfRecordsPerPage();
 
		if( m_ActualMemory + NeededMemory > m_MaxMemory )
		{
			return null;
		}
		else
		{
			T page = CreatePage( c, PageManagerSingleton.getInstance().getRawPage( m_records.get(i).m_filename, m_records.get(i).m_recordLength, pageNumber ) );
			
			if( page != null )
			{
				page.m_pageNumber = pageNumber;
				m_ActualMemory += NeededMemory;
				m_records.get(i).m_pagesTaken.add( Integer.valueOf(pageNumber) );				
			}
			
			return page;
		}  
    }
    
    private <T> int getPageIndex( Class<T> c )
    {
    	return getPageIndex( c.getName() );
    }
    
    private int getPageIndex( String type )
    {
    	for( int i = 0; i < m_records.size(); i++ )
    	{
    		if( m_records.get(i).m_type == type)
    			return i;
    	}
    	return -1;    
    }
    
    public void AddPageType(String type, int recordSize, int recordLength, String filename)
    {
    	// Make sure the page type doesn't exist.
		if( getPageIndex(type) == -1 )
			return;
    	
    	m_records.add(new RecordKeeper(type, recordSize, recordLength));
    	m_records.get(m_records.size()-1).m_filename = filename;
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

    public <T extends Page<?> > void freePage( T page )
    {
    	 int i = getPageIndex( page.getClass() );
    	 int pageNumber = page.m_pageNumber;
    	 
    	 // Make sure we're not cheating by checking the memory entry
    	 if( m_records.get(i).m_pagesTaken.contains( Integer.valueOf(pageNumber) ) )
    	 {
    		 // Restore available memory
    		 int FreedMemory = m_records.get(i).m_recordSize * PageManagerSingleton.getInstance().getNumberOfRecordsPerPage();
    		 m_ActualMemory -= FreedMemory;
    	 
    		 // Remove from memory entry 
    		 m_records.get(i).m_pagesTaken.remove( Integer.valueOf(pageNumber));    
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
    	for( int i = 0; i < m_records.size(); i++ )
    	{
    		if( !m_records.get(i).m_pagesTaken.isEmpty() )
    		{
    			System.out.println( m_records.get(i).m_type + " pages used : " + m_records.get(i).m_pagesTaken.size() + " == " + m_records.get(i).m_pagesTaken.size() * m_records.get(i).m_recordSize + " bytes " );
    		}
    	}
    	
    	System.out.println("------------------");
    	System.out.println("-- Free memory: " + RemainingMemory() + " bytes ");
    	System.out.println("------------------");    	
    }
	
	public <T extends Page<?> > void SetPageFile      ( Class<T> c, String filename ) { m_records.get(getPageIndex(c)).m_filename = filename; }
	public <T extends Page<?> > void SetPageRecordSize( Class<T> c, int size )        { m_records.get(getPageIndex(c)).m_recordSize = size;   }
}
