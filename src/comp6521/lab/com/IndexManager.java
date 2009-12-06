package comp6521.lab.com;

import comp6521.lab.com.Pages.*;
import comp6521.lab.com.Records.DateRecordElement;
import comp6521.lab.com.Records.IntegerRecordElement;
import comp6521.lab.com.Records.LineItemRecord;
import comp6521.lab.com.Records.StringRecordElement;

public class IndexManager 
{
	// Singleton
	private static final IndexManager ms_Instance = new IndexManager();
	public static IndexManager getInstance() { return ms_Instance; }
	
	// Members
	BPlusTree       < LineItemPage, DateRecordElement    > LineItemDateIndex;
	BPlusTree       < CustomerPage, IntegerRecordElement > CustomerPKIndex;
	LinearHashTable < CustomerPage                       > CustomerCountryCodeIndex;
	BPlusTree       < RegionPage,   StringRecordElement  > RegionNameIndex;
	BPlusTree       < NationPage,   IntegerRecordElement > NationFKIndex;
	BPlusTree       < NationPage,   IntegerRecordElement > NationPKIndex;
	BPlusTree       < NationPage,   StringRecordElement  > NationNameIndex;
	BPlusTree       < SupplierPage, IntegerRecordElement > SupplierFKIndex;
	BPlusTree       < SupplierPage, IntegerRecordElement > SupplierPKIndex;
	BPlusTree       < PartSuppPage, IntegerRecordElement > PartSuppPartFKIndex;
	BPlusTree       < PartSuppPage, IntegerRecordElement > PartSuppSuppFKIndex;
	BPlusTree       < PartPage,     IntegerRecordElement > PartSizeIndex; // Should probably be a linear hash..
	BPlusTree       < PartPage,     IntegerRecordElement > PartPKIndex;   // though it can cause problem with this index <<-
	BPlusTree       < OrdersPage,   DateRecordElement    > OrderDateIndex;

	// Constructor
	private IndexManager()
	{
	}
	
	// Methods
	public void CreateIndexes()
	{
		CreateLineItemDateIndex();
		CreateCustomerPKIndex();
		CreateCustomerCountryCodeIndex();
		CreateRegionNameIndex();
		CreateNationFKIndex();
		CreateNationPKIndex();
		CreateNationNameIndex();
		CreateSupplierFKIndex();
		CreateSupplierPKIndex();
		CreatePartSuppPartFKIndex();
		CreatePartSuppSuppFKIndex();
		CreatePartSizeIndex(); 
		CreatePartPKIndex();   
		CreateOrderDateIndex();
	}
	
	public void PurgeIndexes()
	{
		// Delete all ".idx" files
		PageManagerSingleton.getInstance().deleteIdxFiles();
		
		// Set to null everything
		LineItemDateIndex = null;
		CustomerPKIndex = null;
		CustomerCountryCodeIndex = null;
		RegionNameIndex = null;
		NationFKIndex = null;
		NationPKIndex = null;
		NationNameIndex = null;
		SupplierFKIndex = null;
		SupplierPKIndex = null;
		PartSuppPartFKIndex = null;
		PartSuppSuppFKIndex = null;
		PartSizeIndex = null; 
		PartPKIndex = null;  
		OrderDateIndex = null;
	}
	
	public BPlusTree       < LineItemPage, DateRecordElement    > getLineItemDateIndex()        { if(LineItemDateIndex        == null){CreateLineItemDateIndex();       } return LineItemDateIndex; }
	public BPlusTree       < CustomerPage, IntegerRecordElement > getCustomerPKIndex()          { if(CustomerPKIndex          == null){CreateCustomerPKIndex();         } return CustomerPKIndex; }
	public LinearHashTable < CustomerPage                       > getCustomerCountryCodeIndex() { if(CustomerCountryCodeIndex == null){CreateCustomerCountryCodeIndex();} return CustomerCountryCodeIndex; }
	public BPlusTree       < RegionPage,   StringRecordElement  > getRegionNameIndex()          { if(RegionNameIndex          == null){CreateRegionNameIndex();         } return RegionNameIndex; }
	public BPlusTree       < NationPage,   IntegerRecordElement > getNationFKIndex()            { if(NationFKIndex            == null){CreateNationFKIndex();           } return NationFKIndex; }
	public BPlusTree       < NationPage,   IntegerRecordElement > getNationPKIndex()            { if(NationPKIndex            == null){CreateNationPKIndex();           } return NationPKIndex; }
	public BPlusTree       < NationPage,   StringRecordElement  > getNationNameIndex()          { if(NationNameIndex          == null){CreateNationNameIndex();         } return NationNameIndex; }
	public BPlusTree       < SupplierPage, IntegerRecordElement > getSupplierFKIndex()          { if(SupplierFKIndex          == null){CreateSupplierFKIndex();         } return SupplierFKIndex; }
	public BPlusTree       < SupplierPage, IntegerRecordElement > getSupplierPKIndex()          { if(SupplierPKIndex          == null){CreateSupplierPKIndex();         } return SupplierPKIndex; }
	public BPlusTree       < PartSuppPage, IntegerRecordElement > getPartSuppPartFKIndex()      { if(PartSuppPartFKIndex      == null){CreatePartSuppPartFKIndex();     } return PartSuppPartFKIndex; }
	public BPlusTree       < PartSuppPage, IntegerRecordElement > getPartSuppSuppFKIndex()      { if(PartSuppSuppFKIndex      == null){CreatePartSuppSuppFKIndex();     } return PartSuppSuppFKIndex; }
	public BPlusTree       < PartPage,     IntegerRecordElement > getPartSizeIndex()            { if(PartSizeIndex            == null){CreatePartSizeIndex();           } return PartSizeIndex; }
	public BPlusTree       < PartPage,     IntegerRecordElement > getPartPKIndex()              { if(PartPKIndex              == null){CreatePartPKIndex();             } return PartPKIndex; }
	public BPlusTree       < OrdersPage,   DateRecordElement    > getOrderDateIndex()           { if(OrderDateIndex           == null){CreateOrderDateIndex();          } return OrderDateIndex; }
	
	void AddLineItemRecord( LineItemRecord rec, int recordNumber )
	{
		// Only one index uses the line item relation.
		
		// Update the index only if it exists
		if( LineItemDateIndex == null )
			return;
		
		LineItemDateIndex.InsertLeaf(rec, recordNumber);
	}
	
	
	////////////////////////////////////////
	// ----- Implementation details ----- //
	////////////////////////////////////////
	
	void CreateLineItemDateIndex() 
	{
		if(LineItemDateIndex != null )
			return;
		
		LineItemDateIndex = new BPlusTree< LineItemPage, DateRecordElement >();
		LineItemDateIndex.CreateBPlusTree( LineItemPage.class, DateRecordElement.class, "LineItem.txt", "LineItem_Date.idx", "l_receiptDate");
	}
	void CreateCustomerPKIndex() 
	{
		if(CustomerPKIndex != null )
			return;
		
		CustomerPKIndex = new BPlusTree< CustomerPage, IntegerRecordElement >();
		CustomerPKIndex.CreateBPlusTree(CustomerPage.class, IntegerRecordElement.class, "Customer.txt", "customer_pk.idx", "c_custKey");
	}
	void CreateCustomerCountryCodeIndex() 
	{
		if(CustomerCountryCodeIndex != null )
			return;
		
		CustomerCountryCodeIndex = new LinearHashTable< CustomerPage >();
		CountryHashFunction cntry_hf = new CountryHashFunction();
		CustomerCountryCodeIndex.CreateHashTable( CustomerPage.class, "customer_cntrycode.idx", "c_phone", cntry_hf );
	}
	void CreateRegionNameIndex() 
	{
		if(RegionNameIndex != null )
			return;
		
		RegionNameIndex = new BPlusTree< RegionPage, StringRecordElement >();
		RegionNameIndex.CreateBPlusTree( RegionPage.class, StringRecordElement.class, 50, "Region.txt", "Region_rname.idx", "r_name");
	}
	void CreateNationFKIndex() 
	{
		if(NationFKIndex != null )
			return;
		
		NationFKIndex = new BPlusTree< NationPage, IntegerRecordElement >();
		NationFKIndex.CreateBPlusTree( NationPage.class, IntegerRecordElement.class, "Nation.txt", "Nation_FK.idx", "n_regionKey");
	}
	void CreateNationPKIndex() 
	{
		if(NationPKIndex != null )
			return;
		
		NationPKIndex = new BPlusTree< NationPage, IntegerRecordElement >();
		NationPKIndex.CreateBPlusTree( NationPage.class, IntegerRecordElement.class, "Nation.txt", "Nation_PK.idx", "n_nationKey");
	}
	void CreateNationNameIndex() 
	{
		if(NationNameIndex != null )
			return;
		
		NationNameIndex = new BPlusTree< NationPage, StringRecordElement >();
		NationNameIndex.CreateBPlusTree( NationPage.class, StringRecordElement.class, 15, "Nation.txt", "Nation_Name.idx", "n_name");
	}
	void CreateSupplierFKIndex() 
	{
		if(SupplierFKIndex != null )
			return;
		
		SupplierFKIndex = new BPlusTree< SupplierPage, IntegerRecordElement >();
		SupplierFKIndex.CreateBPlusTree( SupplierPage.class, IntegerRecordElement.class, "Supplier.txt", "Supplier_FK.idx", "s_nationKey");
	}
	void CreateSupplierPKIndex() 
	{
		if(SupplierPKIndex != null )
			return;
		
		SupplierPKIndex = new BPlusTree< SupplierPage, IntegerRecordElement >();
		SupplierPKIndex.CreateBPlusTree( SupplierPage.class, IntegerRecordElement.class, "Supplier.txt", "Supplier_PK.idx", "s_suppKey");
	}
	void CreatePartSuppPartFKIndex() 
	{
		if(PartSuppPartFKIndex != null )
			return;
		
		PartSuppPartFKIndex = new BPlusTree< PartSuppPage, IntegerRecordElement >();
		PartSuppPartFKIndex.CreateBPlusTree( PartSuppPage.class, IntegerRecordElement.class, "PartSupp.txt", "PartSupp_partFK.idx", "ps_partKey");
	}
	void CreatePartSuppSuppFKIndex() 
	{
		if(PartSuppSuppFKIndex != null )
			return;
		
		PartSuppSuppFKIndex = new BPlusTree< PartSuppPage, IntegerRecordElement >();
		PartSuppSuppFKIndex.CreateBPlusTree( PartSuppPage.class, IntegerRecordElement.class, "PartSupp.txt", "PartSupp_suppFK.idx", "ps_suppKey");
	}
	void CreatePartSizeIndex() 
	{
		if(PartSizeIndex != null )
			return;
		
		PartSizeIndex = new BPlusTree< PartPage, IntegerRecordElement >();
		PartSizeIndex.CreateBPlusTree( PartPage.class, IntegerRecordElement.class, "Part.txt", "Part_size.idx", "p_size");
	}
	void CreatePartPKIndex() 
	{
		if(PartPKIndex != null )
			return;
		
		PartPKIndex = new BPlusTree< PartPage, IntegerRecordElement >();
		PartPKIndex.CreateBPlusTree( PartPage.class, IntegerRecordElement.class, "Part.txt", "Part_PK.idx", "p_partKey");
	}
	void CreateOrderDateIndex() 
	{
		if(OrderDateIndex != null )
			return;
		
		OrderDateIndex = new BPlusTree< OrdersPage, DateRecordElement >();
		OrderDateIndex.CreateBPlusTree(OrdersPage.class, DateRecordElement.class, "Orders.txt", "orders_date.idx", "o_orderDate");
	}
}
