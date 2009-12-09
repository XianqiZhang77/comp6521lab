package comp6521.lab.com;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Stack;

public class Log 
{
	// Singleton
	private static final Log ms_Instance = new Log();
	public static Log getInstance() { return ms_Instance; }
	
	// Members
	boolean LogStarted;
	boolean LogIO;
	String filename;
	Stack< LogSection > sections;
	ArrayList< String > results;
	String header;
	
	boolean toggle;
	int pause;
	
	// Constructor
	private Log()
	{
		toggle = false;
		pause = -1;
		LogStarted = false;
		sections = new Stack< LogSection >();
		results = new ArrayList< String >();
		header = "";
	}
	
	public static boolean IsQueryRunning(){ return getInstance().LogStarted; }
	public static void    Pause()    { getInstance().PauseInternal();   }
	public static void    Step()     { getInstance().StepInternal();    }
	public static void    StepOut()  { getInstance().StepOutInternal(); }
	public static void    Continue() { getInstance().ContinueInternal();}
	
	public static void StartLog(String outputFilename, boolean logIO) { getInstance().StartLogInternal(outputFilename, logIO); }
	public static void StartLog(String outputFilename) { getInstance().StartLogInternal(outputFilename, true); }
	private void StartLogInternal(String outputFilename, boolean logIO)
	{
		if( !LogStarted )
		{
			LogIO = logIO;
			LogStarted = true;
			filename = outputFilename;
			PageManagerSingleton.getInstance().deleteFile(outputFilename);
		}
		else
		{
			System.out.println("There's already a log in progress.");
		}
	}
	
	public static void EndLog() { getInstance().EndLogInternal(); }
	private void EndLogInternal()
	{
		if( LogStarted )
		{
			FlushInternal();
			LogStarted = false;
			filename = "";
		}
		else
		{
			System.out.println("There's no log in progress.");
		}
	}
	
	// Methods	
	public static void StartLogSection(String text)
	{
		// Push a new section
		getInstance().sections.push(new LogSection(text, getInstance().filename, getInstance().LogIO, getInstance().sections.size()));
		while( Paused() );
	}
	
	public static void EndLogSection()
	{
		LogSection ls = getInstance().sections.pop();
		// Output ls
		ls.Output();
		while( Paused() );
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
	
	private void FlushInternal()
	{
		// First, pop everything out
		while( !sections.empty() )
			EndLogSection();
		
		// Write header
		if( header.length() > 0 )
			PageManagerSingleton.getInstance().writeOutput(filename, header + "\r\n");

		// Flush all results		
		for( int i = 0; i < results.size(); i++)
			PageManagerSingleton.getInstance().writeOutput(filename, results.get(i) + "\r\n");
		
		header = "";
		results.clear();		
	}

	private void    PauseInternal()    { pause = Integer.MAX_VALUE;   toggle = false; }
	private void    StepInternal()     { pause = Integer.MAX_VALUE;   toggle = true;  }
	private void    StepOutInternal()  { pause = sections.size() - 2; toggle = false; }
	private void    ContinueInternal() { pause = -1;                  toggle = false; }
	
	private static boolean Paused()    { return getInstance().PausedInternal(); }
	private boolean PausedInternal()
	{
		if( toggle )
		{
			toggle = false;
			return false;
		}
		else
		{
			return (sections.size() - 1) < pause;
		}
	}
}

class LogSection
{
	String sectionName;
	ArrayList<String> lines;
	String filename;
	Timestamp timer;
	
	boolean LogIO;
	long start_read_io;
	long start_write_io;
	int level;
	
	LogSection(String text, String _filename, boolean logIO, int _level)
	{
		level = _level;
		LogIO = logIO;
		sectionName = text;
		lines = new ArrayList<String>();
		
		start_read_io = PageManagerSingleton.getInstance().getReadIOCount();
		start_write_io = PageManagerSingleton.getInstance().getWriteIOCount();
		
		filename = _filename;
		
		PageManagerSingleton.getInstance().writeOutput(filename, GetLevelOffset() + ">> START -- " + sectionName + " -- START <<\r\n");
		timer = new Timestamp((new Date()).getTime());
	}
	
	void AddLine(String line)
	{
		lines.add(new String(line));
	}
	
	void Output()
	{
		Timestamp endTimer = new Timestamp((new Date()).getTime());
		long end_read_io = PageManagerSingleton.getInstance().getReadIOCount();
		long end_write_io = PageManagerSingleton.getInstance().getWriteIOCount();
		
		long read_io_delta  = end_read_io - start_read_io;
		long write_io_delta = end_write_io - start_write_io;
		
		String offset = GetLevelOffset();
		
		PageManagerSingleton.getInstance().writeOutput(filename, offset + "Duration: " + (endTimer.getTime() - timer.getTime()) + " ms.\r\n" );
		
		if( LogIO )
		{
			PageManagerSingleton.getInstance().writeOutput(filename, offset + "-- Nb read I/Os : " + read_io_delta + " --\r\n");
			PageManagerSingleton.getInstance().writeOutput(filename, offset + "-- Nb write I/Os : " + write_io_delta + " --\r\n");
		}
		
		for( int i = 0; i < lines.size(); i++ )
			PageManagerSingleton.getInstance().writeOutput(filename, offset + lines.get(i) + "\r\n");
		
		PageManagerSingleton.getInstance().writeOutput(filename, offset + "<< END ---- " + sectionName + " ---- END >>\r\n");
	}
	
	String GetLevelOffset()
	{
		String offset = "";
		
		for( int i = 0; i < level; i++ )
			offset += "           ";
		
		return offset;
	}	
}