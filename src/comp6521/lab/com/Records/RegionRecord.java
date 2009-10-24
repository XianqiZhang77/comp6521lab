package comp6521.lab.com.Records;

public class RegionRecord extends Record {
	public int    r_regionKey; // Primary key
	public String r_name;           // 50 chars
	public String r_comment;        // 120 chars
	
	// Total size: 1 int + 170 chars = 174 bytes
	public static int GetRecordSize() { return 174; }
	public static int GetRecordLength() { return 183; } // 181 + 2
	
	public void Parse(String data) {}
}
