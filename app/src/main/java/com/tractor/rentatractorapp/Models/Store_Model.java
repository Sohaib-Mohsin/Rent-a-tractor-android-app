package com.tractor.rentatractorapp.Models;

import java.io.Serializable;
import java.util.ArrayList;

public class Store_Model implements Serializable {

    String Title, Description, TimeStamp, Location, Status,ImagesUrl;
    Float Rating;

    public Store_Model() {
    }

    public Store_Model(String title, String description, String timeStamp, String imagesUrl, String location, Float rating, String status) {
        Title = title;
        Description = description;
        TimeStamp = timeStamp;
        ImagesUrl = imagesUrl;
        Location = location;
        Rating = rating;
        Status = status;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
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

    public String getImagesUrl() {
        return ImagesUrl;
    }

    public void setImagesUrl(String imagesUrl) {
        ImagesUrl = imagesUrl;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public float getRating() {
        return Rating;
    }

    public void setRating(Float rating) {
        Rating = rating;
    }
}
