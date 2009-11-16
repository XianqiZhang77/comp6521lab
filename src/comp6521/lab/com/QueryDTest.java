/**
 * November: 16, 2009
 * QueryDTest.java: QueryD test class.
 */
package comp6521.lab.com;

import comp6521.lab.com.Pages.NationPage;
import comp6521.lab.com.Pages.RegionPage;
import comp6521.lab.com.Pages.SupplierPage;

/**
 * @author dimitri.tiago
 *
 */
public class QueryDTest 
{

	public static void main(String[] args) 
	{
		// set data source path
		PageManagerSingleton myPageManager = PageManagerSingleton.getInstance();
		myPageManager.setPath("Y:\\Dimitri\\Concordia\\Comp6521_AdvancedDatabaseSystemsAndTheory\\Lab\\ShortData\\");
		
		// set default page types
		MemoryManager myMemoryManager = MemoryManager.getInstance();
		myMemoryManager.SetPageFile( RegionPage.class,   "Region.txt" );
		myMemoryManager.SetPageFile( NationPage.class,   "Nation.txt" );
		myMemoryManager.SetPageFile( SupplierPage.class, "Supplier.txt" );
		
		
		// execute query d
		Query_D queryD = new Query_D();
		queryD.ProcessQuery("ASIA");
	}

}
