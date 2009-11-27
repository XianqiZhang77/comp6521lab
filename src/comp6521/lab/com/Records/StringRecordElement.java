package comp6521.lab.com.Records;

public class StringRecordElement extends RecordElement {
	protected int m_size;
	protected String m_value;
	
	// Constructor
	public StringRecordElement( int size ) { m_size = size; }
	
	public void Parse(String data) { m_value = data.trim(); }
	public String Write()          { String custformat= "%1$-" + m_size + "s"; 	return String.format(custformat, m_value); }
	

	public int Size()              { return m_size; }
	
	public String getString() { return m_value; }
	public void   setString(String data) { m_value = data; }
	
	public void set(RecordElement other) { m_value = other.getString(); }
	
	public int CompareTo(RecordElement el) { return m_value.compareTo(el.getString());}
	
	// --- Special methods used to instantiate template objects ---
	public StringRecordElement( ) { m_size = -1; } // USE THIS AT YOUR OWN RISK
	public void setSize( int size ) { m_size = size; }
}
