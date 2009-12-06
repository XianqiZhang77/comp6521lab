package comp6521.lab.com;

import comp6521.lab.com.Pages.CustomerPage;
import comp6521.lab.com.Records.CustomerRecord;

public class Query_B {
	public void PerformQuery( String[] SelList, String[] AvgList )
	{
		Log.StartLog("b.out");
		int p = 0;
		// First, perform the inner query
		// which is select avg(c_acctbal) from customer where c_acctbal > 0 and 
		// substring(c_phone, 1,2) in AvgList
		int countAvg          = 0;
		double avgBalance      = 0;
		CustomerPage custPage = null;
		
		Log.StartLogSection("Going through the Customer relation to compute the average from the matching records (2nd list)");
		while( (custPage = MemoryManager.getInstance().getPage( CustomerPage.class, p++ )) != null )
		{
			// Iterate through the records in the current page
			CustomerRecord[] customers = custPage.m_records;
			for( int r = 0; r < customers.length; r++ )
			{
				if( customers[r].get("c_acctBal").getFloat() > 0 && InList( customers[r].get("c_phone").getString().substring( 0, 2), AvgList ) )
				{
					countAvg++;
					avgBalance += customers[r].get("c_acctBal").getFloat();
				}
			}
			
			MemoryManager.getInstance().freePage( custPage );
		}
		
		if( countAvg > 0 )
			avgBalance /= (double)countAvg;
		
		Log.EndLogSection();
		
		// First step: print the header of the results
		Log.SetResultHeader("cntrycode\tc_acctbal");
		
		// Now, perform the main query
		p        = 0;
		custPage = null;
		
		Log.StartLogSection("Going through the Customer table to output the selected results (1st list + over average)");
		while( (custPage = MemoryManager.getInstance().getPage( CustomerPage.class, p++ )) != null )
		{
			CustomerRecord[] customers = custPage.m_records;
			
			// Iterate through the records in the page
			for( int r = 0; r < customers.length; r++ )
			{
				if( customers[r].get("c_acctBal").getFloat() > avgBalance && InList( customers[r].get("c_phone").getString().substring( 0, 2), SelList) )
				{
					// Print record info
					Log.AddResult(customers[r].get("c_phone").getString().substring( 0, 2) + "\t" + customers[r].get("c_acctBal").getFloat() );
				}
			}
			
			MemoryManager.getInstance().freePage( custPage );
		}
		Log.EndLogSection();
		
		Log.EndLog();
	}
	
	protected boolean InList( String str, String[] list )
	{
		for( int s = 0; s < list.length; s++ )
		{
			if( str.compareTo(list[s]) == 0 )
				return true;
		}
		return false;
	}
}
