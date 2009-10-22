package comp6521.lab.com.Pages;
import comp6521.lab.com.Records.CustomerRecord;

public class CustomerPage extends Page<CustomerRecord> {
	public void Construct( char[] rawData ) 
	{
		String rawPageString    = String.valueOf(rawData);
		String[] rawRecords     = rawPageString.split("\n");
		
		// Count nb. of non-null raw records
		int n = 0;
		for( int i = 0; i < rawRecords.length; i++ )
			if( rawRecords[i].trim().compareTo("") != 0 )
				n++;
		
		m_records = new CustomerRecord[n];
		
		int r = 0;
		for( int i = 0; i < rawRecords.length; i++ )
		{
			String tR = rawRecords[i];
			
			if( tR.trim().compareTo("") == 0)
				continue;
			
			m_records[r] = new CustomerRecord();
			// Fill in the record
			m_records[r].c_custkey    = Integer.parseInt( tR.substring(0,    10).trim());
			m_records[r].c_name       =                   tR.substring(11,   35).trim() ;
			m_records[r].c_address    =                   tR.substring(36,   85).trim() ;
			m_records[r].c_nationKey  = Integer.parseInt( tR.substring(86,   96).trim());
			m_records[r].c_phone      =                   tR.substring(97,  116).trim() ;
			m_records[r].c_acctBal    = Float.parseFloat( tR.substring(117, 138).trim());
			m_records[r].c_mktSegment =                   tR.substring(139, 153).trim() ;
			m_records[r].c_comment    =                   tR.substring(154, 273).trim() ;       
			
			r++;
		}
	}	
}
