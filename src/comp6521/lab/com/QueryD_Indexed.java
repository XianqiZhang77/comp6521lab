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
		MemoryManager.getInstance().AddPageType( QD_Page.class, "d.idx.tmp");	
		
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

		// *** join *** 
		NationSubsetPF nationRecordsPF = new NationSubsetPF(nations, SupplierFKIndex);
		DB.ProcessingLoop(nationRecordsPF);
		
		// *** sort using tpmms ***
		TPMMS<QD_Page> doTPMMS = new TPMMS<QD_Page>(QD_Page.class, "d.idx.tmp");
		String sortedFile = doTPMMS.Execute();	
		
		// invert sorting order of sorted file (i.e. ORDER BY n_name DESC)
		int numOfPages = MemoryManager.getInstance().GetNumberOfPages(QD_Page.class, sortedFile);	// get sorted number of pages
		
		MemoryManager.getInstance().AddPageType(QD_Page.class, "d.idx.out");	// output buffer
		QD_Page outputBuffer = MemoryManager.getInstance().getEmptyPage(QD_Page.class, "d.idx.out");	// output buffer
		
		QD_Page qDResultSetPage = null;	// input buffer
		
		for ( int i = (numOfPages - 1); i >= 0 ; i-- )	// invert sorting order and output
		{
			qDResultSetPage = MemoryManager.getInstance().getPage(QD_Page.class, i, sortedFile);	// get page in DESC order
			
			for ( int j = (qDResultSetPage.m_records.length - 1); j >= 0; j-- )	// invert sorting order of records
			{
				outputBuffer.AddRecord(qDResultSetPage.m_records[j]);	// get jth record
			}
			
			MemoryManager.getInstance().freePage(qDResultSetPage);	// free page
		}
		
		MemoryManager.getInstance().freePage(outputBuffer);	// free remaining contents of page
		
		PageManagerSingleton.getInstance().deleteTmpFiles();	// remove temporary files (i.e. intermediate results)
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
	int recordCount;							// recordCount
	StringBuffer strBuffer;						// output buffer
	
	QDNationSubsetRecord	nationSubsetRec;	// nation record to join
	QD_Page	page;								// output page
	
	// constructor
	public QueryDIdxOutputPF(int[] input, QDNationSubsetRecord qDNationSubsetRecord)
	{
		// initialise
		super( input, SupplierPage.class );	
		this.nationSubsetRec = qDNationSubsetRecord;
		
		strBuffer = new StringBuffer();
	}
	
	// start process
	public void ProcessStart()
	{
		// get empty page
		page = MemoryManager.getInstance().getEmptyPage(QD_Page.class, "d.idx.tmp");
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
			
		// page.AddRecord(qDOutput); // add to page
		
		// build output string
		if (recordCount < page.m_nbRecordsPerPage)
		{
			strBuffer.append(qDOutput.toString());	// add record string
			recordCount++;							// increment record count
		}
		else
		{
			PageManagerSingleton.getInstance().writePage("d.idx.tmp", strBuffer.toString());	// output record string (i.e. block)
			recordCount = 0;	// reset record count
			strBuffer.delete(0, strBuffer.length());
			strBuffer.append(qDOutput.toString());
			recordCount++;	
		}	
	}
	
	// end process
	public int[] EndProcess()
	{
		if (recordCount > 0)
		{
			PageManagerSingleton.getInstance().writePage("d.idx.tmp", strBuffer.toString());	// output record string (i.e. block)
			recordCount = 0;	// reset record count
			strBuffer.delete(0, strBuffer.length());
		}
		
		// free page
		MemoryManager.getInstance().freePage(page);
		return null;
	}
}