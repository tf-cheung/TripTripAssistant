package com.example.tripassistant.models;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class Trip {
    private String tripName;
    private List<String> members;

    private String startDate;

    public Trip() {
        // Default constructor required for calls to DataSnapshot.getValue(Trip.class)
    }

    public Trip(String tripName, List<String> members,String startDate) {
        this.tripName = tripName;
        this.members = members;
        this.startDate=startDate;
    }

    public String getTripName() {
        return tripName;
    }

    public void setTripName(String tripName) {
        this.tripName = tripName;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String starDate) {
        this.startDate = starDate;
    }
}
