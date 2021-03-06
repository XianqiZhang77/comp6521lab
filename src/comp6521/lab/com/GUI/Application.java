/**
 * December 7, 2009
 * Application.java: db application. 
 */
package comp6521.lab.com.GUI;

import javax.swing.JFrame;

import comp6521.lab.com.Util.MemoryReport;

import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

/**
 * @author dimitri.tiago
 */
public class Application
{	
	// method main begins execution
	public static void main(String args[])
	{	
		GUI dBMS = new GUI();	// instantiate GUI
		
		dBMS.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE);	// exit on close
		dBMS.setSize(900, 400);	// set size of frame
		dBMS.setVisible(true);	// display frame
					
		ExecutorService application = Executors.newFixedThreadPool(2);
		application.execute( new MemoryReport("MemoryReport") );
		application.execute( new ControlThread() );
		
		application.shutdown();
	}
}
