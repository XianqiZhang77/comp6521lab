/**
 * September 27, 2009
 * COMP6521 Lab
 * PageManagerSingleton.java: class that retrieves a page of records from a relation data text file. 
 */
package comp6521.lab.com;

/**
 * @author dimitri.tiago
 *
 */

import java.io.FileReader;	
import java.io.FileWriter;
import java.io.IOException;

public class PageManagerSingleton 
{	
	// TODO: method to return size of cbuf array	 
	// TODO: get file information (i.e. File class)?
	// TODO: handle case where page is out of bound. (i.e. program should not crash)
	
	private static final PageManagerSingleton INSTANCE = new PageManagerSingleton(); 	// page manager singleton
	
	private String path;		// store file path 		 
	
	// default constructor
	private PageManagerSingleton()
	{
		path = "C:\\";		// set default path
	}
	
	// retrieve singleton instance with default 
	public static PageManagerSingleton getInstance()
	{
		return INSTANCE;
	}	
	
	// set path instance variable
	public void setPath(String path)
	{
		this.path = path;
	}
	
	// get path instance variable
	public String getPath(String path)
	{
		return this.path;
	}
		
	// get page from disk. a page is equal to 10 records.
	// preconditions: recordSize, pageNumber to retrieve, fileName containing relation data.
	// post-conditions: byte array containing read bytes 
	public char[] getRawPage(String fileName, int pageSize, int pageNumber)
	{
		char[] cbuf = new char[pageSize];	// store data from disk	
						
		try 
		{
			FileReader file = new FileReader(path+fileName);		// open file for reading
				
			file.skip(pageSize*pageNumber);			    	        // move to appropriate page number
			
			if( !file.ready() ) 									// if we're off the file (skipped too far)
				cbuf = null;
			else
				file.read(cbuf);		    						// read data into character buffer
							
			file.close();											// close file
		}
		catch(IOException io)
		{
			// print error stack trace
			io.printStackTrace(System.err);
		}
		
		// return character buffer 
		return cbuf;
	}
	
	// write page to disk
	public void writePage(String filename, char[] cbuf)
	{
		try
		{
			FileWriter file = new FileWriter(path+filename, true);	// append cbuf[]to end of file 
			file.write(cbuf);
			file.close();
		}
		catch(IOException ioException)
		{
			ioException.printStackTrace();
		}
	}
}