package comp6521.lab.com;

import java.util.ArrayList;
import java.util.Arrays;

import comp6521.lab.com.Pages.NationPage;
import comp6521.lab.com.Pages.PartSuppPage;
import comp6521.lab.com.Pages.SupplierPage;
import comp6521.lab.com.Records.FloatRecordElement;
import comp6521.lab.com.Records.IntegerRecordElement;
import comp6521.lab.com.Records.Record;
import comp6521.lab.com.Records.StringRecordElement;
import comp6521.lab.com.Util.DB;
import comp6521.lab.com.Util.ProcessingFunction;
import comp6521.lab.com.Util.RecordNumberToKeyPF;
import comp6521.lab.com.Util.key_page;

public class Query_E_Indexed extends Query_E 
{
	public void ProcessQuery(String innerName, String outerName)
	{
		///////////////////////////
		// Zero : Create indexes //
		///////////////////////////
		// n_name in Nation table
		BPlusTree< NationPage, StringRecordElement > NationNameIndex = new BPlusTree< NationPage, StringRecordElement >();
		NationNameIndex.CreateBPlusTree( NationPage.class, StringRecordElement.class, 15, "Nation.txt", "Nation_Name_tree.txt", "n_name");
		
		// s_nationKey in Supplier (used in query C indexed)
		BPlusTree< SupplierPage, IntegerRecordElement > SupplierFKIndex = new BPlusTree< SupplierPage, IntegerRecordElement >();
		SupplierFKIndex.CreateBPlusTree( SupplierPage.class, IntegerRecordElement.class, "Supplier.txt", "Supplier_FK_tree.txt", "s_nationKey");
		
		// ps_suppKey in PartSupp, used in query C indexed
		BPlusTree< PartSuppPage, IntegerRecordElement > PartSuppSuppFKIndex = new BPlusTree< PartSuppPage, IntegerRecordElement >();
		PartSuppSuppFKIndex.CreateBPlusTree( PartSuppPage.class, IntegerRecordElement.class, "PartSupp.txt", "PartSupp_suppFK_tree.txt", "ps_suppKey");
		
		////////////////////////////
		// Perform query          //
		////////////////////////////
		// First, the inner query
		float totalValue = PerformSubquery(innerName, true, NationNameIndex, SupplierFKIndex, PartSuppSuppFKIndex);
		// Second, perform the outer query (grouping)
		PerformSubquery(outerName, false, NationNameIndex, SupplierFKIndex, PartSuppSuppFKIndex);		
		/////////////////
		// Perform sort//
		// ... TODO ...//
		/////////////////
		// Sort file qei_f
		//
		
		// Perform third pass
		MemoryManager.getInstance().AddPageType( QEGroups_Page.class, "qeig_f.txt" );
		ThirdPass( totalValue, "qei_f.txt", "qeig_f.txt" );
		
		// Fourth pass: sort groups by value, descending order
		// ... TODO ...
		
		
		// Last : output results
		OutputResults( "qeig_f.txt" );
	}
	
	protected float PerformSubquery( String name, boolean isInner, BPlusTree<?,?> NationNameIndex, BPlusTree<?,?> SupplierFKIndex, BPlusTree<?,?> PartSuppSuppFKIndex )
	{
		// First, find the n_nationKey for the UNITED_STATES
		StringRecordElement NN = new StringRecordElement(15);
		NN.setString(name);
		
		String prefix = "tmp_";
		if( isInner )
			prefix += "inner_";
		else
			prefix += "outer_";
		
		int[] nations = NationNameIndex.Get(NN);
		Arrays.sort(nations);
		
		// Record number -> n_nationKey
		RecordNumberToKeyPF<NationPage> NtNKpf = new RecordNumberToKeyPF<NationPage>(nations, NationPage.class, "n_nationKey", prefix + "nation_keys.txt");
		DB.ProcessingLoop(NtNKpf);
		// ATTN:: NtNKpf is not freed yet.
		
		// Second, find all suppliers in the united states
		// nation key(s) -> suppliers record numbers
		int[] suppliersRN = DB.ReverseProcessingLoop( NtNKpf.keys, key_page.class, SupplierFKIndex, "key");
		// supplier record numbers -> supplier keys
		RecordNumberToKeyPF<SupplierPage> StSKpf = new RecordNumberToKeyPF<SupplierPage>(suppliersRN, SupplierPage.class, "s_suppKey", prefix + "supp_keys.txt");
		DB.ProcessingLoop(StSKpf);
		// ATTN:: StSKpf is not free yet
		// supplier keys -> ps record numbers
		int[] psRN = DB.ReverseProcessingLoop( StSKpf.keys , key_page.class, PartSuppSuppFKIndex, "key");
		Arrays.sort(psRN);
		
		// Once we have the matching record numbers, we can free the pages we were taking.
		NtNKpf.Clear(false);
		StSKpf.Clear(false);
		
		// Third : Pre-compute the total value && write kept values --> qei_f.txt
		PartSuppToTotalPrice PStTPpf = new PartSuppToTotalPrice( psRN, isInner );
		DB.ProcessingLoop( PStTPpf );
		
		return PStTPpf.totalValue;
	}
}

class PartSuppToTotalPrice extends ProcessingFunction<PartSuppPage, FloatRecordElement>
{
	boolean isInner;
	QE_Page page;
	public float totalValue;
	
	public PartSuppToTotalPrice( int[] input, boolean isInner ) 
	{ 
		super( input, PartSuppPage.class ); 
		
		if( !isInner )
		{
			// Create new page type, create an empty page.
			MemoryManager.getInstance().AddPageType( QE_Page.class, "qei_f.txt" );
			page = MemoryManager.getInstance().getEmptyPage( QE_Page.class, "qei_f.txt");
		}
	}	
	
	public void  ProcessStart()      { totalValue = 0; }
	public void  Process( Record r ) 
	{ 
		float value = r.get("ps_supplyCost").getFloat() * r.get("ps_availQty").getFloat();
		
		if( !isInner )
		{
			// Create new record
			QE_Record qe = new QE_Record();
			qe.get("ps_partKey").set( r.get("ps_partKey"));
			qe.get("value").setFloat(value);
			page.AddRecord(qe);
		}
		
		totalValue +=  value;
	}
	public int[] EndProcess()        
	{ 
		if( !isInner )
			MemoryManager.getInstance().freePage(page);
		return null; 
	}	
}
