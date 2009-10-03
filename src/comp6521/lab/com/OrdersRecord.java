package comp6521.lab.com;

import java.util.Date;

public class OrdersRecord extends Record {
	int    o_orderKey; // Primary key
	int    l_custKey; // Foreign key in the Customer table
	String o_orderStatus;
	float  o_totalPrice;
	Date   o_orderDate;
	String o_orderPriority;
	String o_clerk;
	int    o_shipPriority;
	String o_comment;
}
