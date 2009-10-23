package comp6521.lab.com.Pages;

import comp6521.lab.com.Records.SupplierRecord;

public class SupplierPage extends Page<SupplierRecord> {
	@Override
	public void Construct(char[] rawData) 
	{
		// TODO Auto-generated method stub
		String rawPageString    = String.valueOf(rawData);
		String[] rawRecords     = rawPageString.split("\n");
		
		// Count nb. of non-null raw records
		int n = 0;
		for( int i = 0; i < rawRecords.length; i++ )
			if( rawRecords[i].trim().compareTo("") != 0 )
				n++;
		
		m_records = new SupplierRecord[n];
		
		int r = 0;
		for( int i = 0; i < rawRecords.length; i++ )
		{
			String tR = rawRecords[i];
			
			if( tR.trim().compareTo("") == 0)
				continue;
			
			m_records[r] = new SupplierRecord();
			
			m_records[r].s_suppKey   = Integer.parseInt(tR.substring(0, 10).trim());
			m_records[r].s_name      =                  tR.substring(11, 35).trim();
			m_records[r].s_address   =                  tR.substring(36, 85).trim();
			m_records[r].s_nationKey = Integer.parseInt(tR.substring(86, 96).trim());
			m_records[r].s_phone     =                  tR.substring(97, 126).trim();
			m_records[r].s_acctBal   = Float.parseFloat(tR.substring(127, 148).trim());
			m_records[r].s_comment   =                  tR.substring(149, 268).trim();
			
			r++;
		}
	}
}
