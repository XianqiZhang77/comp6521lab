/**
 * December 7, 2009
 * Application.java: db application gui. 
 */
package comp6521.lab.com.GUI;

import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import java.util.Date;

import comp6521.lab.com.IndexManager;
import comp6521.lab.com.MemoryManager;
import comp6521.lab.com.PageManagerSingleton;
import comp6521.lab.com.QueryD_Indexed;
import comp6521.lab.com.Query_A;
import comp6521.lab.com.Query_A_Indexed;
import comp6521.lab.com.Query_B;
import comp6521.lab.com.Query_B_Indexed;
import comp6521.lab.com.Query_C;
import comp6521.lab.com.Query_C_Indexed;
import comp6521.lab.com.Query_D;
import comp6521.lab.com.Query_E;
import comp6521.lab.com.Query_E_Indexed;
import comp6521.lab.com.Query_F;
import comp6521.lab.com.Query_F_Indexed;
import comp6521.lab.com.Query_Z;
import comp6521.lab.com.Query_Z_Indexed;
import comp6521.lab.com.Stats;
import comp6521.lab.com.Pages.CustomerPage;
import comp6521.lab.com.Pages.LineItemPage;
import comp6521.lab.com.Pages.NationPage;
import comp6521.lab.com.Pages.OrdersPage;
import comp6521.lab.com.Pages.PartPage;
import comp6521.lab.com.Pages.PartSuppPage;
import comp6521.lab.com.Pages.RegionPage;
import comp6521.lab.com.Pages.SupplierPage;
import comp6521.lab.com.Records.DateRecordElement;


/**
 * @author dimitri.tiago
 */
public class GUI extends JFrame
{
	private JButton buttons[];	// array of buttons
	private final String names[] =
	{"Generate Statistics", "Build Indexes", "Purge Indexes",  
	 "Query A", "Query A - Indexed", "Query B", "Query B - Indexed", "Query C", "Query C - Indexed", 
	 "Query D", "Query D - Indexed", "Query E", "Query E - Indexed", "Query F", "Query F - Indexed",
	 "Query Z", "Query Z - Indexed", "Purge Outputs", "Purge Memory Logs"};
	private GridLayout gridLayout;
	
	// no-argument constructor
	public GUI()
	{
		super("COMP 6521 Project - Query Execution Application. (c)");
		gridLayout = new GridLayout( 5, 5, 5, 5 );	// 2 by 7; gaps of 5 
		setLayout(gridLayout);
		buttons = new JButton[ names.length ];	// create array of JButtons
		
		// add action listeners to buttons
		for ( int count = 0; count < names.length; count++)
		{
			buttons[ count ] = new JButton( names[count] );
			buttons[ count ].addActionListener( new QueryHandler() );	// register listener 
			add(buttons [ count ] );	// add button to JFrame
		}
		
		// initialise page manager
		PageManagerSingleton myPageManager = PageManagerSingleton.getInstance();	// set data source path
		myPageManager.setPath("Y:\\Dimitri\\Concordia\\Comp6521_AdvancedDatabaseSystemsAndTheory\\Lab\\ShortData\\");
		
		// initialise memory manager
		MemoryManager.getInstance().SetPageFile(CustomerPage.class, "Customer.txt");	// set default page types
        MemoryManager.getInstance().SetPageFile(LineItemPage.class, "LineItem.txt");
        MemoryManager.getInstance().SetPageFile(NationPage.class,   "Nation.txt");
        MemoryManager.getInstance().SetPageFile(OrdersPage.class,   "Orders.txt");
        MemoryManager.getInstance().SetPageFile(PartPage.class,     "Part.txt");
        MemoryManager.getInstance().SetPageFile(PartSuppPage.class, "PartSupp.txt");
        MemoryManager.getInstance().SetPageFile(RegionPage.class,   "Region.txt");
        MemoryManager.getInstance().SetPageFile(SupplierPage.class, "Supplier.txt");
	}
	
	// private inner class to handle Query A buttons
	private class QueryHandler implements ActionListener
	{		
		//@Override
		public void actionPerformed(ActionEvent e) 
		{
			// get option selected by user
			String query = e.getActionCommand();	// get source
			
			// execute queries
			if ( (query.compareToIgnoreCase("Query A") == 0) || (query.compareToIgnoreCase("Query A - Indexed") == 0) )  //  A
			{
				// obtain user input from option pane dialogs
				String inputStartDate = JOptionPane.showInputDialog("Enter Start Date");
				String inputEndDate   = JOptionPane.showInputDialog("Enter End Date");

				// date record element for parsing
				DateRecordElement dateElement = new DateRecordElement();
				
				// get start date
				dateElement.Parse(inputStartDate);
				Date startDate = dateElement.getDate();

				// get end date
				dateElement.Parse(inputEndDate);
				Date endDate = dateElement.getDate();
					
				// execute query a
				if (query.compareToIgnoreCase("Query A") == 0)
				{
					// execute query A
					Query_A queryA = new Query_A();
					queryA.PerformQuery(startDate, endDate);
				}
				
				// execute query a indexed
				if (query.compareToIgnoreCase("Query A - Indexed") == 0)
				{
					// execute query A indexed
					Query_A_Indexed queryAIdx = new Query_A_Indexed();
					queryAIdx.PerformQuery(startDate, endDate);	
				}
			}
			
			// execute queries
			if ( (query.compareToIgnoreCase("Query B") == 0) || (query.compareToIgnoreCase("Query B - Indexed") == 0) )  //  B
			{
				// obtain user input from option pane dialogs
				String input1 = JOptionPane.showInputDialog("Enter Input 1 (Comma Separated List of Integers)");
				String input2 = JOptionPane.showInputDialog("Enter Input 2 (Comma Separated List of Integers)");

				String[] input1List = input1.split(",");	// separate tokens
				String[] input2List = input2.split(",");	// separate tokens
			
				// execute query b
				if (query.compareToIgnoreCase("Query B") == 0)
				{
					// execute query b
					Query_B queryB = new Query_B();
					queryB.PerformQuery(input1List, input2List);
				}
				
				// execute query b indexed
				if (query.compareToIgnoreCase("Query B - Indexed") == 0)
				{
					// execute query b indexed
					Query_B_Indexed queryBIdx = new Query_B_Indexed();
					queryBIdx.PerformQuery(input1List, input2List);	
				}
			}
			
			// execute queries
			if ( (query.compareToIgnoreCase("Query C") == 0) || (query.compareToIgnoreCase("Query C - Indexed") == 0) )  //  C
			{
				// obtain user input from option pane dialogs
				int p_size  = Integer.parseInt(JOptionPane.showInputDialog("Enter p_size"));
				
				String r_name1 = JOptionPane.showInputDialog("Enter r_name1");
				String r_name2 = JOptionPane.showInputDialog("Enter r_name2");
				
				// execute query c
				if (query.compareToIgnoreCase("Query C") == 0)
				{
					// execute query c
					Query_C queryC = new Query_C();
					queryC.ProcessQuery(p_size, r_name1, r_name2);
				}
				
				// execute query c indexed
				if (query.compareToIgnoreCase("Query C - Indexed") == 0)
				{
					// execute query c indexed
					Query_C_Indexed queryCIdx = new Query_C_Indexed();
					queryCIdx.ProcessQuery(p_size, r_name1, r_name2);	
				}
			}
			
			// execute queries
			if ( (query.compareToIgnoreCase("Query D") == 0) || (query.compareToIgnoreCase("Query D - Indexed") == 0) )  //  D
			{
				// obtain user input from option pane dialogs
				String r_name = JOptionPane.showInputDialog("Enter r_name");
				
				// execute query d
				if (query.compareToIgnoreCase("Query D") == 0)
				{
					// execute query d
					Query_D queryD = new Query_D();
					queryD.ProcessQuery(r_name);
				}
				
				// execute query d indexed
				if (query.compareToIgnoreCase("Query D - Indexed") == 0)
				{
					// execute query d indexed
					QueryD_Indexed queryDIdx = new QueryD_Indexed();
					queryDIdx.ProcessQuery(r_name);	
				}
			}
			
			// execute queries
			if ( (query.compareToIgnoreCase("Query E") == 0) || (query.compareToIgnoreCase("Query E - Indexed") == 0) )  //  E
			{
				// obtain user input from option pane dialogs
				String n_name1 = JOptionPane.showInputDialog("Enter n_name1");
				String n_name2 = JOptionPane.showInputDialog("Enter n_name2");
				
				// execute query e
				if (query.compareToIgnoreCase("Query E") == 0)
				{
					// execute query e
					Query_E queryE = new Query_E();
					queryE.ProcessQuery(n_name2, n_name1);
				}
				
				// execute query e indexed
				if (query.compareToIgnoreCase("Query E - Indexed") == 0)
				{
					// execute query e indexed
					Query_E_Indexed queryEIdx = new Query_E_Indexed();
					queryEIdx.ProcessQuery(n_name2, n_name1);
				}
			}
			
			// execute queries
			if ( (query.compareToIgnoreCase("Query Z") == 0) || (query.compareToIgnoreCase("Query Z - Indexed") == 0) )  //  Z
			{
				// obtain user input from option pane dialogs
				int yearOrderDate = Integer.parseInt(JOptionPane.showInputDialog("Enter year(o_orderdate)"));
				
				// execute query z
				if (query.compareToIgnoreCase("Query Z") == 0)
				{
					// execute query z
					Query_Z queryZ = new Query_Z();
					queryZ.ProcessQuery(yearOrderDate);
				}
				
				// execute query z indexed
				if (query.compareToIgnoreCase("Query Z - Indexed") == 0)
				{
					// execute query e indexed
					Query_Z_Indexed queryZIdx = new Query_Z_Indexed();
					queryZIdx.ProcessQuery(yearOrderDate);
				}
			}
			
			// execute queries
			if ( (query.compareToIgnoreCase("Query F") == 0) || (query.compareToIgnoreCase("Query F - Indexed") == 0) )  //  F
			{
				// obtain user input from option pane dialogs
				String inputRecord = 
					JOptionPane.showInputDialog("Enter Line Item Record\n\n" +
					"LineItem(L_ORDERKEY, L_PARTKEY, L_SUPPKEY,\n " +
					"		  L_LINENUMBER, L_QUANTITY, L_EXTENDEDPRICE,\n " +
					"		  L_DISCOUNT, L_RETURNFLAG, L_LINESTATUS,\n " +
					"		  L_SHIPDATE, L_COMMITDATE, L_RECEIPTDATE,\n " +
					"		  L_SHIPINSTRUCT, L_SHIPMODE, L_COMMENT)\n\n" +
					"(Dates in YYYY-MM-DD format)\n\n");
				
				String[] input = inputRecord.split(",");	// separate tokens
								
				// execute query z
				if (query.compareToIgnoreCase("Query F") == 0)
				{
					// execute query z
					Query_F queryF = new Query_F();
					queryF.ProcessQuery(input);
				}
				
				// execute query z indexed
				if (query.compareToIgnoreCase("Query F - Indexed") == 0)
				{
					// execute query e indexed
					Query_F_Indexed queryFIdx = new Query_F_Indexed();
					queryFIdx.ProcessQuery(input);
				}
			}
			
			// build statistics
			if ( query.compareToIgnoreCase("Generate Statistics") == 0 )  //  Build Statistics
			{	
				int selection = JOptionPane.showConfirmDialog(GUI.this, "Build Statistics?");	// prompt user
				
				// build statistics
				if (selection == 0) // selection = yes
				{
					Stats statistics = new Stats();
					statistics.GenerateStats();	// generate statistics	
				}		
			}
			
			// build indexes
			if ( query.compareToIgnoreCase("Build Indexes") == 0 )  //  Build Indexes
			{	
				int selection = JOptionPane.showConfirmDialog(GUI.this, "Build Indexes?");	// prompt user
				
				// build statistics
				if (selection == 0) // selection = yes
				{
					IndexManager.getInstance().CreateIndexes();	// create indexes	
				}		
			}
		
			// purge indexes
			if ( query.compareToIgnoreCase("Purge Indexes") == 0 )  //  Purge Indexes
			{	
				int selection = JOptionPane.showConfirmDialog(GUI.this, "Purge Indexes?");	// prompt user
				
				// build statistics
				if (selection == 0) // selection = yes
				{
					IndexManager.getInstance().PurgeIndexes();	// purge indexes	
				}		
			}
			
			// purge outputs
			if ( query.compareToIgnoreCase("Purge Outputs") == 0 )  //  Purge Indexes
			{	
				int selection = JOptionPane.showConfirmDialog(GUI.this, "Purge Outputs?");	// prompt user
				
				// build statistics
				if (selection == 0) // selection = yes
				{
					PageManagerSingleton.getInstance().deleteFileType(".out");	// delete all .out files
				}		
			}
			
			// purge memory logs
			if ( query.compareToIgnoreCase("Purge Memory Logs") == 0 )  //  purge memory logs
			{	
				int selection = JOptionPane.showConfirmDialog(GUI.this, "Purge Memory Logs?");	// prompt user
				
				// build statistics
				if (selection == 0) // selection = yes
				{
					PageManagerSingleton.getInstance().deleteFileType(".mem");	// delete all .mem files
				}		
			}
		}
	}
}