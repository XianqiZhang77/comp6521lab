/**
 * November 16, 2009
 * QueryFTest.java: Query F test class
 */
package comp6521.lab.com;

import comp6521.lab.com.Pages.LineItemPage;
import comp6521.lab.com.Pages.PartPage;
import comp6521.lab.com.Pages.OrdersPage;


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
		myMemoryManager.SetPageFile( PartPage.class, "Part.txt" );
		myMemoryManager.SetPageFile( OrdersPage.class, "Orders.txt" );
		
		// build query argument list
		String[] arguments = new String[15];
		arguments[0]  = "800";			// lOrderKey
		arguments[1]  = "149";			// lPartKey
		arguments[2]  = "990";			// lSuppKey
		arguments[3]  = "990";			// lLineNumber
		arguments[4]  = "990";			// lQuantity
		arguments[5]  = "990";			// lExtendedPrice
		arguments[6]  = "990";			// lDiscount
		arguments[7]  = "A";			// lReturnFlag
		arguments[8]  = "A";			// lLineStatus
		arguments[9]  = "2009-10-25";	// lShipDate
		arguments[10] = "2009-10-25";	// lCommitDate
		arguments[11] = "2009-10-25";	// lReceiptDate
		arguments[12] = "AAA";			// lShipInstruct
		arguments[13] = "AAA";			// lShipMode
		arguments[14] = "AAA";			// lComment
				
		// execute query d
		Query_F queryF = new Query_F();
		queryF.ProcessQuery(arguments);
	}
}