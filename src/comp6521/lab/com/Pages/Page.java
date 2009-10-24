package comp6521.lab.com.Pages;
import comp6521.lab.com.Records.Record;

public abstract class Page<T extends Record> {
	public T[] m_records;
	public int m_pageNumber;
	
	//public abstract void Construct( char[] rawData );
	public boolean isEmpty() { return m_records.length > 0; }
	
	protected abstract T[] CreateArray(int n);
	protected abstract T CreateElement();
	
	public void Construct( char[] rawData )
	{
		String rawPageString    = String.valueOf(rawData);
		String[] rawRecords     = rawPageString.split("\n");
		
		// Count nb. of non-null raw records
		int n = 0;
		for( int i = 0; i < rawRecords.length; i++ )
			if( rawRecords[i].trim().compareTo("") != 0 )
				n++;
		
		m_records = CreateArray(n);
		
		int r = 0;
		for( int i = 0; i < rawRecords.length; i++ )
		{
			String tR = rawRecords[i];
			
			if( tR.trim().compareTo("") == 0)
				continue;
			
			m_records[r] = CreateElement();
			// Fill in the record
			m_records[r].Parse(tR);
			
			r++;
		}
	}
}
