package com.example.reviste_app;

public class Product {
    public String name;
    public String price;
    public String image;

    public Product() {
        // Constructor vacío requerido para Firestore
    }

    public Product(String name, String price, String image) {
        this.name = name;
        this.price = price;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public String getImage() {
        return image;
    }
}
