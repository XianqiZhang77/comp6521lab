package comp6521.lab.com.Util;

import comp6521.lab.com.MemoryManager;
import comp6521.lab.com.Pages.Page;
import comp6521.lab.com.Records.IntegerRecordElement;
import comp6521.lab.com.Records.Record;

public class RecordNumberToKeyPF<T extends Page<?> > extends ProcessingFunction<T, IntegerRecordElement> 
{
	String skey;
	public String filename;
	public key_page keys;
	
	public RecordNumberToKeyPF( int[] input, Class<T> c, String key, String _filename )
	{
		super( input, c );
		keys = null;
		skey = key;
		filename = _filename;
	}
	
	public void ProcessStart()
	{
		MemoryManager.getInstance().AddPageType(key_page.class, filename);
		keys = MemoryManager.getInstance().getEmptyPage(key_page.class, filename);
	}
	
	public void Process( Record r )
	{
		key_record key = new key_record();
		key.get("key").set(r.get(skey));
		keys.AddRecord(key);		
	}
	
	public int[] EndProcess()
	{
		return null;
	}
	
	public void Clear()
	{
		MemoryManager.getInstance().freePage(keys);
		keys = null;
	}
}