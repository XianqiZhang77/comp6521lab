package comp6521.lab.com.Pages;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import comp6521.lab.com.MemoryManager;
import comp6521.lab.com.Records.Record;

public abstract class Page<T extends Record> {
	public T[] m_records;
	public int m_pageNumber;
	public int m_insertionIndex = -1;
	
	
	public boolean isEmpty() { return m_records.length > 0; }
	// To be overriden by custom page classes
	public static int GetNumberRecordsPerPage() { return 10; }
	
	protected abstract T[] CreateArray(int n);
	protected abstract T CreateElement();
	
	public void Construct( char[] rawData )
	{
		String rawPageString    = String.valueOf(rawData);
		String[] rawRecords     = rawPageString.split("\n");
		
		// Count nb. of non-null raw records
		int n = 0;
		for( int i = 0; i < rawRecords.length; i++ )
			if( rawRecords[i].trim().compareTo("") != 0 )
				n++;
		
		m_records = CreateArray(n);
		
		int r = 0;
		for( int i = 0; i < rawRecords.length; i++ )
		{
			String tR = rawRecords[i];
			
			if( tR.trim().compareTo("") == 0)
				continue;
			
			m_records[r] = CreateElement();
			// Fill in the record
			m_records[r].Parse(tR);
			
			r++;
		}
	}
	
	public char[] GetRawData()
	{
		String stringData = "";
		for( int i = 0; i < m_insertionIndex; i++ )
		{
			stringData += m_records[i].Write();
		}
		return stringData.toCharArray();
	}
	
	public void CreateEmptyPage()
	{
		m_records = CreateArray( GetNumberRecordsPerPage() );
		m_insertionIndex = 0;
	}
	
	public void AddRecord( T record )
	{
		if( m_insertionIndex == -1 )
		{
			System.out.println("Trying to insert data in a non-empty page!");
		}
		
		// Insert record
		m_records[m_insertionIndex++] = record;
		
		// If the page is full, write it to file
		if( m_insertionIndex == GetNumberRecordsPerPage() )
		{
			WritePageToFile();
			m_insertionIndex = 0;
		}
	}
	
	public void Cleanup()
	{
		if( m_insertionIndex > 0 ) // not -1 (not an empty page) , not 0 (no records added)
		{
			// Write the page to the file.
			WritePageToFile();
		}
	}
	
	public void WritePageToFile()
	{
		MemoryManager.getInstance().writePage( this );
	}
	
	public void readFile(String filePath){
		
		try{
		    // Open the file that is the first 
		    // command line parameter

		FileInputStream fstream = new FileInputStream(filePath);
	    // Get the object of DataInputStream
	    DataInputStream inputStream = new DataInputStream(fstream);
	        BufferedReader bufferReader = new BufferedReader(new InputStreamReader(inputStream));
	    String strLine;
	    //Read File Line By Line
	    while ((strLine = bufferReader.readLine()) != null)   {
	      // Print the content on the console
	      System.out.println (strLine);
	    }
	    //Close the input stream
	    inputStream.close();
	    }catch (Exception e){//Catch exception if any
	      System.err.println("Error: " + e.getMessage());
	    }	
	}
	public static void addRecord(String filePath, String rawDataRecord){
		
		File file = new File(filePath);
		PrintWriter o = null;
		try {
			file.createNewFile();
			FileOutputStream out = new FileOutputStream(file, true);
            			
			OutputStreamWriter os = new OutputStreamWriter(out, "UTF8");
			
			BufferedWriter br = new BufferedWriter(os);
			o = new PrintWriter(br);
			o.println();
			o.print(rawDataRecord);
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (o != null){
			 o.close();
			}
		}

		//reConstructIndex()
		
	}
	public int getNumberOfRecords(){
		return m_records.length;
	}

}