package comp6521.lab.com.Records;

public class FloatRecordElement extends RecordElement {
	protected double m_value;
	
	public void Parse(String data) { m_value = Double.parseDouble(data.trim()); }
	public String Write()          { return String.format("%1$-22f", m_value); }
	public int Size()              { return 22; }
	
	public double getFloat()       { return m_value; }
	public void setFloat(double val){ m_value = val; }
	
	public void set(RecordElement other) { m_value = other.getFloat(); }
	
	public int CompareTo(RecordElement el)
	{
		double val = el.getFloat();
		
		if( m_value == val )
			return 0;
		else if( m_value > val )
			return 1;
		else
			return -1;			
	}
}
