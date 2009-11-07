package comp6521.lab.com.Records;

public abstract class RecordElement {

	public abstract void   Parse(String data);
	public abstract String Write();
	public abstract int    Size();
	
	// Return types to be overriden
	public String getString()            { assert(false); return ""; }
	public void   setString(String data) { assert(false);            }
	public int getInt()	                 { assert(false); return 0;  }
	public void setInt(int val)          { assert(false);            }
	public float getFloat()              { assert(false); return 0;  }
	public void  setFloat(float val)     { assert(false);            }
}
