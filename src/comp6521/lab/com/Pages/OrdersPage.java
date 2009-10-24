package comp6521.lab.com.Pages;

import comp6521.lab.com.Records.OrdersRecord;

public class OrdersPage extends Page<OrdersRecord> {
	public OrdersRecord[] CreateArray(int n){ return new OrdersRecord[n]; }
	public OrdersRecord   CreateElement() { return new OrdersRecord(); }
}
