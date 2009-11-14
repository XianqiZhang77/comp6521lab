package comp6521.lab.com.Hashing;

import comp6521.lab.com.Records.RecordElement;

public class IntegerHashFunction extends HashFunction
{
	public int Hash( RecordElement el )
	{
		// Very bad
		return el.getInt();
	}
}