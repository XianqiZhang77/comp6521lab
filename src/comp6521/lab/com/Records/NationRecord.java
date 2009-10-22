package comp6521.lab.com.Records;

public class NationRecord extends Record {
	public int    n_nationKey; // Primary key
	public String n_name;          // 15 chars
	public int    n_regionKey; // Foreign key in the Region table.
	public String n_comment;       // 120 chars
	
	// Total size : 2 ints + 135 chars = 143 bytes
	public static int GetRecordSize() { return 143; }
	public static int GetRecordLength() { return 198; } // 196 + 2
}
