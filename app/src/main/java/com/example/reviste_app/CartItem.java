package com.example.reviste_app;

// CartItem.java
public class CartItem {
    private String productId;
    private String productName;
    private int quantity;
    private double price;
    private String productImage;

    public CartItem(String productId, String productName, int quantity, double price, String productImage) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
        this.productImage = productImage;
    }

    public String getProductImage() {
        return productImage;
    }

    public String getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }
}
