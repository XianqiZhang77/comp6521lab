package comp6521.lab.com.Records;

public class RegionRecord extends Record implements Comparable<Record>
{
	public RegionRecord()
	{
		AddElement( "r_regionKey", new IntegerRecordElement()  ); // PK
		AddElement( "r_name",      new StringRecordElement(50) );
		AddElement( "r_comment",   new StringRecordElement(120));
	}
	
	public int compareTo(Record r)
	{
		// TODO: insert compare code
		return 1;
	}
}
