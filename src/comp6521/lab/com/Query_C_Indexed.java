package comp6521.lab.com;

import java.util.ArrayList;
import java.util.Arrays;

import comp6521.lab.com.Query_C;
import comp6521.lab.com.Pages.*;
import comp6521.lab.com.Records.*;
import comp6521.lab.com.Util.DB;
import comp6521.lab.com.Util.ProcessingFunction;


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

		// s_nationKey in Supplier    [b-tree]
		BPlusTree< SupplierPage, IntegerRecordElement > SupplierFKIndex = new BPlusTree< SupplierPage, IntegerRecordElement >();
		SupplierFKIndex.CreateBPlusTree( SupplierPage.class, IntegerRecordElement.class, "Supplier.txt", "Supplier_FK_tree.txt", "s_nationKey");
		
		// ps_partKey in PartSupp     [b-tree]
		//int partsuppPageSize = MemoryManager.getInstance().GetPageSize( PartSuppPage.class );
		//BPlusTree< PartSuppPage, IntegerRecordElement > PartSuppFKIndex = new BPlusTree< PartSuppPage, IntegerRecordElement >();
		//PartSuppFKIndex.CreateBPlusTree( PartSuppPage.class, IntegerRecordElement.class, "PartSupp.txt", "PartSupp_FK_tree.txt", "ps_partKey");
		
		// p_size in Part             [b-tree] for no obvious reason
		BPlusTree< PartPage, IntegerRecordElement > PartSizeIndex = new BPlusTree< PartPage, IntegerRecordElement >();
		PartSizeIndex.CreateBPlusTree( PartPage.class, IntegerRecordElement.class, "Part.txt", "Part_Size_tree.txt", "p_size");
		
		// FIRST: 
		//  sel(size[PART]) X sel(good supps(ps))
		
		// Find list of selected suppliers
		// Region(s) with selName, -> nation(s) -> supplier list
		//String selSupps = "qc_i_selSupps.txt";
		//MemoryManager.getInstance().AddPageType(qci_SupplierPage.class, selSupps);
		//qci_SupplierPage selSuppliers = MemoryManager.getInstance().getEmptyPage(qci_SupplierPage.class);
		
		int[] regions   = null;
		
		// Find region(s)
		StringRecordElement RS = new StringRecordElement(50);
		RS.setString(regionNameSel);
		
		regions = RegionNameIndex.Get(RS);
		Arrays.sort(regions);
		
		RegionToNationPF RtNpf = new RegionToNationPF( regions, NationFKIndex, "r_regionKey" );
		// Get all record numbers from the nations that matched regions found & sort them
		int[] nations = DB.ProcessingLoop(RtNpf);
		
		// Find suppliers
		NationToSupplierPF NtSpf = new NationToSupplierPF( nations, SupplierFKIndex, "n_nationKey");
		// Get all record numbers from the suppliers that matched a nation found & sort them
		int[] suppliers = DB.ProcessingLoop(NtSpf);

		// Find all products that match the size requirement
		IntegerRecordElement partEL = new IntegerRecordElement();
		partEL.setInt( partSize );
		
		int[] parts = PartSizeIndex.Get(partEL);
		Arrays.sort(parts);
		
		//  Take the parts & the approved suppliers, and restrict that using partsupp..
		
		
		
		// SECOND:
		// find min supplycost of that product from the other list of suppliers
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
		
		
		
		
		
		
		
	
		// Step 1 : Find region(s) with name regionNameSel -> R(S) {save only r_regionKey}
		//          Find region(s) with name regionNameMin -> R(M) {save only r_regionKey}
		// Step 2 : Find nation(s) with n_regionKey in R(S) -> N(S) {save n_name, n_nationKey}
		//          Find nation(s) with n_regionKey in R(M) -> N(M) {save n_nationKey}
		// Step 3 : Find supplier(s) with s_nationKey in N(S) -> S(S) {save s_suppKey, s_acctbal, s_name, s_address, s_phone, s_comment}
		//          Find supplier(s) with s_nationKey in N(M) -> S(M) {save s_suppKey}
		// Step 4 : Find partsupp(s) with ps_suppKey in S(S) and p_size in condition ..
		//          Find partsupp(s) with that p_partKey
		 
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



class qci_Supplier extends Record
{
	public qci_Supplier()
	{
		AddElement( "s_suppKey", new IntegerRecordElement()   );
		AddElement( "s_acctBal", new FloatRecordElement()     );
		AddElement( "s_name",    new StringRecordElement(25)  );
		AddElement( "n_name",    new StringRecordElement(15)  );
		AddElement( "s_address", new StringRecordElement(50)  );
		AddElement( "s_phone",   new StringRecordElement(30)  );
		AddElement( "s_comment", new StringRecordElement(120) );
	}
}

class qci_SupplierPage extends Page<qci_Supplier>
{
	public qci_Supplier[] CreateArray(int n){ return new qci_Supplier[n]; }
	public qci_Supplier   CreateElement(){ return new qci_Supplier(); }
}

/*class QC_RS extends Record
{ 
	public QC_RS(){ AddElement( "r_regionKey", new IntegerRecordElement() ); } 
}
class QC_RSP extends Page<QC_RS>
{
	public QC_RSP() { super(); m_nbRecordsPerPage = 100;	}
	public QC_RS[] CreateArray(int n){ return new QC_RS[n]; }
	public QC_RS   CreateElement(){ return new QC_RS(); }
}

class QC_NS extends Record
{ 
	public QC_NS(){ AddElement("n_name", new StringRecordElement(15)); AddElement("n_nationKey", new IntegerRecordElement());}
}
class QC_NSP extends Page<QC_NS>
{
	public QC_NSP() { super(); m_nbRecordsPerPage = 100;	}
	public QC_NS[] CreateArray(int n){ return new QC_NS[n]; }
	public QC_NS   CreateElement(){ return new QC_NS(); }
}

class QC_SS extends Record
{
	public QC_SS()
	{
		AddElement( "s_suppKey", new IntegerRecordElement()  );
		AddElement( "s_acctBal", new FloatRecordElement()    );
		AddElement( "s_name",    new StringRecordElement(25) );
		AddElement( "s_address", new StringRecordElement(50) );
		AddElement( "s_phone",   new StringRecordElement(30) );
		AddElement( "s_comment", new StringRecordElement(120));
	}
}
class QC_SSP extends Page<QC_SS>
{
	public QC_SS[] CreateArray(int n){ return new QC_SS[n]; }
	public QC_SS   CreateElement(){ return new QC_SS(); }	
}*/
