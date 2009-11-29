package comp6521.lab.com.Util;
import comp6521.lab.com.Pages.Page;

public class key_page extends Page<key_record>
{
	public key_page()                     { super(); m_nbRecordsPerPage = 100; }
	public key_record[] CreateArray(int n){ return new key_record[n]; }
	public key_record   CreateElement()   { return new key_record();  }
}
