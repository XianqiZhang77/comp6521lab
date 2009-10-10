package comp6521.lab.com;
import comp6521.lab.com.Record;

public class PartRecord extends Record {
	int    p_partKey; // Primary key
	String p_name;          // 60 chars
	String p_mfgr;          // 15 chars
	String p_brand;         // 10 chars
	String p_type;          // 30 chars
	int    p_size;
	String p_container;     // 15 chars
	float  p_retailPrice;
	String p_comment;       // 120 chars
	
	// Total size: 2 ints, 1 float, 250 chars = 262 bytes 
	public static int GetRecordSize() { return 262; }
}
