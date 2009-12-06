package comp6521.lab.com;
import comp6521.lab.com.MemoryManager;
import comp6521.lab.com.Pages.LineItemPage;
import comp6521.lab.com.Records.LineItemRecord;

import java.util.Date;

public class Query_A {
	int   sum_qty;
	double sum_base_price;
	double sum_disc_price;
	double sum_charge; // should be same as sum_disc_price since the tax was removed??
	double avg_qty;
	int   count;	
	boolean m_queryPerformed;
	
	public Query_A()
	{
		m_queryPerformed = false;
	}
	
	public void ClearResults()
	{
		sum_qty          = 0;
		sum_base_price   = 0;
		sum_disc_price   = 0;
		sum_charge       = 0;
		avg_qty          = 0;
		count            = 0;
		m_queryPerformed = false;
	}
	
	public String getLogFilename() { return "a.out"; }
	
	public void PerformQuery( Date StartDate, Date EndDate )
	{
		Log.StartLog(getLogFilename());
		ProcessQuery( StartDate, EndDate );
		WriteResults();
		Log.EndLog();
	}
	
	public void ProcessQuery( Date StartDate, Date EndDate )
	{
		ClearResults();
		
		// Go through all records in the LineItem table.
		int p = 0;
		LineItemPage liPage = null;

		Log.StartLogSection("Going through all records in the LineItem table to see if they match, and compound results");
		while( (liPage = MemoryManager.getInstance().getPage( LineItemPage.class, p++ )) != null)
		{			
			LineItemRecord[] LineItems = liPage.m_records;
			
			// Either get pages already constructed, or construct pages here..
			for( int r = 0; r < LineItems.length; r++ )
			{
				// Check condition
				if( LineItems[r].get("l_receiptDate").getDate().compareTo( StartDate ) >= 0 &&
				    LineItems[r].get("l_receiptDate").getDate().compareTo( EndDate )   <= 0   )
				{
					sum_qty        += LineItems[r].get("l_quantity").getInt();
					sum_base_price += LineItems[r].get("l_extendedPrice").getFloat();
					sum_disc_price += LineItems[r].get("l_extendedPrice").getFloat() * (1.0f - LineItems[r].get("l_discount").getFloat());
					sum_charge     += LineItems[r].get("l_extendedPrice").getFloat() * (1.0f - LineItems[r].get("l_discount").getFloat())/* * (1 + LineItems[r].get("l_tax").getFloat())*/;
					count++;
				}
			}
			
			MemoryManager.getInstance().freePage( liPage );			
		}
		Log.EndLogSection();
		
		// Compute averages
		avg_qty = (count == 0 ? 0 : (sum_qty / (double)count) );
		m_queryPerformed = true;
	}
	
	public void WriteResults()
	{
		if( m_queryPerformed )
		{
			Log.SetResultHeader("sum_qty\tsum_base_price\tsum_disc_price\tsum_charge\tavg_qty\tcount_order");
			Log.AddResult(sum_qty + "\t" + sum_base_price + "\t" + sum_disc_price + "\t" + sum_charge + "\t" + avg_qty + "\t" + count );
		
		}
		else
		{
			Log.AddResult("Perform query before outputting results");
		}
	}
}
