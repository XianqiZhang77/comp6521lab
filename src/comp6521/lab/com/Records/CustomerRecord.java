package comp6521.lab.com.Records;

public class CustomerRecord extends Record {
	public CustomerRecord()
	{
		AddElement( "c_custKey",    new IntegerRecordElement()  ); // PK
		AddElement( "c_name",       new StringRecordElement(25) );
		AddElement( "c_address",    new StringRecordElement(50) );
		AddElement( "c_nationKey",  new IntegerRecordElement()  ); // FK(nation)
		AddElement( "c_phone",      new StringRecordElement(20) );
		AddElement( "c_acctBal",    new FloatRecordElement()    );
		AddElement( "c_mktSegment", new StringRecordElement(15) );
		AddElement( "c_comment",    new StringRecordElement(120));
	}
}
