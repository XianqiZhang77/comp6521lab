package comp6521.lab.com;
import comp6521.lab.com.Record;

public class PartSuppRecord extends Record {
	int    ps_partKey; // Primary key (with suppkey) and foreign key in the Part table
	int    ps_suppKey; // Primary key (with partKey) and foreign key in the supplier table
	int    ps_availQty;
	float  ps_supplyCost;
	String ps_comment;    // 120 chars
	
	// Total size: 3 ints, 1 float, 120 chars = 136 bytes
	public static int GetRecordSize() { return 136; }
}
