package com.example.reviste_app;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.List;

public class Product implements Parcelable {
    public String id;
    public String name;
    public Double price; // Cambiado a Double
    public String image;
    public String description;
    public String sellerName;
    public List<String> additionalImages;
    public float ratings;
    public boolean isPurchased; // New field indicating whether the product has been purchased

    public Product() {
        // Constructor sin argumentos requerido para Firestore
    }

    // Actualiza el constructor de la clase Product
    public Product(String name, Double price, String image, String description, String sellerName, List<String> additionalImages, float ratings) {
        this.name = name;
        this.price = price;
        this.image = image;
        this.description = description;
        this.sellerName = sellerName;
        this.additionalImages = additionalImages;
        this.ratings = ratings;
        this.isPurchased = false; // Initialize as not purchased by default

    }


    protected Product(Parcel in) {
        id = in.readString();
        name = in.readString();
        // Cambiado para leer el valor de price como Double
        price = in.readDouble();
        image = in.readString();
        description = in.readString();
        sellerName = in.readString();
        additionalImages = in.createStringArrayList();
        ratings = in.readFloat();
        isPurchased = in.readByte() != 0; // Read boolean from parcel

    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    public boolean isPurchased() {
        return isPurchased;
    }

    public void setPurchased(boolean purchased) {
        isPurchased = purchased;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
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

    public List<String> getAdditionalImages() {
        return additionalImages;
    }

    public void setAdditionalImages(List<String> additionalImages) {
        this.additionalImages = additionalImages;
    }

    public float getRatings() {
        return ratings;
    }

    public void setRatings(float ratings) {
        this.ratings = ratings;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        // Cambiado para escribir el valor de price como Double
        dest.writeDouble(price);
        dest.writeString(image);
        dest.writeString(description);
        dest.writeString(sellerName);
        dest.writeStringList(additionalImages);
        dest.writeFloat(ratings);
        dest.writeByte((byte) (isPurchased ? 1 : 0)); // Write boolean to parcel

    }
}
