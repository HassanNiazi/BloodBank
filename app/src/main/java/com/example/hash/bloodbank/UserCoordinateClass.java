package com.example.hash.bloodbank;

/**
 * Created by hash on 10/18/16.
 */

public class UserCoordinateClass {
    double lat;
    double lng;
    String phoneNo;
    String name;

    public UserCoordinateClass() {
    }

    public UserCoordinateClass(double lat, double lng, String phoneNo, String name) {
        this.lat = lat;
        this.lng = lng;
        this.phoneNo = phoneNo;
        this.name = name;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
