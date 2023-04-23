package com.example.tripassistant.models;

public class StopPoint {
    String tripId,spId,name,address,date,time;
    String liked;
    double latitude,longitude;


    public StopPoint(String tripId, String spId, String name, String address, String date, String time, double latitude, double longitude, String liked) {
        this.tripId=tripId;
        this.spId = spId;
        this.name = name;
        this.address = address;
        this.date = date;
        this.time = time;
        this.latitude = latitude;
        this.longitude = longitude;
        this.liked = liked;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public String getSpId() {
        return spId;
    }

    public void setSpId(String spId) {
        this.spId = spId;
    }

    public void setLiked(String liked) {
        this.liked = liked;
    }

    public String getLiked() { return liked;}


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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
}
