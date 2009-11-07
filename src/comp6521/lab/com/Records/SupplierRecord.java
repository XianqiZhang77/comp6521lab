package comp6521.lab.com.Records;

public class SupplierRecord extends Record {
	SupplierRecord()
	{
		AddElement( "s_suppKey",   new IntegerRecordElement()   );
		AddElement( "s_name",      new StringRecordElement(25)  );
		AddElement( "s_address",   new StringRecordElement(50)  );
		AddElement( "s_nationKey", new IntegerRecordElement()   );
		AddElement( "s_phone",     new StringRecordElement(30)  );
		AddElement( "s_acctBal",   new FloatRecordElement()     );
		AddElement( "s_comment",   new StringRecordElement(120) );
	}
}
