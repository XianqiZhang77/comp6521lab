package comp6521.lab.com;

import java.util.Hashtable;

import comp6521.lab.com.Pages.*;
import comp6521.lab.com.Pages.Page;
import comp6521.lab.com.Records.RecordElement;

public class Stats {

	// In general:
	// 1 - Nb pages
	// 2 - Nb records
	// 3 - Nb distinct values of one or multiple fields
	// 3.1 - min/max nb. values per distinct value
	// 4 - nb years for dates(?)
	
	public void GenerateStats()
	{
		System.out.println("-- Generating all stats -- ");
		CustomerStats();
		System.out.println("");
		LineItemStats();
		System.out.println("");
		NationStats();
		System.out.println("");
		OrdersStats();
		System.out.println("");
		PartStats();
		System.out.println("");
		PartSuppStats();
		System.out.println("");
		RegionStats();
		System.out.println("");
		SupplierStats();
	}
	
	public void CustomerStats()
	{
		// For query B
		// - Count nb. differing first two digits & distribution
		// - Count nb acct balances > 0 ** TODO **
		String[] attlist = new String[1];
		attlist[0] = new String("c_phone");
		
		StatsObject<?>[] statsObjects = new StatsObject[1];
		statsObjects[0] = new PhoneSubsetStatsObject();
		
		GenericStats( CustomerPage.class, attlist, statsObjects);
	}
	
	public void LineItemStats()
	{
		// For query A:
		// - Possibly count number of different dates, but that's unlikely to be useful. ** TODO ** ?
		// - Possibly nb. of years, since the best implementation would use radix-hash bins.
		String[] attlist = new String[1];
		attlist[0] = new String("l_receiptDate");
		
		StatsObject<?>[] statsObjects = new StatsObject[1];
		statsObjects[0] = new DateYearStatsObject();
		
		GenericStats( LineItemPage.class, attlist, statsObjects );
	}
	
	public void NationStats()
	{
		// For query C/D:
		// - Count nb. of nations per region. (i.e. distinct n_regionKey)
		String[] attlist = new String[1];
		attlist[0] = new String("n_regionKey");
		
		StatsObject<?>[] statsObjects = new StatsObject[1];
		statsObjects[0] = new KeyStatsObject();
		
		GenericStats( NationPage.class, attlist, statsObjects );
	}
	
	public void OrdersStats()
	{
		// For query Z:
		// - count nb. values per year / nb. different years
		// - count nb. orders per customer / distinct customers.
		String[] attlist = new String[2];
		attlist[0] = new String("o_orderDate");
		attlist[1] = new String("o_custKey");
		
		StatsObject<?>[] statsObjects = new StatsObject[2];
		statsObjects[0] = new DateYearStatsObject();
		statsObjects[1] = new KeyStatsObject();
		
		GenericStats( OrdersPage.class, attlist, statsObjects );
	}
	
	public void PartStats()
	{
		// For query C:
		// - count nb. sizes & distribution
		String[] attlist = new String[1];
		attlist[0] = new String("p_size");
		
		StatsObject<?>[] statsObjects = new StatsObject[1];
		statsObjects[0] = new KeyStatsObject();
		
		GenericStats( PartPage.class, attlist, statsObjects );
	}
	
	public void PartSuppStats()
	{
		// For query C/E:
		// - count nb. partsupps per supplier (i.e. distinct ps_suppkey)
		// - count nb. partsupps per product  (i.e. distinct ps_partKey);
		String[] attlist = new String[2];
		attlist[0] = new String("ps_partKey");
		attlist[1] = new String("ps_suppKey");
		
		StatsObject<?>[] statsObjects = new StatsObject[2];
		statsObjects[0] = new KeyStatsObject();
		statsObjects[1] = new KeyStatsObject();
		
		GenericStats( PartSuppPage.class, attlist, statsObjects );
	}
	
	public void RegionStats()
	{
		// For query C/D:
		// - Count nb different names (?) not likely to be useful
		String[] attlist = new String[0];
		StatsObject<?>[] statsObjects = new StatsObject[0];
		
		GenericStats( RegionPage.class, attlist, statsObjects );
	}
	
	public void SupplierStats()
	{
		// For query C/D:
		// - Count nb. suppliers per nation (i.e. distinct s_nationKey)
		String[] attlist = new String[1];
		attlist[0] = new String("s_nationKey");
		
		StatsObject<?>[] statsObjects = new StatsObject[1];
		statsObjects[0] = new KeyStatsObject();
		
		GenericStats( SupplierPage.class, attlist, statsObjects );
	}
	
	////////////////////////////////////////////////////////////
	// Generic stats extraction ////////////////////////////////
	////////////////////////////////////////////////////////////
	protected <T extends Page<?> > void GenericStats( Class<T> pageClass, String[] attributeList, StatsObject<?>[] statsObjects )
	{
		assert( attributeList.length == statsObjects.length );
		
		int NumberOfRecords = 0;
		int NumberOfPages   = 0;
		
		T page = null;
		int p = 0;
		
		while( (page = MemoryManager.getInstance().getPage(pageClass, p++)) != null )
		{
			for( int i = 0; i < page.m_records.length; i++ )
			{
				for( int att = 0; att < attributeList.length; att++ )
				{
					statsObjects[att].AddElement( page.m_records[i].get( attributeList[att] ) );
				}
			}
			
			NumberOfRecords += page.m_records.length;
			
			MemoryManager.getInstance().freePage(page);
		}
		
		NumberOfPages = p - 1; // because we stop when we get one page too far
		
		System.out.println("Relation name: " + pageClass.getName());
		System.out.println("Number of pages: " + NumberOfPages);
		System.out.println("Number of records: " + NumberOfRecords);
		
		for( int att = 0; att < attributeList.length; att++ )
		{
			System.out.println("Stats for attribute: " + attributeList[att] + " " + statsObjects[att].StatType());
			statsObjects[att].PrintStats();
		}
	}	
	
	////////////////////////////////////////////////////////////
	// Stats objects implementation                           //
	////////////////////////////////////////////////////////////
	public abstract class StatsObject<T>
	{
		protected Hashtable<T, Integer> m_stats;
		
		protected abstract T GetKeyFromElement( RecordElement el );
		
		public void AddElement( RecordElement el )
		{
			assert(el != null);
			T keyValue = GetKeyFromElement(el);
			Integer newValue = new Integer(1);
			
			if( m_stats.containsKey( keyValue ) )
				newValue = new Integer( m_stats.get(keyValue).intValue() + 1 );
				
			m_stats.put( keyValue, newValue );
		}
		
		public void PrintStats()
		{
			// Nb of distinct values
			// Min / max number of values per distinct values
			int min = Integer.MAX_VALUE;
			int max = Integer.MIN_VALUE;
			
			Integer[] values = m_stats.values().toArray( new Integer[0] );
			
			for( int i = 0; i < values.length; i++ )
			{
				if( values[i] < min )
					min = values[i];
				if( values[i] > max )
					max = values[i];
			}
			
			System.out.println("Number of distinct values: " + values.length);
			System.out.println("Minimum number of occurrences of a value: " + min);
			System.out.println("Maximum number of occurrences of a value: " + max);
		}
		
		public String StatType(){ return new String(""); }
	}
		
	public class KeyStatsObject extends StatsObject<Integer>
	{
		public KeyStatsObject()	{ m_stats = new Hashtable<Integer, Integer>(); }
		protected Integer GetKeyFromElement( RecordElement el ) { return new Integer( el.getInt() ); }
	}
	
	public class PhoneSubsetStatsObject extends StatsObject<String>
	{
		public PhoneSubsetStatsObject() { m_stats = new Hashtable<String, Integer>(); }
		protected String GetKeyFromElement( RecordElement el ) { return new String( el.getString().substring(0, 2) ); }
		public String StatType(){ return new String("(First two digits)"); }
	}
	
	public class DateYearStatsObject extends StatsObject<Integer>
	{
		public DateYearStatsObject() { m_stats = new Hashtable<Integer, Integer>(); }
		protected Integer GetKeyFromElement( RecordElement el ) { return new Integer( el.getDate().getYear() ); }
		public String StatType() { return new String("(grouped by year)"); }
	}
	
	
}
