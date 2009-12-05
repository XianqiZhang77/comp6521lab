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
		// r_name in RegionTable      [b-tree]
		BPlusTree< RegionPage, StringRecordElement > RegionNameIndex = new BPlusTree< RegionPage, StringRecordElement >();
		RegionNameIndex.CreateBPlusTree( RegionPage.class, StringRecordElement.class, 50, "Region.txt", "regionBTreeIndex.idx", "r_name");
		
		// n_regionKey in Nation      [b-tree]
		BPlusTree< NationPage, IntegerRecordElement > NationFKIndex = new BPlusTree< NationPage, IntegerRecordElement >();
		NationFKIndex.CreateBPlusTree( NationPage.class, IntegerRecordElement.class, "Nation.txt", "Nation_FK_tree.txt", "n_regionKey");

		// s_nationKey in Supplier    [b-tree]
		BPlusTree< SupplierPage, IntegerRecordElement > SupplierFKIndex = new BPlusTree< SupplierPage, IntegerRecordElement >();
		SupplierFKIndex.CreateBPlusTree( SupplierPage.class, IntegerRecordElement.class, "Supplier.txt", "Supplier_FK_tree.txt", "s_nationKey");

		// *** process query ***
		
		StringRecordElement searchKey = new StringRecordElement(15);	// initialise search key
		searchKey.setString(r_name);
		
		int[] regions = RegionNameIndex.Get(searchKey);	// lookup region record numbers
		Arrays.sort(regions);	// sort array using quick-sort
																										// record number to key processing function
		RecordNumberToKeyPF<RegionPage> recNumToKeyPF = new RecordNumberToKeyPF<RegionPage> (regions, RegionPage.class, "n_name", "regionKeys.tmp");
		DB.ProcessingLoop(recNumToKeyPF);	// process region keys
	}			
}
