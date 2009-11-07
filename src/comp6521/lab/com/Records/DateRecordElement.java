package comp6521.lab.com.Records;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateRecordElement extends RecordElement {
	protected Date m_value;
	
	public void Parse(String data)
	{
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try
		{
			m_value = (Date)formatter.parse(data);
		}
		catch(ParseException pe)
		{
			System.err.println(pe.toString());
		}
	}
	public String Write()
	{
		return DateFormat.getInstance().format(m_value);
	}
	
	public int Size() { return 19; }
	
	public Date getDate() { return m_value; }
	public void setDate(Date val) { m_value = val; }
}
