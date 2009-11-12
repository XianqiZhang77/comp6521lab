package comp6521.lab.com;

import java.util.ArrayList;

import comp6521.lab.com.Pages.Page;
import comp6521.lab.com.Records.Record;
import comp6521.lab.com.Records.RecordElement;

public class LinearHashTable {
	boolean m_indexCreated;
	int m_bucketUniqueIndex;
	
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
		m_indexCreated = false;
		m_bucketUniqueIndex = 0;
		m_i = 1; // nb bits used for the has
		m_p2i = 2; // 2 ^ i
		m_n = 0; // number of buckets
		m_r = 0; // number of records
		m_buckets = new ArrayList< HashBucket >();
	}
	
	public <T extends Page<?> > void CreateHashTable( Class<T> pageClass, String filename, String key, HashFunction hf )
	{
		m_key = key;
		m_hf  = hf;
		
		// Methodology:
		// 1 - Get size of a page of the relation
		/*T pageDummy = MemoryManager.getInstance().getEmptyPage(pageClass);
		int pageSize = pageDummy.GetNumberRecordsPerPage() * pageDummy.CreateElement().GetRecordSize();		
		// 2 - Compute amount of buckets we can have in memory with this page.
		int remainingMemory = MemoryManager.getInstance().RemainingMemory();
		MemoryManager.getInstance().freePage( pageDummy );*/
		

		// Parse the whole table
		int p = 0;
		T page = null;
		while ( (page = MemoryManager.getInstance().getPage( pageClass, p++ )) != null )
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
			m_buckets.add( new HashBucket() );
			// Split the corresponding bucket
			int newB = m_buckets.size();
			int correspondingB = newB - (m_p2i / 2);
			
			// We re-hash everything in the bucket correspondingB.
			HashBucket newBucket = m_buckets.get(newB);
			HashBucket oldBucket = m_buckets.get(correspondingB);
			HashBucket roldBucket = new HashBucket();
			
			// ...
			
			m_buckets.set( correspondingB, roldBucket );
			
			m_n++;
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
	protected class HashBucket<T extends Page<?> >
	{
		int m_nbRecords;
		int m_nbBlocks;
		
		void AddRecord( Record rec )
		{
			//...
		}
	}	
	
	public abstract class HashFunction
	{
		abstract int Hash( RecordElement el );
	}
	

}
