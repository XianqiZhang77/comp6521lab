package comp6521.lab.com.Records;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LineItemRecord extends Record {
	public int    l_orderKey;   // Primary key (with line number)
	public int    l_partKey;    // Foreign key in the Part table
	public int    l_suppKey;    // Foreign key in the Supplier table
	public int    l_lineNumber; // Primary key (with order key)
	public int    l_quantity;
	public float  l_extendedPrice;
	public float  l_discount;
	public float  l_tax;        // not in project, but is in file.... ?
	public String l_returnFlag;      // 2 chars
	public String l_lineStatus;      // 2 chars
	public Date   l_shipDate;
	public Date   l_commitDate;
	public Date   l_receiptDate;
	public String l_shipInstruct;    // 20 chars
	public String l_shipMode;        // 20 chars
	public String l_comment;         // 120 chars
	
	// Let us assume that the date type is 8 bytes.
	// Total size: 5 ints, 3 floats, 3 dates, 164 chars : 216 bytes
	public static int GetRecordSize() { return 220; }
	public static int GetRecordLength() { return 334; } // 310 + 2
	
	public void Parse(String data)
	{
		l_orderKey      = Integer.parseInt(data.substring(0, 10).trim());
		l_partKey       = Integer.parseInt(data.substring(11, 21).trim());
		l_suppKey       = Integer.parseInt(data.substring(22, 32).trim());
		l_lineNumber    = Integer.parseInt(data.substring(33, 43).trim());
		l_quantity      = Integer.parseInt(data.substring(44, 54).trim());
		l_extendedPrice = Float.parseFloat(data.substring(55, 76).trim());
		l_discount      = Float.parseFloat(data.substring(77, 98).trim());
		l_tax           = Float.parseFloat(data.substring(99,120).trim());
		l_returnFlag    =                  data.substring(121, 122).trim();
		l_lineStatus    =                  data.substring(123, 124).trim();
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");	// date formatter
		try
		{
			l_shipDate      = (Date)formatter.parse(data.substring(125, 143).trim());
			l_commitDate    = (Date)formatter.parse(data.substring(144, 162).trim());
			l_receiptDate   = (Date)formatter.parse(data.substring(163, 181).trim());
		} 
		catch(ParseException pe) 
		{
			System.err.println(pe.toString()); // send to error stream
		}
		
		l_shipInstruct  = data.substring(182, 201).trim();
		l_shipMode      = data.substring(202, 211).trim();
		l_comment       = data.substring(212, 331).trim();
	}
}
