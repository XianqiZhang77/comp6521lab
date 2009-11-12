package comp6521.lab.com.Pages;
/*import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;*/

import comp6521.lab.com.MemoryManager;
import comp6521.lab.com.Records.Record;

public abstract class Page<T extends Record> {
	public T[] m_records;
	public int m_pageNumber;
	public String m_filename; // the file this page comes from
	public int m_insertionIndex;
	public int m_nbRecordsPerPage;
	
	public Page()
	{
		m_records = null;
		m_pageNumber = -1;
		m_filename = "";
		m_insertionIndex = -1;
		m_nbRecordsPerPage = 10; // default value
	}
	
	// To be overriden by custom page classes
	public int GetNumberRecordsPerPage() { return m_nbRecordsPerPage; }
	
	public /*abstract*/ T[] CreateArray(int n) {return null;}
	public /*abstract*/ T CreateElement() {return null;}
	
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
			stringData += m_records[i].Write() + "\r\n";
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
}