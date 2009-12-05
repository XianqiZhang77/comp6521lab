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
	// fields
	private Class<T> myPageType;	// page class
	private String filename;		// filename of file to sort
	
	private int pageSize;			// page size (i.e. blocksize)
	private int numberOfPages;		// number of pages of R (i.e. B(R))
	private int numMemPages;		// number of R pages we can fit in M
	private int numInputBuffers;	// number of phase 2 input buffers
	
	// constructor
	public TPMMS(Class<T> c, String filename)
	{
		// initialise fields
		myPageType = c;				// set page type
		this.filename = filename;	// set page filename 
	
		pageSize       = MemoryManager.getInstance().GetPageSize(myPageType);					// get page size
		numberOfPages  = MemoryManager.getInstance().GetNumberOfPages(myPageType, filename);	// get number of r pages
		numMemPages    = (MemoryManager.getInstance().RemainingMemory()/pageSize);				// number of pages we can fit in m
		numInputBuffers = numMemPages - 1;														// set # of input buffers for phase 2	
	}
	
	// execute TPMMS Algorithm
	public String Execute()
	{
		DoPhaseI();		// TPMMS Phase I
		return DoPhaseII(); 	// TPMMS Phase II
	}
	
	// perform phase 1
	private boolean DoPhaseI()
	{
		// get m pages
				
		// TODO: remove line 
		System.out.printf("Current Memory Size: %s\nPage Size: %s\nNumber of Pages (R): %s\n", MemoryManager.getInstance().RemainingMemory(), pageSize, numberOfPages);
		System.out.printf("#Pages That Fit in M: %s\n", numMemPages);
		
		T currPage = null;	// current page
		ArrayList<Record> memRecords = new ArrayList<Record>();		// memory records array for quick-sort
		for ( int pageCount = 0; pageCount <= (numberOfPages-1); pageCount++ )	// while there are pages to be read
		{
			currPage = MemoryManager.getInstance().getPage(myPageType, pageCount);	// get page
			
			if (currPage != null)	// ensure currPage is not null
			{	
				int numPageRecords = currPage.m_nbRecordsPerPage;	// number of records per page
				
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
					
					// perform quick-sort on memory contents
					Object[] recObjectArray = memRecords.toArray();
					Arrays.sort(recObjectArray);
										
					// TODO: test this to make sure it works correctly 
					// build output string
					StringBuffer strBuffer = new StringBuffer();	// buffer for output string
					for (int recordCount = 0; recordCount <= ((numMemPages * numPageRecords) - 1); recordCount++)	
					{
						if (recordCount < recObjectArray.length)	// append to string all records in memory
						{
							strBuffer.append(recObjectArray[recordCount].toString());	// add record string
						}
						else
						{
							strBuffer.append("\r\n");	// add blank records to complete pages (e.g. 4 exact pages)							
						}	
					}
					
					// output record string
					PageManagerSingleton.getInstance().writePage(filename + "phase1.txt", strBuffer.toString());
					
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
	private String DoPhaseII()
	{		
		// TODO: remove lines
		System.out.println("PHASE II");
		System.out.printf("Availabe Memory (M) = %s\n", MemoryManager.getInstance().RemainingMemory());
		System.out.printf("M(R) = %s\n", numMemPages);
		System.out.printf("B(R) = %s\n", numberOfPages);
		System.out.printf("#Input Buffers = %s\n", numInputBuffers);
		
		String inFilename  =  filename + "phase1.txt";	// input file
		String outFilename =  filename + "pass1.txt";  // output file
		
		// add input pages to memory manager for tracking
		MemoryManager.getInstance().AddPageType(myPageType, inFilename);
		
		// determine number of sublists
		int numOfSubLists = (int) Math.ceil( ((double) numberOfPages / (double) numMemPages) );
					
		// determine number of passes needed
		int numOfPasses = (int) Math.ceil( (Math.log10(numOfSubLists) / Math.log10(numInputBuffers)) );
				
		// for each phase 2 pass
		for (int passCount = 1; passCount <= numOfPasses; passCount++)
		{
			// add output filename to page manager for tracking get empty pages.
			MemoryManager.getInstance().AddPageType(myPageType, outFilename);
			T outPage = MemoryManager.getInstance().getEmptyPage(myPageType, outFilename);	// get empty page (i.e. output buffer)
		
			// TODO: Remove Test Line
			System.out.printf("Get Out Page. Availabe Memory (M) = %s\n", MemoryManager.getInstance().RemainingMemory());
			
			// for each group of m-1 sublists
			int numGroups = (int) Math.ceil((double) numOfSubLists / (double) numInputBuffers);
			for (int groupCount = 0; groupCount < numGroups; groupCount++)
			{
				int startList = groupCount * numInputBuffers;		// starting list of group in file
				int endList   = startList + (numInputBuffers - 1);	// end list of group in file
				
				// for each sorted list in group (m-1)
				ArrayList<T> buffers = new ArrayList<T>();	// page buffers
				for (int sortedList = startList; sortedList <= endList; sortedList++)
				{
					//int blockNumber = (sortedList * numMemPages * ((int) Math.pow(2, (passCount - 1))));	// block number to retrieve (i.e. head of each list)
					int blockNumber = (sortedList * numMemPages * ((int) Math.pow(numInputBuffers, (passCount - 1))));	// block number to retrieve (i.e. head of each list)
					T currPage = MemoryManager.getInstance().getPage(myPageType, blockNumber, inFilename);	// get block from disk
					
					// TODO: Remove Test Line
					System.out.printf("Get Curr Page. Availabe Memory (M) = %s\n", MemoryManager.getInstance().RemainingMemory());
					
					if (currPage != null)
					{
						buffers.add(currPage);	// add page to memory buffer
					}
				}
				
				// merge buffers
				while (buffers.size() > 0)	// while buffers have blocks in them											
				{							
					ArrayList<Record> records = new ArrayList<Record>();	// records to compare --at heads of arrays
					for (int inBuffer = 0; inBuffer < buffers.size(); inBuffer++)	// for each page/buffer get record at head of array
					{
						records.add(buffers.get(inBuffer).m_records[0]);	// add record
					}
					
					Record myRecord     = records.get(0); // get first record
					int myRecListNumber = 0;	// my record's sub list number
					for (int bufNumber = 1; bufNumber < buffers.size(); bufNumber++)	// compare to obtain lowest record
					{
						if ( myRecord.compareTo(records.get(bufNumber)) >= 0 )	// if lowest
						{
							myRecord = records.get(bufNumber);	// keep record
							myRecListNumber = bufNumber;	// keep buffer number
						}					
					}
					
					// remove record from page/block (i.e. buffer) or remove page entirely if no records are left
					if ( buffers.get(myRecListNumber).m_records.length > 1)	// are there at least two records?
					{
						Record[] tempRecordArray = new Record[buffers.get(myRecListNumber).m_records.length - 1];	// temp array
						
						for (int i = 0; i < tempRecordArray.length; i++)	// eliminate by not copying element to array
						{
							tempRecordArray[i] = buffers.get(myRecListNumber).m_records[i+1];	// skip copy of first item (i.e. lowest)				
						}
					
						buffers.get(myRecListNumber).SetRecordArray(tempRecordArray);// replace with array with removed lowest record
					}
					else	// this is the last record
					{
						buffers.get(myRecListNumber).SetRecordArray(new Record[0]);// remove last record
					}
						
					// add to output buffer	TODO: remove test line to system.out
					System.out.printf("Lowest Record: %s\n", myRecord.toString());
					outPage.AddRecord(myRecord);	// add record to output page
					
					// TODO: there is a bug in MemoryManager.freepage: pages are overwritten due to pageNum never changing
					//if (outPage.m_insertionIndex == 0 )	// we just wrote a page
					//{
					//	outPage.m_pageNumber++; // increment page number to avoid overwriting page
					//}
							
					// determine if we need to load next block
					if (buffers.get(myRecListNumber).m_records.length == 0)	// if buffer is exhausted
					{
						// get next page of sublist
						//if ( ((buffers.get(myRecListNumber).m_pageNumber + 1) % (numMemPages * ((int) Math.pow(2, (passCount - 1)))) ) != 0 )	// are there remaining pages in this 
						if ( ((buffers.get(myRecListNumber).m_pageNumber + 1) % (numMemPages * ((int) Math.pow(numInputBuffers, (passCount - 1)))) ) != 0 )	// are there remaining pages in this
						{																														// sublist?	
							// get next page
							MemoryManager.getInstance().freePage(buffers.get(myRecListNumber));	// free page from memory
							
							// TODO: Remove Test Line
							System.out.printf("Free Page. Availabe Memory (M) = %s\n", MemoryManager.getInstance().RemainingMemory());
							
							T nextPage = MemoryManager.getInstance().getPage(myPageType, buffers.get(myRecListNumber).m_pageNumber + 1, inFilename);
							
							// TODO: Remove Test Line
							System.out.printf("Get Next Page. Availabe Memory (M) = %s\n", MemoryManager.getInstance().RemainingMemory());
							
							buffers.remove(myRecListNumber);	// remove exhausted page
							
							if ((nextPage != null) && (nextPage.m_records.length != 0))	// ensure we are not reading a null page from the end of last sublist
							{
								buffers.add(nextPage);		// add next page
							}
							else if (nextPage  == null)
							{
								// do nohting
							}
							else if (nextPage.m_records.length == 0)	// TODO: ensure this is necessary
							{
								// TODO: ensure freeing a page here is correct
								MemoryManager.getInstance().freePage(nextPage);	// free page from memory
								
								// TODO: Remove Test Line
								System.out.printf("Array[0]. Availabe Memory (M) = %s\n", MemoryManager.getInstance().RemainingMemory());								
							}
						}
						else
						{
							MemoryManager.getInstance().freePage(buffers.get(myRecListNumber));	// free page from memory
							
							// TODO: Remove Test Line
							System.out.printf("End of Buffers. Free Page. Availabe Memory (M) = %s\n", MemoryManager.getInstance().RemainingMemory());
							
							buffers.remove(myRecListNumber);	// remove page from buffers
						}
					}
				}
			}
			
			// TODO: do we free outpage again? set it to null? doesn't free it!!!! (is it because of the auto writes?)
			//if ( outPage.m_insertionIndex > 0)
			//{
				MemoryManager.getInstance().freePage(outPage);
				
				// TODO: Remove Test Line
				System.out.printf("End Pass. Free Out Page. Availabe Memory (M) = %s\n", MemoryManager.getInstance().RemainingMemory());
			//}
			
			// set next input and output files
			inFilename  = filename + "pass" + passCount + ".txt";
			outFilename = filename + "pass" + (passCount + 1) + ".txt";
			
			// determine number of sublists for next pass
			numOfSubLists = (int) Math.ceil((double) numOfSubLists / (double) (numInputBuffers)) ;
		}
		
		 
		if (numOfPasses == 0)
		{
			return inFilename;
		}
		else
		{
			return outFilename;
		}
	}
}