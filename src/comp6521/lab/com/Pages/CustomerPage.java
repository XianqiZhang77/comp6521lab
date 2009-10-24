package comp6521.lab.com.Pages;
import comp6521.lab.com.Records.CustomerRecord;

public class CustomerPage extends Page<CustomerRecord> {
	public CustomerRecord[] CreateArray(int n){ return new CustomerRecord[n]; }
	public CustomerRecord   CreateElement() { return new CustomerRecord(); }
}
