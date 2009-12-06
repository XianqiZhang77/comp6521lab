package comp6521.lab.com;

import java.util.ArrayList;
import java.util.Stack;

public class Log 
{
	// Singleton
	private static final Log ms_Instance = new Log();
	public static Log getInstance() { return ms_Instance; }
	
	// Members
	Stack< LogSection > sections;
	ArrayList< String > results;
	String header;
	
	// Constructor
	private Log()
	{
		sections = new Stack< LogSection >();
		results = new ArrayList< String >();
		header = "";
	}
	
	// Methods	
	public static void StartLogSection(String text)
	{
		// Push a new section
		getInstance().sections.push(new LogSection(text));
	}
	
	public static void EndLogSection()
	{
		LogSection ls = getInstance().sections.pop();
		// Output ls
		ls.Output();
	}
	
	public static void SetResultHeader(String _header)
	{
		getInstance().header = new String(_header);
	}
	
	public static void AddResult(String result)
	{
		getInstance().results.add(new String(result));
	}
	
	public static void LogSomething(String line)
	{
		getInstance().sections.peek().AddLine(line);
	}
	
	public static void Flush() { getInstance().FlushInternal(); }
	private void FlushInternal()
	{
		// First, pop everything out
		while( sections.peek() != null )
			EndLogSection();
		
		// Flush all results
		System.out.println(header);
		for( int i = 0; i < results.size(); i++)
			System.out.println(results.get(i));
		
		header = "";
		results.clear();		
	}

}

class LogSection
{
	String sectionName;
	ArrayList<String> lines;
	
	long start_read_io;
	long start_write_io;
	
	LogSection(String text)
	{
		sectionName = text;
		lines = new ArrayList<String>();
		
		start_read_io = PageManagerSingleton.getInstance().getReadIOCount();
		start_write_io = PageManagerSingleton.getInstance().getWriteIOCount();
	}
	
	void AddLine(String line)
	{
		lines.add(new String(line));
	}
	
	void Output()
	{
		long end_read_io = PageManagerSingleton.getInstance().getReadIOCount();
		long end_write_io = PageManagerSingleton.getInstance().getWriteIOCount();
		
		long read_io_delta  = end_read_io - start_read_io;
		long write_io_delta = end_write_io - start_write_io;
		
		System.out.println(">> START -- " + sectionName + " -- START <<");
		System.out.println("-- Nb read I/Os : " + read_io_delta + " -- ");
		System.out.println("-- Nb write I/Os : " + write_io_delta + " -- ");
		
		for( int i = 0; i < lines.size(); i++ )
			System.out.println(lines.get(i));
		
		System.out.println("<< END -- " + sectionName + " -- END >>");
	}
	
}