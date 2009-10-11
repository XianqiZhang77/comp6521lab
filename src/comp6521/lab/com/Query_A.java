package comp6521.lab.com;
import comp6521.lab.com.MemoryManager;
import comp6521.lab.com.Pages.LineItemPage;
import comp6521.lab.com.Records.LineItemRecord;

import java.util.Date;

public class Query_A {
	int   sum_qty;
	float sum_base_price;
	float sum_disc_price;
	float sum_charge; // should be same as sum_disc_price since the tax was removed??
	float avg_qty;
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
	
	public void PerformQuery( Date StartDate, Date EndDate )
	{
		ProcessQuery( StartDate, EndDate );
		WriteResults();
	}
	
	public void ProcessQuery( Date StartDate, Date EndDate )
	{
		ClearResults();
		
		// Go through all records in the LineItem table.
		int p = 0;
		LineItemPage liPage = null;

		do
		{
			liPage = MemoryManager.getInstance().getPage( LineItemPage.class, p );
			LineItemRecord[] LineItems = liPage.m_records;
			
			// Either get pages already constructed, or construct pages here..
			for( int r = 0; r < LineItems.length; r++ )
			{
				// Check condition
				if( LineItems[r].l_receiptDate.compareTo( StartDate ) >= 0 &&
				    LineItems[r].l_receiptDate.compareTo( EndDate )   <= 0   )
				{
					sum_qty        += LineItems[r].l_quantity;
					sum_base_price += LineItems[r].l_extendedPrice;
					sum_disc_price += LineItems[r].l_extendedPrice * (1 - LineItems[r].l_discount);
					count++;
				}
			}
			
			MemoryManager.getInstance().freePage( liPage, p );			
			p++;			
		} while(!liPage.isEmpty());
		
		// Compute averages
		avg_qty = (count == 0 ? 0 : (sum_qty / count) );
		// Copy sum_charge which is the same as sum_disc_price
		sum_charge = sum_disc_price;
		m_queryPerformed = true;
	}
	
	public void WriteResults()
	{
		if( m_queryPerformed )
		{
			System.out.println("sum_qty\tsum_base_price\tsum_disc_price\tsum_charge\tavg_qty\tcount_order");
			System.out.println( sum_qty + "\t" + sum_base_price + "\t" + sum_disc_price + "\t" + sum_charge + "\t" + avg_qty + "\t" + count);
		}
		else
		{
			System.out.println("Perform query before asking for results");
		}
	}
}