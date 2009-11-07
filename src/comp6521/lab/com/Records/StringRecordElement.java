package comp6521.lab.com.Records;

public class StringRecordElement extends RecordElement {
	protected int m_size;
	protected String m_value;
	
	// Constructor
	StringRecordElement( int size ) { m_size = size; }
	
	public void Parse(String data) { m_value = data.trim(); }
	public String Write()          { String custformat= "%1$-" + m_size + "c"; return String.format(custformat, m_value.toCharArray()); }
	public int Size()              { return m_size; }
	
	public String getString() { return m_value; }
	public void   setString(String data) { m_value = data; }
}
