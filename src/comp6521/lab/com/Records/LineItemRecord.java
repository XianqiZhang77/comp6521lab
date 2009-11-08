package comp6521.lab.com.Records;

public class LineItemRecord extends Record {
	public LineItemRecord()
	{
		AddElement( "l_orderkey",      new IntegerRecordElement()  ); // PK, FK(orders)
		AddElement( "l_partKey",       new IntegerRecordElement()  ); // FK (part)
		AddElement( "l_suppKey",       new IntegerRecordElement()  ); // FK (supp)
		AddElement( "l_lineNumber",    new IntegerRecordElement()  ); // PK
		AddElement( "l_quantity",      new IntegerRecordElement()  );
		AddElement( "l_extendedPrice", new FloatRecordElement()    );
		AddElement( "l_discount",      new FloatRecordElement()    );
		AddElement( "l_tax",           new FloatRecordElement()    );
		AddElement( "l_returnFlag",    new StringRecordElement(2)  );
		AddElement( "l_lineStatus",    new StringRecordElement(2)  );
		AddElement( "l_shipDate",      new DateRecordElement()     );
		AddElement( "l_commitDate",    new DateRecordElement()     );
		AddElement( "l_receiptDate",   new DateRecordElement()     );
		AddElement( "l_shipInstruct",  new StringRecordElement(20) );
		AddElement( "l_shipMode",      new StringRecordElement(10) );
		AddElement( "l_comment",       new StringRecordElement(120));
	}
}
