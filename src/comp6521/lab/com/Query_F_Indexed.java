package comp6521.lab.com;

import comp6521.lab.com.Pages.LineItemPage;
import comp6521.lab.com.Pages.OrdersPage;
import comp6521.lab.com.Pages.PartPage;
import comp6521.lab.com.Pages.SupplierPage;
import comp6521.lab.com.Records.IntegerRecordElement;
import comp6521.lab.com.Records.LineItemRecord;

public class Query_F_Indexed extends Query_F 
{
	public String getLogFilename() { return "f_i.out"; }
	
	public boolean CheckPrimaryKey( long _orderKey, long _lineNumber )
	{
		int orderKey = (int)_orderKey;
		int lineNumber = (int)_lineNumber;
		
		boolean unique = true;
		LinearHashTable<LineItemPage> LineItemOrderIndex = IndexManager.getInstance().getLineItemOrderIndex();
		
		IntegerRecordElement keyEl = new IntegerRecordElement();
		keyEl.setInt(orderKey);
		int[] pageList = LineItemOrderIndex.getPageList(keyEl);
		
		for(int p = 0; p < pageList.length && unique; p++)
		{
			LineItemPage page = MemoryManager.getInstance().getPage( LineItemPage.class, pageList[p], LineItemOrderIndex.m_filename );
			LineItemRecord[] lineItems = page.m_records;
			
			for(int r = 0; r < lineItems.length && unique; r++ )
			{
				if( lineItems[r].get("l_orderKey").getInt() == orderKey &&
					lineItems[r].get("l_lineNumber").getInt() == lineNumber )
				{
					unique = false;
				}
			}
			
			MemoryManager.getInstance().freePage(page);
		}
		
		return unique;
	}
	
	public boolean CheckPartKey( long _partKey )
	{
		int partKey = (int)_partKey;
	
		BPlusTree<PartPage, IntegerRecordElement> PartPKIndex = IndexManager.getInstance().getPartPKIndex();
		
		IntegerRecordElement keyEl = new IntegerRecordElement();
		keyEl.setInt(partKey);
		int[] parts = PartPKIndex.Get(keyEl);
			
		return (parts.length > 0);
	}
	
	public boolean CheckOrdersKey( long _orderKey )
	{
		int orderKey = (int)_orderKey;
		
		BPlusTree<OrdersPage, IntegerRecordElement> OrderPKIndex = IndexManager.getInstance().getOrderPKIndex();
		
		IntegerRecordElement keyEl = new IntegerRecordElement();
		keyEl.setInt(orderKey);
		int[] orders = OrderPKIndex.Get(keyEl);
		
		return (orders.length > 0);		
	}
	
	public boolean CheckSupplierKey( long _suppKey )
	{
		int suppKey = (int)_suppKey;
		
		BPlusTree<SupplierPage, IntegerRecordElement> SupplierPKIndex = IndexManager.getInstance().getSupplierPKIndex();
		
		IntegerRecordElement keyEl = new IntegerRecordElement();
		keyEl.setInt(suppKey);
		int[] suppliers = SupplierPKIndex.Get(keyEl);
		
		return (suppliers.length > 0);
	}
}
