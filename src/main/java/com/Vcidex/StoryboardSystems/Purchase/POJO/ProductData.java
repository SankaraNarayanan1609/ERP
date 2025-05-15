package com.Vcidex.StoryboardSystems.Purchase.POJO;

public class ProductData {
    private String name;
    private int quantity;
    private double price;
    private double discount;

    public ProductData(String name, int quantity, double price, double discount) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.discount = discount;
    }

    // Getters
    public String getName() { return name; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }
    public double getDiscount() { return discount; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setPrice(double price) { this.price = price; }
    public void setDiscount(double discount) { this.discount = discount; }

    @Override
    public String toString() {
        return String.format("Product{name='%s', quantity=%d, price=%.2f, discount=%.1f%%}",
                name, quantity, price, discount);
    }
}

