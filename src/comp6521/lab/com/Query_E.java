package comp6521.lab.com;

import comp6521.lab.com.Pages.NationPage;
import comp6521.lab.com.Pages.Page;
import comp6521.lab.com.Pages.PartSuppPage;
import comp6521.lab.com.Pages.SupplierPage;
import comp6521.lab.com.Records.FloatRecordElement;
import comp6521.lab.com.Records.IntegerRecordElement;
import comp6521.lab.com.Records.NationRecord;
import comp6521.lab.com.Records.PartSuppRecord;
import comp6521.lab.com.Records.Record;
import comp6521.lab.com.Records.SupplierRecord;

public class Query_E {
	public void ProcessQuery(String innerName, String outerName)
	{
		// Zeroeth pass:
		// Initialise custom pages
		
		// Non grouped results
		MemoryManager.getInstance().AddPageType( QE_Page.class, "qe_f.txt" );
		// Grouped results
		MemoryManager.getInstance().AddPageType( QEGroups_Page.class, "qeg_f.txt" );
		// Kept nations keys
		MemoryManager.getInstance().AddPageType( NationSubsetPage.class,  "qe_ns.txt");
		MemoryManager.getInstance().AddPageType( NationSubsetPage.class, "qe_inner_ns.txt");
		// Kept supplier keys
		MemoryManager.getInstance().AddPageType( SupplierSubsetPage.class, "qe_ss.txt");
		MemoryManager.getInstance().AddPageType( SupplierSubsetPage.class, "qe_inner_ss.txt");
				
		// Info for the "having" clause:
		double totalValue = 0;
		
		// First pass:
		// Construct the (ps_suppkey, value) records, without any aggregation, sorting or anything.
		//   -> 1- Find nation(s) with name 'UNITED STATES' .. likely to be only one
		NationSubsetPage snPage = MemoryManager.getInstance().getEmptyPage( NationSubsetPage.class );
		NationSubsetPage sniPage = MemoryManager.getInstance().getEmptyPage( NationSubsetPage.class, "qe_inner_ns.txt" );
		
		NationPage nationPage = null;
		int n_p = 0;
		while( (nationPage = MemoryManager.getInstance().getPage( NationPage.class, n_p++) ) != null )
		{
			NationRecord[] nationRecords = nationPage.m_records;
			for( int i = 0; i < nationRecords.length; i++ )
			{
				if( nationRecords[i].get("n_name").getString().compareToIgnoreCase(outerName) == 0)
				{
					NationSubsetRecord ns = new NationSubsetRecord();
					ns.get("n_nationKey").set( nationRecords[i].get("n_nationKey"));
					snPage.AddRecord( ns );
				}
				
				if( nationRecords[i].get("n_name").getString().compareToIgnoreCase(innerName) == 0)
				{
					NationSubsetRecord nsi = new NationSubsetRecord();
					nsi.get("n_nationKey").set( nationRecords[i].get("n_nationKey"));
					sniPage.AddRecord(nsi);
				}
			}
			
			MemoryManager.getInstance().freePage(nationPage);
		}
		// Write back the nation subset kept
		MemoryManager.getInstance().freePage( snPage );
		snPage = null;
		
		MemoryManager.getInstance().freePage( sniPage );
		sniPage = null;
		
		//   -> 2- Find suppliers with matching s_nationkey.
		SupplierSubsetPage ssPage = MemoryManager.getInstance().getEmptyPage( SupplierSubsetPage.class );
		
		// Loop on all subset nations
		int sn_p = 0;
		while( (snPage = MemoryManager.getInstance().getPage( NationSubsetPage.class, sn_p++)) != null )
		{
			NationSubsetRecord[] nsRecords = snPage.m_records;

			// Note: instead of looping for nations, we'll loop for regions first.. should be much less I/Os.
			SupplierPage suppPage = null;
			int s_p = 0;
			while( (suppPage = MemoryManager.getInstance().getPage( SupplierPage.class, s_p++)) != null )
			{
				SupplierRecord[] suppRecords = suppPage.m_records;
				for(int i = 0; i < nsRecords.length; i++ )
				{
					for( int j = 0; j < suppRecords.length; j++ )
					{
						if( suppRecords[j].get("s_nationKey").getInt() == nsRecords[i].get("n_nationKey").getInt() )
						{
							// We found a good supplier
							SupplierSubsetRecord ss = new SupplierSubsetRecord();
							ss.get("s_suppKey").set( suppRecords[j].get("s_suppKey"));
							
							ssPage.AddRecord(ss);
						}
					}
				}
				
				MemoryManager.getInstance().freePage( suppPage );
			}
			
			MemoryManager.getInstance().freePage(snPage);
		}
		
		// Write back the kept suppliers
		MemoryManager.getInstance().freePage( ssPage );
		ssPage = null;
		
		SupplierSubsetPage ssiPage = MemoryManager.getInstance().getEmptyPage( SupplierSubsetPage.class, "qe_inner_ss.txt");
		
		sn_p = 0;
		while( (sniPage = MemoryManager.getInstance().getPage( NationSubsetPage.class, sn_p++, "qe_inner_ns.txt")) != null )
		{
			NationSubsetRecord[] nsRecords = sniPage.m_records;

			// Note: instead of looping for nations, we'll loop for regions first.. should be much less I/Os.
			SupplierPage suppPage = null;
			int s_p = 0;
			while( (suppPage = MemoryManager.getInstance().getPage( SupplierPage.class, s_p++)) != null )
			{
				SupplierRecord[] suppRecords = suppPage.m_records;
				for(int i = 0; i < nsRecords.length; i++ )
				{
					for( int j = 0; j < suppRecords.length; j++ )
					{
						if( suppRecords[j].get("s_nationKey").getInt() == nsRecords[i].get("n_nationKey").getInt() )
						{
							// We found a good supplier
							SupplierSubsetRecord ss = new SupplierSubsetRecord();
							ss.get("s_suppKey").set( suppRecords[j].get("s_suppKey"));
							
							ssiPage.AddRecord(ss);
						}
					}
				}
				
				MemoryManager.getInstance().freePage( suppPage );
			}
			
			MemoryManager.getInstance().freePage(sniPage);
		}
		
		// Write back the kept suppliers
		MemoryManager.getInstance().freePage( ssiPage );
		ssiPage = null;

		//   -> 3- Write all results from PartSupp with the ps_suppKey matching one found
		QE_Page qe = MemoryManager.getInstance().getEmptyPage( QE_Page.class );
		
		// Loop on all subset suppliers
		int ss_p = 0;
		while( (ssPage = MemoryManager.getInstance().getPage( SupplierSubsetPage.class, ss_p++)) != null )
		{
			SupplierSubsetRecord[] ssRecords = ssPage.m_records;
			
			// Note: instead of looping on the subset suppliers now, we'll loop on the partsupp's.
			PartSuppPage psPage = null;
			int psp = 0;
			while( (psPage = MemoryManager.getInstance().getPage( PartSuppPage.class, psp++)) != null )
			{
				PartSuppRecord[] psRecords = psPage.m_records;
				
				for(int i = 0; i < ssRecords.length; i++)
				{
					for(int j = 0; j < psRecords.length; j++)
					{
						if( psRecords[j].get("ps_suppKey").getInt() == ssRecords[i].get("s_suppKey").getInt() )
						{
							// We found a partsupp entry for a supplier in the united states.
							QE_Record qer = new QE_Record();
							qer.get("ps_partKey").set( psRecords[j].get("ps_partKey") );
							double value = psRecords[j].get("ps_supplyCost").getFloat() * (double)(psRecords[j].get("ps_availQty").getInt());
							qer.get("value").setFloat( value );
							
							qe.AddRecord( qer );
						}
					}
				}
				
				MemoryManager.getInstance().freePage(psPage);
			}
			
			MemoryManager.getInstance().freePage(ssPage);
		}
		// Write back the kept qe results
		MemoryManager.getInstance().freePage(qe);
		qe = null;
		
		// Compute the total value now :
		ss_p = 0;
		while( (ssiPage = MemoryManager.getInstance().getPage( SupplierSubsetPage.class, ss_p++, "qe_inner_ss.txt")) != null )
		{
			SupplierSubsetRecord[] ssRecords = ssiPage.m_records;
			
			// Note: instead of looping on the subset suppliers now, we'll loop on the partsupp's.
			PartSuppPage psPage = null;
			int psp = 0;
			while( (psPage = MemoryManager.getInstance().getPage( PartSuppPage.class, psp++)) != null )
			{
				PartSuppRecord[] psRecords = psPage.m_records;
				
				for(int i = 0; i < ssRecords.length; i++)
				{
					for(int j = 0; j < psRecords.length; j++)
					{
						if( psRecords[j].get("ps_suppKey").getInt() == ssRecords[i].get("s_suppKey").getInt() )
						{
							// Keep track of total , will be used for the "having" clause
							double value = psRecords[j].get("ps_supplyCost").getFloat() * (double)(psRecords[j].get("ps_availQty").getInt());
							totalValue += value;
						}
					}
				}
				
				MemoryManager.getInstance().freePage(psPage);
			}
			
			MemoryManager.getInstance().freePage(ssiPage);
		}
		
		// Second pass:
		// perform a 2PMMS on the data, sorting on ps_partkey (ascending or descending)
		TPMMS<?> sort = new TPMMS<QE_Page>(QE_Page.class, "qe_f.txt");
		String sortedFilename = sort.Execute();
		MemoryManager.getInstance().AddPageType(QE_Page.class, sortedFilename);
		
		ThirdPass(totalValue, sortedFilename, "qeg_f.txt");
		
		// Fourth pass 
		// Sort the groups by value in descending order
		sort = new TPMMS<QEGroups_Page>( QEGroups_Page.class, "qeg_f.txt");
		String groupedSorted = sort.Execute();
		MemoryManager.getInstance().AddPageType(QEGroups_Page.class, groupedSorted);
		
		// Last: output results
		OutputResults( groupedSorted );
	}
	
	public void ThirdPass(double totalValue, String qeFile, String groupsFile)
	{
		// Third pass:
		// Creating the groups and applying the having clause
		// We now assume that the results in the QE table are sorted
		QEGroups_Page qeg = MemoryManager.getInstance().getEmptyPage( QEGroups_Page.class, groupsFile );
		QE_Page qe = null;
				
		int previousKey = -1;
		QEGroups_Record group = null;
		
		int qe_p = 0;
		while( (qe = MemoryManager.getInstance().getPage( QE_Page.class, qe_p++, qeFile)) != null )
		{
			QE_Record[] qeRecords = qe.m_records;
			for( int i = 0; i < qeRecords.length; i++ )
			{
				if( qeRecords[i].get("ps_partKey").getInt() == previousKey )
				{
					assert(group != null);
					// Update total value of the group
					group.get("value").setFloat( group.get("value").getFloat() + qeRecords[i].get("value").getFloat() );
				}
				else
				{
					// Write group
					if( group != null && group.get("value").getFloat() > totalValue * 0.0001 )
						qeg.AddRecord(group);
					
					group = new QEGroups_Record();
					group.get("ps_partKey").set( qeRecords[i].get("ps_partKey") );
					group.get("value").set( qeRecords[i].get("value" ) );
					
					previousKey = qeRecords[i].get("ps_partKey").getInt();	
				}
			}			
			
			MemoryManager.getInstance().freePage(qe);
		}
		// Write last group
		if( group != null && group.get("value").getFloat() > totalValue * 0.0001 )
		{
			qeg.AddRecord(group);
		}
		
		// Write back page.
		MemoryManager.getInstance().freePage(qeg);
		qeg = null;
	}
	
	public void OutputResults(String groupsFile)
	{
		QEGroups_Page qeg = null;
		// Finally,
		// Write the results
		System.out.println("ps_partKey\tvalue");
		// Here we assume the groups are sorted.
		int qeg_p = 0;
		while( (qeg = MemoryManager.getInstance().getPage( QEGroups_Page.class, qeg_p++, groupsFile)) != null )
		{
			QE_Record[] qegRecords = qeg.m_records;
			for(int i = 0; i < qegRecords.length; i++)
			{
				System.out.println( qegRecords[i].get("ps_partKey").getInt() + "\t" + qegRecords[i].get("value").getFloat() );
			}
			
			MemoryManager.getInstance().freePage(qeg);
		}
	}
}

///////////////////////////////
// Subclasses implementation //
///////////////////////////////
class QE_Record extends Record
{
	public QE_Record()
	{
		AddElement( "ps_partKey", new IntegerRecordElement() );
		AddElement( "value",      new FloatRecordElement()   );
	}
	
	public int compareTo(Record rec)
	{
		return get("ps_partKey").CompareTo( rec.get("ps_partKey"));
	}
}

class QE_Page extends Page<QE_Record>
{
	public QE_Page()
	{
		super();
		m_nbRecordsPerPage = 50;
	}
	public QE_Record[] CreateArray(int n){ return new QE_Record[n]; }
	public QE_Record   CreateElement(){ return new QE_Record(); }
}

class QEGroups_Record extends QE_Record
{
	public QEGroups_Record() { super(); }
	public int compareTo(Record rec)
	{
		return - get("value").CompareTo(rec.get("value"));
	}
}

class QEGroups_Page extends Page<QEGroups_Record>
{
	public QEGroups_Page()
	{
		super();
		m_nbRecordsPerPage = 50;
	}
	public QEGroups_Record[] CreateArray(int n){ return new QEGroups_Record[n]; }
	public QEGroups_Record   CreateElement(){ return new QEGroups_Record(); }		
}

// First phase records
class NationSubsetRecord extends Record
{
	public NationSubsetRecord()
	{
		AddElement( "n_nationKey", new IntegerRecordElement() );
	}
}

class NationSubsetPage extends Page<NationSubsetRecord>
{
	public NationSubsetPage()
	{
		super();
		m_nbRecordsPerPage = 100;
	}
	public NationSubsetRecord[] CreateArray(int n){ return new NationSubsetRecord[n]; }
	public NationSubsetRecord   CreateElement(){ return new NationSubsetRecord(); }
}

class SupplierSubsetRecord extends Record
{
	public SupplierSubsetRecord()
	{
		AddElement( "s_suppKey" , new IntegerRecordElement() );
	}
}

class SupplierSubsetPage extends Page<SupplierSubsetRecord>
{
	public SupplierSubsetPage()
	{
		super();
		m_nbRecordsPerPage = 100;
	}
	public SupplierSubsetRecord[] CreateArray(int n){ return new SupplierSubsetRecord[n]; }
	public SupplierSubsetRecord   CreateElement(){ return new SupplierSubsetRecord(); }	
}
