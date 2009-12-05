/**
 * 
 */
package comp6521.lab.com;

import java.util.Arrays;

import comp6521.lab.com.Pages.NationPage;
import comp6521.lab.com.Pages.RegionPage;
import comp6521.lab.com.Pages.RegionSubsetPage;
import comp6521.lab.com.Pages.SupplierPage;
import comp6521.lab.com.Records.NationRecord;
import comp6521.lab.com.Records.RegionRecord;
import comp6521.lab.com.Records.RegionSubsetRecord;
import comp6521.lab.com.Records.SupplierRecord;

import comp6521.lab.com.Records.FloatRecordElement;
import comp6521.lab.com.Records.IntegerRecordElement;
import comp6521.lab.com.Records.StringRecordElement;

import comp6521.lab.com.Util.DB;
import comp6521.lab.com.Util.ProcessingFunction;
import comp6521.lab.com.Util.RecordNumberToKeyPF;
import comp6521.lab.com.Util.key_page;

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
		// *** build indexes ***
		
		// build b+ tree index on region.r_name
		BPlusTree<RegionPage, StringRecordElement> regionNameIndex = new BPlusTree<RegionPage, StringRecordElement>();	// instantiate b+ tree  
		regionNameIndex.CreateBPlusTree(RegionPage.class, StringRecordElement.class, "Region.txt", "regionBTreeIndex.idx", "r_name");
		
		// build b+ tree index on nation.n_nationKey
		BPlusTree<NationPage, StringRecordElement> nationNameIndex = new BPlusTree<NationPage, StringRecordElement>();	// instantiate b+ tree
		nationNameIndex.CreateBPlusTree(NationPage.class, StringRecordElement.class, "Nation.txt", "nationBTreeIndex.idx", "n_regionKey");
		
		// build b+ tree index on supplier.s_nationKey
		BPlusTree<SupplierPage, StringRecordElement> supNameIndex = new BPlusTree<SupplierPage, StringRecordElement>();	// instantiate b+ tree
		supNameIndex.CreateBPlusTree(SupplierPage.class, StringRecordElement.class, "Supplier.txt", "supBTreeIndex.idx", "s_nationKey");
		
		// *** process query ***
		
		StringRecordElement searchKey = new StringRecordElement(15);	// initialise search key
		searchKey.setString(r_name);
		
		int[] regions = regionNameIndex.Get(searchKey);	// lookup region record numbers
		Arrays.sort(regions);	// sort array using quick-sort
																										// record number to key processing function
		RecordNumberToKeyPF<RegionPage> recNumToKeyPF = new RecordNumberToKeyPF<RegionPage> (regions, RegionPage.class, "n_name", "regionKeys.tmp");
		DB.ProcessingLoop(recNumToKeyPF);	// process region keys
	}			
}
