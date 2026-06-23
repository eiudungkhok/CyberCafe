package cybercafe.model;

public class CustomerProfile {
    private String username;
    private String tierName;
    private int totalPlayMinutes;
    private int totalSpent;
    private int totalOrders;

    public CustomerProfile(String username, String tierName, int totalPlayMinutes, int totalSpent, int totalOrders) {
        this.username = username;
        this.tierName = tierName;
        this.totalPlayMinutes = totalPlayMinutes;
        this.totalSpent = totalSpent;
        this.totalOrders = totalOrders;
    }

    public String getUsername() { return username; }
    public String getTierName() { return tierName; }
    public int getTotalPlayMinutes() { return totalPlayMinutes; }
    public int getTotalSpent() { return totalSpent; }
    public int getTotalOrders() { return totalOrders; }
}