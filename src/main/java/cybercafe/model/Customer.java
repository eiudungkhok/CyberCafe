package cybercafe.model;

public class Customer {
    private String username;
    private Membership membership;

    public Customer(String username, Membership membership) {
        this.username = username;
        this.membership = membership;
    }

    public Membership getMembership() {
        return membership;
    }
}