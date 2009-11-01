package comp6521.lab.com.Records;

public class CustomerRecord extends Record {
	public int    c_custkey;   // Primary key
	public String c_name;           // 25 chars
	public String c_address;        // 50 chars
	public int    c_nationKey; // Foreign key in the Nation table
	public String c_phone;          // 20 chars
	public float  c_acctBal;
	public String c_mktSegment;     // 15 chars
	public String c_comment;        // 120 chars
	
	// Total size : 2 ints, 1 float, 230 chars = 254 bytes	
	//public static int GetRecordSize() { return 254; }
	//public static int GetRecordLength() { return 276; } // 274 + 2
	public static int GetRecordSize() { return 276; }
	
	public void Parse(String data)
	{
		c_custkey    = Integer.parseInt( data.substring(0,    10).trim());
		c_name       =                   data.substring(11,   35).trim() ;
		c_address    =                   data.substring(36,   85).trim() ;
		c_nationKey  = Integer.parseInt( data.substring(86,   96).trim());
		c_phone      =                   data.substring(97,  116).trim() ;
		c_acctBal    = Float.parseFloat( data.substring(117, 138).trim());
		c_mktSegment =                   data.substring(139, 153).trim() ;
		c_comment    =                   data.substring(154, 273).trim() ;
	}
	
	public String Write()
	{
		String data = "";
		return data;
	}
}
