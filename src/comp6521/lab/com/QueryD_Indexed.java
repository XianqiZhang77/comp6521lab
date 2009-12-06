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

		NationToSupplierPF nationToSuppPF = new NationToSupplierPF(nations, SupplierFKIndex, "n_nationKey");	// get supplier record #s for nations
		int[] suppliers = DB.ProcessingLoop(nationToSuppPF);
		
		System.out.println("TEST");
	}			
}