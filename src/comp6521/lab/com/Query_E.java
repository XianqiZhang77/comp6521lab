package comp6521.lab.com;

import comp6521.lab.com.Records.Record;

public class Query_E {
	public void ProcessQuery()
	{
		// First pass:
		// Construct the (ps_suppkey, value) records, without any aggregation, sorting or anything.
		//   -> 1- Find nation(s) with name 'UNITED STATES' .. likely to be only one
		//   -> 2- Find suppliers with matching s_nationkey.
		//   -> 3- Write all results from PartSupp with the ps_suppKey matching one found
		
		// Second pass:
		// perform a 2PMMS on the data, sorting on ps_partkey
		// If possible, compute sum in the first phase of the 2PMMS
		
	}
	
	public class QE_Record extends Record
	{
		// 33 + 2 chars
		public int   ps_partKey;
		public float value;
		
		public void Parse(String data)
		{
			ps_partKey = Integer.parseInt(data.substring(0,10).trim());
			value      = Float.parseFloat(data.substring(11,32).trim());			
		}
		
		public String Write()
		{
			String data = "";
			data += String.format("%1$-11d", ps_partKey);
			data += String.format("%1$-22f", value);
			
			return data;
		}
	}
}
