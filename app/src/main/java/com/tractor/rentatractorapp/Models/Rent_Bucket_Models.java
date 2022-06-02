package com.tractor.rentatractorapp.Models;

public class Rent_Bucket_Models {

    String ImageUrl, Title, Description, Store, TimeStamp, Cost, Time, Duration;

    public Rent_Bucket_Models() {
    }

    public Rent_Bucket_Models(String imageUrl, String title, String description, String store, String timeStamp, String cost, String time, String duration) {
        ImageUrl = imageUrl;
        Title = title;
        Description = description;
        Store = store;
        TimeStamp = timeStamp;
        Cost = cost;
        Time = time;
        Duration = duration;
    }

    public String getDuration() {
        return Duration;
    }

    public void setDuration(String duration) {
        Duration = duration;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
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

    public String getStore() {
        return Store;
    }

    public void setStore(String store) {
        Store = store;
    }

    public String getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        TimeStamp = timeStamp;
    }

    public String getCost() {
        return Cost;
    }

    public void setCost(String cost) {
        Cost = cost;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }
}
