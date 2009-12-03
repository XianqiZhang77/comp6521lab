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
	// order by n_name DESC
	
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
		MemoryManager.getInstance().AddPageType(QD_Page.class, "qd_resultset.txt");
		
		// get empty region subset page
		RegionSubsetPage rSubsetPage = MemoryManager.getInstance().getEmptyPage(RegionSubsetPage.class);
		
		// select r_regionKey from Region where r_name = ? 
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
				while ( (nationPage = MemoryManager.getInstance().getPage(NationPage.class, n_page++)) != null )				// get nation page
				{
					NationRecord[] nationRecords = nationPage.m_records;														// get records array
				
					// select * where Region, Nation r_regionKey = r_regionKey
					for (NationRecord nRec: nationRecords)
					{
						// if match found, add to nation subset page
						if (nRec.get("n_regionKey").getInt() == rRec.get("r_regionKey").getInt())
						{
							// create nation subset record
							QDNationSubsetRecord nSubsetRecord = new QDNationSubsetRecord();									// select n_nationKey
							nSubsetRecord.get("n_nationKey").set(nRec.get("n_nationKey"));										// select n_name
							nSubsetRecord.get("n_name").set(nRec.get("n_name"));
														
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
		nSubsetPage = null;
	
		// get empty query d result set page for result set
		QD_Page qDResultSetPage = MemoryManager.getInstance().getEmptyPage(QD_Page.class);
		
		// select s_acctbal, s_name, n_name, s_address, s_phone, s_comment 
		// from nation, supplier
		// where s_nationKey = n_nationKey
		int n_page = 0;																											// nation page counter
		while ( (nSubsetPage = MemoryManager.getInstance().getPage(QDNationSubsetPage.class, n_page++)) != null )				// get nation subset page
		{
			QDNationSubsetRecord[] nSubsetRecords = nSubsetPage.m_records;														// get records array
			
			// compare each region subset record against nation pages
			for (QDNationSubsetRecord nRec: nSubsetRecords)
			{
				// iterate through supplier pages
				int s_page = 0;
				SupplierPage supplierPage = null;
				while ( (supplierPage = MemoryManager.getInstance().getPage(SupplierPage.class, s_page++)) != null )				// get supplier subset page
				{
					SupplierRecord[] supplierRecords = supplierPage.m_records;														// get records array
					
					// compare nation subset tuples to supplier tuples
					for (SupplierRecord sRec: supplierRecords)
					{
						// if match found, add to query d result set page
						if (sRec.get("s_nationKey").getInt() == nRec.get("n_nationKey").getInt())
						{
							// create query d result set record
							QD_Record qDRecord = new QD_Record();
							qDRecord.get("s_acctBal").set(sRec.get("s_acctBal"));											// add attributes to record
							qDRecord.get("s_name").set(sRec.get("s_name"));
							qDRecord.get("n_name").set(nRec.get("n_name"));
							qDRecord.get("s_address").set(sRec.get("s_address"));
							qDRecord.get("s_phone").set(sRec.get("s_phone"));
							qDRecord.get("s_comment").set(sRec.get("s_comment"));

							// TODO: remove test line
							System.out.println(qDRecord);
							
							// add record to page
							qDResultSetPage.AddRecord(qDRecord);
						}
					}
					
					// free current supplier page
					MemoryManager.getInstance().freePage(supplierPage);	
				}
			}
			
			// free nation subset page
			MemoryManager.getInstance().freePage(nSubsetPage);
		}
	
		// write out query d result set page
		MemoryManager.getInstance().freePage(qDResultSetPage);
		
		// Sort using TPMMS
		TPMMS<QD_Page> doTPMMS = new TPMMS<QD_Page>(QD_Page.class, qDResultSetPage.m_filename);
		doTPMMS.Execute();
	}
	
}

//private RegionSubsetRecord inner class stores temporary region 
class QDNationSubsetRecord extends Record implements Comparable<Record>
{
	public QDNationSubsetRecord()
	{
		// elements we need to keep
		AddElement( "n_nationKey", new IntegerRecordElement() );
		AddElement( "n_name", new StringRecordElement(15) ); 
	}
	
	public int compareTo(Record r)
	{
		// TODO: insert compare code
		return 1;
	}
}

class QDNationSubsetPage extends Page<QDNationSubsetRecord>
{
	// no-argument constructor
	public QDNationSubsetPage()
	{
		super();
		super.m_records = CreateArray(100);
	}
	
	public QDNationSubsetRecord[] CreateArray(int n){ return new QDNationSubsetRecord[n]; }
	public QDNationSubsetRecord   CreateElement(){ return new QDNationSubsetRecord(); }
}

class QD_Record extends Record
{
	public QD_Record()
	{
		AddElement("s_acctBal", new FloatRecordElement());											// add attributes to record
		AddElement("s_name",    new StringRecordElement(25));
		AddElement("n_name",    new StringRecordElement(15));
		AddElement("s_address", new StringRecordElement(50));
		AddElement("s_phone",   new StringRecordElement(30));
		AddElement("s_comment", new StringRecordElement(120));
	}
	
	// comparator interface compare method implementation
	public int compareTo(Record rec)
	{	
		return (this.get("n_name").getString().compareToIgnoreCase(rec.get("n_name").getString()));
	}
	
	// return string representation of record
	public String toString() 
	{
		// return record string
		return String.format("%-22.2f%-25s%-15s%-50s%-30s%-120s\r\n", 
								this.get("s_acctBal").getFloat(), 
								this.get("s_name").getString(), 
								this.get("n_name").getString(), 
								this.get("s_address").getString(),
								this.get("s_phone").getString(), 
								this.get("s_comment").getString()
							 );
	}
}

class QD_Page extends Page<QD_Record>
{
	public QD_Page()
	{
		super();
		super.m_records = CreateArray(100);
	}
	public QD_Record[] CreateArray(int n){ return new QD_Record[n]; }
	public QD_Record   CreateElement(){ return new QD_Record(); }
}
