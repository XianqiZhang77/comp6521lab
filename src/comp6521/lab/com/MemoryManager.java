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
		int m_pageNumber; // Will be used to get page numbers at the end of the file...
		
		RecordKeeper( String type, int pageSize, String filename )
		{
			m_type = type;
			m_filename = filename;
			m_pageSize = pageSize;
			m_pagesTaken = new HashSet< Integer >();
			m_pageNumber = (int)(PageManagerSingleton.getInstance().getLength( filename ) / (long)pageSize);
		}
	}
	
	// Members
	ArrayList<RecordKeeper> m_records;
	
	// Constructor
	private MemoryManager()
	{
		m_ActualMemory = 0;
		m_MaxMemory = 10240; // 10k = 10 * 2^10 in bytes
		
		m_records = new ArrayList<RecordKeeper>();
		
		AddPageType( CustomerPage.class, "" );
		AddPageType( LineItemPage.class, "" );
		AddPageType( NationPage.class,   "" );
		AddPageType( OrdersPage.class,   "" );
		AddPageType( PartPage.class,     "" );
		AddPageType( PartSuppPage.class, "" );
		AddPageType( RegionPage.class,   "" );
		AddPageType( SupplierPage.class, "" );
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
    	assert(i >= 0);
    	return getPage( c, m_records.get(i), pageNumber );
    }
    
    public <T extends Page<?> > T getPage( Class<T> c, int pageNumber, String filename)
    {
    	int i = getPageIndex( c, filename );
    	assert(i >= 0);
    	return getPage( c, m_records.get(i), pageNumber );
    }
        
    public <T extends Page<?> > T getPage( Class<T> c, RecordKeeper rk, int pageNumber )
    {
    	int NeededMemory = 0;
    	T page = null;
	   	// First, check if we already have the page in memory.
	   	// In the case we do, output a log message to the console,
	   	// but read the page anyway and do not consume memory
	   	if( rk.m_pagesTaken.contains( Integer.valueOf(pageNumber) ) )
		{
			System.out.println("Warning: asking for a page already in memory!");
			System.out.println("-- " + rk.m_type + " page n." + pageNumber );
			page = CreatePage( c, PageManagerSingleton.getInstance().getRawPage( rk.m_filename, rk.m_pageSize, pageNumber ) );
		}
	   	else
	   	{
    	   	// Check if we have enough memory left, if not, return null
		   	NeededMemory = rk.m_pageSize;
	 
			if( m_ActualMemory + NeededMemory > m_MaxMemory )
			{
				// Out of mem --
				System.out.println("** Warning ** Out of memory!" );
			}
			else
			{
				// Normal case
				page = CreatePage( c, PageManagerSingleton.getInstance().getRawPage( rk.m_filename, rk.m_pageSize, pageNumber ) );
			}
	   	}
		
		// Setup page info
		if( page != null )
		{
			page.m_pageNumber = pageNumber;
			page.m_filename   = rk.m_filename;
			
			// And count memory
			m_ActualMemory += NeededMemory;
			rk.m_pagesTaken.add( Integer.valueOf(pageNumber) );				
		}		
		
		return page;
    }
    
    public <T extends Page<?> > T getEmptyPage( Class<T> c )
    {
    	int i = getPageIndex( c );
    	assert(i >= 0);
    	return getEmptyPage( c, m_records.get(i), -1 );
    }
    
    public <T extends Page<?> > T getEmptyPage( Class<T> c, String filename )
    {
    	int i = getPageIndex( c, filename );
    	assert(i >= 0);
    	return getEmptyPage( c, m_records.get(i), -1 );
    }
    
    public <T extends Page<?> > T getEmptyPage( Class<T> c, RecordKeeper rk, int pn )
    {
    	// Check if we have enough memory to create the page
    	int NeededMemory = rk.m_pageSize;
    	
    	if( m_ActualMemory + NeededMemory > m_MaxMemory )
    		return null;
    	else
    	{
    		T page = CreateEmptyPage( c );
    		if( page != null )
    		{
    			if( pn < 0 )
    			{
    				page.m_pageNumber = rk.m_pageNumber; 
    				rk.m_pageNumber++;
    			}
    			else
    			{
    				page.m_pageNumber = pn;
    				if( pn >= rk.m_pageNumber )
    					rk.m_pageNumber = pn + 1;
    			}
    			
    			page.m_filename = rk.m_filename;
    			m_ActualMemory += NeededMemory;
    			rk.m_pagesTaken.add( Integer.valueOf(page.m_pageNumber));
    		}
    		return page;
    	}
    }
    
    public <T extends Page<?> > T getRWPage( Class<T> c, int pageNumber, String filename )
    {
    	// Get the record keeper
    	RecordKeeper rk = m_records.get( getPageIndex( c, filename ) );
    	
  		if( pageNumber < rk.m_pageNumber )
   		{
    		return getPage( c, rk, pageNumber );
    	}
    	else
    	{
    		return getEmptyPage( c, rk, pageNumber );
  		}
    }
    
    private <T> int getPageIndex( Class<T> c )                  { return getPageIndex(c, "", false); }
    private <T> int getPageIndex( Class<T> c, String filename ) { return getPageIndex(c, filename, true); }
    private <T> int getPageIndex( Class<T> c, String filename, boolean CheckFilename )
    {
    	String type = c.getName();
    	for( int i = 0; i < m_records.size(); i++ )
    		if( m_records.get(i).m_type.compareTo(type) == 0 && 
    		    (!CheckFilename || m_records.get(i).m_filename.compareTo(filename) == 0) )
    				return i;
    	return -1;
    }
    
    private RecordKeeper getRecordKeeperFromPage( Page<?> p )
    {
    	int i = getPageIndex( p.getClass(), p.m_filename );
    	assert(i >= 0);
    	return m_records.get(i);    		
    }
    
    public <T extends Page<?> > int GetNumberOfRecordsPerPage( Class<T> c )
    {
		T dummyPage = CreateEmptyPage( c );    	
		return dummyPage.GetNumberRecordsPerPage();
    }
    
    public <T extends Page<?> > int GetPageSize( Class<T> c )
    {
		// Compute page size
		T dummyPage = CreateEmptyPage( c );
		Record dummyRecord = dummyPage.CreateElement();
		
		return dummyPage.GetNumberRecordsPerPage() * dummyRecord.GetRecordSize();
    }
    
    public <T extends Page<?> > void AddPageType( Class<T> c, String filename )
    {
    	// Make sure the page type doesn't exist.
		if( getPageIndex(c, filename) != -1 )
			return;
		    	
		RecordKeeper rk = new RecordKeeper(c.getName(), GetPageSize(c), filename);
		m_records.add(rk);
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
		
		if( page != null )
			page.CreateEmptyPage();
    
		return page;
    }
         
    public int RemainingMemory() { return m_MaxMemory - m_ActualMemory; }

    public <T extends Page<?> > void freePage( T page )
    {
         RecordKeeper rk = getRecordKeeperFromPage( page ); 
    	 int pageNumber = page.m_pageNumber;
    	 
    	 // Make sure we're not cheating by checking the memory entry
    	 if( rk.m_pagesTaken.contains( Integer.valueOf(pageNumber) ) )
    	 {
    		 // Restore available memory
    		 int FreedMemory = rk.m_pageSize;
    		 m_ActualMemory -= FreedMemory;
    	 
    		 // Remove from memory entry 
    		 rk.m_pagesTaken.remove( Integer.valueOf(pageNumber));    
    		 
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
    
    public <T extends Page<?> > void GetNextEmptyPage( T page )
    {
    // Duplicate the freePage / getEmpty page process
    RecordKeeper rk = getRecordKeeperFromPage( page ); 
   	int pageNumber = page.m_pageNumber;
   	 
   	 // Make sure we're not cheating by checking the memory entry
   	 if( rk.m_pagesTaken.contains( Integer.valueOf(pageNumber) ) )
   	 	{
   		 // Remove from memory entry 
   		 rk.m_pagesTaken.remove( Integer.valueOf(pageNumber));    
   		 
   		 // Perform any cleanup needed (auto-writes, for example)
   		 page.Cleanup();    		 
   		 
   	   	 page.m_pageNumber = rk.m_pageNumber;
   	   	 rk.m_pageNumber++;
   	   	 
   	   	 rk.m_pagesTaken.add(Integer.valueOf(page.m_pageNumber));
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
    	{
    		System.out.println("*Warning* trying to write a null page");
    		return;
    	}
    	
    	RecordKeeper rk = getRecordKeeperFromPage( page );
  		PageManagerSingleton.getInstance().writePage( rk.m_filename, rk.m_pageSize, page.m_pageNumber, page.GetRawData() );  	
    }
    
    public <T extends Page<?> > void writePage( T page, String filename, int pageNumber )
    {
    	if( page == null )
    	{
    		System.out.println("*Warning* trying to write a null page");
    		return;
    	}
    	
    	RecordKeeper rk = getRecordKeeperFromPage( page );
  		PageManagerSingleton.getInstance().writePage( filename, rk.m_pageSize, pageNumber, page.GetRawData() );    	
    }
    
    public <T extends Page<?> > int GetNumberOfPages( Class<T> c, String filename )
    {
    	int i = getPageIndex( c, filename );
    	RecordKeeper rk = m_records.get(i);
    	
    	int len = (int)PageManagerSingleton.getInstance().getLength( rk.m_filename );
    	
    	double result = (double) len % (double) rk.m_pageSize;
    	if (result != 0)
    	{
    		result = ((double) len / (double) rk.m_pageSize) + 1;
    	}
    	else
    	{
    		result = (double) len / (double) rk.m_pageSize;
    	}
    	
    	return (int) result;
    }
    
    public <T extends Page<?> > int GetNumberOfRecords( Class<T> c, String filename )
    {
		// Compute page size
		T dummyPage = CreateEmptyPage( c );
		Record dummyRecord = dummyPage.CreateElement();
    	
    	int len = (int)PageManagerSingleton.getInstance().getLength( filename );
    	
    	return len / dummyRecord.GetRecordSize();   
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
}
