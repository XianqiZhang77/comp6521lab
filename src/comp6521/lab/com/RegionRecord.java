package comp6521.lab.com;
import comp6521.lab.com.Record;

public class RegionRecord extends Record {
	int    r_regionKey; // Primary key
	String r_name;           // 50 chars
	String r_comment;        // 120 chars
	
	// Total size: 1 int + 170 chars = 174 bytes
	public static int GetRecordSize() { return 174; }
}
