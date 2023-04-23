package com.example.tripassistant.models;

public class StopPoint {
    String id,name,address,date,time,plike;
    double latitude,longitude;

    public StopPoint() {

    }
    public StopPoint(String id, String name, String address, String date, String time, double latitude, double longitude, String plike) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.date = date;
        this.time = time;
        this.latitude = latitude;
        this.longitude = longitude;
        this.plike = plike;
    }

    public String getPlike() {
        if (plike == null) {
            plike = "0";
        }
        return plike;}

    public void setPlike(String plike) { this.plike = plike;}

    public String addOnePlike() {
        if (plike == null) {
            plike = "1";
        } else {
            plike = Integer.parseInt(plike) + 1 + "";
        }
        return plike;
    }

    public String removeOnePlike() {
        if (plike == null || plike.equals("0") || plike.equals("1")) {
            plike = "0";
        } else {
            plike = Integer.parseInt(plike) - 1 + "";
        }
        return plike;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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
