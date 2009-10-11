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
	public static int GetRecordSize() { return 237; }
}
