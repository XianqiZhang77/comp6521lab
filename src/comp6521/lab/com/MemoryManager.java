package comp6521.lab.com;

/**
 * 
 * @author Julien
 *
 */
public class MemoryManager 
{
	// Singleton
	private static MemoryManager ms_Instance;
	
	// Singleton enforcing
	private MemoryManager(){}
	public Object clone() throws CloneNotSupportedException { throw new CloneNotSupportedException(); }
	public static synchronized MemoryManager getInstance()
	{
		if( ms_Instance == null )
			ms_Instance = new MemoryManager();
		
		return ms_Instance;
	}
	
	// Memory manager interface to the page manager
	//...
}
