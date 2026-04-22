package com.example.sahaysathi.ui.volunteer.browseRequests;

public class Event {

    private String requestId;
    private String eventName;
    private String location;
    private String description;
    private String date;
    private String deadline;
    private String time;
    private String volunteerCount;
    private String ngoId;
    private String experience;

    // New fields
    private String venueName;
    private String fullAddress;
    private String landmark;
    private String areaSector;
    private String city;
    private String state;
    private String pincode;
    private String searchableLocation;

    public Event() {
        // Required for Firebase
    }

    public String getRequestId() { return requestId; }
    public String getEventName() { return eventName; }
    public String getLocation() { return location; }
    public String getDescription() { return description; }
    public String getDate() { return date; }
    public String getDeadline() { return deadline; }
    public String getTime() { return time; }
    public String getVolunteerCount() { return volunteerCount; }
    public String getNgoId() { return ngoId; }
    public String getExperience() { return experience; }

    public String getVenueName() { return venueName; }
    public String getFullAddress() { return fullAddress; }
    public String getLandmark() { return landmark; }
    public String getAreaSector() { return areaSector; }
    public String getCity() { return city; }
    public String getState() { return state; }
    public String getPincode() { return pincode; }
    public String getSearchableLocation() { return searchableLocation; }
}