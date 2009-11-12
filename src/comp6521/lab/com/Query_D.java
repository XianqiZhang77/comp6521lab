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
		MemoryManager.getInstance().AddPageType(RegionSubsetPage.class, "r_subset.txt");
		MemoryManager.getInstance().AddPageType(QDNationSubsetPage.class, "n_subset.txt");
		
		// get empty region subset page
		RegionSubsetPage rSubsetPage = MemoryManager.getInstance().getEmptyPage(RegionSubsetPage.class);
		
		// select r_regionKey from Region where r_name = ? 
		int r_page =  0;																								// region page counter
		RegionPage regionPage = null;																					// region page
		String path = "Y:\\Dimitri\\Concordia\\Comp6521_AdvancedDatabaseSystemsAndTheory\\Lab\\ShortData\\";
		PageManagerSingleton.getInstance().setPath(path);	
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
		
		// write rSubsetPage information selected
		MemoryManager.getInstance().freePage(rSubsetPage);
		rSubsetPage = null;
		
		// get empty nation page
		QDNationSubsetPage nSubsetPage = MemoryManager.getInstance().getEmptyPage(QDNationSubsetPage.class);
		
		// select n_regionKey, n_name from Nation, Region where r_regionKey = n_regionKey and r_name = ?
		r_page = 0;
		while ( (rSubsetPage = MemoryManager.getInstance().getPage(RegionSubsetPage.class, r_page++)) != null )				// get region subset page
		{
			RegionSubsetRecord[] rSubsetRecords = rSubsetPage.m_records;													// get records array
			
			// compare each region subset record against nation pages
			for (RegionSubsetRecord rRec: rSubsetRecords)
			{
				// iterate through nation pages
				int n_page = 0;
				NationPage nationPage = null;
				while ( (nationPage = MemoryManager.getInstance().getPage(NationPage.class, n_page++)) != null )				// get region subset page
				{
					NationRecord[] nationRecords = nationPage.m_records;														// get records array
				
					// select * where Region, Nation r_regionKey = r_regionKey
					for (NationRecord nRec: nationRecords)
					{
						// if match found, add to nation subset page
						if (nRec.get("n_regionKey").getInt() == rRec.get("r_regionKey").getInt())
						{
							// create nation subset record
							QDNationSubsetRecord nSubsetRecord = new QDNationSubsetRecord();
							nSubsetRecord.AddElement("n_regionKet", nRec.get("n_regionKey"));									// select n_regionKey
							nSubsetRecord.AddElement("n_name", nRec.get("n_name"));												// select n_name
							
							// add record to page
							nSubsetPage.AddRecord(nSubsetRecord);
						}
					}
					
					// free current nation page
					MemoryManager.getInstance().freePage(nationPage);
				}
			}
			
			// free current region subset page
			MemoryManager.getInstance().freePage(rSubsetPage);
		}
		
		// write out selected nation subset page
		MemoryManager.getInstance().freePage(nSubsetPage);
	}
	
	//
/*	
*/
}

//private RegionSubsetRecord inner class stores temporary region 
class QDNationSubsetRecord extends Record
{
	public QDNationSubsetRecord()
	{
		// elements we need to keep
		AddElement( "n_regionKey", new IntegerRecordElement() );
		AddElement( "n_name", new StringRecordElement(15) ); 
	}
}

class QDNationSubsetPage extends Page<QDNationSubsetRecord>
{
	// no-argument constructor
	public QDNationSubsetPage()
	{
		super();
		super.m_records = super.CreateArray(100);
	}
	
	public QDNationSubsetRecord[] CreateArray(int n){ return new QDNationSubsetRecord[n]; }
	public QDNationSubsetRecord   CreateElement(){ return new QDNationSubsetRecord(); }
}