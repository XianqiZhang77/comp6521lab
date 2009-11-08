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
		int m_pageSize;
		Set< Integer > m_pagesTaken;
		
		RecordKeeper( String type, int pageSize )
		{
			m_type = type;
			m_pageSize = pageSize;
			m_pagesTaken = new HashSet< Integer >();
		}
	}
	
	// Members
	ArrayList<RecordKeeper> m_records;
	static int m_createdPagesIndex;
	
	// Constructor
	private MemoryManager()
	{
		m_ActualMemory = 0;
		m_MaxMemory = 10240; // 10k = 10 * 2^10 in bytes
		m_createdPagesIndex = -1;
		
		m_records = new ArrayList<RecordKeeper>();
		
		AddPageType( CustomerPage.class, "");
		AddPageType( LineItemPage.class, "");
		AddPageType( NationPage.class,   "");
		AddPageType( OrdersPage.class,   "");
		AddPageType( PartPage.class,     "");
		AddPageType( PartSuppPage.class, "");
		AddPageType( RegionPage.class,   "");
		AddPageType( SupplierPage.class, "");
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
			return CreatePage( c, PageManagerSingleton.getInstance().getRawPage( m_records.get(i).m_filename, m_records.get(i).m_pageSize, pageNumber ) );
		}
    	
	   	// Check if we have enough memory left, if not, return null
	   	int NeededMemory = m_records.get(i).m_pageSize;
 
		if( m_ActualMemory + NeededMemory > m_MaxMemory )
		{
			return null;
		}
		else
		{
			T page = CreatePage( c, PageManagerSingleton.getInstance().getRawPage( m_records.get(i).m_filename, m_records.get(i).m_pageSize, pageNumber ) );
			
			if( page != null )
			{
				page.m_pageNumber = pageNumber;
				m_ActualMemory += NeededMemory;
				m_records.get(i).m_pagesTaken.add( Integer.valueOf(pageNumber) );				
			}
			
			return page;
		}  
    }
    
    public <T extends Page<?> > T getEmptyPage( Class<T> c )
    {
    	int i = getPageIndex( c );
    	// Check if we have enough memory to create the page
    	int NeededMemory = m_records.get(i).m_pageSize;
    	
    	if( m_ActualMemory + NeededMemory > m_MaxMemory )
    		return null;
    	else
    	{
    		T page = CreateEmptyPage( c );
    		if( page != null )
    		{
    			int pageNumber = m_createdPagesIndex--;
    			page.m_pageNumber = pageNumber;
    			m_ActualMemory += NeededMemory;
    			m_records.get(i).m_pagesTaken.add( Integer.valueOf(pageNumber));
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
    
    public <T extends Page<?> > void AddPageType( Class<T> c, String filename )
    {
    	String type = c.getName();
    	// Make sure the page type doesn't exist.
		if( getPageIndex(type) != -1 )
			return;
		
		// Compute page size
		T dummyPage = CreateEmptyPage( c );
		Record dummyRecord = dummyPage.CreateElement();
		
		int pageSize = dummyPage.GetNumberRecordsPerPage() * dummyRecord.GetRecordSize();
    	
    	m_records.add(new RecordKeeper(type, pageSize));
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
    
    private <T extends Page<?> > T CreateEmptyPage( Class<T> c )
    {
    	T page = null;
    	
    	try {
			page = c.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
    
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
    		 int FreedMemory = m_records.get(i).m_pageSize;
    		 m_ActualMemory -= FreedMemory;
    	 
    		 // Remove from memory entry 
    		 m_records.get(i).m_pagesTaken.remove( Integer.valueOf(pageNumber));    
    		 
    		 // Perform any cleanup needed (auto-writes, for example)
    		 page.Cleanup();
    		 
    		 page = null;
    	 }
    	 else
    	 {
    		 // Explode
    		 System.out.println("Trying to free a page that wasn't loaded or that was freed already!");
    	 }
    }
    
    public <T extends Page<?> > void writePage( T page )
    {
    	if( page == null )
    		return;
    	
    	int i = getPageIndex( page.getClass() );   	
    	PageManagerSingleton.getInstance().writePage( m_records.get(i).m_filename, page.GetRawData(), page.m_pageNumber);    	
    }
    
    public void ReportMemoryUse()
    {
    	System.out.println("------------------");
    	System.out.println("-- Memory usage --");
    	for( int i = 0; i < m_records.size(); i++ )
    	{
    		if( !m_records.get(i).m_pagesTaken.isEmpty() )
    		{
    			System.out.println( m_records.get(i).m_type + " pages used : " + m_records.get(i).m_pagesTaken.size() + " == " + m_records.get(i).m_pagesTaken.size() * m_records.get(i).m_pageSize + " bytes " );
    		}
    	}
    	
    	System.out.println("------------------");
    	System.out.println("-- Free memory: " + RemainingMemory() + " bytes ");
    	System.out.println("------------------");    	
    }
	
	public <T extends Page<?> > void SetPageFile( Class<T> c, String filename ) { m_records.get(getPageIndex(c)).m_filename = filename; }
	public <T extends Page<?> > void SetPageSize( Class<T> c, int size )        { m_records.get(getPageIndex(c)).m_pageSize = size;     }
}
