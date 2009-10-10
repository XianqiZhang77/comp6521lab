package comp6521.lab.com;
import comp6521.lab.com.Record;

public class SupplierRecord extends Record {
	int    s_suppKey;   // Primary key
	String s_name;          // 25 chars
	String s_address;       // 50 chars
	int    s_nationKey; // Foreign key in Nation table
	String s_phone;         // 30 chars (?? not same as in customer ??)
	float  s_acctBal;
	String s_comment;       // 120 chars
	
	// Total size: 2 ints + 1 float + 225 chars
	public static int GetRecordSize() { return 237; }
}
