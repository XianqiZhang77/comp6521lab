/**
 * 
 */
package comp6521.lab.com;

import java.util.Arrays;

import comp6521.lab.com.Pages.NationPage;
import comp6521.lab.com.Pages.Page;
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
		Log.StartLog("d_i.out");
		
		// *** build indexes ***
		// r_name in RegionTable      [b-tree]
		BPlusTree< RegionPage, StringRecordElement > RegionNameIndex = IndexManager.getInstance().getRegionNameIndex();
		
		// n_regionKey in Nation      [b-tree]
		BPlusTree< NationPage, IntegerRecordElement > NationFKIndex = IndexManager.getInstance().getNationFKIndex();
		
		// s_nationKey in Supplier    [b-tree]
		BPlusTree< SupplierPage, IntegerRecordElement > SupplierFKIndex = IndexManager.getInstance().getSupplierFKIndex();
		
		// *** process query ***		
		StringRecordElement searchKey = new StringRecordElement(50);	// initialise search key
		searchKey.setString(r_name);
		
		Log.StartLogSection("Getting all regions with name: " + r_name);
		int[] regions = RegionNameIndex.Get(searchKey);	// lookup region record numbers
		Log.EndLogSection();
		Arrays.sort(regions);	// sort array using quick-sort
		
		RegionToNationPF regionToNationPF = new RegionToNationPF(regions, NationFKIndex, "r_regionKey");	// get nation record #s for regions
		Log.StartLogSection("Getting all nations from the regions found");
		int[] nations = DB.ProcessingLoop(regionToNationPF);
		Log.EndLogSection();
		Arrays.sort(nations);	// sort nations
		
		// *** Get all nations, save (n_nationKey, n_name) to file
		NationRNToFilePF nationRNtoFilePF = new NationRNToFilePF(nations, "nation_subset.tmp");
		Log.StartLogSection("Save the subset of nations needed to file (n_nationKey, n_name) to sort");
		DB.ProcessingLoop(nationRNtoFilePF);
		Log.EndLogSection();
		
		// *** Sort nation subset file using TPMMS
		TPMMS<QDNationSubsetPage> nationSort = new TPMMS<QDNationSubsetPage>(QDNationSubsetPage.class, "nation_subset.tmp");
		Log.StartLogSection("Sorting the nation subset");
		String sortedNationFilename = nationSort.Execute();
		Log.EndLogSection();
		
		// *** Output results ***
		Log.SetResultHeader("s_acctbal\ts_name\tn_name\ts_address\ts_phone\ts_comment");
		NationSubsetPF nationRecordsPF = new NationSubsetPF(SupplierFKIndex);
		Log.StartLogSection("For all nations ... ");
		DB.ProcessingLoopOnFile(QDNationSubsetPage.class, sortedNationFilename, nationRecordsPF);
		Log.EndLogSection();
		
		Log.EndLog();
	}			
}

class NationRNToFilePF extends ProcessingFunction<NationPage, IntegerRecordElement>
{
	String filename;
	Page<?> page;
	
	public NationRNToFilePF( int[] input, String _filename )
	{
		super(input, NationPage.class);
		filename = _filename;
	}
	
	public void ProcessStart()
	{
		MemoryManager.getInstance().AddPageType(QDNationSubsetPage.class, filename);
		page = MemoryManager.getInstance().getEmptyPage(QDNationSubsetPage.class, filename);
	}
	
	public void Process( Record r )
	{
		QDNationSubsetRecord qd = new QDNationSubsetRecord();
		qd.get("n_nationKey").set( r.get("n_nationKey") );
		qd.get("n_name").set( r.get("n_name"));
		page.AddRecord(qd);
	}
	
	public int[] EndProcess()
	{
		MemoryManager.getInstance().freePage(page);
		return null;
	}	
}

class NationSubsetPF extends ProcessingFunction<NationPage, DateRecordElement>
{
	BPlusTree< SupplierPage, IntegerRecordElement > suppIdx;	// supplier b+ tree index
	
	// constructor
	public NationSubsetPF( BPlusTree< SupplierPage, IntegerRecordElement > suppIdx )
	{
		// initialise
		super( new int[0], NationPage.class );	
		this.suppIdx = suppIdx;				 
	}
	
	// start process
	public void ProcessStart()
	{
		// no-op
	}
	
	// process method
	public void Process( Record r )
	{
		int[] suppliers = suppIdx.Get( r.get("n_nationKey") );
		Arrays.sort(suppliers);	// sort suppliers	
		
		QueryDIdxOutputPF output = new QueryDIdxOutputPF(suppliers, r.get("n_name").getString());	// output nation-supplier join
		Log.StartLogSection("Output all suppliers");
		DB.ProcessingLoop(output);
		Log.EndLogSection();
	}
	
	// end process
	public int[] EndProcess()
	{
		// no-op
		return null;
	}
}

//get the nation records indexed
class QueryDIdxOutputPF extends ProcessingFunction<SupplierPage, IntegerRecordElement>
{
	String nationName;
	
	// constructor
	public QueryDIdxOutputPF(int[] input, String _nationName)
	{
		// initialise
		super( input, SupplierPage.class );	
		nationName = _nationName;
	}
	
	// start process
	public void ProcessStart()
	{
	}
	
	// process method
	public void Process( Record r )
	{
		Log.AddResult( r.get("s_acctBal").getFloat()  + "\t" +
				       r.get("s_name").getString()    + "\t" +
				       nationName                     + "\t" +
				       r.get("s_address").getString() + "\t" +
				       r.get("s_phone").getString()   + "\t" +
				       r.get("s_comment").getString()          );
	}
	
	// end process
	public int[] EndProcess()
	{
		return null;
	}
}