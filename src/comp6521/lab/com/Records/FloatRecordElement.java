package comp6521.lab.com.Records;

public class FloatRecordElement extends RecordElement {
	protected float m_value;
	
	public void Parse(String data) { m_value = Float.parseFloat(data.trim()); }
	public String Write()          { return String.format("%1$-22f", m_value); }
	public int Size()              { return 22; }
	
	public float getFloat()        { return m_value; }
	public void setFloat(float val){ m_value = val; }
	
	public void set(RecordElement other) { m_value = other.getFloat(); }
}
