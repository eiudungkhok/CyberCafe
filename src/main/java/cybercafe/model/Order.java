package cybercafe.model;

public class Order {
    private int id;
    private int machineId;
    private String customerName;
    private String itemName;
    private int quantity;
    private int totalCost;
    private String orderTime;

    public Order(int id, int machineId, String customerName, String itemName, int quantity, int totalCost, String orderTime) {
        this.id = id; this.machineId = machineId; this.customerName = customerName;
        this.itemName = itemName; this.quantity = quantity; this.totalCost = totalCost; this.orderTime = orderTime;
    }

    public int getId() { return id; }
    public int getMachineId() { return machineId; }
    public String getCustomerName() { return customerName; }
    public String getItemName() { return itemName; }
    public int getQuantity() { return quantity; }
    public int getTotalCost() { return totalCost; }
    public String getOrderTime() { return orderTime; }
}