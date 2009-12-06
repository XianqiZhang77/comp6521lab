/**
 * November. 16, 2009
 * Query_F.java: Implementation of COMP6521 Lab Query F
 */
package comp6521.lab.com;

import comp6521.lab.com.Records.*;
import comp6521.lab.com.Pages.*;
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
			// TODO: Handle ' ' insertion and null insertion
			
			// initialise query arguments - local variables	
			long lOrderKey        = Long.parseLong(args[0]);
			long lPartKey         = Long.parseLong(args[1]);
			long lSuppKey         = Long.parseLong(args[2]);
			long lLineNumber      = Long.parseLong(args[3]);
			long lQuantity        = Long.parseLong(args[4]);
			double lExtendedPrice = Double.parseDouble(args[5]);
			double lDiscount      = Double.parseDouble(args[6]);
			String lTax           = new String("0");		// lTax attribute not in schema
			String lShipDate;
			String lCommitDate;
			String lReceiptDate;
			String lReturnFlag    = new String(args[7]);
			String lLineStatus    = new String(args[8]);
			String lShipInstruct  = new String(args[12]);
			String lShipMode      = new String(args[13]);
			String lComment       = new String(args[14]);       
			
			// parse date attributes and initialise date strings
			try
			{
				// date formatter
				SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
				
				// parse dates
				Date tmplShipDate    = (Date) dateFormatter.parse(args[9]);
				Date tmplCommitDate  = (Date) dateFormatter.parse(args[10]);
				Date tmplReceiptDate = (Date) dateFormatter.parse(args[11]);
				
				// set new pattern
				dateFormatter.applyPattern("yyyy-MM-dd H:mm:ss");
				
				// convert dates to string representation
				lShipDate    = dateFormatter.format(tmplShipDate);
				lCommitDate  = dateFormatter.format(tmplCommitDate);
				lReceiptDate = dateFormatter.format(tmplReceiptDate);
			}
			catch(ParseException parseException)
			{
				// re-throw exception
				throw parseException;
			}
			
			try
			{
				// check field size constraints
				String[] fieldNames = {"l_orderkey", "l_partKey", "l_suppKey", "l_lineNumber", "l_quantity", "l_extendedPrice", "l_discount",
									   "l_returnFlag", "l_lineStatus", "l_shipDate", "l_commitDate", "l_receiptDate", "l_shipInstruct", 
									   "l_shipMode", "l_comment"};	// line item field names
				int[] fieldSizes = {11, 11, 11, 11, 11, 22, 22, 2, 2, 19, 19, 19, 20, 10, 120};		// line item table field sizes 
				
				for (int i = 0; i < args.length; i++)
				{
					if (args[i].length() >  fieldSizes[i])	// check field size
					{
						throw new Exception("0 Records Affected. "+ fieldNames[i] + " Field Size Constraint Exceeded.");
					}
				}
			}
			catch (Exception e)
			{
				// re-throw exception
				throw e;
			}
			
			boolean PrimaryKeyOk = CheckPrimaryKey(lOrderKey, lLineNumber);

			if( !PrimaryKeyOk )
				throw new Exception("0 Records Affected. Primary Key Is Not Unique.");
			
			boolean PartPKOk = CheckPartKey( lPartKey );

			if( !PartPKOk )
				throw new Exception("0 Records Affected. Failed l_partKey FK Contraint.");
			
			boolean OrdersPKOk = CheckOrdersKey( lOrderKey );

			if( !OrdersPKOk )
				throw new Exception("0 Records Affected. Failed l_orderKey FK Constraint.");
			
			boolean SupplierPKOk = CheckSupplierKey( lSuppKey );
			
			if( !SupplierPKOk )
				throw new Exception("0 Records Affected. Failed l_suppKey FK Constraint.");
			
			// build record
			String rec = String.format("%-11d%-11d%-11d%-11d%-11d%-22.2f%-22.2f%-22s%-2s%-2s%-19s%-19s%-19s%-20s%-10s%-120s\r\n", 
									   lOrderKey, lPartKey, lSuppKey, lLineNumber, lQuantity, 
									   lExtendedPrice, lDiscount, lTax, lReturnFlag, lLineStatus, 
									   lShipDate, lCommitDate, lReceiptDate, lShipInstruct, lShipMode, lComment);
			
			LineItemRecord liRec = new LineItemRecord();
			liRec.Parse(rec);
			
			int nbRecordsPerPage = MemoryManager.getInstance().GetNumberOfRecordsPerPage(LineItemPage.class);
			int pageNumber = MemoryManager.getInstance().GetNumberOfPages(LineItemPage.class, "LineItem.txt") - 1;
			
			int recordNumber = -1;
			LineItemPage liPage = MemoryManager.getInstance().getPage(LineItemPage.class, pageNumber);
	
			if(liPage.m_records.length == nbRecordsPerPage)
			{
				// We need to create a new page
				LineItemPage newPage = MemoryManager.getInstance().getEmptyPage(LineItemPage.class);
				newPage.AddRecord(liRec);
				
				recordNumber = newPage.m_pageNumber * nbRecordsPerPage;
				// Free & Write the new page
				MemoryManager.getInstance().freePage(newPage);
			}
			else
			{
				recordNumber = liPage.m_pageNumber * nbRecordsPerPage + liPage.m_records.length;
				liPage.setRecord(liPage.m_records.length, liRec);				
			}
			
			// Free loaded page
			MemoryManager.getInstance().freePage(liPage);
			
			// Update the index (if it exists)
			IndexManager.getInstance().AddLineItemRecord(liRec, recordNumber);			

			// log message
			String logMsg = String.format("%s\r\n", "1 Record Affected.");
				 
			// output log message to f.out
			PageManagerSingleton.getInstance().writePage("f.out", logMsg);
		}
		catch(Exception e)
		{
			// log message
			String logMsg = String.format("%s\r\n", e.toString());
						
			// output log message to f.out
			PageManagerSingleton.getInstance().writePage("f.out", logMsg);
			
			// return failure status
			return false;
		}
		
		// return success status
		return true;
	}
	
	public boolean CheckPrimaryKey( long orderKey, long lineNumber )
	{
		// check for unique primary key
		boolean unique = true;
		int liItemCounter       =  0;																					// region page counter
		LineItemPage liItemPage = null;																					// region page		
		while ( unique && (liItemPage = MemoryManager.getInstance().getPage(LineItemPage.class, liItemCounter++)) != null )		// get region page
		{
			LineItemRecord[] liItemRecords = liItemPage.m_records;	// store line item records
			
			// determine if primary key is unique 
			for (LineItemRecord rec : liItemRecords)
			{
				// if primary key exists set isUnique = false
				if ( (rec.get("l_orderkey").getInt() == orderKey) && (rec.get("l_lineNumber").getInt() == lineNumber)	)
				{
					unique = false;
				}
			}
			
			// free page
			MemoryManager.getInstance().freePage(liItemPage);
		}

		return unique;
	}
	
	public boolean CheckPartKey( long partKey )
	{
		// check for referential integrity constraints against Part table
		int partCounter   =  0;																						// part page counter
		PartPage partPage = null;																					// part page		
		boolean pFKExists = false;																					// part FK flag		
		while ( !pFKExists && (partPage = MemoryManager.getInstance().getPage(PartPage.class, partCounter++)) != null )			// get part page
		{
			PartRecord[] partRecords = partPage.m_records;	// store part records
			
			// determine if foreign key exists 
			for (PartRecord rec : partRecords)
			{
				// if foreign key exists set fkExists = true
				if ( (rec.get("p_partKey").getInt() == partKey) )
				{
					pFKExists = true;
				}
			}
			
			// free page
			MemoryManager.getInstance().freePage(partPage);
		}

		return pFKExists;
	}
	
	public boolean CheckOrdersKey( long orderKey )
	{
		// check for referential integrity constraints against Orders table
		int ordersCounter   =  0;																						// orders page counter
		OrdersPage ordersPage = null;																					// orders page		
		boolean oFKExists = false;																						// order FK flag
		while ( !oFKExists && (ordersPage = MemoryManager.getInstance().getPage(OrdersPage.class, ordersCounter++)) != null )			// get orders page
		{
			OrdersRecord[] ordersRecords = ordersPage.m_records;	// store orders records
			
			// determine if foreign key exists 
			for (OrdersRecord rec : ordersRecords)
			{
				// if foreign key exists set fkExists = true
				if ( (rec.get("o_orderKey").getInt() == orderKey) )
				{
					oFKExists = true;
				}
			}
			
			// free page
			MemoryManager.getInstance().freePage(ordersPage);
		}

		return oFKExists;
	}
	
	public boolean CheckSupplierKey( long suppKey )
	{
		int p = 0;
		SupplierPage page = null;
		boolean sFKExists = false;
		
		while( !sFKExists && (page = MemoryManager.getInstance().getPage(SupplierPage.class, p++)) != null)
		{
			SupplierRecord[] supplierRecords = page.m_records;
			
			// determine if foreign key exists
			for(SupplierRecord rec : supplierRecords)
			{
				if( rec.get("s_suppKey").getInt() == suppKey )
					sFKExists = true;
			}
			
			// free page
			MemoryManager.getInstance().freePage(page);
		}
		
		return sFKExists;
	}
}