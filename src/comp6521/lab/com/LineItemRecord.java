package comp6521.lab.com;

import java.util.Date;

public class LineItemRecord extends Record {
	int    l_orderKey; // Primary key (with line number)
	int    l_partKey; // Foreign key in the Part table
	int    l_suppKey; // Foreign key in the Supplier table
	int    l_lineNumber; // Primary key (with order key)
	int    l_quantity;
	float  l_extendedPrice;
	float  l_discount;
	String l_returnFlag;
	String l_lineStatus;
	Date   l_shipDate;
	Date   l_commitDate;
	Date   l_receiptDate;
	String l_shipInstruct;
	String l_shipMode;
	String l_comment;
}
