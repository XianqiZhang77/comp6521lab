package comp6521.lab.com;
import comp6521.lab.com.Record;
import java.util.Date;

public class LineItemRecord extends Record {
	int    l_orderKey;   // Primary key (with line number)
	int    l_partKey;    // Foreign key in the Part table
	int    l_suppKey;    // Foreign key in the Supplier table
	int    l_lineNumber; // Primary key (with order key)
	int    l_quantity;
	float  l_extendedPrice;
	float  l_discount;
	String l_returnFlag;      // 2 chars
	String l_lineStatus;      // 2 chars
	Date   l_shipDate;
	Date   l_commitDate;
	Date   l_receiptDate;
	String l_shipInstruct;    // 20 chars
	String l_shipMode;        // 20 chars
	String l_comment;         // 120 chars
	
	// Let us assume that the date type is 8 bytes.
	// Total size: 5 ints, 2 floats, 3 dates, 164 chars : 216 bytes
	public static int GetRecordSize() { return 216; }
}
