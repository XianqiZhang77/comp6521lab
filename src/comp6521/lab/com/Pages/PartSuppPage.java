package comp6521.lab.com.Pages;

import comp6521.lab.com.Records.PartSuppRecord;

public class PartSuppPage extends Page<PartSuppRecord> {
	public PartSuppRecord[] CreateArray(int n){ return new PartSuppRecord[n]; }
	public PartSuppRecord   CreateElement() { return new PartSuppRecord(); }
}
