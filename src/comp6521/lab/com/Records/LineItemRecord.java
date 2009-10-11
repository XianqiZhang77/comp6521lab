package comp6521.lab.com.Records;
import java.util.Date;

public class LineItemRecord extends Record {
	public int    l_orderKey;   // Primary key (with line number)
	public int    l_partKey;    // Foreign key in the Part table
	public int    l_suppKey;    // Foreign key in the Supplier table
	public int    l_lineNumber; // Primary key (with order key)
	public int    l_quantity;
	public float  l_extendedPrice;
	public float  l_discount;
	public String l_returnFlag;      // 2 chars
	public String l_lineStatus;      // 2 chars
	public Date   l_shipDate;
	public Date   l_commitDate;
	public Date   l_receiptDate;
	public String l_shipInstruct;    // 20 chars
	public String l_shipMode;        // 20 chars
	public String l_comment;         // 120 chars
	
	// Let us assume that the date type is 8 bytes.
	// Total size: 5 ints, 2 floats, 3 dates, 164 chars : 216 bytes
	public static int GetRecordSize() { return 216; }
}
