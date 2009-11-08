/**
 * November. 07, 2009
 * Query_D.java: Implementation of COMP6521 Lab Query D
 */
package comp6521.lab.com;

import comp6521.lab.com.Records.*;
import comp6521.lab.com.Pages.*;

/**
 * @author dimitri tiago
 */
public class Query_D 
{
	// select s_acctbal, s_name, n_name, s_address, s_phone, s_comment
	// from region, nation, supplier
	// where s_nationKey = n_nationKey and n_regionKey = r_regionKey and r_name = ?
	// group by n_name DESC
	
	// memory available = 10240
	// page sizes: 
	//				region page size   = 1830
	//				nation page size   = 1980
	//				supplier page size = 2710
	
	// execute query:
	public void ProcessQuery(String r_name)
	{
		// initialise memory manager tracking for custom pages		
		MemoryManager.getInstance().AddPageType(RegionSubsetPage.class.getName(), (new RegionSubsetRecord()).GetRecordSize() * 100, "r_subset.txt");
	
		// get empty region subset page
		RegionSubsetPage rSubsetPage = MemoryManager.getInstance().getEmptyPage(RegionSubsetPage.class);
		
		// select * from Region where r_name = ? 
		int r_page =  0;																								// region page counter
		RegionPage regionPage = null;																					// region page
		while ( (regionPage = MemoryManager.getInstance().getPage(RegionPage.class, r_page++)) != null )				// get region page
		{
			// process page records
			RegionRecord[] rRecords = regionPage.m_records;																// store page records
			for (int r = 0; r < rRecords.length; r++)																	// iterate through page records
			{
				// select records with r_name = ?
				if (rRecords[r].get("r_name").getString().compareToIgnoreCase(r_name) == 0)
				{
					// add r_regionKey to rSubsetPage
					RegionSubsetRecord rSubsetRecord = new RegionSubsetRecord();										
					rSubsetRecord.get("r_regionKey").set(rRecords[r].get("r_regionKey"));
					
					rSubsetPage.AddRecord(rSubsetRecord);
				}
			}
			
			// free current region page
			MemoryManager.getInstance().freePage(regionPage);
		}	
	}
	
	// Phase I Records
	private class RegionSubsetRecord extends Record																		// region subset record
	{
		public RegionSubsetRecord()
		{
			AddElement( "r_regionKey", new IntegerRecordElement() );
		}
	}
	
	private class RegionSubsetPage extends Page<RegionSubsetRecord>														// region subset page
	{
		public RegionSubsetRecord[] CreateArray(int n){ return new RegionSubsetRecord[n]; }
		public RegionSubsetRecord   CreateElement(){ return new RegionSubsetRecord(); }
	}
}