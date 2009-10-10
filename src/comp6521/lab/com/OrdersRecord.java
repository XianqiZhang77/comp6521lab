package comp6521.lab.com;
import comp6521.lab.com.Record;
import java.util.Date;

public class OrdersRecord extends Record {
	int    o_orderKey; // Primary key
	int    l_custKey;  // Foreign key in the Customer table
	String o_orderStatus;   // 2 chars
	float  o_totalPrice;
	Date   o_orderDate;
	String o_orderPriority; // 20 chars
	String o_clerk;         // 20 chars
	int    o_shipPriority;
	String o_comment;       // 120 chars
	
	// Total size: 3 ints, 1 float, 1 date (8B), 162 chars : 186 bytes
	public static int GetRecordSize() { return 186; }
}
