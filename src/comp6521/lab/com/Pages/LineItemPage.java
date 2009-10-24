package comp6521.lab.com.Pages;

import comp6521.lab.com.Records.LineItemRecord;

public class LineItemPage extends Page<LineItemRecord> {
	public LineItemRecord[] CreateArray(int n){ return new LineItemRecord[n]; }
	public LineItemRecord   CreateElement() { return new LineItemRecord(); }
}
