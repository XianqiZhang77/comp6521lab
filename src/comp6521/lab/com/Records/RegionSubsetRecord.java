/**
 * November 08, 2009
 * RegionSubsetRecord.java: record to hold intermediate region tuple in query execution.
 */
package comp6521.lab.com.Records;

/**
 * Dimitri Tiago
 */
public class RegionSubsetRecord extends Record implements Comparable<Record>
{
	public RegionSubsetRecord()
	{
		AddElement( "r_regionKey", new IntegerRecordElement() );
	}
	
	public int compareTo(Record r)
	{
		// TODO: insert compare code
		return 1;
	}
}
