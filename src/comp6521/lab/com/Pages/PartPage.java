package comp6521.lab.com.Pages;

import comp6521.lab.com.Records.PartRecord;

public class PartPage extends Page<PartRecord> {
	public PartRecord[] CreateArray(int n){ return new PartRecord[n]; }
	public PartRecord   CreateElement() { return new PartRecord(); }
}
