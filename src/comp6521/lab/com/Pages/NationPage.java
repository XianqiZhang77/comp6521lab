package comp6521.lab.com.Pages;

import comp6521.lab.com.Records.NationRecord;

public class NationPage extends Page<NationRecord> {
	public NationRecord[] CreateArray(int n){ return new NationRecord[n]; }
	public NationRecord   CreateElement() { return new NationRecord(); }
}
