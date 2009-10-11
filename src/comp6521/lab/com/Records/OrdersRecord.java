package comp6521.lab.com.Records;
import java.util.Date;

public class OrdersRecord extends Record {
	public int    o_orderKey; // Primary key
	public int    l_custKey;  // Foreign key in the Customer table
	public String o_orderStatus;   // 2 chars
	public float  o_totalPrice;
	public Date   o_orderDate;
	public String o_orderPriority; // 20 chars
	public String o_clerk;         // 20 chars
	public int    o_shipPriority;
	public String o_comment;       // 120 chars
	
	// Total size: 3 ints, 1 float, 1 date (8B), 162 chars : 186 bytes
	public static int GetRecordSize() { return 186; }
}
