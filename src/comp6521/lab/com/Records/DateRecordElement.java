package comp6521.lab.com.Records;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateRecordElement extends RecordElement {
	protected Date m_value;
	
	public DateRecordElement()
	{
		m_value = new Date();
	}
	
	public void Parse(String data)
	{
		SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat formatter2 = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

		try
		{
			m_value = (Date)formatter1.parse(data);
		}
		catch(ParseException pe1)
		{
			try
			{
				m_value = (Date)formatter2.parse(data);
			}
			catch(ParseException pe2)
			{
				System.err.println(pe2.toString());
			}
		}
	}
	public String Write()
	{
		//return DateFormat.getInstance().format(m_value);
		SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return formatter1.format(m_value);
	}
	
	public int Size() { return 19; }
	
	public Date getDate() { return m_value; }
	public void setDate(Date val) { m_value = val; }
	
	public int CompareTo(RecordElement el) { return m_value.compareTo(el.getDate()); }
}
