package comp6521.lab.com.Pages;

import comp6521.lab.com.Records.SupplierRecord;

public class SupplierPage extends Page<SupplierRecord> {
	public SupplierRecord[] CreateArray(int n){ return new SupplierRecord[n]; }
	public SupplierRecord   CreateElement() { return new SupplierRecord(); }
}
