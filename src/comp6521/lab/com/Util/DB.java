package comp6521.lab.com.Util;

import java.util.ArrayList;

import comp6521.lab.com.BPlusTree;
import comp6521.lab.com.MemoryManager;
import comp6521.lab.com.Pages.Page;

public class DB 
{
	public static int[] ProcessingLoop( ProcessingFunction<?,?> pf )
	{
		pf.ProcessStart();
		
		Page<?> page = null;
		int pageNb = -1;
		
		for( int i = 0; i < pf.array.length; i++ )
		{
			// Compute page number & record index
			int cp = pf.array[i] / pf.pageSize;
			int r  = pf.array[i] % pf.pageSize;
			
			// Get new page if needed
			if( cp != pageNb )
			{
				if( page != null )
					MemoryManager.getInstance().freePage(page);
				
				pageNb = cp;
				page   = MemoryManager.getInstance().getPage(pf.pageClass, pageNb);
			}
				
			pf.Process( page.m_records[r] );
		}
		if( page != null )
			MemoryManager.getInstance().freePage(page);	
		
		return pf.EndProcess();
	}
	
	// Loop on records in a page -> process the key value
	public static <T extends Page<?> > ArrayList<ArrayList<Integer>> ReverseProcessingLoopAAI( Page<?> page, Class<T> pc, BPlusTree<?,?> bt, String key)
	{
		String filename = page.m_filename;
		ArrayList<ArrayList<Integer>> reclist = new ArrayList<ArrayList<Integer>>();
		int pageNb = page.m_pageNumber;
		
		Page<?> curpage = null;
		int p = MemoryManager.getInstance().GetNumberOfPages(pc, filename);
		
		// If the current page hasn't been written to disk, we must count it also in p.
		if( page.m_cleanupToDo )
			p++;
		
		for( int i = 0; i < p; i++ )
		{
			if( i == pageNb )
				curpage = page;
			else
				curpage = MemoryManager.getInstance().getPage(pc, i, filename);
			
			for( int j = 0; j < curpage.m_records.length; j++ )
			{
				if( curpage.m_records[j] != null )
					reclist.add( bt.GetList(curpage.m_records[j].get(key)));
			}
			
			if( i != pageNb )
				MemoryManager.getInstance().freePage(curpage);
		}
		
		return reclist;
	}
	
	public static <T extends Page<?> > ArrayList<Integer> ReverseProcessingLoopAI( Page<?> page, Class<T> pc, BPlusTree<?,?> bt, String key)
	{
		ArrayList<ArrayList<Integer>> AAI = ReverseProcessingLoopAAI(page, pc, bt, key);
		
		// Collapse array
		ArrayList<Integer> reclist = new ArrayList<Integer>();
		
		for( int i = 0; i < AAI.size(); i++ )
			reclist.addAll(AAI.get(i));
				
		return reclist;
	}	
	
	public static ArrayList<Integer> Intersect( ArrayList<Integer> a, ArrayList<Integer> b )
	{
		ArrayList<Integer> intersection = new ArrayList<Integer>();
		
		for(int i = 0; i < a.size(); i++)
		{
			boolean found = false;
			for(int j = 0; j < b.size() && !found; j++)
			{
				if( a.get(i).intValue() == b.get(j).intValue() )
				{
					intersection.add(a.get(i));
					found = true;
				}
			}
		}
		return intersection;
	}
}