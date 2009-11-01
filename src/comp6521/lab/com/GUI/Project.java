package comp6521.lab.com.GUI;

import java.util.Date;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import comp6521.lab.com.MemoryManager;
import comp6521.lab.com.Query_A;
import comp6521.lab.com.Query_B;
import comp6521.lab.com.Query_C;
import comp6521.lab.com.Pages.*;



public class Project {
	
	public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI(); 
            }
        });
    }
	private static void createAndShowGUI() {
        JFrame f = new JFrame("Swing Paint Demo");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        f.add(new MainPanel());
        f.setSize(250,250);
        f.setVisible(true);
        
        // Impossibly do stuff!!!!!
        MemoryManager.getInstance().SetPageFile(CustomerPage.class, "Users\\Julien\\Documents\\Concordia\\COMP6521\\Customer.txt");
        MemoryManager.getInstance().SetPageFile(LineItemPage.class, "Users\\Julien\\Documents\\Concordia\\COMP6521\\LineItem.txt");
        MemoryManager.getInstance().SetPageFile(NationPage.class,   "Users\\Julien\\Documents\\Concordia\\COMP6521\\Nation.txt");
        MemoryManager.getInstance().SetPageFile(OrdersPage.class,   "Users\\Julien\\Documents\\Concordia\\COMP6521\\Orders.txt");
        MemoryManager.getInstance().SetPageFile(PartPage.class,     "Users\\Julien\\Documents\\Concordia\\COMP6521\\Part.txt");
        MemoryManager.getInstance().SetPageFile(PartSuppPage.class, "Users\\Julien\\Documents\\Concordia\\COMP6521\\PartSupp.txt");
        MemoryManager.getInstance().SetPageFile(RegionPage.class,   "Users\\Julien\\Documents\\Concordia\\COMP6521\\Region.txt");
        MemoryManager.getInstance().SetPageFile(SupplierPage.class, "Users\\Julien\\Documents\\Concordia\\COMP6521\\Supplier.txt");
        
        Query_A qA = new Query_A();
        Date StartDate = new Date((long)1234567);
        Date EndDate = new Date((long)12345678);
        
        qA.PerformQuery(StartDate, EndDate);
        
        Query_B qB = new Query_B();
        String[] SelList = { "15", "22" };
        String[] MinList = { "22", "18" };
        qB.PerformQuery( SelList, MinList);
        
        Query_C qc = new Query_C();
        qc.ProcessQuery(0, "banana", "republic");
        
    } 

}
