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
	public static int GetRecordSize() { return 254; }
}
