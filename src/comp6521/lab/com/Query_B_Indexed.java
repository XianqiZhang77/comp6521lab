package comp6521.lab.com;

import comp6521.lab.com.Hashing.HashFunction;
import comp6521.lab.com.Pages.CustomerPage;
import comp6521.lab.com.Records.CustomerRecord;
import comp6521.lab.com.Records.RecordElement;
import comp6521.lab.com.Records.StringRecordElement;

public class Query_B_Indexed extends Query_B 
{
	public void PerformQuery( String[] SelList, String[] AvgList )
	{
		Log.StartLog("b_i.out");
		// First, use a hash table for the cntrycode
		LinearHashTable< CustomerPage > index = IndexManager.getInstance().getCustomerCountryCodeIndex();
		String indexFilename = index.m_filename;
		
		// Perform the inner query ...
		int countAvg = 0;
		double avgBalance = 0;
		
		// String length of 3 == "x12"
		StringRecordElement el = new StringRecordElement(2);
		
		CustomerPage page = null;

		Log.StartLogSection("Loop on all elements in the second list (used for the average) and compute average");
		for(int k = 0; k < AvgList.length; k++ )
		{
			el.setString(AvgList[k]);
			Log.StartLogSection("Get all records with country code matching " + AvgList[k]);
			int[] pageList = index.getPageList( el );
			Log.EndLogSection();
			
			for( int p = 0; p < pageList.length; p++ )
			{
				page = MemoryManager.getInstance().getPage( CustomerPage.class, pageList[p], indexFilename );
				CustomerRecord[] customers = page.m_records;

				for( int r = 0; r < customers.length; r++ )
				{
					// Check if the record matches the conditions
					if( customers[r].get("c_phone").getString().substring(0,2).compareTo(AvgList[k]) == 0 &&
						customers[r].get("c_acctBal").getFloat() > 0 )
					{
						countAvg++;
						avgBalance += customers[r].get("c_acctBal").getFloat();
					}							
				}
				
				MemoryManager.getInstance().freePage(page);
			}			
		}
		
		if( countAvg > 0 )
			avgBalance /= (double)countAvg;
		
		Log.EndLogSection();
		
		// Perform the outer query ...
		// First step: print the header of the results
		Log.SetResultHeader("cntrycode\tc_acctbal");
		
		// Now, perform the main query
		page = null;
		
		Log.StartLogSection("Loop on all country codes in the first list and output results if over the computed average");
		for( int k = 0; k < SelList.length; k++ )
		{
			el.setString( SelList[k]);
			Log.StartLogSection("Get all records with country code matching " + SelList[k]);
			int[] pageList = index.getPageList( el );
			Log.EndLogSection();
			
			for( int p = 0; p < pageList.length; p++ )
			{
				page = MemoryManager.getInstance().getPage( CustomerPage.class, pageList[p], indexFilename );
				CustomerRecord[] customers = page.m_records;

				for( int r = 0; r < customers.length; r++ )
				{				
					// Check if the record matches the conditions
					if( customers[r].get("c_phone").getString().substring(0,2).compareTo(SelList[k]) == 0 &&
						customers[r].get("c_acctBal").getFloat() > avgBalance )
					{
						// Print record info
						Log.AddResult(customers[r].get("c_phone").getString().substring( 0, 2) + "\t" + customers[r].get("c_acctBal").getFloat() );
					}					
				}
				
				MemoryManager.getInstance().freePage(page);
			}
		}	
		Log.EndLogSection();
		
		Log.EndLog();
	}
}

class CountryHashFunction extends HashFunction
{
	public int Hash( RecordElement el )
	{
		return Integer.parseInt(el.getString().substring(0, 2));
	}
}