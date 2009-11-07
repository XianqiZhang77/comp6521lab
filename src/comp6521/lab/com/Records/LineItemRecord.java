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
		AddElement( "l_shipMode",      new StringRecordElement(20) );
		AddElement( "l_comment",       new StringRecordElement(120));
	}

	/*public void insertLineItem(){
		String SEPARATOR = "\t";
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String lineItemRawRecord = Integer.toString(this.l_orderKey)  + SEPARATOR 
				+ Integer.toString(this.l_orderKey) + SEPARATOR
				+ Integer.toString(this.l_partKey) + SEPARATOR
				+ Integer.toString(this.l_suppKey) + SEPARATOR
				+ Integer.toString(this.l_lineNumber) + SEPARATOR
				+ Integer.toString(this.l_quantity) + SEPARATOR
				+ Float.toString(this.l_extendedPrice) + SEPARATOR
				+ Float.toString(this.l_discount) + SEPARATOR
				+ Float.toString(this.l_tax) + SEPARATOR + this.l_returnFlag
				+ SEPARATOR + this.l_lineStatus + SEPARATOR
				+ formatter.format(this.l_shipDate) + SEPARATOR
				+ formatter.format(this.l_commitDate) + SEPARATOR
				+ formatter.format(this.l_receiptDate) + SEPARATOR
				+ this.l_shipInstruct + SEPARATOR + this.l_shipMode + SEPARATOR
				+ this.l_comment;
		Page.addRecord("LineItem.txt", lineItemRawRecord);
		
		
	}
	public String convertLineItemRecordToRawData(){
		String SEPARATOR = "\t";
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String lineItemRawData = Integer.toString(l_orderKey)  + SEPARATOR 
				+ Integer.toString(l_orderKey) + SEPARATOR
				+ Integer.toString(l_partKey) + SEPARATOR
				+ Integer.toString(l_suppKey) + SEPARATOR
				+ Integer.toString(l_lineNumber) + SEPARATOR
				+ Integer.toString(l_quantity) + SEPARATOR
				+ Float.toString(l_extendedPrice) + SEPARATOR
				+ Float.toString(l_discount) + SEPARATOR
				+ Float.toString(l_tax) + SEPARATOR + l_returnFlag
				+ SEPARATOR + l_lineStatus + SEPARATOR
				+ formatter.format(l_shipDate) + SEPARATOR
				+ formatter.format(l_commitDate) + SEPARATOR
				+ formatter.format(l_receiptDate) + SEPARATOR
				+ l_shipInstruct + SEPARATOR + l_shipMode + SEPARATOR
				+ l_comment;
		return lineItemRawData;
	}*/
}
