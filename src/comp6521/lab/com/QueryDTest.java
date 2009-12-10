/**
 * November: 16, 2009
 * QueryDTest.java: QueryD test class.
 */
package comp6521.lab.com;

import java.sql.Date;

import comp6521.lab.com.Pages.CustomerPage;
import comp6521.lab.com.Pages.LineItemPage;
import comp6521.lab.com.Pages.NationPage;
import comp6521.lab.com.Pages.PartPage;
import comp6521.lab.com.Pages.PartSuppPage;
import comp6521.lab.com.Pages.RegionPage;
import comp6521.lab.com.Pages.SupplierPage;
import comp6521.lab.com.Records.DateRecordElement;

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

		myMemoryManager.SetPageFile( LineItemPage.class,   "LineItem.txt" );
		myMemoryManager.SetPageFile( RegionPage.class,     "Region.txt" );
		myMemoryManager.SetPageFile( NationPage.class,     "Nation.txt" );
		myMemoryManager.SetPageFile( SupplierPage.class,   "Supplier.txt" );
		myMemoryManager.SetPageFile( CustomerPage.class,   "Customer.txt" );
		myMemoryManager.SetPageFile( PartPage.class,       "Part.txt" );
		myMemoryManager.SetPageFile( PartSuppPage.class,   "PartSupp.txt");
		
		// execute query d
		//Query_D queryD = new Query_D();
		//queryD.ProcessQuery("ASIA");
		
		// execute query d indexed
		QueryD_Indexed queryD = new QueryD_Indexed();
		queryD.ProcessQuery("ASIA");
		
		//DateRecordElement myDRE = new DateRecordElement();
		//myDRE.Parse("08/25/1992 00:00:00");
		//Date startDate = myDRE.getDate();

		//myDRE.Parse("25/08/1992");
		//Date starDate = myDRE.getDate();
		
		//DateRecordElement myDDRE = new DateRecordElement();
		//myDDRE.Parse("07/14/1997 00:00:00");

		//String[] arg1 = {"24"};
		//String[] arg2 = {"10"};
		
		//Query_E queryE = new Query_E();
		//queryE.ProcessQuery("UNITED STATES", "UNITED STATES");
	}

}
