package cybercafe.model;

public class Membership {
    private String tierName;
    private double discountPercent;

    public Membership(String tierName, double discountPercent) {
        this.tierName = tierName;
        this.discountPercent = discountPercent;
    }

    public double getDiscountPercent() {
        return discountPercent;
    }
}