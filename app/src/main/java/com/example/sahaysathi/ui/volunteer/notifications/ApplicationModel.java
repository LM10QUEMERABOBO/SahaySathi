package com.example.sahaysathi.ui.volunteer.notifications;

public class ApplicationModel {

    private String applicationId;
    private String eventId;
    private String ngoId;
    private String userId;
    private String status;
    private long timestamp;
    private String eventName;
    private String location;
    private String instructions;

    public ApplicationModel() {}
    public String getApplicationId() { return applicationId; }
    public void setApplicationId(String applicationId) { this.applicationId = applicationId; }
    public String getEventId() { return eventId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getStatus() { return status; }
    public String getEventName() { return eventName; }

    public String getlocation() { return location; }

    public String getInstructions() { return instructions; }
}