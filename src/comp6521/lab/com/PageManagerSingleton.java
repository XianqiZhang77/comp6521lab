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
import java.io.IOException;		
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import comp6521.lab.com.Records.*;

public class PageManagerSingleton 
{	
	// TODO: method to return size of cbuf array	
	// TODO: file writer methods (i.e. write pages) 
	// TODO: get file information (i.e. File class)?
	// TODO: record classes getter and setter methods
	// TODO: add toString override to record classes
	// TODO: add record size constant to records
	// TODO: handle case where page is out of bound. (i.e. progam should not crash)
	
	private static final PageManagerSingleton INSTANCE = new PageManagerSingleton(); 	// page manager singleton
	
	private String path;	// store file path 		 
	
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
	
	public final int getNumberOfRecordsPerPage() { return 10; }
	
	// get page from disk. a page is equal to 10 records.
	// preconditions: recordSize, pageNumber to retrieve, fileName containing relation data.
	// post-conditions: byte array containing read bytes 
	public char[] getRawPage(String fileName, int recordSize, int pageNumber)
	{
		int pageSize = recordSize*10;			// page size
		char[] cbuf = new char[recordSize*10];	// store data from disk	
				
		try 
		{
			FileReader file = new FileReader(path+fileName);		// open file for reading
																	//TODO: check filesize against # of characters skipped to ensure we are in file
			file.skip(pageSize*pageNumber);			    	        // move to appropriate page number
			
			if( !file.ready() ) 									// if we're off the file (skipped too far)
				cbuf = null;
			else
				file.read(cbuf);		    						// read data into character buffer
				
			System.out.println(cbuf.length);
			System.out.println(String.copyValueOf(cbuf));
			
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

	// parse page character stream and return page records array-list
	public ArrayList<Record> getPage(String fileName, int recordSize, int pageNumber)
	{
		ArrayList<Record> page = new ArrayList<Record>(10);	// parsed page array-list variable
		
		char[] rawPage = this.getRawPage(fileName, recordSize, pageNumber);		// get raw character[] page contents
		String rawPageString = String.valueOf(rawPage);							// raw page string
		String[] records = rawPageString.split("\n");							// split raw page string into string records array
		
		// parse each record in records array
		for (String record : records) 
		{
			// parse record and add to page array-list
			page.add(this.parseRecord(record, fileName));
		}
		
		// return parsed page
		return page;
	}
	
	// parse record from string
	private Record parseRecord(String strRec, String filename)
	{
		Record record;	// record object
		record = null;  // assigned null reference
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");	// date formatter
		
		try
		{
			if (filename.compareToIgnoreCase("customer.txt") == 0)	
			{
				// parse record
				String c_custkey    = strRec.substring(0, 10);
				String c_name       = strRec.substring(11, 35);
				String c_address    = strRec.substring(36, 85); 
				String c_nationKey  = strRec.substring(86, 96);
				String c_phone      = strRec.substring(97, 116);
				String c_acctBal    = strRec.substring(117, 138);
				String c_mktSegment = strRec.substring(139, 153);
				String c_comment    = strRec.substring(154, 273);
				
				// initialise record object
				CustomerRecord customerRecord = new CustomerRecord();	// create customer record instance
				
				customerRecord.c_custkey = Integer.parseInt(c_custkey.trim());               
			    customerRecord.c_name = c_name.trim();
			    customerRecord.c_address = c_address.trim();		    
			    customerRecord.c_nationKey = Integer.parseInt(c_nationKey.trim());
			    customerRecord.c_phone = c_phone.trim();
			    customerRecord.c_acctBal = Float.parseFloat(c_acctBal.trim());
			    customerRecord.c_mktSegment = c_mktSegment.trim();
			    customerRecord.c_comment = c_comment.trim();
			    
				record = (Record) customerRecord;	// cast reference as superclass type
			}	
			else if (filename.compareToIgnoreCase("supplier.txt") == 0)
			{		
				// parse record
				String s_suppKey    = strRec.substring(0, 10);
				String s_name       = strRec.substring(11, 35);
				String s_address    = strRec.substring(36, 85); 
				String s_nationKey  = strRec.substring(86, 96);
				String s_phone      = strRec.substring(97, 126);
				String s_acctBal    = strRec.substring(127, 148);
				String s_comment    = strRec.substring(149, 268);
				
				// initialise record object
				SupplierRecord supplierRecord = new SupplierRecord();	// create supplier record instance
				
				supplierRecord.s_suppKey = Integer.parseInt(s_suppKey.trim());               
				supplierRecord.s_name = s_name.trim();
				supplierRecord.s_address = s_address.trim();		    
				supplierRecord.s_nationKey = Integer.parseInt(s_nationKey.trim());
				supplierRecord.s_phone = s_phone.trim();
				supplierRecord.s_acctBal = Float.parseFloat(s_acctBal.trim());
				supplierRecord.s_comment = s_comment.trim();
			    
			    record = (Record) supplierRecord;	// cast reference as superclass type
			}
			else if (filename.compareToIgnoreCase("lineitem.txt") == 0)
			{		
				// parse record
				String l_orderKey      = strRec.substring(0, 10);
				String l_partKey       = strRec.substring(11, 21);
				String l_suppKey       = strRec.substring(22, 32); 
				String l_lineNumber    = strRec.substring(33, 43);
				String l_quantity      = strRec.substring(44, 54);
				String l_extendedPrice = strRec.substring(55, 76);
				String l_discount      = strRec.substring(77, 98);
				//TODO: column missing from table definition here
				String l_returnFlag    = strRec.substring(121, 122);
				String l_lineStatus    = strRec.substring(123, 124);
				String l_shipDate      = strRec.substring(125, 143); 
				String l_commitDate    = strRec.substring(144, 162);
				String l_receiptDate   = strRec.substring(163, 181);
				String l_shipInstruct  = strRec.substring(182, 201);
				String l_shipMode      = strRec.substring(202, 211);
				String l_comment       = strRec.substring(212, 331);
				
				// initialise record object
				LineItemRecord liRecord = new LineItemRecord();	// create supplier record instance
				
				liRecord.l_orderKey      = Integer.parseInt(l_orderKey.trim());               
				liRecord.l_partKey       = Integer.parseInt(l_partKey.trim());
				liRecord.l_suppKey       = Integer.parseInt(l_suppKey.trim());		    
				liRecord.l_lineNumber    = Integer.parseInt(l_lineNumber.trim());
				liRecord.l_quantity      = Integer.parseInt(l_quantity.trim());
				liRecord.l_extendedPrice = Float.parseFloat(l_extendedPrice.trim());
				liRecord.l_discount      = Float.parseFloat(l_discount.trim());
				liRecord.l_returnFlag    = l_returnFlag.trim();               
				liRecord.l_lineStatus    = l_lineStatus.trim();
				liRecord.l_shipDate      = (Date)formatter.parse(l_shipDate.trim());					
				liRecord.l_commitDate    = (Date)formatter.parse(l_commitDate.trim());
				liRecord.l_receiptDate   = (Date)formatter.parse(l_receiptDate.trim());
				liRecord.l_shipInstruct  = l_shipInstruct.trim();
				liRecord.l_shipMode      = l_shipMode.trim();
				liRecord.l_comment       = l_comment.trim();
				
			    record = (Record) liRecord;	// cast reference as superclass type
			}
		}
		catch(ParseException pe)
		{
			System.err.println(pe.toString()); // send to error stream	
		}
			
		// return parsed record
		return record;
	}
	
	// write page to disk
	public void writePage(String filename, char[] cbuf, int recordSize)
	{
		// TODO	
	}
}