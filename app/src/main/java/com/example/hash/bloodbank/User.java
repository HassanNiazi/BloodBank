package com.example.hash.bloodbank;

import android.net.Uri;

/**
 * Created by hash on 9/25/16.
 */

public class User {
    private String name;
    private String bloodGroup;
    private String city;
    private String country;
    private double latitude;
    private double longitude;
    private boolean availableToDonate;

    public User(String name, String bloodGroup, String city, String country, double latitude, double longitude, boolean availableToDonate) {
        this.name = name;
        this.bloodGroup = bloodGroup;
        this.city = city;
        this.country = country;
        this.latitude = latitude;
        this.longitude = longitude;
        this.availableToDonate = availableToDonate;
    }

    public User() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public boolean isAvailableToDonate() {
        return availableToDonate;
    }

    public void setAvailableToDonate(boolean availableToDonate) {
        this.availableToDonate = availableToDonate;
    }
}
