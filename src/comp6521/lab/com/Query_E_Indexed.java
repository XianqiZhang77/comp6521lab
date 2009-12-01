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
	public void ProcessQuery()
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
		
		// First, find the n_nationKey for the UNITED_STATES
		StringRecordElement NN = new StringRecordElement(15);
		NN.setString("UNITED STATES");
		
		int[] nations = NationNameIndex.Get(NN);
		Arrays.sort(nations);
		
		// Record number -> n_nationKey
		RecordNumberToKeyPF<NationPage> NtNKpf = new RecordNumberToKeyPF<NationPage>(nations, NationPage.class, "n_nationKey", "tmp_nation_keys.txt");
		DB.ProcessingLoop(NtNKpf);
		// ATTN:: NtNKpf is not freed yet.
		
		// Second, find all suppliers in the united states
		// nation key(s) -> suppliers record numbers
		int[] suppliersRN = DB.ReverseProcessingLoop( NtNKpf.keys, key_page.class, SupplierFKIndex, "key");
		// supplier record numbers -> supplier keys
		RecordNumberToKeyPF<SupplierPage> StSKpf = new RecordNumberToKeyPF<SupplierPage>(suppliersRN, SupplierPage.class, "s_suppKey", "tmp_supp_keys.txt");
		DB.ProcessingLoop(StSKpf);
		// ATTN:: StSKpf is not free yet
		// supplier keys -> ps record numbers
		ArrayList<ArrayList<Integer> > psSuppliersRN = DB.ReverseProcessingLoopAAI( StSKpf.keys , key_page.class, PartSuppSuppFKIndex, "key");
		
		// Once we have the matching record numbers, we can free the pages we were taking.
		NtNKpf.Clear(false);
		StSKpf.Clear(false);
		
		// Third : Pre-compute the total value
		// -> Collapse the array<array<int>> we got previously and sort to minimize I/O.
		int[] psRN = DB.CollapseAAI( psSuppliersRN );
		Arrays.sort(psRN);
		
		PartSuppToTotalPrice PStTPpf = new PartSuppToTotalPrice( psRN );
		DB.ProcessingLoop( PStTPpf );
		
		float totalValue = PStTPpf.totalValue;
		
		// Fourth: Create groups & cull them if needed (under the total sum value)
		PartSuppToGroup PSGpf = new PartSuppToGroup( totalValue );
		for( int i = 0; i < psSuppliersRN.size(); i++ )
		{
			PSGpf.Reset(psSuppliersRN.get(i));
			DB.ProcessingLoop(PSGpf);
		}
		
		// Write page to file .. or sort in memory if we're taking less than a page.
		// TODO !! 
		PSGpf.Clear();
		
		/////////////////
		// Perform sort//
		// ... TODO ...//
		/////////////////
		
		// And then output the results ....
		System.out.println("ps_partKey\tvalue");
		// ...
	}
}

class PartSuppToTotalPrice extends ProcessingFunction<PartSuppPage, FloatRecordElement>
{
	public float totalValue;
	
	public PartSuppToTotalPrice( int[] input ) { super( input, PartSuppPage.class ); }	
	public void  ProcessStart()      { totalValue = 0; }
	public void  Process( Record r ) { totalValue += r.get("ps_supplyCost").getFloat() * r.get("ps_availQty").getFloat(); }
	public int[] EndProcess()        { return null; }	
}

class PartSuppToGroup extends ProcessingFunction<PartSuppPage, FloatRecordElement>
{
	QE_Page page;
	int partKey;
	float value;
	float totalvalue;
	
	public PartSuppToGroup( float totalValue )
	{
		super();
		
		totalvalue = totalValue;
		
		// Create new page type, create an empty page.
		MemoryManager.getInstance().AddPageType( QE_Page.class, "qei_f.txt" );
		page = MemoryManager.getInstance().getEmptyPage( QE_Page.class, "qei_f.txt");
	}
	
	public void Reset( ArrayList<Integer> inputArray )
	{
		int[] input = new int[inputArray.size()];
		for(int i = 0; i < input.length; i++)
			input[i] = inputArray.get(i).intValue();
		
		Init( input, PartSuppPage.class );	
	}
	
	public void ProcessStart()
	{
		partKey = 0;
		value = 0;
	}
	
	public void Process( Record r )
	{
		partKey = r.get("ps_partKey").getInt();
		value   = r.get("ps_supplyCost").getFloat() * r.get("ps_availQty").getFloat();
	}
	
	public int[] EndProcess()
	{
		// Add a result
		if( value > totalvalue * 0.0001 )
		{
			QE_Record qe = new QE_Record();
			qe.get("ps_partKey").setInt(partKey);
			qe.get("value").setFloat(value);
			page.AddRecord(qe);
		}
		return null;
	}
	
	public void Clear()
	{
		MemoryManager.getInstance().freePage(page);
	}
}