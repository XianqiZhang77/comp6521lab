package comp6521.lab.com;

public class Query_B {
	public void PerformQuery( String[] SelList, String[] AvgList )
	{
		int p;
		// First, perform the inner query
		// which is select avg(c_acctbal) from customer where c_acctbal > 0 and 
		// substring(c_phone, 1,2) in AvgList
		int countAvg     = 0;
		float avgBalance = 0;
		
		p              = 0;
		char[] rawData = null;
		
		do
		{
			rawData = MemoryManager.getInstance().getPage( MemoryManager.RecordType.eCustomerPage, p );
			
			// Iterate through the records in the current page
			// .. either get the pages filled or construct here!
			CustomerRecord[] customers = null;
			for( int r = 0; r < customers.length; r++ )
			{
				if( customers[r].c_acctBal > 0 && InList( customers[r].c_phone.substring( 0, 2), AvgList ) )
				{
					countAvg++;
					avgBalance += customers[r].c_acctBal;
				}
			}
			
			p++;
		} while( rawData != null );
		
		if( countAvg > 0 )
			avgBalance /= countAvg;
		
		// First step: print the header of the results
		System.out.println("cntrycode\tc_acctbal");
		
		// Now, perform the main query
		p       = 0;
		rawData = null;
		
		do
		{
			rawData = MemoryManager.getInstance().getPage( MemoryManager.RecordType.eCustomerPage, p );
			
			// Construct the records somehow...
			CustomerRecord[] customers = null;
			
			// Iterate through the records in the page
			for( int r = 0; r < customers.length; r++ )
			{
				if( customers[r].c_acctBal > avgBalance && InList( customers[r].c_phone.substring( 0, 2), SelList) )
				{
					// Print record info
					System.out.println(customers[r].c_phone.substring( 0, 2) + "\t" + customers[r].c_acctBal );
				}
			}
			
			p++;
		} while( rawData != null );
		
	}
	
	private boolean InList( String str, String[] list )
	{
		for( int s = 0; s < list.length; s++ )
		{
			if( str == list[s] )
				return true;
		}
		return false;
	}
}
