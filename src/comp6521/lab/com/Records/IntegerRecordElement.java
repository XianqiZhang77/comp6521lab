package comp6521.lab.com.Records;

public class IntegerRecordElement extends RecordElement {
	protected int m_value;
	
	public void Parse(String data) { m_value = Integer.parseInt(data.trim()); }
	public String Write()          { return String.format("%1$-11d", m_value); }
	public int Size()              { return 11; }
	
	public int  getInt()           { return m_value; }
	public void setInt(int val)    { m_value = val; }
	
	public void set(RecordElement other) { m_value = other.getInt(); }
}
