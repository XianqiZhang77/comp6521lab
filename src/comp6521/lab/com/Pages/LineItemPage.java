package comp6521.lab.com.Pages;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import comp6521.lab.com.Records.LineItemRecord;

public class LineItemPage extends Page<LineItemRecord> {
	public void Construct( char[] rawData ) 
	{
		String rawPageString    = String.valueOf(rawData);
		String[] rawRecords     = rawPageString.split("\n");
		
		// Count nb. of non-null raw records
		int n = 0;
		for( int i = 0; i < rawRecords.length; i++ )
			if( rawRecords[i].trim().compareTo("") != 0 )
				n++;
		
		m_records = new LineItemRecord[n];
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");	// date formatter
		
		int r = 0;
		for( int i = 0; i < rawRecords.length; i++ )
		{
			String tR = rawRecords[i];
			
			if( tR.trim().compareTo("") == 0)
				continue;
			
			m_records[r] = new LineItemRecord();
			// Fill in the record
			m_records[r].l_orderKey      = Integer.parseInt(tR.substring(0, 10).trim());
			m_records[r].l_partKey       = Integer.parseInt(tR.substring(11, 21).trim());
			m_records[r].l_suppKey       = Integer.parseInt(tR.substring(22, 32).trim());
			m_records[r].l_lineNumber    = Integer.parseInt(tR.substring(33, 43).trim());
			m_records[r].l_quantity	     = Integer.parseInt(tR.substring(44, 54).trim());
			m_records[r].l_extendedPrice = Float.parseFloat(tR.substring(55, 76).trim());
			m_records[r].l_discount      = Float.parseFloat(tR.substring(77, 98).trim());
			m_records[r].l_tax           = Float.parseFloat(tR.substring(99,120).trim());
			m_records[r].l_returnFlag    =                  tR.substring(121, 122).trim();
			m_records[r].l_lineStatus    =                  tR.substring(123, 124).trim();
			
			try
			{
				m_records[r].l_shipDate      = (Date)formatter.parse(tR.substring(125, 143).trim());
				m_records[r].l_commitDate    = (Date)formatter.parse(tR.substring(144, 162).trim());
				m_records[r].l_receiptDate   = (Date)formatter.parse(tR.substring(163, 181).trim());
			} 
			catch(ParseException pe) 
			{
				System.err.println(pe.toString()); // send to error stream
			}
			
			m_records[r].l_shipInstruct  = tR.substring(182, 201).trim();
			m_records[r].l_shipMode      = tR.substring(202, 211).trim();
			m_records[r].l_comment       = tR.substring(212, 331).trim();

			r++;
		}
	}
}
