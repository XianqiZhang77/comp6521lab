package comp6521.lab.com.Records;

public class PartRecord extends Record {
	public int    p_partKey; // Primary key
	public String p_name;          // 60 chars
	public String p_mfgr;          // 15 chars
	public String p_brand;         // 10 chars
	public String p_type;          // 30 chars
	public int    p_size;
	public String p_container;     // 15 chars
	public float  p_retailPrice;
	public String p_comment;       // 120 chars
	
	// Total size: 2 ints, 1 float, 250 chars = 262 bytes 
	public static int GetRecordSize() { return 262; }
}
