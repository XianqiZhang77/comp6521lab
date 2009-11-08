package comp6521.lab.com.Records;

public class OrdersRecord extends Record {
	public OrdersRecord()
	{
		AddElement( "o_orderKey",      new IntegerRecordElement()   ); // PK
		AddElement( "o_custKey",       new IntegerRecordElement()   ); // FK customer
		AddElement( "o_orderStatus",   new StringRecordElement(2)   );
		AddElement( "o_totalPrice",    new FloatRecordElement()     );
		AddElement( "o_orderDate",     new DateRecordElement()      );
		AddElement( "o_orderPriority", new StringRecordElement(20)  );
		AddElement( "o_clerk",         new StringRecordElement(20)  );
		AddElement( "o_shipPriority",  new IntegerRecordElement()   );
		AddElement( "o_comment",       new StringRecordElement(120) );
	}
}
