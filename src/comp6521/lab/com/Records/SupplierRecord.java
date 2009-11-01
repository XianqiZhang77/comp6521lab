package comp6521.lab.com.Records;

public class SupplierRecord extends Record {
	public int    s_suppKey;   // Primary key
	public String s_name;          // 25 chars
	public String s_address;       // 50 chars
	public int    s_nationKey; // Foreign key in Nation table
	public String s_phone;         // 30 chars (?? not same as in customer ??)
	public float  s_acctBal;
	public String s_comment;       // 120 chars
	
	// Total size: 2 ints + 1 float + 225 chars
	//public static int GetRecordSize() { return 237; }
	//public static int GetRecordLength() { return 271; } // 269 + 2
	public static int GetRecordSize() { return 271; }
	
	public void Parse(String data)
	{
		s_suppKey   = Integer.parseInt(data.substring(0, 10).trim());
		s_name      =                  data.substring(11, 35).trim();
		s_address   =                  data.substring(36, 85).trim();
		s_nationKey = Integer.parseInt(data.substring(86, 96).trim());
		s_phone     =                  data.substring(97, 126).trim();
		s_acctBal   = Float.parseFloat(data.substring(127, 148).trim());
		s_comment   =                  data.substring(149, 268).trim();
	}
	
	public String Write()
	{
		String data = "";
		return data;
	}
}
