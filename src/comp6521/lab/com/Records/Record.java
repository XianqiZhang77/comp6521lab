package comp6521.lab.com.Records;

import java.util.Hashtable;

public abstract class Record {
	Record() { m_OrderKey = 0; }
		
	Integer m_OrderKey;
	Hashtable<Integer, RecordElement> m_recordElementsOrder;
	Hashtable<String, RecordElement> m_recordElements;
	
	public void AddElement( String name, RecordElement el )
	{
		m_recordElements.put(name, el);
		m_recordElementsOrder.put(m_OrderKey++, el);		
	}
	
	RecordElement getElement(String name) { return m_recordElements.get(name); }
	
	public void Parse(String data)
	{
		int pos = 0;
		int len = 0;
		for( int i = 0; i < m_OrderKey.intValue(); i++ )
		{
			
			len = m_recordElementsOrder.get( Integer.valueOf(i) ).Size();
			m_recordElementsOrder.get( Integer.valueOf(i) ).Parse( data.substring(pos, pos + len - 1));
			pos += len;
		}
	}
	
	public String Write()
	{
		String data = "";
		for( int i = 0; i < m_OrderKey.intValue(); i++ )
			data += m_recordElementsOrder.get( Integer.valueOf(i) ).Write();
		return data;
	}
	
	public int GetRecordSize()
	{
		int len = 0;
		for( int i = 0; i < m_OrderKey.intValue(); i++ )
			len += m_recordElementsOrder.get( Integer.valueOf(i) ).Size();
		return len + 2; // for the CF+LN	
	}
}