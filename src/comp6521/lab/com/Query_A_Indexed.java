package comp6521.lab.com;

import java.util.Arrays;
import java.util.Date;

import comp6521.lab.com.Pages.LineItemPage;
import comp6521.lab.com.Records.DateRecordElement;

public class Query_A_Indexed extends Query_A 
{
	public void ProcessQuery( Date StartDate, Date EndDate )
	{
		ClearResults();
		
		// Create index, if it's not already done
		BPlusTree< LineItemPage, DateRecordElement > index = IndexManager.getInstance().getLineItemDateIndex();
		
		// Create the date elements so they can be compared in the tree
		DateRecordElement sd = new DateRecordElement();
		sd.setDate(StartDate);
		DateRecordElement ed = new DateRecordElement();
		ed.setDate(EndDate);
		
		// Get the list of all records in the range [start date, end date]
		int[] matchingRecords = index.Get(sd, ed);
		
		// Sort by record number
		Arrays.sort(matchingRecords);
		
		LineItemPage dummy = new LineItemPage();
		int pageSize = dummy.GetNumberRecordsPerPage();
		dummy = null;
		
		// Perform aggregation
		LineItemPage page = null;
		int curPageNb = -1;
		
		for(int i = 0; i < matchingRecords.length; i++)
		{
			// Compute page number & record index
			int pageNb = matchingRecords[i] / pageSize;
			int r      = matchingRecords[i] % pageSize;
			
			// Get new page if needed
			if( pageNb != curPageNb )
			{
				if( page != null )
					MemoryManager.getInstance().freePage(page);
				
				curPageNb = pageNb;
				page = MemoryManager.getInstance().getPage( LineItemPage.class, curPageNb);
			}
			
			// Aggregate results
			sum_qty        += page.m_records[r].get("l_quantity").getInt();
			sum_base_price += page.m_records[r].get("l_extendedPrice").getFloat();
			sum_disc_price += page.m_records[r].get("l_extendedPrice").getFloat() * (1 - page.m_records[r].get("l_discount").getFloat());
			sum_charge     += page.m_records[r].get("l_extendedPrice").getFloat() * (1 - page.m_records[r].get("l_discount").getFloat())/* * (1 + page.m_records[r].get("l_tax").getFloat())*/;
			count++;
		}
		
		if( page != null )
			MemoryManager.getInstance().freePage(page);
		
		// Compute averages
		avg_qty = (count == 0 ? 0 : (sum_qty / count) );
		m_queryPerformed = true;		
	}
}
