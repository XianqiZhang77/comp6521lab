package comp6521.lab.com.Util;

import comp6521.lab.com.MemoryManager;
import comp6521.lab.com.Pages.Page;

public class DB 
{
	public static int[] ProcessingLoop( ProcessingFunction<?,?> pf )
	{
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
}