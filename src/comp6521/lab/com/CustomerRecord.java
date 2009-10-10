package comp6521.lab.com;
import comp6521.lab.com.Record;

public class CustomerRecord extends Record {
	int    c_custkey;   // Primary key
	String c_name;           // 25 chars
	String c_address;        // 50 chars
	int    c_nationKey; // Foreign key in the Nation table
	String c_phone;          // 20 chars
	float  c_acctBal;
	String c_mktSegment;     // 15 chars
	String c_comment;        // 120 chars
	
	// Total size : 2 ints, 1 float, 230 chars = 254 bytes	
	public static int GetRecordSize() { return 254; }
}
