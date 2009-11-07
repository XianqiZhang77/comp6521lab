package comp6521.lab.com.Records;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import comp6521.lab.com.Pages.Page;

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
	//public static int GetRecordSize() { return 220; }
	//public static int GetRecordLength() { return 334; } // 310 + 2
	public static int GetRecordSize() { return 334; }
	
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
	
	public String Write()
	{
		String data = "";
		return data;
	}
	public void insertLineItem(){
		String SEPARATOR = "\t";
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String lineItemRawRecord = Integer.toString(this.l_orderKey)  + SEPARATOR 
				+ Integer.toString(this.l_orderKey) + SEPARATOR
				+ Integer.toString(this.l_partKey) + SEPARATOR
				+ Integer.toString(this.l_suppKey) + SEPARATOR
				+ Integer.toString(this.l_lineNumber) + SEPARATOR
				+ Integer.toString(this.l_quantity) + SEPARATOR
				+ Float.toString(this.l_extendedPrice) + SEPARATOR
				+ Float.toString(this.l_discount) + SEPARATOR
				+ Float.toString(this.l_tax) + SEPARATOR + this.l_returnFlag
				+ SEPARATOR + this.l_lineStatus + SEPARATOR
				+ formatter.format(this.l_shipDate) + SEPARATOR
				+ formatter.format(this.l_commitDate) + SEPARATOR
				+ formatter.format(this.l_receiptDate) + SEPARATOR
				+ this.l_shipInstruct + SEPARATOR + this.l_shipMode + SEPARATOR
				+ this.l_comment;
		Page.addRecord("LineItem.txt", lineItemRawRecord);
		
		
	}
	public String convertLineItemRecordToRawData(){
		String SEPARATOR = "\t";
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String lineItemRawData = Integer.toString(l_orderKey)  + SEPARATOR 
				+ Integer.toString(l_orderKey) + SEPARATOR
				+ Integer.toString(l_partKey) + SEPARATOR
				+ Integer.toString(l_suppKey) + SEPARATOR
				+ Integer.toString(l_lineNumber) + SEPARATOR
				+ Integer.toString(l_quantity) + SEPARATOR
				+ Float.toString(l_extendedPrice) + SEPARATOR
				+ Float.toString(l_discount) + SEPARATOR
				+ Float.toString(l_tax) + SEPARATOR + l_returnFlag
				+ SEPARATOR + l_lineStatus + SEPARATOR
				+ formatter.format(l_shipDate) + SEPARATOR
				+ formatter.format(l_commitDate) + SEPARATOR
				+ formatter.format(l_receiptDate) + SEPARATOR
				+ l_shipInstruct + SEPARATOR + l_shipMode + SEPARATOR
				+ l_comment;
		return lineItemRawData;
	}
}
