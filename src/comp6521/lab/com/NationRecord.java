package comp6521.lab.com;
import comp6521.lab.com.Record;

public class NationRecord extends Record {
	int    n_nationKey; // Primary key
	String n_name;          // 15 chars
	int    n_regionKey; // Foreign key in the Region table.
	String n_comment;       // 120 chars
	
	// Total size : 2 ints + 135 chars = 143 bytes
	public static int GetRecordSize() { return 143; }
}
