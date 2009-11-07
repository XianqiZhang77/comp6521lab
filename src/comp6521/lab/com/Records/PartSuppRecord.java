package comp6521.lab.com.Records;

public class PartSuppRecord extends Record {
	PartSuppRecord()
	{
		AddElement( "ps_partKey",    new IntegerRecordElement()  ); // PK , FK part
		AddElement( "ps_suppKey",    new IntegerRecordElement()  ); // PK , FK supplier
		AddElement( "ps_availQty",   new IntegerRecordElement()  );
		AddElement( "ps_supplyCost", new FloatRecordElement()    );
		AddElement( "ps_comment",    new StringRecordElement(120));
	}
}
