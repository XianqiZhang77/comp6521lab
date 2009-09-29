package comp6521.lab.com;

/**
 * 
 * @author Julien
 *
 */
public class MemoryManager 
{
	// Constructor
	private MemoryManager(){}
	
	// Singleton
	private static final MemoryManager ms_Instance = new MemoryManager();
	public MemoryManager getInstance() { return ms_Instance; }
	
	// Memory manager interface to the page manager
	//...
}
