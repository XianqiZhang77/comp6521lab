package comp6521.lab.com;

public class NationRecord extends Record {
	int    n_nationKey; // Primary key
	String n_name;
	int    n_regionKey; // Foreign key in the Region table.
	String n_comment;
}
