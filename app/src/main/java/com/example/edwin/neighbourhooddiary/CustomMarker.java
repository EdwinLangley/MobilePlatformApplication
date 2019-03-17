package com.example.edwin.neighbourhooddiary;

import java.util.ArrayList;

public class CustomMarker {

    public double lat;
    public double lng;
    public boolean isExpirable;
    public String eventName;
    public String eventType;
    public long startTime;
    public long endTime;
    public String descrip;
    public ArrayList<String> groupsWelcome;
    public String addedBy;
    public float rating;
    public int numberOfRatings;
    public String imageString;

    public int getNumberOfRatings() {
        return numberOfRatings;
    }

    public void setNumberOfRatings(int numberOfRatings) {
        this.numberOfRatings = numberOfRatings;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getImageString() {
        return imageString;
    }

    public void setImageString(String imageString) {
        this.imageString = imageString;
    }

    public CustomMarker(double lat, double lng, boolean isExpirable, String eventName, String eventType, long startTime, long endTime, String descrip, String addedBy) {
        this.lat = lat;
        this.lng = lng;
        this.isExpirable = isExpirable;
        this.eventName = eventName;
        this.eventType = eventType;
        this.startTime = startTime;
        this.endTime = endTime;
        this.descrip = descrip;
        this.groupsWelcome = groupsWelcome;
        this.addedBy = addedBy;
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

    public boolean isExpirable() {
        return isExpirable;
    }

    public void setExpirable(boolean expirable) {
        isExpirable = expirable;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String getDescrip() {
        return descrip;
    }

    public void setDescrip(String descrip) {
        this.descrip = descrip;
    }

    public ArrayList<String> getGroupsWelcome() {
        return groupsWelcome;
    }

    public void setGroupsWelcome(ArrayList<String> groupsWelcome) {
        this.groupsWelcome = groupsWelcome;
    }

    public String getAddedBy() {
        return addedBy;
    }

    public void setAddedBy(String addedBy) {
        this.addedBy = addedBy;
    }







    public CustomMarker() {
    }
}
