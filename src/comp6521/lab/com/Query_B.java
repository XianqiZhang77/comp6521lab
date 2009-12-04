package comp6521.lab.com;

import comp6521.lab.com.Pages.CustomerPage;
import comp6521.lab.com.Records.CustomerRecord;

public class Query_B {
	public void PerformQuery( String[] SelList, String[] AvgList )
	{
		int p = 0;
		// First, perform the inner query
		// which is select avg(c_acctbal) from customer where c_acctbal > 0 and 
		// substring(c_phone, 1,2) in AvgList
		int countAvg          = 0;
		float avgBalance      = 0;
		CustomerPage custPage = null;
		
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
			avgBalance /= countAvg;
		
		// First step: print the header of the results
		System.out.println("cntrycode\tc_acctbal");
		
		// Now, perform the main query
		p        = 0;
		custPage = null;
		
		while( (custPage = MemoryManager.getInstance().getPage( CustomerPage.class, p++ )) != null )
		{
			CustomerRecord[] customers = custPage.m_records;
			
			// Iterate through the records in the page
			for( int r = 0; r < customers.length; r++ )
			{
				if( customers[r].get("c_acctBal").getFloat() > avgBalance && InList( customers[r].get("c_phone").getString().substring( 0, 2), SelList) )
				{
					// Print record info
					System.out.println(customers[r].get("c_phone").getString().substring( 0, 2) + "\t" + customers[r].get("c_acctBal").getFloat() );
				}
			}
			
			MemoryManager.getInstance().freePage( custPage );
		}		
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
