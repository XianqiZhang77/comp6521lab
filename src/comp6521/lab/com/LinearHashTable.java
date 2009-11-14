package comp6521.lab.com;

import java.util.ArrayList;

import comp6521.lab.com.Hashing.HashFunction;
import comp6521.lab.com.Pages.Page;
import comp6521.lab.com.Records.Record;
import comp6521.lab.com.Records.RecordElement;

public class LinearHashTable< T extends Page<?> > {
	boolean m_indexCreated;
	//static int m_bucketUniqueIndex;
	
	Class<T> m_pageType;
	String m_filename;
	int m_i;
	int m_p2i;
	int m_n;
	int m_r;
	double m_threshold;
	
	String       m_key;
	HashFunction m_hf;
	
	ArrayList< HashBucket > m_buckets;
	
	public LinearHashTable()
	{
		m_pageType = null;
		m_indexCreated = false;
		//m_bucketUniqueIndex = 0;
		m_i = 1; // nb bits used for the has
		m_p2i = 2; // 2 ^ i
		m_n = 0; // number of buckets
		m_r = 0; // number of records
		m_buckets = new ArrayList< HashBucket >();
		m_threshold = 1.5;
	}
	
	public void CreateHashTable( Class<T> pageClass, String filename, String key, HashFunction hf )
	{
		m_pageType = pageClass;
		m_filename = filename;
		m_key = key;
		m_hf  = hf;
		
		// Add the new entry to the memory manager
		MemoryManager.getInstance().AddPageType( m_pageType, m_filename );
		
		// Add the first two buckets
		m_buckets.add( new HashBucket(m_pageType, m_filename) );
		m_buckets.add( new HashBucket(m_pageType, m_filename) );
		
		// Parse the whole table
		int p = 0;
		T page = null;
		while ( (page = MemoryManager.getInstance().getPage( m_pageType, p++ )) != null )
		{
			for(int i = 0; i < page.m_records.length; i++)
				Insertion( page.m_records[i] );
		}
	}
	
	protected void Insertion( Record rec )
	{
		// Compute h(K)
		int hk = m_hf.Hash( rec.get(m_key) );
		
		// Get a1a2...ai
		int m = hk % m_p2i; // hash
		int b = 0;          // bucket number
		
		if( m < m_n )
			b = m; // The bucket exists
		else if( m < m_p2i )
			b = m - (m_p2i / 2); // The bucket does not yet exist, so we place it in
		else
			assert(false); // Not supposed to happen

		// If there is room in the bucket, add it there.
		// If there is no room, add an overflow block..
		m_buckets.get(b).AddRecord( rec );
		m_r++;
		
		// If r/n  > threshold
		if( ((double)m_r / (double)m_n) > m_threshold)
		{
			// Add a bucket.
			m_buckets.add( new HashBucket(m_pageType, m_filename) );
			m_n++;
			// Split the corresponding bucket
			int newB = m_buckets.size();
			int correspondingB = newB - (m_p2i / 2);
			
			// We re-hash everything in the bucket correspondingB.
			HashBucket oldBucket = m_buckets.get(correspondingB);
						
			// Replace old bucket with a new shiny one.
			HashBucket ShinyOldBucket = new HashBucket(m_pageType, m_filename);
			m_buckets.set( correspondingB, ShinyOldBucket );
			
			// IMPORTANT NOTE :: THE BUCKET SPLITTING IS HIGHLY INEFFICIENT
			// DUE TO THE FACT THAT WE ADD EACH RECORD ONE AT A TIME
			// Now, this is a bit funky..
			int oldr = m_r;
			int oldn = m_n;
			int oldi = m_i;
			// We simulate that we remove all the records in the old bucket
			m_r -= oldBucket.m_nbRecords;
					
			// And re-add them to the hash index
			for( int i = 0; i < oldBucket.m_nbRecords; i++ )
			{
				Insertion( oldBucket.GetRecord(i) );
			}
			
			assert( oldr == m_r );
			assert( oldn == m_n );
			assert( oldi == m_i );			
		}
		
		// If n > 2^i
		// Increment i, 
		if( m_n > m_p2i)
		{
			m_i++;
			m_p2i *= 2;
		}		
	}
	
	////////////////
	// Subclasses //
	////////////////
	protected class HashBucket
	{
		int m_nbRecordsPerPage;
		public int m_nbRecords;
		ArrayList< Integer > m_pageNumbers;
		Class<T> m_pageType;
		String m_filename;
		
		public HashBucket( Class<T> c, String filename )
		{
			m_pageType = c;
			m_filename = filename;
			m_nbRecordsPerPage = 10; // TO CHANGE	
			m_pageNumbers = new ArrayList< Integer >();
		}
		
		public <S extends Record> void AddRecord( Record rec )
		{
			// If current page is full, create new page.
			int nextRecPage = (m_nbRecords + 1) / m_nbRecordsPerPage;
			int nextRecNb   = (m_nbRecords + 1) % m_nbRecordsPerPage;
			
			T page = null;
			
			// Add a new page if needed
			if( ( nextRecPage + 1 ) > m_pageNumbers.size() )
			{
				assert( (nextRecPage + 1) == m_pageNumbers.size() + 1 );
				// ...
				page = MemoryManager.getInstance().getEmptyPage( m_pageType , m_filename );
				m_pageNumbers.add( new Integer(page.m_pageNumber) );
			}
			else
			{
				// Get page from the memory manager.
				page = MemoryManager.getInstance().getPage( m_pageType, m_pageNumbers.get(nextRecPage).intValue(), m_filename );
			}
			
			page.setRecord( nextRecNb, rec );
			m_nbRecords++;
		}
		
		Record GetRecord( int i )
		{
			int recPage = i / m_nbRecordsPerPage;
			int recIdx  = i % m_nbRecordsPerPage;
			
			assert( recPage < m_pageNumbers.size() );
			T page = MemoryManager.getInstance().getPage( m_pageType, m_pageNumbers.get(recPage).intValue(), m_filename );
			return page.m_records[recIdx];
		}
	}	
}
