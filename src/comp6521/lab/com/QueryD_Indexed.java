/**
 * 
 */
package comp6521.lab.com;

import java.util.Arrays;

import comp6521.lab.com.Pages.NationPage;
import comp6521.lab.com.Pages.RegionPage;
import comp6521.lab.com.Pages.SupplierPage;

import comp6521.lab.com.Records.Record;
import comp6521.lab.com.Records.DateRecordElement;
import comp6521.lab.com.Records.IntegerRecordElement;
import comp6521.lab.com.Records.StringRecordElement;

import comp6521.lab.com.Util.DB;
import comp6521.lab.com.Util.ProcessingFunction;

/**
 * @author dimitri.tiago
 *
 */
public class QueryD_Indexed 
{
	// select s_acctbal, s_name, n_name, s_address, s_phone, s_comment
	// from region, nation, supplier
	// where s_nationKey = n_nationKey and n_regionKey = r_regionKey and r_name = ?
	// order by n_name DESC
	
	// execute query:
	public void ProcessQuery(String r_name)
	{
		// *** initialise memory manager ***
		MemoryManager.getInstance().AddPageType( QDNationSubsetPage.class, "qd_ns_i.txt");	// add page type
		MemoryManager.getInstance().AddPageType( QD_Page.class, "d.idx.out");	
		
		// *** build indexes ***
		// r_name in RegionTable      [b-tree]
		BPlusTree< RegionPage, StringRecordElement > RegionNameIndex = new BPlusTree< RegionPage, StringRecordElement >();
		RegionNameIndex.CreateBPlusTree( RegionPage.class, StringRecordElement.class, 50, "Region.txt", "r_nameBTreeIndex.txt", "r_name" );
		
		// n_regionKey in Nation      [b-tree]
		BPlusTree< NationPage, IntegerRecordElement > NationFKIndex = new BPlusTree< NationPage, IntegerRecordElement >();
		NationFKIndex.CreateBPlusTree( NationPage.class, IntegerRecordElement.class, "Nation.txt", "Nation_FK_tree.txt", "n_regionKey" );

		// n_nationKey in Nation    [b-tree]
		BPlusTree< NationPage, IntegerRecordElement > NationPKIndex = new BPlusTree< NationPage, IntegerRecordElement >();
		NationPKIndex.CreateBPlusTree( NationPage.class, IntegerRecordElement.class, "Nation.txt", "Nation_PK_tree.txt", "n_nationKey" );
		
		// s_nationKey in Supplier    [b-tree]
		BPlusTree< SupplierPage, IntegerRecordElement > SupplierFKIndex = new BPlusTree< SupplierPage, IntegerRecordElement >();
		SupplierFKIndex.CreateBPlusTree( SupplierPage.class, IntegerRecordElement.class, "Supplier.txt", "Supplier_FK_tree.txt", "s_nationKey" );
		
		// *** process query ***
		
		StringRecordElement searchKey = new StringRecordElement(50);	// initialise search key
		searchKey.setString(r_name);
		
		int[] regions = RegionNameIndex.Get(searchKey);	// lookup region record numbers
		Arrays.sort(regions);	// sort array using quick-sort
		
		RegionToNationPF regionToNationPF = new RegionToNationPF(regions, NationFKIndex, "r_regionKey");	// get nation record #s for regions
		int[] nations = DB.ProcessingLoop(regionToNationPF);
		Arrays.sort(nations);	// sort nations

		// *** join *** and output
		NationSubsetPF nationRecordsPF = new NationSubsetPF(nations, SupplierFKIndex);
		DB.ProcessingLoop(nationRecordsPF);
		
		System.out.println("TEST");
	}			
}

// get the nation records indexed
class NationSubsetPF extends ProcessingFunction<NationPage, DateRecordElement>
{
	// fields
	int recCount = 0;											// count of records processed
	int[] recordNumbers;											// array of record numbers
	
	QDNationSubsetPage page;									// nation subset page
	BPlusTree< SupplierPage, IntegerRecordElement > suppIdx;	// supplier b+ tree index
	
	// constructor
	public NationSubsetPF( int[] input, BPlusTree< SupplierPage, IntegerRecordElement > suppIdx )
	{
		// initialise
		super( input, NationPage.class );	
		this.suppIdx = suppIdx;				 
		this.recordNumbers = input;			
	}
	
	// start process
	public void ProcessStart()
	{
		// no-op
	}
	
	// process method
	public void Process( Record r )
	{
		QDNationSubsetRecord ns = new QDNationSubsetRecord();	// populate nation record
		ns.get("n_nationKey").set( r.get("n_nationKey") );
		ns.get("n_name").set( r.get("n_name") );
			
		int[] nation = {recordNumbers[recCount]};	// current nation record number
		recCount++;									// increment record count
		
		NationToSupplierPF nationToSuppPF = new NationToSupplierPF(nation, suppIdx, "n_nationKey");	// get supplier record #s for suppliers
		int[] suppliers = DB.ProcessingLoop(nationToSuppPF);
		Arrays.sort(suppliers);	// sort suppliers	
		
		QueryDIdxOutputPF output = new QueryDIdxOutputPF(suppliers, ns);	// output nation-supplier join
		DB.ProcessingLoop(output);
	}
	
	// end process
	public int[] EndProcess()
	{
		// no-op
		return null;
	}
}

//get the nation records indexed
class QueryDIdxOutputPF extends ProcessingFunction<SupplierPage, DateRecordElement>
{
	// fields
	QDNationSubsetRecord	nationSubsetRec;	// nation record to join
	QD_Page	page;								// output page
	
	// constructor
	public QueryDIdxOutputPF(int[] input, QDNationSubsetRecord qDNationSubsetRecord)
	{
		// initialise
		super( input, SupplierPage.class );	
		this.nationSubsetRec = qDNationSubsetRecord;
	}
	
	// start process
	public void ProcessStart()
	{
		// get empty page
		page = MemoryManager.getInstance().getEmptyPage(QD_Page.class, "d.idx.out");
	}
	
	// process method
	public void Process( Record r )
	{
		QD_Record qDOutput = new QD_Record();	// populate query d output record
		qDOutput.get("s_acctBal").set( r.get("s_acctBal") );
		qDOutput.get("s_name").set( r.get("s_name") );
		qDOutput.get("n_name").set( nationSubsetRec.get("n_name") );
		qDOutput.get("s_address").set( r.get("s_address") );
		qDOutput.get("s_phone").set( r.get("s_phone") );
		qDOutput.get("s_comment").set( r.get("s_comment") );
			
		page.AddRecord(qDOutput); // add to page
	}
	
	// end process
	public int[] EndProcess()
	{
		// free page
		MemoryManager.getInstance().freePage(page);
		return null;
	}
}