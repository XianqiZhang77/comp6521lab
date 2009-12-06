package comp6521.lab.com;

import java.util.ArrayList;
import java.util.Arrays;

import comp6521.lab.com.Query_C;
import comp6521.lab.com.Pages.*;
import comp6521.lab.com.Records.*;
import comp6521.lab.com.Util.DB;
import comp6521.lab.com.Util.ProcessingFunction;
import comp6521.lab.com.Util.RecordNumberToKeyPF;
import comp6521.lab.com.Util.key_page;


public class Query_C_Indexed extends Query_C 
{
	public void ProcessQuery( int partSize, String regionNameSel, String regionNameMin )
	{
		Log.StartLog("c_i.out");
		///////////////////////////////////////////////////////////////////////
		// Here we'll use a few indexes:
		// r_name in RegionTable      [b-tree]
		BPlusTree< RegionPage, StringRecordElement > RegionNameIndex = IndexManager.getInstance().getRegionNameIndex();
		// n_regionKey in Nation      [b-tree]
		BPlusTree< NationPage, IntegerRecordElement > NationFKIndex = IndexManager.getInstance().getNationFKIndex();
		// n_nationKey in Nation      [b-tree]
		BPlusTree< NationPage, IntegerRecordElement > NationPKIndex = IndexManager.getInstance().getNationPKIndex();
		// s_nationKey in Supplier    [b-tree]
		BPlusTree< SupplierPage, IntegerRecordElement > SupplierFKIndex = IndexManager.getInstance().getSupplierFKIndex();
		// s_suppKey in Supplier    [b-tree]
		BPlusTree< SupplierPage, IntegerRecordElement > SupplierPKIndex = IndexManager.getInstance().getSupplierPKIndex();
		// ps_partKey in PartSupp     [b-tree]
		BPlusTree< PartSuppPage, IntegerRecordElement > PartSuppPartFKIndex = IndexManager.getInstance().getPartSuppPartFKIndex();
		// ps_suppKey in PartSupp     [b-tree]
		BPlusTree< PartSuppPage, IntegerRecordElement > PartSuppSuppFKIndex = IndexManager.getInstance().getPartSuppSuppFKIndex();
		// p_size in Part             [b-tree] for no obvious reason
		BPlusTree< PartPage, IntegerRecordElement > PartSizeIndex = IndexManager.getInstance().getPartSizeIndex();
		// p_partKey in Part
		BPlusTree< PartPage, IntegerRecordElement > PartPKIndex = IndexManager.getInstance().getPartPKIndex();
		
		///////////////////////////////////////////////////////////////////////
		// FIRST: 
		// Find list of selected suppliers (region -> nation -> suppliers )
		// Find region(s)
		StringRecordElement RS = new StringRecordElement(50);
		RS.setString(regionNameSel);
		
		Log.StartLogSection("Getting all regions matching the given name: " + regionNameSel);
		int[] regions = RegionNameIndex.Get(RS);
		Log.EndLogSection();
		Arrays.sort(regions);
		
		RegionToNationPF RtNpf = new RegionToNationPF( regions, NationFKIndex, "r_regionKey" );
		// Get all record numbers from the nations that matched regions found & sort them
		Log.StartLogSection("Getting all record numbers from the Nations table that match the region key found");
		int[] nations = DB.ProcessingLoop(RtNpf);
		Log.EndLogSection();
		
		// Find suppliers
		NationToSupplierPF NtSpf = new NationToSupplierPF( nations, SupplierFKIndex, "n_nationKey");
		// Get all record numbers from the suppliers that matched a nation found & sort them
		Log.StartLogSection("Getting all RN from the suppliers that matched a nation found");
		int[] suppliers = DB.ProcessingLoop(NtSpf);
		Log.EndLogSection();
		
		// Suppliers record number -> s_suppKey
		RecordNumberToKeyPF<SupplierPage> StSKpf = new RecordNumberToKeyPF<SupplierPage>(suppliers, SupplierPage.class, "s_suppKey", "supp_keys.tmp");
		Log.StartLogSection("Getting all suppliers keys (s_suppKey) from the RN");
		DB.ProcessingLoop(StSKpf);
		Log.EndLogSection();
		// ATTN :: StSKpf is not freed right now, check the Clear() method a few lines down

		///////////////////////////////////////////////////////////////////////
    	// SECOND:
		// find suppliers from which we'll check the minimum cost
		RS.setString(regionNameMin);
		Log.StartLogSection("Getting the region(s) RN matching the name " + regionNameMin + " for the min price selection");
		int[] min_regions = RegionNameIndex.Get(RS);
		Log.EndLogSection();
		Arrays.sort(min_regions);
		
		RegionToNationPF min_RtNpf = new RegionToNationPF( min_regions, NationFKIndex, "r_regionKey" );
		// Get all record numbers from the nations that matched regions found & sort them
		Log.StartLogSection("Getting the nations RN that matched the found region(s) key");
		int[] min_nations = DB.ProcessingLoop(min_RtNpf);
		Log.EndLogSection();
		
		// Find suppliers
		NationToSupplierPF min_NtSpf = new NationToSupplierPF( min_nations, SupplierFKIndex, "n_nationKey");
		// Get all record numbers from the suppliers that matched a nation found & sort them
		Log.StartLogSection("Getting all suppliers RN that matched a nation key");
		int[] min_suppliers = DB.ProcessingLoop(min_NtSpf);
		Log.EndLogSection();
		
		// Suppliers record number -> s_suppKey
		RecordNumberToKeyPF<SupplierPage> min_StSKpf = new RecordNumberToKeyPF<SupplierPage>(min_suppliers, SupplierPage.class, "s_suppKey", "min_supp_keys.tmp");
		Log.StartLogSection("Getting all suppliers keys (s_suppKey) from the RN");
		DB.ProcessingLoop(min_StSKpf);
		Log.EndLogSection();
		// ATTN :: min_StSKpf is not freed right now, check the Clear() method a few lines down
		
		// THIRD:
		// Find all products that match the size requirement
		IntegerRecordElement partEL = new IntegerRecordElement();
		partEL.setInt( partSize );
		
		Log.StartLogSection("Find all products RN with a size of " + partSize );
		int[] parts = PartSizeIndex.Get(partEL);
		Log.EndLogSection();
		Arrays.sort(parts);
		
		// parts record number -> p_partKey
		RecordNumberToKeyPF<PartPage > PRtPKpf = new RecordNumberToKeyPF<PartPage>(parts, PartPage.class, "p_partKey", "part_keys.tmp");
		Log.StartLogSection("Getting all part keys (p_partKey) from the RN");
		DB.ProcessingLoop(PRtPKpf);
		Log.EndLogSection();
		// ATTN: the PRtPKpf PF is not free right now, check the Clear() method a few lines down.
		
		// FOURTH:
		// For each part we have, lookup in the partsupp table.
		// -- From that, we can check the prices
		// 
		// Intersect ps_suppKey with the ones we found ..
		// part PK -> ps record numbers
		Log.StartLogSection("Getting all partSupp RN that match the parts key (ps_partKey == p_partKey)");
		ArrayList<ArrayList<Integer> > psPartsRN = DB.ReverseProcessingLoopAAI( PRtPKpf.keys, key_page.class, PartSuppPartFKIndex, "key");
		Log.EndLogSection();
		// suppliers PK -> ps record numbers
		Log.StartLogSection("Getting all partSupp RN that match the supplier keys (ps_suppKey == s_suppKey)");
		ArrayList<Integer> psSuppliersRN = DB.ReverseProcessingLoopAI( StSKpf.keys , key_page.class, PartSuppSuppFKIndex, "key");
		Log.EndLogSection();
		// min_suppliers PK -> ps record numbers
		Log.StartLogSection("Getting all partSupp RN that match the minimum selection supplier keys (ps_suppKey == s_suppKey)");
		ArrayList<Integer> psMinSuppliersRN = DB.ReverseProcessingLoopAI( min_StSKpf.keys , key_page.class, PartSuppSuppFKIndex, "key");
		Log.EndLogSection();
		
		// Once we have the matching record numbers, we can free the pages we were taking
		// false here is to make sure we don't write the last page.. it's not needed anyways
		PRtPKpf.Clear(false);
		StSKpf.Clear(false);
		min_StSKpf.Clear(false);
		
		// Use an object that'll keep track of taken pages correctly, in order to minimize IO if possible
		SupplierToOutputPF StOpf = new SupplierToOutputPF( PartPKIndex, SupplierPKIndex, NationPKIndex);
		
		//Output header -- results will be printed in the StOpf processing loop
		Log.SetResultHeader( "s_acctbal\ts_name\tn_name\tp_partkey\tp_mfgr\ts_address\ts_phone\ts_comment");
		
		// Now we can intersect the record numbers 
		Log.StartLogSection("Get all needed information for the results & output");
		for( int i = 0; i < psPartsRN.size(); i++ )
		{
			ArrayList<Integer> PartsSuppliers = DB.Intersect( psPartsRN.get(i), psSuppliersRN );
			ArrayList<Integer> PartsMinSuppliers = DB.Intersect( psPartsRN.get(i), psMinSuppliersRN);
			
			if(PartsSuppliers.size() > 0 && PartsMinSuppliers.size() > 0)
			{				
				// We now have the exact record numbers we need to look at
				MinSuppliersToMinPricePF MStMPpf = new MinSuppliersToMinPricePF(PartsMinSuppliers);
				Log.StartLogSection("Find minimum price in the min. suppliers");
				DB.ProcessingLoop(MStMPpf);
				Log.EndLogSection();
				double minPrice = MStMPpf.minPrice;
				
				StOpf.Reset( PartsSuppliers, minPrice );
				Log.StartLogSection("Get result data & output if constraints are met");
				DB.ProcessingLoop(StOpf);
				Log.EndLogSection();
			}
		}
		Log.EndLogSection();
		
		StOpf.Clear();
		
		Log.EndLog();
	}
}

// --- Implementation classes ---
class RegionToNationPF extends ProcessingFunction<RegionPage, IntegerRecordElement>
{
	public RegionToNationPF(int[] a, BPlusTree<?,?> idx, String _key)
	{
		super(a, RegionPage.class, idx, IntegerRecordElement.class, _key);
	}
}

class NationToSupplierPF extends ProcessingFunction<NationPage, IntegerRecordElement>
{
	public NationToSupplierPF(int[] a, BPlusTree<?,?> idx, String _key)
	{
		super(a, NationPage.class, idx, IntegerRecordElement.class, _key);
	}
}

class MinSuppliersToMinPricePF extends ProcessingFunction<PartSuppPage, FloatRecordElement>
{
	public double minPrice;
	
	public MinSuppliersToMinPricePF( ArrayList<Integer> input )
	{
		super();
		int[] inputArray = new int[input.size()];
		for( int i = 0; i < inputArray.length; i++ )
			inputArray[i] = input.get(i).intValue();
		
		Init( inputArray, PartSuppPage.class );
	}
	
	public void ProcessStart()
	{
		minPrice = Double.MAX_VALUE;	
	}
	
	public void Process( Record r )
	{
		double rprice = r.get("ps_supplyCost").getFloat(); 
		if( rprice < minPrice )
			minPrice = rprice;
	}
	
	public int[] EndProcess()
	{
		return null;
	}
}

class SupplierToOutputPF extends ProcessingFunction<PartSuppPage, IntegerRecordElement>
{
	BPlusTree<?,?> partIndex;
	BPlusTree<?,?> suppIndex;
	BPlusTree<?,?> nationIndex;
	double minPrice;
	
	PartPage partpage;
	SupplierPage supppage;
	NationPage nationpage;
	
	int partpagesize;
	int supppagesize;
	int nationpagesize;
	
	public SupplierToOutputPF( BPlusTree<?,?> PartIndex, BPlusTree<?,?> SupplierIndex, BPlusTree<?,?> NationIndex )
	{
		super();
		partIndex = PartIndex;
		suppIndex = SupplierIndex;
		nationIndex = NationIndex;
		
		partpage = null;
		supppage = null;
		nationpage = null;
		
		partpagesize = MemoryManager.getInstance().GetNumberOfRecordsPerPage( PartPage.class );
		supppagesize = MemoryManager.getInstance().GetNumberOfRecordsPerPage( SupplierPage.class );
		nationpagesize = MemoryManager.getInstance().GetNumberOfRecordsPerPage( NationPage.class );
	}
	
	public void Reset( ArrayList<Integer> input, double _minPrice )
	{
		int[] inputArray = new int[input.size()];
		for( int i = 0; i < inputArray.length; i++ )
			inputArray[i] = input.get(i).intValue();
		
		Init( inputArray, PartSuppPage.class );
		
		minPrice = _minPrice;
	}
	
	public void Clear()
	{
		if( partpage != null )
		{
			MemoryManager.getInstance().freePage(partpage);
			partpage = null;
		}
		
		if( supppage != null )
		{
			MemoryManager.getInstance().freePage(supppage);
			supppage = null;
		}
		
		if( nationpage != null )
		{
			MemoryManager.getInstance().freePage(nationpage);
			nationpage = null;
		}
	}
	
	public void ProcessStart() {}
	
	public void Process( Record r )
	{
		double price = r.get("ps_supplyCost").getFloat();
		
		if( price == minPrice )
			{
				// Output result
				int[] partRN = partIndex.Get( r.get("ps_partKey") );
				int[] suppRN = suppIndex.Get( r.get("ps_suppKey") );
				
				// Sanity check
				if( partRN.length != 1 || suppRN.length != 1)
					System.out.println("Sanity check failed -- PK failure");

				int partP = partRN[0] / partpagesize;
				int partR = partRN[0] % partpagesize;
				
				if( partpage == null || partpage.m_pageNumber != partP )
				{
					if( partpage != null )
						MemoryManager.getInstance().freePage(partpage);
					partpage = MemoryManager.getInstance().getPage(PartPage.class, partP);
				}
				
				PartRecord part = partpage.m_records[partR];
				
				int suppP = suppRN[0] / supppagesize;
				int suppR = suppRN[0] % supppagesize;
				
				if( supppage == null || supppage.m_pageNumber != suppP )
				{
					if( supppage != null )
						MemoryManager.getInstance().freePage(supppage);
					supppage = MemoryManager.getInstance().getPage(SupplierPage.class, suppP);
				}
				
				SupplierRecord supplier = supppage.m_records[suppR];
				
				int[] nationRN = nationIndex.Get( supplier.get("s_nationKey") );
				
				// Sanity check
				if( nationRN.length != 1 )
					System.out.println("Sanity check failed");
				
				int nationP = nationRN[0] / nationpagesize;
				int nationR = nationRN[0] % nationpagesize;

				if( nationpage == null || nationpage.m_pageNumber != nationP )
				{
					if( nationpage != null )
						MemoryManager.getInstance().freePage(nationpage);
					nationpage = MemoryManager.getInstance().getPage(NationPage.class, nationP);
				}
				
				NationRecord nation = nationpage.m_records[nationR];
				
				// Output 
				System.out.println( supplier.get("s_acctBal").getFloat()  + "\t" + 
						            supplier.get("s_name").getString()    + "\t" +
						            nation.get("n_name").getString()      + "\t" +
						            part.get("p_partKey").getInt()        + "\t" +
						            part.get("p_mfgr").getString()        + "\t" +
						            supplier.get("s_address").getString() + "\t" +
						            supplier.get("s_phone").getString()   + "\t" +
						            supplier.get("s_comment").getString()         );
			}
	}
}
