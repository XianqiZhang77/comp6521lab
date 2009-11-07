package comp6521.lab.com.Pages;

import java.util.ArrayList;

import comp6521.lab.com.Records.LineItemRecord;

public class LineItemPage extends Page<LineItemRecord> {
	public LineItemRecord[] CreateArray(int n){ return new LineItemRecord[n]; }
	public LineItemRecord   CreateElement() { return new LineItemRecord(); }
	
	/*public  ArrayList<LineItemRecord> lineItemRecords = new ArrayList<LineItemRecord>();   
	
	public LineItemPage(int pageNumber){
		
		
	}
	public String getLastPage(){
		int numberOfPages ;
		if((m_records.length % 10) == 0){
			numberOfPages = (m_records.length % 10);
		}
		else{
			numberOfPages = (m_records.length % 10) + 1;
		}
		int indexBase = 0;
		indexBase = (numberOfPages - 1)*10 + 1;
		String  lastPageRawData = "";
		
		if((m_records.length % 10) == 0){
			return "";
			
		}
		else{
		
			for (int index=indexBase; index<m_records.length; index++){
				LineItemRecord lineItemRecord = lineItemRecords.get(index);
				
				lastPageRawData += lineItemRecord.convertLineItemRecordToRawData(); 
				
			}
			
			return lastPageRawData;
		}
		
		
	}*/
	
	
	
	
}
