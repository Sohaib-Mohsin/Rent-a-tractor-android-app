package com.tractor.rentatractorapp.Models;

public class View_users {

    String Email, Location, Name, Phone, image;

    public View_users() {
    }

    public View_users(String email, String location, String name, String phone, String image) {
        Email = email;
        Location = location;
        Name = name;
        Phone = phone;
        this.image = image;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
