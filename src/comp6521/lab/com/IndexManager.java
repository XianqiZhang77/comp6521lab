package comp6521.lab.com;

import comp6521.lab.com.Hashing.IntegerHashFunction;
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
	LinearHashTable < LineItemPage                       > LineItemOrderIndex;
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
	BPlusTree       < OrdersPage,   IntegerRecordElement > OrderPKIndex;

	// Constructor
	private IndexManager()
	{
	}
	
	// Methods
	public void CreateIndexes()
	{
		CreateLineItemDateIndex();
		CreateLineItemOrderIndex();
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
		CreateOrderPKIndex();
	}
	
	public void PurgeIndexes()
	{
		// Delete all ".idx" files
		PageManagerSingleton.getInstance().deleteIdxFiles();
		
		// Set to null everything
		LineItemDateIndex = null;
		LineItemOrderIndex = null;
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
		OrderPKIndex = null;
	}
	
	public BPlusTree       < LineItemPage, DateRecordElement    > getLineItemDateIndex()        { if(LineItemDateIndex        == null){CreateLineItemDateIndex();       } return LineItemDateIndex; }
	public LinearHashTable < LineItemPage                       > getLineItemOrderIndex()       { if(LineItemOrderIndex       == null){CreateLineItemOrderIndex();      } return LineItemOrderIndex; }
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
	public BPlusTree       < OrdersPage,   IntegerRecordElement > getOrderPKIndex()             { if(OrderPKIndex             == null){CreateOrderPKIndex();            } return OrderPKIndex; }
	
	void AddLineItemRecord( LineItemRecord rec, int recordNumber )
	{
		// Only one index uses the line item relation.
		
		// Update the index only if it exists
		if( LineItemDateIndex != null )
			LineItemDateIndex.InsertLeaf(rec, recordNumber);
		
		if( LineItemOrderIndex != null )
			LineItemOrderIndex.Insertion(rec);
	}
	
	
	////////////////////////////////////////
	// ----- Implementation details ----- //
	////////////////////////////////////////
	
	void CreateLineItemDateIndex() 
	{
		if(LineItemDateIndex != null )
			return;
		
		Log.StartLogSection("Build index -- LineItemDateIndex");
		LineItemDateIndex = new BPlusTree< LineItemPage, DateRecordElement >();
		LineItemDateIndex.CreateBPlusTree( LineItemPage.class, DateRecordElement.class, "LineItem.txt", "LineItem_Date.idx", "l_receiptDate");
		Log.EndLogSection();
	}
	void CreateLineItemOrderIndex()
	{
		if(LineItemOrderIndex != null)
			return;
		
		Log.StartLogSection("Build index -- LineItemOrderIndex");
		LineItemOrderIndex = new LinearHashTable< LineItemPage >();
		IntegerHashFunction ihf = new IntegerHashFunction();
		LineItemOrderIndex.CreateHashTable( LineItemPage.class, "LineItem_order.idx", "l_orderKey", ihf);
		Log.EndLogSection();
	}
	void CreateCustomerPKIndex() 
	{
		if(CustomerPKIndex != null )
			return;
		
		Log.StartLogSection("Build index -- CustomerPKIndex");
		CustomerPKIndex = new BPlusTree< CustomerPage, IntegerRecordElement >();
		CustomerPKIndex.CreateBPlusTree(CustomerPage.class, IntegerRecordElement.class, "Customer.txt", "customer_pk.idx", "c_custKey");
		Log.EndLogSection();
	}
	void CreateCustomerCountryCodeIndex() 
	{
		if(CustomerCountryCodeIndex != null )
			return;
		
		Log.StartLogSection("Build index -- CustomerCountryCodeIndex");
		CustomerCountryCodeIndex = new LinearHashTable< CustomerPage >();
		CountryHashFunction cntry_hf = new CountryHashFunction();
		CustomerCountryCodeIndex.CreateHashTable( CustomerPage.class, "customer_cntrycode.idx", "c_phone", cntry_hf );
		Log.EndLogSection();
	}
	void CreateRegionNameIndex() 
	{
		if(RegionNameIndex != null )
			return;
		
		Log.StartLogSection("Build index -- RegionNameIndex");
		RegionNameIndex = new BPlusTree< RegionPage, StringRecordElement >();
		RegionNameIndex.CreateBPlusTree( RegionPage.class, StringRecordElement.class, 50, "Region.txt", "Region_rname.idx", "r_name");
		Log.EndLogSection();
	}
	void CreateNationFKIndex() 
	{
		if(NationFKIndex != null )
			return;
		
		Log.StartLogSection("Build index -- NationFKIndex");
		NationFKIndex = new BPlusTree< NationPage, IntegerRecordElement >();
		NationFKIndex.CreateBPlusTree( NationPage.class, IntegerRecordElement.class, "Nation.txt", "Nation_FK.idx", "n_regionKey");
		Log.EndLogSection();
	}
	void CreateNationPKIndex() 
	{
		if(NationPKIndex != null )
			return;
		
		Log.StartLogSection("Build index -- NationPKIndex");
		NationPKIndex = new BPlusTree< NationPage, IntegerRecordElement >();
		NationPKIndex.CreateBPlusTree( NationPage.class, IntegerRecordElement.class, "Nation.txt", "Nation_PK.idx", "n_nationKey");
		Log.EndLogSection();
	}
	void CreateNationNameIndex() 
	{
		if(NationNameIndex != null )
			return;
		
		Log.StartLogSection("Build index -- NationNameIndex");
		NationNameIndex = new BPlusTree< NationPage, StringRecordElement >();
		NationNameIndex.CreateBPlusTree( NationPage.class, StringRecordElement.class, 15, "Nation.txt", "Nation_Name.idx", "n_name");
		Log.EndLogSection();
	}
	void CreateSupplierFKIndex() 
	{
		if(SupplierFKIndex != null )
			return;
		
		Log.StartLogSection("Build index -- SupplierFKIndex");
		SupplierFKIndex = new BPlusTree< SupplierPage, IntegerRecordElement >();
		SupplierFKIndex.CreateBPlusTree( SupplierPage.class, IntegerRecordElement.class, "Supplier.txt", "Supplier_FK.idx", "s_nationKey");
		Log.EndLogSection();
	}
	void CreateSupplierPKIndex() 
	{
		if(SupplierPKIndex != null )
			return;
		
		Log.StartLogSection("Build index -- SupplierPKIndex");
		SupplierPKIndex = new BPlusTree< SupplierPage, IntegerRecordElement >();
		SupplierPKIndex.CreateBPlusTree( SupplierPage.class, IntegerRecordElement.class, "Supplier.txt", "Supplier_PK.idx", "s_suppKey");
		Log.EndLogSection();
	}
	void CreatePartSuppPartFKIndex() 
	{
		if(PartSuppPartFKIndex != null )
			return;
		
		Log.StartLogSection("Build index -- PartSuppPartFKIndex");
		PartSuppPartFKIndex = new BPlusTree< PartSuppPage, IntegerRecordElement >();
		PartSuppPartFKIndex.CreateBPlusTree( PartSuppPage.class, IntegerRecordElement.class, "PartSupp.txt", "PartSupp_partFK.idx", "ps_partKey");
		Log.EndLogSection();
	}
	void CreatePartSuppSuppFKIndex() 
	{
		if(PartSuppSuppFKIndex != null )
			return;
		
		Log.StartLogSection("Build index -- PartSuppSuppFKIndex");
		PartSuppSuppFKIndex = new BPlusTree< PartSuppPage, IntegerRecordElement >();
		PartSuppSuppFKIndex.CreateBPlusTree( PartSuppPage.class, IntegerRecordElement.class, "PartSupp.txt", "PartSupp_suppFK.idx", "ps_suppKey");
		Log.EndLogSection();
	}
	void CreatePartSizeIndex() 
	{
		if(PartSizeIndex != null )
			return;
		
		Log.StartLogSection("Build index -- PartSizeIndex");
		PartSizeIndex = new BPlusTree< PartPage, IntegerRecordElement >();
		PartSizeIndex.CreateBPlusTree( PartPage.class, IntegerRecordElement.class, "Part.txt", "Part_size.idx", "p_size");
		Log.EndLogSection();
	}
	void CreatePartPKIndex() 
	{
		if(PartPKIndex != null )
			return;
		
		Log.StartLogSection("Build index -- PartPKIndex");
		PartPKIndex = new BPlusTree< PartPage, IntegerRecordElement >();
		PartPKIndex.CreateBPlusTree( PartPage.class, IntegerRecordElement.class, "Part.txt", "Part_PK.idx", "p_partKey");
		Log.EndLogSection();
	}
	void CreateOrderDateIndex() 
	{
		if(OrderDateIndex != null )
			return;
		
		Log.StartLogSection("Build index -- OrderDateIndex");
		OrderDateIndex = new BPlusTree< OrdersPage, DateRecordElement >();
		OrderDateIndex.CreateBPlusTree(OrdersPage.class, DateRecordElement.class, "Orders.txt", "orders_date.idx", "o_orderDate");
		Log.EndLogSection();
	}
	void CreateOrderPKIndex()
	{
		if(OrderPKIndex != null)
			return;
		
		Log.StartLogSection("Build index -- OrderPKIndex");
		OrderPKIndex = new BPlusTree< OrdersPage, IntegerRecordElement >();
		OrderPKIndex.CreateBPlusTree(OrdersPage.class, IntegerRecordElement.class, "Orders.txt", "orders_pk.idx", "o_orderKey");
		Log.EndLogSection();
	}
}
