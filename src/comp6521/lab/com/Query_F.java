/**
 * November. 16, 2009
 * Query_F.java: Implementation of COMP6521 Lab Query F
 */
package comp6521.lab.com;

import comp6521.lab.com.Records.*;
import comp6521.lab.com.Pages.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author dimitri tiago
 */

public class Query_F 
{
	/* method that executes query f: 
	Insert into lineitem
	(
	L_ORDERKEY, L_PARTKEY, L_SUPPKEY, L_LINENUMBER,	L_QUANTITY,	L_EXTENDEDPRICE, L_DISCOUNT,
	L_RETURNFLAG, L_LINESTATUS, L_SHIPDATE, L_COMMITDATE, L_RECEIPTDATE, ,L_SHIPINSTRUCT,
	L_SHIPMODE, L_COMMENT
	)
	values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)
	*/
	public boolean ProcessQuery(String[] args)
	{ 
		try 
		{		
			// initialise query arguments - local variables
			int lOrderKey        = Integer.parseInt(args[0]);
			int lPartKey         = Integer.parseInt(args[1]);
			int lSuppKey         = Integer.parseInt(args[2]);
			int lLineNumber      = Integer.parseInt(args[3]);
			int lQuantity        = Integer.parseInt(args[4]);
			float lExtendedPrice = Float.parseFloat(args[5]);
			float lDiscount      = Float.parseFloat(args[6]);;
			String lReturnFlag   = new String(args[7]);
			String lLineStatus   = new String(args[8]);
			String lShipInstruct = new String(args[12]);
			String lShipMode     = new String(args[13]);
			String lComment      = new String(args[14]);       
			
			// date formatter
			SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			// initialise query arguments - local variables
			try
			{
				// parse dates
				Date lShipDate    = (Date) dateFormatter.parse(args[9]);
				Date lCommitDate  = (Date) dateFormatter.parse(args[10]);
				Date lReceiptDate = (Date) dateFormatter.parse(args[11]);
			}
			catch(ParseException pe)
			{
				// re-throw exception to outter catch
				throw pe;
			}
			
			// insert into lineItem relation 
			int liItemCounter       =  0;																					// region page counter
			LineItemPage liItemPage = null;																					// region page	
			boolean isUnique = true;																						// unique primary key flag	
			while ( (liItemPage = MemoryManager.getInstance().getPage(LineItemPage.class, liItemCounter++)) != null )		// get region page
			{
				LineItemRecord[] liItemRecords = liItemPage.m_records;	// store line item records
				
				// determine if primary key is unique 
				for (LineItemRecord rec : liItemRecords)
				{
					// if primary key exists set isUnique = false
					if (rec.get("l_orderkey").getInt() == lOrderKey)
					{
						isUnique = false;
					}
				}
				
				// free page
				MemoryManager.getInstance().freePage(liItemPage);
			}
			
			// if primary key is unique insert record
			if (isUnique == true)
			{
				// TODO: build real record
				//String rec = String.format("", lOrderKey, lPartKey, lSuppKey, lLineNumber, lQuantity, lExtendedPrice, lDiscount, lReturnFlag,
				//		lLineStatus, lShipDate, lCommitDate, lReceiptDate, lShipInstruct, lShipMode, lComment);
			
				String rec = String.format("%d11\r\n", lOrderKey);		// build record
				PageManagerSingleton.getInstance().writePage("LineItem.txt", rec);		// write page
				
				// log message
				String logMsg = String.format("%s\r\n", "1 Record Affected");
				 
				// output log message to f.out
				PageManagerSingleton.getInstance().writePage("f.out", logMsg);
			}
			else
			{
				// log message
				String logMsg = String.format("%s: %s\r\n", "0 Records Affected", "primary key not unique.");
						
				// output log message to f.out
				PageManagerSingleton.getInstance().writePage("f.out", logMsg);		
			}
		}
		catch(Exception e)
		{	
			String logMsg = String.format("%s\r\n", e.toString());
						
			// output log message to f.out
			PageManagerSingleton.getInstance().writePage("f.out", logMsg);
			
			// return failure status
			return false;
		}
		
		// return success status
		return true;
	}
}
