package comp6521.lab.com.Records;

public class NationRecord extends Record implements Comparable<Record>
{
	public NationRecord()
	{
		AddElement( "n_nationKey", new IntegerRecordElement()  ); // PK
		AddElement( "n_name",      new StringRecordElement(15) );
		AddElement( "n_regionKey", new IntegerRecordElement()  ); // FK (region)
		AddElement( "n_comment",   new StringRecordElement(120));
	}
	
	public int compareTo(Record r)
	{
		// TODO: insert compare code
		return 1;
	}
}
