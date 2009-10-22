package comp6521.lab.com.Records;

public class PartSuppRecord extends Record {
	public int    ps_partKey; // Primary key (with suppkey) and foreign key in the Part table
	public int    ps_suppKey; // Primary key (with partKey) and foreign key in the supplier table
	public int    ps_availQty;
	public float  ps_supplyCost;
	public String ps_comment;    // 120 chars
	
	// Total size: 3 ints, 1 float, 120 chars = 136 bytes
	public static int GetRecordSize() { return 136; }
	public static int GetRecordLength() { return 177; } // 175 + 2
}
