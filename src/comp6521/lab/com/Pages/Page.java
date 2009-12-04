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
	public boolean m_cleanupToDo;
	
	public Page()
	{
		m_records = null;
		m_pageNumber = -1;
		m_filename = "";
		m_insertionIndex = -1;
		m_nbRecordsPerPage = 10; // default value
		m_cleanupToDo = false;
	}
	
	// To be overriden by custom page classes
	public int GetNumberRecordsPerPage() { return m_nbRecordsPerPage; }
	
	public /*abstract*/ T[] CreateArray(int n) {return null;}
	public /*abstract*/ T CreateElement() {return null;}
	
	public void Construct( char[] rawData )
	{
		String rawPageString    = String.valueOf(rawData);
		String[] rawRecords     = rawPageString.split("\r\n");
		
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
	
	public String GetRawData()
	{
		String stringData = "";
		
		// Prepare "empty record"
		int recLength = CreateElement().GetRecordSize() - 2;
		String emptyFormat = "%1$-" + recLength + "s";
		String EmptyRecord = String.format(emptyFormat, "");
		
		for( int i = 0; i < m_records.length; i++ )
		{
			if( m_records[i] != null )
				stringData += m_records[i].Write() + "\r\n";
			else
				stringData += EmptyRecord + "\r\n";
		}
		
		for( int i = m_records.length; i < m_nbRecordsPerPage; i++ )
		{
			stringData += EmptyRecord + "\r\n";
		}
		
		return stringData;
	}
	
	public void CreateEmptyPage()
	{
		m_records = CreateArray( GetNumberRecordsPerPage() );
		m_insertionIndex = 0;
	}
	
	public void AddRecord( Record record )
	{
		if( m_insertionIndex == -1 )
		{
			System.out.println("Trying to insert data in a non-empty page!");
		}
		
		// Insert record
		m_records[m_insertionIndex++] = (T) record;
		
		// If the page is full, write it to file
		if( m_insertionIndex == GetNumberRecordsPerPage() )
		{
			WritePageToFile();
			m_insertionIndex = 0;
			m_cleanupToDo = false;
			MemoryManager.getInstance().GetNextEmptyPage(this);
			m_records = CreateArray(m_nbRecordsPerPage);
		}
		else
		{		
			m_cleanupToDo = true;
		}
	}
	
	public void Cleanup()
	{
		if( m_cleanupToDo ) // not -1 (not an empty page) , not 0 (no records added)
		{
			// Write the page to the file.
			WritePageToFile();
			m_cleanupToDo = false;
		}
	}
	
	public void WritePageToFile()
	{
		MemoryManager.getInstance().writePage( this );
	}
	
	@SuppressWarnings("unchecked")
	public void setRecord( int i, Record rec )
	{
		assert( i >=0 && i < m_nbRecordsPerPage );
		assert( m_records[i].getClass() == rec.getClass() );
		
		// Reallocate the array if we didn't parse it fully.
		if( i >= m_records.length && i < m_nbRecordsPerPage )
		{
			T[] recs = CreateArray(i + 1);
			for( int k = 0; k < m_records.length; k++ )
				recs[k] = m_records[k];
			for( int l = m_records.length; l < i; l++ )
				recs[l] = null;
			m_records = recs;
		}
		
		m_records[i] = (T)rec;
		m_cleanupToDo = true;
	}
}