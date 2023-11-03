package com.example.reviste_app;

import java.util.List;

public class Product {
    public String name;
    public String price;
    public String image;
    public String description;
    public String sellerName;
    public String id;  // Campo para el ID del producto
    public List<String> additionalImages;

    public Product() {
        // Constructor sin argumentos requerido para Firestore
    }

    public Product(String id, String name, String price, String image, String description, String sellerName, List<String> additionalImages) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.image = image;
        this.description = description;
        this.sellerName = sellerName;
        this.additionalImages = additionalImages;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getAdditionalImages() {
        return additionalImages;
    }

    public void setAdditionalImages(List<String> additionalImages) {
        this.additionalImages = additionalImages;
    }
}
