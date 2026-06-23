package cybercafe.model;

public class MenuItem {
    private int id; // Thêm ID định danh
    private String name;
    private int price;
    private String category;

    public MenuItem(String name, int price) {
        this.name = name;
        this.price = price;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}