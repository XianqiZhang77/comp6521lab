/**
 * November 24, 2009
 * TPMMS.java: Two-Phase Multiway Merge Sort Algorithm Implementation 
 */
package comp6521.lab.com;

import comp6521.lab.com.Pages.*;
import comp6521.lab.com.Records.*;

import java.util.Arrays;
import java.util.ArrayList;

/**
 * @author dimitri.tiago
 */
public class TPMMS <T extends Page<?>> 
{
	private Class<T> myPageType;	// store page class
	
	// constructor
	public TPMMS(Class<T> c)
	{
		// initialise fields
		myPageType = c;
	}
	
	// execute TPMMS Algorithm
	public boolean Execute()
	{
		DoPhaseI();		// TPMMS Phase I
		DoPhaseII(); 	// TPMMS Phase II
		
		return true;
	}
	
	// perform phase 1
	private boolean DoPhaseI()
	{
		// get m pages
		int currMemSize   = MemoryManager.getInstance().RemainingMemory();	// get available memory
		int pageSize      = MemoryManager.getInstance().GetPageSize(myPageType);	// get page size
		int numberOfPages = MemoryManager.getInstance().GetNumberOfPages(myPageType, "qd_resultset.txt");	// get number of r pages
		int numMemPages = (currMemSize/pageSize);	// number of pages we can fit in m
		
		// TODO: remove line 
		System.out.printf("Current Memory Size: %s\nPage Size: %s\nNumber of Pages (R): %s\n", currMemSize, pageSize, numberOfPages);
		
		// TODO: remove line 
		System.out.printf("#Pages That Fit in M: %s\n", numMemPages);
		
		T currPage = null;	// current page
		ArrayList<Record> memRecords = new ArrayList<Record>();
		for ( int pageCount = 0; pageCount <= (numberOfPages-1); pageCount++ )	// while there are pages to be read
		{
			currPage = MemoryManager.getInstance().getPage(myPageType, pageCount);	// get page
			
			if (currPage != null)	// ensure currPage is not null
			{ 
				for (Record rec : currPage.m_records)	// add array contents to master array-list
				{
					memRecords.add(rec);
				}
				
				// TODO: need a way to free all pages at once
				MemoryManager.getInstance().freePage(currPage); // free current page
				
				if ( (((pageCount + 1) % numMemPages) == 0) || ((pageCount + 1) == numberOfPages) )	// if memory is full or we have read all pages
				{
					// TODO: remove line 
					System.out.printf("%s\n", "Perform QuickSort()!");
					
					// TODO: implement quick-sort
					// perform quick-sort on memory contents
					Object[] recObjectArray = memRecords.toArray();
					Arrays.sort(recObjectArray);
					
					// TODO: remove testing code block
					for (Object o : recObjectArray)
					{
						Record r = (Record) o;
						
						System.out.println(r.get("n_name").getString());
					}		
					
					// empty array list
					memRecords.clear();
										
					// TODO: remove line 
					System.out.printf("%s %s\n", "Available Memory:", MemoryManager.getInstance().RemainingMemory());
				}	
			}
		}
		
		// TODO: use try catch to send ack of succ/failure
		return true;
	}
	
	// perform phase 2
	private boolean DoPhaseII()
	{
		
		return true;
	}
}
