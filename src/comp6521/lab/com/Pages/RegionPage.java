package comp6521.lab.com.Pages;

import comp6521.lab.com.Records.RegionRecord;

public class RegionPage extends Page<RegionRecord> {
	public RegionRecord[] CreateArray(int n){ return new RegionRecord[n]; }
	public RegionRecord   CreateElement() { return new RegionRecord(); }
}
