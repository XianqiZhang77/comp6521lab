package comp6521.lab.com;

public class SupplierRecord extends Record {
	int    s_suppKey; // Primary key
	String s_name;
	String s_address;
	int    s_nationKey; // Foreign key in Nation table
	String s_phone;
	float  s_acctBal;
	String s_comment;
}
