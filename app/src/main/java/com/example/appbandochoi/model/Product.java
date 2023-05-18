package com.example.appbandochoi.model;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.example.appbandochoi.BR;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class Product extends BaseObservable implements Serializable {
    private int productID;
    private String productName;
    private String description;
    private int quantity;
    private long price;
    private String images;
    private boolean status;

    @Bindable
    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
        notifyPropertyChanged(BR.productID);
    }

    @Bindable
    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
        notifyPropertyChanged(BR.productName);
    }

    @Bindable
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        notifyPropertyChanged(BR.description);
    }

    @Bindable
    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        notifyPropertyChanged(BR.quantity);
    }

    @Bindable
    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
        notifyPropertyChanged(BR.price);
    }

    @Bindable
    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
        notifyPropertyChanged(BR.images);
    }

    @Bindable
    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
        notifyPropertyChanged(BR.images);
    }

    @Override
    public String toString() {
        return "Product ID: " + productID +
                "\nProduct Name: " + productName +
                "\nDescription: " + description +
                "\nQuantity: " + quantity +
                "\nPrice: " + price +
                "\nImages: " + images +
                "\nStatus: " + status;
    }
}
