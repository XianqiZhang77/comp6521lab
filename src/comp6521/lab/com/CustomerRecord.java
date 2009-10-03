package comp6521.lab.com;

public class CustomerRecord extends Record {
	int    c_custkey; // Primary key
	String c_name;
	String c_address;
	int    c_nationKey; // Foreign key in the Nation table
	String c_phone;
	float  c_acctBal;
	String c_mktSegment;
	String c_comment;
}
