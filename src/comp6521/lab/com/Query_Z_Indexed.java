package comp6521.lab.com;

import java.util.Arrays;

import comp6521.lab.com.Pages.CustomerPage;
import comp6521.lab.com.Pages.OrdersPage;
import comp6521.lab.com.Records.DateRecordElement;
import comp6521.lab.com.Records.IntegerRecordElement;
import comp6521.lab.com.Records.Record;
import comp6521.lab.com.Util.DB;
import comp6521.lab.com.Util.ProcessingFunction;

public class Query_Z_Indexed extends Query_Z 
{
	public void ProcessQuery(int year)
	{
		////////////////////////////////////////////////////////////////////
		// Zeroeth phase:
		// Initialization
		////////////////////////////////////////////////////////////////////
		MemoryManager.getInstance().AddPageType( OrdersSubsetPage.class, "qz_os_i.txt");
		MemoryManager.getInstance().AddPageType( OrdersGroupsPage.class, "qzg_os_i.txt");
		
		// Indexes
		BPlusTree< OrdersPage, DateRecordElement > OrderDateIndex = new BPlusTree< OrdersPage, DateRecordElement >();
		OrderDateIndex.CreateBPlusTree(OrdersPage.class, DateRecordElement.class, "orders.txt", "orders_date_tree.txt", "o_orderDate");
		// customer key -> record index
		BPlusTree< CustomerPage, IntegerRecordElement > CustomerPKIndex = new BPlusTree< CustomerPage, IntegerRecordElement >();
		CustomerPKIndex.CreateBPlusTree(CustomerPage.class, IntegerRecordElement.class, "customer.txt", "customer_pk_tree.txt", "c_custKey");
		
		
		// What we must do:
		// Group orders by month & customer key
		// First: get all orders in the given year.
		String startDate = "01/01/" + year + " 0:00:00";
		String endDate = "12/31/" + year + " 23:59:59";
		
		DateRecordElement startDateElement = new DateRecordElement();
		startDateElement.Parse(startDate);
		DateRecordElement endDateElement = new DateRecordElement();
		endDateElement.Parse(endDate);
		
		int[] OrdersList = OrderDateIndex.Get(startDateElement, endDateElement);
		Arrays.sort(OrdersList);
		
		// Write all results to the subset file containing the customer key, the total price & the order date
		OrdersSubsetPF OutputSubsetOrdersPF = new OrdersSubsetPF(OrdersList);
		DB.ProcessingLoop(OutputSubsetOrdersPF);
		
		// Sort by o_custKey & by month
		TPMMS<OrdersSubsetPage> sort = new TPMMS<OrdersSubsetPage>(OrdersSubsetPage.class, "qz_os_i.txt");
		String sortedOS = sort.Execute();
		
		// Group by o_custKey (sum total price) & by month
		FourthPhase( sortedOS, "qzg_os_i.txt" );
		
		// Translate customer key to customer name and output results
		CustomerPage custPage = null;
		int prevCustPage = -1;
		int custRecordsPerPage = MemoryManager.getInstance().GetNumberOfRecordsPerPage(CustomerPage.class);
		OrdersGroupsPage osgPage = null;
		int osg_p = 0;
		
		IntegerRecordElement key = new IntegerRecordElement();
		String previousName = "";
		int previousCustKey = -1;
		
		// Print header first
		System.out.println("customerName\tJAN\tFEB\tMAR\tAPR\tMAY\tJUN\tJUL\tAUG\tSEP\tOCT\tNOV\tDEC");
		int prevMonth = 0;

		String result = "";
		
		while( (osgPage = MemoryManager.getInstance().getPage(OrdersGroupsPage.class, osg_p++, "qzg_os_i.txt")) != null )
		{
			OrdersSubsetRecord[] osgRecords = osgPage.m_records;
			
			for( int i = 0; i < osgRecords.length; i++ )
			{
				int curCustKey = osgRecords[i].get("o_custKey").getInt();
				
				// Fetch the name only if it's different
				if( curCustKey != previousCustKey )
				{
					// Output previous result
					if( result.length() > 0 )
						System.out.println(result);
					
					result = "";	
					prevMonth = 0;

					previousCustKey = curCustKey;
					
					key.setInt(curCustKey);
					int[] customer = CustomerPKIndex.Get(key);
					
					if( customer.length != 1 )
						System.out.println("PK insanity");
					
					int curCustPageNb = customer[0] / custRecordsPerPage;
					int curCustRec    = customer[0] % custRecordsPerPage;
					
					// Load another page only if it's needed
					if( curCustPageNb != prevCustPage )
					{
						prevCustPage = curCustPageNb;
						if( custPage != null )
							MemoryManager.getInstance().freePage(custPage);
						
						custPage = MemoryManager.getInstance().getPage(CustomerPage.class, curCustPageNb);
					}
					
					previousName = new String(custPage.m_records[curCustRec].get("c_name").getString());
					
					result += previousName;
					prevMonth = 0;
				}
				
				// Count the number of tabs to add
				int monthDiff = osgRecords[i].get("o_orderDate").getDate().getMonth() - prevMonth;
				prevMonth += monthDiff;
				
				for(int m = 0; m < monthDiff; m++)
					result += "\t";
				
				// Add result
				result += osgRecords[i].get("o_totalPrice").getFloat();
			}			
			
			MemoryManager.getInstance().freePage(osgPage);
		}
		
		if( custPage != null )
			MemoryManager.getInstance().freePage(custPage);
		
		// Output last result if needed
		if( result.length() > 0 )
			System.out.println(result);
		
	}
}

class OrdersSubsetPF extends ProcessingFunction<OrdersPage, DateRecordElement>
{
	OrdersSubsetPage page;
	
	public OrdersSubsetPF( int[] input )
	{
		super( input, OrdersPage.class );
	}
	
	public void ProcessStart()
	{
		page = MemoryManager.getInstance().getEmptyPage(OrdersSubsetPage.class, "qz_os_i.txt");
	}
	
	public void Process( Record r )
	{
		OrdersSubsetRecord os = new OrdersSubsetRecord();
		os.get("o_custKey").set( r.get("o_custKey") );
		os.get("o_totalPrice").set( r.get("o_totalPrice") );
		os.get("o_orderDate").set( r.get("o_orderDate") );
		page.AddRecord(os);
	}
	
	public int[] EndProcess()
	{
		MemoryManager.getInstance().freePage(page);
		return null;
	}
}
