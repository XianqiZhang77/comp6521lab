/**
 * November 16, 2009
 * QueryFTest.java: Query F test class
 */
package comp6521.lab.com;

import comp6521.lab.com.Pages.LineItemPage;


/**
 * @author dimitri.tiago
 */
public class QueryFTest 
{
	public static void main(String[] args) 
	{
		// set data source path
		PageManagerSingleton myPageManager = PageManagerSingleton.getInstance();
		myPageManager.setPath("Y:\\Dimitri\\Concordia\\Comp6521_AdvancedDatabaseSystemsAndTheory\\Lab\\ShortData\\");
			
		// set default page types
		MemoryManager myMemoryManager = MemoryManager.getInstance();
		myMemoryManager.SetPageFile( LineItemPage.class, "LineItem.txt" );
		
		// build query argument list
		String[] arguments = new String[15];
		arguments[0] = "800" ;
		
		// execute query d
		Query_F queryF = new Query_F();
		queryF.ProcessQuery(arguments);
		
		
		
	}
}
