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
		///////////////////////////////////////////////////////////////////////
		// Here we'll use a few indexes:
		// r_name in RegionTable      [b-tree]
		BPlusTree< RegionPage, StringRecordElement > RegionNameIndex = new BPlusTree< RegionPage, StringRecordElement >();
		RegionNameIndex.CreateBPlusTree( RegionPage.class, StringRecordElement.class, 50, "Region.txt", "Region_rname_tree.txt", "r_name");
		
		// n_regionKey in Nation      [b-tree]
		BPlusTree< NationPage, IntegerRecordElement > NationFKIndex = new BPlusTree< NationPage, IntegerRecordElement >();
		NationFKIndex.CreateBPlusTree( NationPage.class, IntegerRecordElement.class, "Nation.txt", "Nation_FK_tree.txt", "n_regionKey");
		
		// n_nationKey in Nation      [b-tree]
		BPlusTree< NationPage, IntegerRecordElement > NationPKIndex = new BPlusTree< NationPage, IntegerRecordElement >();
		NationFKIndex.CreateBPlusTree( NationPage.class, IntegerRecordElement.class, "Nation.txt", "Nation_PK_tree.txt", "n_nationKey");		

		// s_nationKey in Supplier    [b-tree]
		BPlusTree< SupplierPage, IntegerRecordElement > SupplierFKIndex = new BPlusTree< SupplierPage, IntegerRecordElement >();
		SupplierFKIndex.CreateBPlusTree( SupplierPage.class, IntegerRecordElement.class, "Supplier.txt", "Supplier_FK_tree.txt", "s_nationKey");
		
		// s_suppKey in Supplier    [b-tree]
		BPlusTree< SupplierPage, IntegerRecordElement > SupplierPKIndex = new BPlusTree< SupplierPage, IntegerRecordElement >();
		SupplierPKIndex.CreateBPlusTree( SupplierPage.class, IntegerRecordElement.class, "Supplier.txt", "Supplier_PK_tree.txt", "s_suppKey");
		
		// ps_partKey in PartSupp     [b-tree]
		BPlusTree< PartSuppPage, IntegerRecordElement > PartSuppPartFKIndex = new BPlusTree< PartSuppPage, IntegerRecordElement >();
		PartSuppPartFKIndex.CreateBPlusTree( PartSuppPage.class, IntegerRecordElement.class, "PartSupp.txt", "PartSupp_FK_tree.txt", "ps_partKey");
		
		// ps_suppKey in PartSupp     [b-tree]
		BPlusTree< PartSuppPage, IntegerRecordElement > PartSuppSuppFKIndex = new BPlusTree< PartSuppPage, IntegerRecordElement >();
		PartSuppSuppFKIndex.CreateBPlusTree( PartSuppPage.class, IntegerRecordElement.class, "PartSupp.txt", "PartSupp_suppFK_tree.txt", "ps_suppKey");		
		
		// p_size in Part             [b-tree] for no obvious reason
		BPlusTree< PartPage, IntegerRecordElement > PartSizeIndex = new BPlusTree< PartPage, IntegerRecordElement >();
		PartSizeIndex.CreateBPlusTree( PartPage.class, IntegerRecordElement.class, "Part.txt", "Part_Size_tree.txt", "p_size");
		
		// p_partKey in Part
		BPlusTree< PartPage, IntegerRecordElement > PartPKIndex = new BPlusTree< PartPage, IntegerRecordElement >();
		PartPKIndex.CreateBPlusTree( PartPage.class, IntegerRecordElement.class, "Part.txt", "Part_PK_tree.txt", "p_partKey");
		
		///////////////////////////////////////////////////////////////////////
		// FIRST: 
		// Find list of selected suppliers (region -> nation -> suppliers )
		// Find region(s)
		StringRecordElement RS = new StringRecordElement(50);
		RS.setString(regionNameSel);
		
		int[] regions = RegionNameIndex.Get(RS);
		Arrays.sort(regions);
		
		RegionToNationPF RtNpf = new RegionToNationPF( regions, NationFKIndex, "r_regionKey" );
		// Get all record numbers from the nations that matched regions found & sort them
		int[] nations = DB.ProcessingLoop(RtNpf);
		
		// Find suppliers
		NationToSupplierPF NtSpf = new NationToSupplierPF( nations, SupplierFKIndex, "n_nationKey");
		// Get all record numbers from the suppliers that matched a nation found & sort them
		int[] suppliers = DB.ProcessingLoop(NtSpf);
		
		// Suppliers record number -> s_suppKey
		RecordNumberToKeyPF<SupplierPage> StSKpf = new RecordNumberToKeyPF<SupplierPage>(suppliers, SupplierPage.class, "s_suppKey", "tmp_supp_keys.txt");
		DB.ProcessingLoop(StSKpf);
		// ATTN :: StSKpf is not freed right now, check the Clear() method a few lines down

		///////////////////////////////////////////////////////////////////////
    	// SECOND:
		// find suppliers from which we'll check the minimum cost
		RS.setString(regionNameMin);
		int[] min_regions = RegionNameIndex.Get(RS);
		Arrays.sort(min_regions);
		
		RegionToNationPF min_RtNpf = new RegionToNationPF( min_regions, NationFKIndex, "r_regionKey" );
		// Get all record numbers from the nations that matched regions found & sort them
		int[] min_nations = DB.ProcessingLoop(min_RtNpf);
		
		// Find suppliers
		NationToSupplierPF min_NtSpf = new NationToSupplierPF( min_nations, SupplierFKIndex, "n_nationKey");
		// Get all record numbers from the suppliers that matched a nation found & sort them
		int[] min_suppliers = DB.ProcessingLoop(min_NtSpf);
		
		// Suppliers record number -> s_suppKey
		RecordNumberToKeyPF<SupplierPage> min_StSKpf = new RecordNumberToKeyPF<SupplierPage>(min_suppliers, SupplierPage.class, "s_suppKey", "tmp_min_supp_keys.txt");
		DB.ProcessingLoop(min_StSKpf);
		// ATTN :: min_StSKpf is not freed right now, check the Clear() method a few lines down
		
		// THIRD:
		// Find all products that match the size requirement
		IntegerRecordElement partEL = new IntegerRecordElement();
		partEL.setInt( partSize );
		
		int[] parts = PartSizeIndex.Get(partEL);
		Arrays.sort(parts);
		
		// parts record number -> p_partKey
		RecordNumberToKeyPF<PartPage > PRtPKpf = new RecordNumberToKeyPF<PartPage>(parts, PartPage.class, "p_partKey", "tmp_part_keys.txt");
		DB.ProcessingLoop(PRtPKpf);
		// ATTN: the PRtPKpf PF is not free right now, check the Clear() method a few lines down.
		
		// FOURTH:
		// For each part we have, lookup in the partsupp table.
		// -- From that, we can check the prices
		// 
		// Intersect ps_suppKey with the ones we found ..
		// part PK -> ps record numbers
		ArrayList<ArrayList<Integer> > psPartsRN = DB.ReverseProcessingLoopAAI( PRtPKpf.keys, key_page.class, PartSuppPartFKIndex, "key");
		// suppliers PK -> ps record numbers
		ArrayList<Integer> psSuppliersRN = DB.ReverseProcessingLoopAI( StSKpf.keys , key_page.class, PartSuppSuppFKIndex, "key");
		// min_suppliers PK -> ps record numbers
		ArrayList<Integer> psMinSuppliersRN = DB.ReverseProcessingLoopAI( min_StSKpf.keys , key_page.class, PartSuppSuppFKIndex, "key");
		
		// Once we have the matching record numbers, we can free the pages we were taking
		PRtPKpf.Clear();
		StSKpf.Clear();
		min_StSKpf.Clear();
	
		// Use an object that'll keep track of taken pages correctly, in order to minimize IO if possible
		SupplierToOutputPF StOpf = new SupplierToOutputPF( PartPKIndex, SupplierPKIndex, NationPKIndex);
		
		//Output header -- results will be printed in the StOpf processing loop
		System.out.println( "s_acctbal\ts_name\tn_name\tp_partkey\tp_mfgr\ts_address\ts_phone\ts_comment");
		
		// Now we can intersect the record numbers 
		for( int i = 0; i < psPartsRN.size(); i++ )
		{
			ArrayList<Integer> PartsSuppliers = DB.Intersect( psPartsRN.get(i), psSuppliersRN );
			ArrayList<Integer> PartsMinSuppliers = DB.Intersect( psPartsRN.get(i), psMinSuppliersRN);
			
			// We now have the exact record numbers we need to look at
			MinSuppliersToMinPricePF MStMPpf = new MinSuppliersToMinPricePF(PartsMinSuppliers);
			DB.ProcessingLoop(MStMPpf);			
			float minPrice = MStMPpf.minPrice;
			
			StOpf.Reset( PartsSuppliers, minPrice );
			DB.ProcessingLoop(StOpf);
		}
		
		StOpf.Clear();
		
		// REMINDER::
		// TODO DELETE :: tmp_part_keys.txt, tmp_supp_keys.txt, tmp_min_supp_keys.txt
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
	public float minPrice;
	
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
		minPrice = Float.MAX_VALUE;	
	}
	
	public void Process( Record r )
	{
		float rprice = r.get("ps_supplyCost").getFloat(); 
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
	float minPrice;
	
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
	
	public void Reset( ArrayList<Integer> input, float _minPrice )
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
		float price = r.get("ps_supplyCost").getFloat();
		
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
