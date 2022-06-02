package com.tractor.rentatractorapp.Models;

import java.io.Serializable;
import java.util.ArrayList;

public class Tractor_Models implements Serializable {

    String Title, Description, Store, TimeStamp, Rent, ImagesUrls_1,ImagesUrls_2, Status, Store_ID, isAvailable;

    public Tractor_Models() {
    }

    public Tractor_Models(String title, String description, String store, String timeStamp, String rent, String imagesUrls_1, String imagesUrls_2, String status, String store_ID, String isAvailable) {
        Title = title;
        Description = description;
        Store = store;
        TimeStamp = timeStamp;
        Rent = rent;
        ImagesUrls_1 = imagesUrls_1;
        ImagesUrls_2 = imagesUrls_2;
        Status = status;
        Store_ID = store_ID;
        this.isAvailable = isAvailable;
    }

    public String getStore_ID() {
        return Store_ID;
    }

    public void setStore_ID(String store_ID) {
        Store_ID = store_ID;
    }

    public String getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(String isAvailable) {
        this.isAvailable = isAvailable;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getRent() {
        return Rent;
    }

    public void setRent(String rent) {
        Rent = rent;
    }

    public String getStore() {
        return Store;
    }

    public void setStore(String store) {
        Store = store;
    }

    public String getImagesUrls_1() {
        return ImagesUrls_1;
    }

    public void setImagesUrls_1(String imagesUrls_1) {
        ImagesUrls_1 = imagesUrls_1;
    }

    public String getImagesUrls_2() {
        return ImagesUrls_2;
    }

    public void setImagesUrls_2(String imagesUrls_2) {
        ImagesUrls_2 = imagesUrls_2;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        TimeStamp = timeStamp;
    }
}
