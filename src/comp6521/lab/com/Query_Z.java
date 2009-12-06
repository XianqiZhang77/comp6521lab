package comp6521.lab.com;

import comp6521.lab.com.Pages.CustomerPage;
import comp6521.lab.com.Pages.OrdersPage;
import comp6521.lab.com.Pages.Page;
import comp6521.lab.com.Records.CustomerRecord;
import comp6521.lab.com.Records.DateRecordElement;
import comp6521.lab.com.Records.FloatRecordElement;
import comp6521.lab.com.Records.IntegerRecordElement;
import comp6521.lab.com.Records.OrdersRecord;
import comp6521.lab.com.Records.Record;

public class Query_Z {
	public void ProcessQuery(int year)
	{
		/////////////////////////////////////////////////////////////////////
		// The expected result:
		// 
		// customerName    JAN FEB MAR APR MAY JUN JUL AUG SEP OCT NOV DEC
		// Adrian Onet     3.2             1.2             3.4
		//
		// I.e. :
		// ROWS : customer names.
		// COLUMNS: months
		// CELL VALUES: total order value for that month, by that client
		//
		// NOTES:
		// 1) This query MUST BE TESTED IN ACCESS.
		// 2) It is possible that the "empty" columns shouldn't be shown.
		// 3) We assume that the query means this:
		//       Find the total order price per month in a given year,
		//       for the one client that has the minimum month in these clients.
		////////////////////////////////////////////////////////////////////
		// Zeroeth phase:
		// Initialization
		////////////////////////////////////////////////////////////////////
		MemoryManager.getInstance().AddPageType( OrdersSubsetPage.class, "qz_os.txt");
		MemoryManager.getInstance().AddPageType( OrdersGroupsPage.class, "qzg_os.txt");
		////////////////////////////////////////////////////////////////////
		// First phase:
		// Select orders subset that satisfy the year condition and
		// Generate the (o_custKey, o_totalPrice, month(o_orderDate)) tuples
		////////////////////////////////////////////////////////////////////
		OrdersSubsetPage osPage = MemoryManager.getInstance().getEmptyPage( OrdersSubsetPage.class );
		
		OrdersPage orderPage = null;
		int o_p = 0;
		while( (orderPage = MemoryManager.getInstance().getPage(OrdersPage.class, o_p++)) != null )
		{
			OrdersRecord[] orders = orderPage.m_records;
			for(int i = 0; i < orders.length; i++)
			{
				if( orders[i].get("o_orderDate").getDate().getYear() + 1900 == year )
				{
					// We'll keep that order
					OrdersSubsetRecord os = new OrdersSubsetRecord();
					os.get("o_custKey").set( orders[i].get("o_custKey") );
					os.get("o_totalPrice").set( orders[i].get("o_totalPrice") );
					os.get("o_orderDate").set( orders[i].get("o_orderDate") );
					osPage.AddRecord( os );
				}
			}
			
			MemoryManager.getInstance().freePage(orderPage);
		}
		// Write back the data
		MemoryManager.getInstance().freePage(osPage);
		osPage = null;
        ////////////////////////////////////////////////////////////////////
		// Third phase:
		// Sort by o_custKey & month
		////////////////////////////////////////////////////////////////////
		TPMMS<OrdersSubsetPage> sort = new TPMMS<OrdersSubsetPage>(OrdersSubsetPage.class, "qz_os.txt");
		String sortedOS = sort.Execute();
		
		////////////////////////////////////////////////////////////////////
		// Fourth phase:
		// Group by o_custKey (sum total price) & month
		////////////////////////////////////////////////////////////////////
		FourthPhase(sortedOS, "qzg_os.txt");
		
		////////////////////////////////////////////////////////////////////
		// Fifth phase:
		// Find the name (matching o_cust with c_cust, getting c_name)
		////////////////////////////////////////////////////////////////////
		OrdersGroupsPage osgPage = null;
		int osg_p = 0;
		
		String previousName = "";
		int previousCustKey = -1;
		
		// Print header first
		System.out.println("customerName\tJAN\tFEB\tMAR\tAPR\tMAY\tJUN\tJUL\tAUG\tSEP\tOCT\tNOV\tDEC");
		int prevMonth = 0;

		String result = "";
		
		while( (osgPage = MemoryManager.getInstance().getPage(OrdersGroupsPage.class, osg_p++, "qzg_os.txt")) != null )
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
					
					// Find appropriate page
					CustomerPage custPage = null;
					int custPageNb = 0;
					boolean found = false;
					while( !found && (custPage = MemoryManager.getInstance().getPage(CustomerPage.class, custPageNb++)) != null )
					{
						for( int c = 0; c < custPage.m_records.length; c++ )
						{
							if( custPage.m_records[c].get("c_custKey").getInt() == curCustKey )
							{
								found = true;
								previousName = new String(custPage.m_records[c].get("c_name").getString());
							}
						}
						MemoryManager.getInstance().freePage(custPage);
					}
					
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
		
		// Output last result if needed
		if( result.length() > 0 )
			System.out.println(result);
	}
	
	public void FourthPhase( String subsetFilename, String groupsFilename )
	{
		// Assume orders subset table is sorted
		OrdersSubsetPage osPage = null;
		OrdersGroupsPage osgPage = MemoryManager.getInstance().getEmptyPage( OrdersGroupsPage.class, groupsFilename );
		
		int previousKey = -1;
		int previousMonth = -1;
		OrdersSubsetRecord group = null;
		
		int os_p = 0;
		while( (osPage = MemoryManager.getInstance().getPage( OrdersSubsetPage.class, os_p++, subsetFilename)) != null)
		{
			OrdersSubsetRecord[] osRecords = osPage.m_records;
			for( int i = 0; i < osRecords.length; i++ )
			{
				if( osRecords[i].get("o_custKey").getInt() == previousKey &&
					osRecords[i].get("o_orderDate").getDate().getMonth() == previousMonth )
				{
					assert(group != null);
					// Update total price of the group
					group.get("o_totalPrice").setFloat( group.get("o_totalPrice").getFloat() + osRecords[i].get("o_totalPrice").getFloat() );
				}
				else
				{
					// Write group
					if( group != null )
						osgPage.AddRecord( group );
					
					group = new OrdersSubsetRecord();
					group.get("o_custKey").set( osRecords[i].get("o_custKey") );
					group.get("o_totalPrice").set( osRecords[i].get("o_totalPrice") );
					group.get("o_orderDate").set( osRecords[i].get("o_orderDate") );
					
					previousKey   = osRecords[i].get("o_custKey").getInt();
					previousMonth = osRecords[i].get("o_orderDate").getDate().getMonth(); 
				}
			}
			
			MemoryManager.getInstance().freePage(osPage);
		}
		// Write last group
		if( group != null )
		{
			osgPage.AddRecord( group );
		}
		
		// Write back page
		MemoryManager.getInstance().freePage(osgPage);
		osgPage = null;
	}
}

////////////////////////////////////////////////////////////////////
// Sub-results classes
////////////////////////////////////////////////////////////////////
class OrdersSubsetRecord extends Record
{
	public OrdersSubsetRecord()
	{
		AddElement( "o_custKey",    new IntegerRecordElement());
		AddElement( "o_totalPrice", new FloatRecordElement()  );
		AddElement( "o_orderDate",  new DateRecordElement()   );
	}
	
	public int compareTo(Record rec)
	{
		int keyCompare = get("o_custKey").CompareTo(rec.get("o_custKey"));
		if( keyCompare != 0 )
			return keyCompare;
		else
			return get("o_orderDate").CompareTo(rec.get("o_orderDate"));
	}
}

class OrdersSubsetPage extends Page<OrdersSubsetRecord>
{
	public OrdersSubsetRecord[] CreateArray(int n){ return new OrdersSubsetRecord[n]; }
	public OrdersSubsetRecord   CreateElement(){ return new OrdersSubsetRecord(); }	
}

class OrdersGroupsPage extends Page<OrdersSubsetRecord>
{
	public OrdersSubsetRecord[] CreateArray(int n){ return new OrdersSubsetRecord[n]; }
	public OrdersSubsetRecord   CreateElement(){ return new OrdersSubsetRecord(); }		
}
