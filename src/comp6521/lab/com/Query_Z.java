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
		OrdersSubsetRecord os_dummy = new OrdersSubsetRecord();
		
		MemoryManager.getInstance().AddPageType( OrdersSubsetPage.class.getName(), os_dummy.GetRecordSize() * 10, "qz_os.txt");
		MemoryManager.getInstance().AddPageType( OrdersGroupsPage.class.getName(), os_dummy.GetRecordSize() * 10, "qz_os.txt");
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
				if( orders[i].get("o_orderDate").getDate().getYear() == year )
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
		// ...
		// TODO !!
		// ...
		
		////////////////////////////////////////////////////////////////////
		// Fourth phase:
		// Group by o_custKey (sum total price) & month
		////////////////////////////////////////////////////////////////////
		// Assume orders subset table is sorted
		OrdersGroupsPage osgPage = MemoryManager.getInstance().getEmptyPage( OrdersGroupsPage.class );
		
		int previousKey = -1;
		int previousMonth = -1;
		OrdersSubsetRecord group = null;
		
		int os_p = 0;
		while( (osPage = MemoryManager.getInstance().getPage( OrdersSubsetPage.class, os_p++)) != null)
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
		
		////////////////////////////////////////////////////////////////////
		// Fifth phase:
		// Find minimum name (matching o_cust with c_cust, getting c_name)
		// The trick here is to go through all the orders subset groups, BUT
		// remember which is the "best" name so far, i.e. with page & index
		////////////////////////////////////////////////////////////////////
		// ATTENTION: MAKE SURE WE ALWAYS GET THE REAL FIRST ONE
		// THIS SHOULD BE TRUE, BUT REVERIFY!!!
		String minName = "ZZZZZZZZZZZZZZZZZZZZZZZZZ"; // 25 z's
		int minPage    = -1;
		int minIndex   = -1;
		
		int osg_p = 0;
		while ( (osgPage = MemoryManager.getInstance().getPage( OrdersGroupsPage.class, osg_p++)) != null )
		{
			OrdersSubsetRecord[] osgRecords = osgPage.m_records;
			
			// -- Interleave loops --
			// Match with customers
			CustomerPage custPage = null;
			int c_p = 0;
			while( (custPage = MemoryManager.getInstance().getPage( CustomerPage.class, c_p++)) != null )
			{
				CustomerRecord[] customers = custPage.m_records;
				
				for(int i = 0; i < osgRecords.length; i++ )
				{
					for( int j = 0; j < customers.length; j++ )
					{
						// Key match
						if( customers[j].get("c_custKey").getInt() == customers[i].get("o_custKey").getInt() )
						{
							// Check for min name
							if( customers[j].get("c_name").getString().compareTo( minName ) < 0 )
							{
								minName  = customers[j].get("c_name").getString();
								minPage  = osg_p - 1; // IMPORTANT.. since we pre-increment!
								minIndex = i;
							}
						}
					}
				}
				
				MemoryManager.getInstance().freePage( custPage );
			}			
			
			// ATTENTION minPage = osg_p - 1 !!!
			MemoryManager.getInstance().freePage( osgPage );
		}
		
		////////////////////////////////////////////////////////////////////
		//  AND 
		// Output results
		////////////////////////////////////////////////////////////////////
		// Output header
		System.out.println("customerName\tJAN\tFEB\tMAR\tAPR\tMAY\tJUN\tJUL\tAUG\tSEP\tOCT\tNOV\tDEC");
		
		String result = minName + "\t";
		// Important note: it is possible that the results spread on two pages.
		osgPage = MemoryManager.getInstance().getPage( OrdersGroupsPage.class, minPage );
		for( int i = 0; i < 12; i++ )
		{
			if( osgPage.m_records[minIndex].get("o_orderDate").getDate().getMonth() == i )
			{
				// Output this result, increment minIndex
				result += osgPage.m_records[minIndex].get("o_totalPrice").getFloat() + "\t";
				
				minIndex++;
				
				if( minIndex >= osgPage.m_records.length && i != 11 )
				{
					minIndex = 0;
					minPage++;
					MemoryManager.getInstance().freePage(osgPage);
					osgPage = MemoryManager.getInstance().getPage( OrdersGroupsPage.class, minPage );
					assert(osgPage != null); // this should always be ok..
				}
			}
			else
			{
				// No result, just add a tab
				result += "\t";
			}
		}
		MemoryManager.getInstance().freePage(osgPage);
		
		System.out.println(result);
	}
		
	////////////////////////////////////////////////////////////////////
	// Sub-results classes
	////////////////////////////////////////////////////////////////////
	public class OrdersSubsetRecord extends Record
	{
		public OrdersSubsetRecord()
		{
			AddElement( "o_custKey",    new IntegerRecordElement());
			AddElement( "o_totalPrice", new FloatRecordElement()  );
			AddElement( "o_orderDate",  new DateRecordElement()   );
		}
	}
	
	public class OrdersSubsetPage extends Page<OrdersSubsetRecord>
	{
		public OrdersSubsetRecord[] CreateArray(int n){ return new OrdersSubsetRecord[n]; }
		public OrdersSubsetRecord   CreateElement(){ return new OrdersSubsetRecord(); }	
	}
	
	public class OrdersGroupsPage extends Page<OrdersSubsetRecord>
	{
		public OrdersSubsetRecord[] CreateArray(int n){ return new OrdersSubsetRecord[n]; }
		public OrdersSubsetRecord   CreateElement(){ return new OrdersSubsetRecord(); }		
	}
}
