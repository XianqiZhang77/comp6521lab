package comp6521.lab.com.Util;

import java.util.ArrayList;
import java.util.Arrays;

import comp6521.lab.com.BPlusTree;
import comp6521.lab.com.MemoryManager;
import comp6521.lab.com.Pages.Page;
import comp6521.lab.com.Records.Record;
import comp6521.lab.com.Records.RecordElement;

public abstract class ProcessingFunction<T extends Page<?>, S extends RecordElement >
{
	int[] array;
	int   pageSize;	
	Class<T> pageClass;
	
	// typical arguments
	ArrayList<Integer> records;
	BPlusTree<?,?> index;
	String key;
	Class<S> REClass;
	
	// Minimal interface
	public ProcessingFunction(){}
	
	public ProcessingFunction( int[] input, Class<T> c )
	{
		Init(input, c);
	}
	
	public void Init( int[] input, Class<T> c )
	{
		array = input;
		pageSize = MemoryManager.getInstance().GetNumberOfRecordsPerPage(c);
		pageClass = c;	}
	
	// Common interface
	public ProcessingFunction( int[] input, Class<T> c, BPlusTree<?,?> idx, Class<S> rc, String _key)
	{
		array = input;
		pageSize = MemoryManager.getInstance().GetNumberOfRecordsPerPage(c);
		pageClass = c;
		index = idx;
		REClass = rc;
		key     = _key;
		
		records = new ArrayList<Integer>();
	}
	
	// Used in the processing loop
	public void Process( Record r )
	{
		RecordElement rel = CreateRecordElement();
		rel.set( r.get(key) );
		
		ArrayList<Integer> sublist = index.GetList(rel);
		records.addAll(sublist);
	}
	
	// Final call of the processing loop
	public int[] EndProcess()
	{
		if( records == null )
			return null;
		
		int[] outputArray = new int[records.size()];
		
		for(int i = 0; i < outputArray.length; i++)
			outputArray[i] = records.get(i);
		
		Arrays.sort(outputArray);
		
		return outputArray;
	}	
	
	public void ProcessStart()
	{
		
	}
	
	private RecordElement CreateRecordElement()
	{
		RecordElement rel = null;
		try {
			rel = REClass.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return rel;
	}
}
