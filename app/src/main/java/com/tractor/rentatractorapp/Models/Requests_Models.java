package com.tractor.rentatractorapp.Models;

public class Requests_Models {

    String image_url, title, rent, time, timeStamp, request_status, from;

    public Requests_Models() {
    }

    public Requests_Models(String image_url, String title, String rent, String time, String timeStamp, String request_status, String from) {
        this.image_url = image_url;
        this.title = title;
        this.rent = rent;
        this.time = time;
        this.timeStamp = timeStamp;
        this.request_status = request_status;
        this.from = from;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getRequest_status() {
        return request_status;
    }

    public void setRequest_status(String request_status) {
        this.request_status = request_status;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRent() {
        return rent;
    }

    public void setRent(String rent) {
        this.rent = rent;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

}
