package comp6521.lab.com.Records;

public class PartRecord extends Record {
	PartRecord()
	{
		AddElement( "p_partKey", new IntegerRecordElement() ); // PK
		AddElement( "p_name",    new StringRecordElement(60));
		AddElement( "p_mfgr",    new StringRecordElement(15));
		AddElement( "p_brand",   new StringRecordElement(10));
		AddElement( "p_type",    new StringRecordElement(30));
		AddElement( "p_size",    new IntegerRecordElement() );
		AddElement( "p_container", new StringRecordElement(15));
		AddElement( "p_retailPrice", new FloatRecordElement() );
		AddElement( "p_comment", new StringRecordElement(120));
	}
}
