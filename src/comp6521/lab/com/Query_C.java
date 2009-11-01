package comp6521.lab.com;

import java.util.ArrayList;

import comp6521.lab.com.Records.*;
import comp6521.lab.com.Pages.*;

public class Query_C {

	public void ProcessQuery( int partSize, String regionNameSel, String regionNameMin )
	{		
		// Now, this query promises to be ugly.
		// Memory notes:
		// 1 region page + 1 nation page + 1 supplier page + 
		// 1 page of type1 + 1 page of type2 == 1830 + 1980 + 2170 + 2730 + 110 < 10240
		
		// Add the custom pages to the memory manager
		MemoryManager.getInstance().AddPageType( QCSN_Page.class.getName(), 1750, "qc_t1.txt" ); // 275 * 10
		MemoryManager.getInstance().AddPageType( QCSK_Page.class.getName(), 1300, "qc_t2.txt" ); // 13 * 100
		
		// Phase I
		// Find all regions with one or the other name.
		// Follow the region name to get the nation and supplier.
		// In the first case:
		//   -- Keep all useful attributes:
		//       s_acctbal, s_name, n_name, s_address, s_phone, s_comment AND s_suppKey (needed for query)
		// In the second case,
		//   -- Keep only s_suppkey.
		SupplierPage suppPage     = null;
		NationPage   nationPage   = null;
		RegionPage   regionPage   = null;
		
		QCSN_Page qcsn_page = MemoryManager.getInstance().getEmptyPage( QCSN_Page.class );
		QCSK_Page qcsk_page = MemoryManager.getInstance().getEmptyPage( QCSK_Page.class );
		
		int r_p = 0;
		while( (regionPage = MemoryManager.getInstance().getPage( RegionPage.class, r_p++)) != null )
		{
			RegionRecord[] Regions = regionPage.m_records;
			
			// In the current page:
			// First : check if there are region(s) meeting the constraints (region name)
			ArrayList<Integer> regionsKept = new ArrayList<Integer>();
			for( int r = 0; r < Regions.length; r++ )
			{
				if( Regions[r].r_name.compareToIgnoreCase( regionNameSel ) == 0 ||
					Regions[r].r_name.compareToIgnoreCase( regionNameMin ) == 0   )
				{
					regionsKept.add(Integer.valueOf(r));
				}
			}
			
			if(!regionsKept.isEmpty())
			{
				// Second: Parse the nation relation for nations in the region			
				int n_p = 0;
				while( (nationPage = MemoryManager.getInstance().getPage(NationPage.class, n_p++)) != null )
				{
					NationRecord[] Nations = nationPage.m_records;
					// Try to match the nations with the regions (r_regionkey == n_regionkey)
					ArrayList<Integer> nationsKept = new ArrayList<Integer>();
					for( int n = 0; n < Nations.length; n++ )
					{
						boolean matched = false;
						for( int kr = 0; kr < regionsKept.size() && !matched; kr++ )
						{
							if( Nations[n].n_regionKey == Regions[regionsKept.get(kr)].r_regionKey )
							{
								matched = true;
								nationsKept.add(Integer.valueOf(n));
							}
						}
					}
					
					if( !nationsKept.isEmpty())
					{
						// Third: match with suppliers
						int s_p = 0;
						while( (suppPage = MemoryManager.getInstance().getPage(SupplierPage.class, s_p++)) != null )
						{
							SupplierRecord[] Suppliers = suppPage.m_records;
							// Match n_nationkey with s_nationkey
							for( int s = 0; s < Suppliers.length; s++ )
							{
								boolean matched = false;
								for( int kn = 0; kn < nationsKept.size() && !matched; kn++ )
								{
									int n = nationsKept.get(kn).intValue();
									if( Suppliers[s].s_nationKey == Nations[n].n_nationKey )
									{
										matched = true;
										
										// Supplier -> nation -> region
										for( int kr = 0; kr < regionsKept.size(); kr++ )
										{
											int r = regionsKept.get(kr).intValue();
											if( Nations[n].n_regionKey == Regions[r].r_regionKey )
											{
												// Check whether we have a match of type 1
												if( Regions[r].r_name.compareToIgnoreCase( regionNameSel ) == 0 )
												{
													QCSN_Record selRegion = new QCSN_Record();
													selRegion.s_suppKey = Suppliers[s].s_suppKey;
													selRegion.s_acctBal = Suppliers[s].s_acctBal;
													selRegion.s_name    = Suppliers[s].s_name;
													selRegion.n_name    = Nations[n].n_name;
													selRegion.s_address = Suppliers[s].s_address;
													selRegion.s_phone   = Suppliers[s].s_phone;
													selRegion.s_comment = Suppliers[s].s_comment;
													
													// Add to QCSN Page
													qcsn_page.AddRecord( selRegion );
																										
												}
												// Or of type 2												
												if( Regions[r].r_name.compareToIgnoreCase( regionNameMin ) == 0 )
												{
													QCSK_Record minRegion = new QCSK_Record();
													minRegion.s_suppKey = Suppliers[s].s_suppKey;
													
													// Add to QCSK Page
													qcsk_page.AddRecord( minRegion );
												}
											}
										}
									}
								}
							}							
							
							MemoryManager.getInstance().freePage(suppPage);
						}
					}					
					
					MemoryManager.getInstance().freePage(nationPage);
				}
			}			

			MemoryManager.getInstance().freePage(regionPage);
		}
		
		// Free (and auto-write) the qcsX_pages
		MemoryManager.getInstance().freePage( qcsn_page );
		MemoryManager.getInstance().freePage( qcsk_page );
		
		// Phase II
		// We now have all tuples of QCSN_Record & QCSK_Record to 
		// 1- Find all parts in (parts -> partSupp -> Supplier ) where supplier.s_suppKey in QCSN_Table
		//                                                         and part.size = partSize
		// 2- For all these parts, find the min supply cost of that part in any (partSupp -> Supplier) where partSupp.p_partkey = taken from 1)
		//                                                                                             and Supplier in QCSK_Table.
		// Implementation note: start from our QCSN pages since the cardinality should be low
		QCSN_Page qn_page = null;
		int qn_p = 0;
		while( (qn_page = MemoryManager.getInstance().getPage( QCSN_Page.class, qn_p++)) != null )
		{
			QCSN_Record QCSN[] = qn_page.m_records;
			for( int qn = 0; qn < QCSN.length; qn++ )
			{
				//...
			}			
			
			MemoryManager.getInstance().freePage(qn_page);
		}
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	// Sub classes implementation                                                                //
	///////////////////////////////////////////////////////////////////////////////////////////////
	public class QCSN_Record extends Record
	{
		// Record size is 273 ** Size in char length **
		// Record length is 275
		public int    s_suppKey;
		public float  s_acctBal;
		public String s_name;
		public String n_name;
		public String s_address;
		public String s_phone;
		public String s_comment;	
		
		public void Parse(String data)
		{		
			s_suppKey   = Integer.parseInt(data.substring(0,    10).trim());
			s_name      =                  data.substring(11,   35).trim();
			s_address   =                  data.substring(36,   85).trim();
			s_phone     =                  data.substring(86,  115).trim();
			s_acctBal   = Float.parseFloat(data.substring(116, 137).trim());
			s_comment   =                  data.substring(138, 157).trim();
			n_name      =                  data.substring(158, 172).trim();
		}
		
		public String Write()
		{
			String data = "";
			data += String.format("%1$-11d", s_suppKey);
			data += String.format("%1$-25c", s_name.toCharArray());
			data += String.format("%1$-50c", s_address.toCharArray());
			data += String.format("%1$-30c", s_phone.toCharArray());
			data += String.format("%1$-22f", s_acctBal);
			data += String.format("%1$-20c", s_comment.toCharArray());
			data += String.format("%1$-15c", n_name.toCharArray());
			
			return data;
		}
	}
	
	public class QCSN_Page extends Page<QCSN_Record>
	{
		public QCSN_Record[] CreateArray(int n){ return new QCSN_Record[n]; }
		public QCSN_Record   CreateElement(){ return new QCSN_Record(); }
	}
	
	public class QCSK_Record extends Record
	{
		// Record size is 11
		// Record length is 13
		public int s_suppKey;
		
		public void Parse(String data)
		{
			s_suppKey   = Integer.parseInt(data.substring(0, 10).trim());
		}
		
		public String Write()
		{
			return String.format("%1$-11d", s_suppKey);
		}
	}
	
	public class QCSK_Page extends Page<QCSK_Record>
	{
		public QCSK_Record[] CreateArray(int n){ return new QCSK_Record[n]; }
		public QCSK_Record   CreateElement(){ return new QCSK_Record(); }
	}
}
