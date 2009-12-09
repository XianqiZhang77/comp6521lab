/**
 * December 8, 2009
 * Memory Report.java: class that gets the available memory every x milliseconds
 */
package comp6521.lab.com.Util;

import comp6521.lab.com.MemoryManager;
import comp6521.lab.com.PageManagerSingleton;

/**
 * @author dimitri.tiago
 *
 */
public class MemoryReport implements Runnable
{
	// output filename
	private String filename;
	
	public MemoryReport(String filename)
	{
		// initialise fields
		this.filename = filename;
	}
	
	@Override
	public void run() 
	{
		while (true)
		{
			try 
			{
				Thread.sleep(1);
				PageManagerSingleton.getInstance().writeOutput(
						filename + ".mem", "Memory Usage: " + MemoryManager.getInstance().RemainingMemory() + "\r\n");	// report memory use
			}
			catch (InterruptedException e) 
			{
				e.printStackTrace();
			}
		}
	}
}
