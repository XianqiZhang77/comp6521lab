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
		// 1 page of type1 + 1 page of type2 == 1830 + 1980 + 2170 + 2730 + 1300 < 10240
		
		// Add the custom pages to the memory manager
		MemoryManager.getInstance().AddPageType( QCSN_Page.class.getName(), 2750, "qc_t1.txt" ); // 275 * 10
		MemoryManager.getInstance().AddPageType( QCSK_Page.class.getName(), 1300, "qc_t2.txt" ); // 13 * 100
		MemoryManager.getInstance().AddPageType( QCFinal_Page.class.getName(), 3120, "qc_final.txt" ); // 312 * 10
		
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
		QCFinal_Page qf_page = MemoryManager.getInstance().getEmptyPage( QCFinal_Page.class );
		
		QCSN_Page qn_page = null;
		int qn_p = 0;
		while( (qn_page = MemoryManager.getInstance().getPage( QCSN_Page.class, qn_p++)) != null )
		{
			QCSN_Record[] qcsn = qn_page.m_records;
			
			// First, take note of all product keys in the partSupp pages that fit any record in QCSN
			PartSuppPage psPage = null;
			int psp = 0;
			while ( (psPage = MemoryManager.getInstance().getPage( PartSuppPage.class, psp++ ) ) != null )
			{
				PartSuppRecord[] psRecords = psPage.m_records;
				
				// Keep indexes
				ArrayList<Integer> keptProducts = new ArrayList<Integer>();
				ArrayList<Integer> keptProductsSupp = new ArrayList<Integer>();
				ArrayList<Float>   keptProductsCost = new ArrayList<Float>();
				
				for( int i = 0; i < psRecords.length; i++ )
					for( int j = 0; j < qcsn.length; j++ )
						if( psRecords[i].ps_suppKey == qcsn[j].s_suppKey )
						{
							keptProducts.add(Integer.valueOf(psRecords[i].ps_partKey));
							keptProductsCost.add(Float.valueOf(psRecords[i].ps_supplyCost));
							keptProductsSupp.add(Integer.valueOf(j));
						}
			
				MemoryManager.getInstance().freePage(psPage);
				
				if( keptProducts.isEmpty() )
					continue;
				
				// If we found potential products, find them.
				PartPage pPage = null;
				int pp = 0;
				while( (pPage = MemoryManager.getInstance().getPage(PartPage.class, pp++)) != null )
				{
					PartRecord[] pRecords = pPage.m_records;
					for( int i = 0; i < pRecords.length; i++ )
						for( int j = 0; j < keptProducts.size(); j++ )
							if( pRecords[i].p_partKey == keptProducts.get(j) && pRecords[i].p_size == partSize )
							{
								//keptProducts.add(Integer.valueOf(pRecords[i].p_partKey));
								// Final candidates before min removal
								QCFinal_Record qf = new QCFinal_Record();
								qf.copyFromQCSN( qcsn[keptProductsSupp.get(j)]);
								
								qf.ps_supplycost = keptProductsCost.get(j).floatValue();
								
								qf.p_partkey = pRecords[i].p_partKey;
								qf.p_mfgr    = pRecords[i].p_mfgr;		
								// Save record
								qf_page.AddRecord( qf );
							}
					
					MemoryManager.getInstance().freePage(pPage);
				}	
			}	
			
			MemoryManager.getInstance().freePage(qn_page);
		}
		
		// Write back the semi-final results
		MemoryManager.getInstance().freePage(qf_page);
		qf_page = null;
		// Now, for each entry in our semi-final results,
		// Make sure there are no matches "our product key -- suppliers from QCSK" that have a lower price
		// If that's true, output the result!
		
		// 1. Output header
		System.out.println( "s_acctbal\ts_name\tn_name\tp_partkey\tp_mfgr\ts_address\ts_phone\ts_comment");
		
		// 2. Check for matches		
		int qf_p = 0;
		while( (qf_page = MemoryManager.getInstance().getPage( QCFinal_Page.class, qf_p++)) != null )
		{
			QCFinal_Record[] qf = qf_page.m_records;
			for( int i = 0; i < qf.length; i++ )
			{
				boolean OutputRecord = true;
				// For all suppliers in the other table
				qcsk_page = null;
				int qcsk_p = 0;
				while( (qcsk_page = MemoryManager.getInstance().getPage( QCSK_Page.class, qcsk_p++)) != null && OutputRecord )
				{
					QCSK_Record[] qcsk = qcsk_page.m_records;
					
					for( int j = 0; j < qcsk.length && OutputRecord; j++ )
					{
						// For matching in the partSupp table
						PartSuppPage psPage = null;
						int psp = 0;
						while ( (psPage = MemoryManager.getInstance().getPage( PartSuppPage.class, psp++ ) ) != null )
						{
							PartSuppRecord[] psRecords = psPage.m_records;
							
							for( int k = 0; k < psRecords.length && OutputRecord; k++ )
							{
								// Check part key match (our product - part supp part key )
								//                      (min sel supp key - part supp supp key )
								//                      ( price check )
								if( psRecords[k].ps_partKey == qf[i].p_partkey && 
									psRecords[k].ps_suppKey == qcsk[j].s_suppKey &&
									psRecords[k].ps_supplyCost < qf[i].ps_supplycost )
								{
									OutputRecord = false;
								}
							}
							
							MemoryManager.getInstance().freePage(psPage);
						}
					}					
					
					MemoryManager.getInstance().freePage(qcsk_page);
				}
				
				if( OutputRecord )
				{
					// Succesful result found! yay!!!
					System.out.println( qf[i].s_acctBal + "\t" +
							            qf[i].s_name + "\t" +
							            qf[i].n_name + "\t" +
							            qf[i].p_partkey + "\t" +
							            qf[i].p_mfgr + "\t" +
							            qf[i].s_address + "\t" +
							            qf[i].s_phone + "\t" +
							            qf[i].s_comment );
				}
			}			
			
			MemoryManager.getInstance().freePage( qf_page );
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
			s_comment   =                  data.substring(138, 257).trim();
			n_name      =                  data.substring(258, 272).trim();
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
		//public static int GetNumberRecordsPerPage() { return 10; }
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
	
	public class QCFinal_Record extends Record
	{
		// record size is 310+2 chars.
		public float  s_acctBal;
		public String s_name;
		public String n_name;
		public int    p_partkey; 
		public String p_mfgr;		
		public String s_address;
		public String s_phone;
		public String s_comment;
		public float  ps_supplycost; // Needed for final comparison
		
		public void copyFromQCSN(QCSN_Record other)
		{
			//s_suppKey = other.s_suppKey; // not needed
			s_acctBal = other.s_acctBal;
			s_name    = other.s_name;
			//p_partkey = 0;
			//p_mfgr    = "";
			n_name    = other.n_name;
			s_address = other.s_address;
			s_phone   = other.s_phone;
			s_comment = other.s_comment;
		}
		
		public void Parse(String data)
		{
			//s_suppKey   = Integer.parseInt(data.substring(0,   10).trim());
			s_acctBal   = Float.parseFloat(data.substring(0,  21).trim());
			s_name      =                  data.substring(22,  46).trim();
			n_name      =                  data.substring(47,  61).trim();
			p_partkey   = Integer.parseInt(data.substring(62,  72).trim());
			p_mfgr      =                  data.substring(73,  87).trim();
			s_address   =                  data.substring(88,  137).trim();
			s_phone     =                  data.substring(138, 167).trim();			
			s_comment   =                  data.substring(168, 287).trim();		
			ps_supplycost = Float.parseFloat(data.substring(288, 309).trim());
		}
		
		public String Write()
		{
			String data = "";
			//data += String.format("%1$-11d", s_suppKey);
			data += String.format("%1$-22f", s_acctBal);
			data += String.format("%1$-25c", s_name.toCharArray());
			data += String.format("%1$-15c", n_name.toCharArray());
			data += String.format("%1$-11d", p_partkey);
			data += String.format("%1$-15c", p_mfgr.toCharArray());			
			data += String.format("%1$-50c", s_address.toCharArray());
			data += String.format("%1$-30c", s_phone.toCharArray());			
			data += String.format("%1$-20c", s_comment.toCharArray());
			data += String.format("%1$-22f", ps_supplycost);
						
			return data;
		}
	}
	
	public class QCFinal_Page extends Page<QCFinal_Record>
	{
		public QCFinal_Record[] CreateArray(int n){ return new QCFinal_Record[n]; }
		public QCFinal_Record   CreateElement(){ return new QCFinal_Record(); }
	}
}
