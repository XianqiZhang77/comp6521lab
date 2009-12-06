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
		Log.StartLog("z_i.out");
		////////////////////////////////////////////////////////////////////
		// Zeroeth phase:
		// Initialization
		////////////////////////////////////////////////////////////////////
		MemoryManager.getInstance().AddPageType( OrdersSubsetPage.class, "qz_os_i.tmp");
		MemoryManager.getInstance().AddPageType( OrdersGroupsPage.class, "qzg_os_i.tmp");
		
		// Indexes
		BPlusTree< OrdersPage, DateRecordElement > OrderDateIndex = IndexManager.getInstance().getOrderDateIndex();
		// customer key -> record index
		BPlusTree< CustomerPage, IntegerRecordElement > CustomerPKIndex = IndexManager.getInstance().getCustomerPKIndex();
		
		// What we must do:
		// Group orders by month & customer key
		// First: get all orders in the given year.
		String startDate = "01/01/" + year + " 0:00:00";
		String endDate = "12/31/" + year + " 23:59:59";
		
		DateRecordElement startDateElement = new DateRecordElement();
		startDateElement.Parse(startDate);
		DateRecordElement endDateElement = new DateRecordElement();
		endDateElement.Parse(endDate);
		
		Log.StartLogSection("Getting the list of orders between the dates from the OrderDateIndex");
		int[] OrdersList = OrderDateIndex.Get(startDateElement, endDateElement);
		Arrays.sort(OrdersList);
		Log.EndLogSection();
		
		// Write all results to the subset file containing the customer key, the total price & the order date
		Log.StartLogSection("Writing the subset of attributes needed -- custKey, totalPrice, orderDate");
		OrdersSubsetPF OutputSubsetOrdersPF = new OrdersSubsetPF(OrdersList);
		DB.ProcessingLoop(OutputSubsetOrdersPF);
		Log.EndLogSection();
		
		// Sort by o_custKey & by month
		Log.StartLogSection("Sorting by custKey and by month");
		TPMMS<OrdersSubsetPage> sort = new TPMMS<OrdersSubsetPage>(OrdersSubsetPage.class, "qz_os_i.tmp");
		String sortedOS = sort.Execute();
		Log.EndLogSection();
		
		// Group by o_custKey (sum total price) & by month
		Log.StartLogSection("Grouping by custKey and by month");
		FourthPhase( sortedOS, "qzg_os_i.tmp" );
		Log.EndLogSection();
		
		// Translate customer key to customer name and output results
		CustomerPage custPage = null;
		int prevCustPage = -1;
		int custRecordsPerPage = MemoryManager.getInstance().GetNumberOfRecordsPerPage(CustomerPage.class);
		OrdersGroupsPage osgPage = null;
		int osg_p = 0;
		
		IntegerRecordElement key = new IntegerRecordElement();
		String previousName = "";
		int previousCustKey = -1;
		
		Log.StartLogSection("Outputting results and translating the custKey -> c_name");
		// Print header first
		Log.SetResultHeader("customerName\tJAN\tFEB\tMAR\tAPR\tMAY\tJUN\tJUL\tAUG\tSEP\tOCT\tNOV\tDEC");
		int prevMonth = 0;

		String result = "";
		
		while( (osgPage = MemoryManager.getInstance().getPage(OrdersGroupsPage.class, osg_p++, "qzg_os_i.tmp")) != null )
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
						Log.AddResult(result);
					
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
			Log.AddResult(result);
		
		Log.EndLogSection();
		
		Log.EndLog();
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
		page = MemoryManager.getInstance().getEmptyPage(OrdersSubsetPage.class, "qz_os_i.tmp");
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
