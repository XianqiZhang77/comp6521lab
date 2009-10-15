package comp6521.lab.com.Pages;
import comp6521.lab.com.Records.Record;

public abstract class Page<T extends Record> {
	public T[] m_records;
	public int m_pageNumber;
	
	public abstract void Construct( char[] rawData );
	public boolean isEmpty() { return m_records.length > 0; }
}
