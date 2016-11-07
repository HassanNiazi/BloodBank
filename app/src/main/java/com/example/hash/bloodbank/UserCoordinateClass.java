package com.example.hash.bloodbank;


public class UserCoordinateClass {
    private double lat;
    private double lng;
    private String phoneNo;
    private String name;
    private String bloodGroup;


    public UserCoordinateClass() {
    }

    public UserCoordinateClass(double lat, double lng, String phoneNo, String name, String bloodGroup) {
        this.lat = lat;
        this.lng = lng;
        this.phoneNo = phoneNo;
        this.name = name;
        this.bloodGroup = bloodGroup;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
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
