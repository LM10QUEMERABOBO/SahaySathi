package com.example.sahaysathi.ui.ngo.applicants;
public class Applicant {

    private String applicationId;
    private String volunteerId;
    private String name;
    private String city;
    private String skill;
    private String status;

    // NEW FIELDS
    private String eventName;
    private String location;

    public Applicant() {}

    public Applicant(String applicationId, String volunteerId,
                     String name, String city, String skill, String status,
                     String eventName, String location) {

        this.applicationId = applicationId;
        this.volunteerId = volunteerId;
        this.name = name;
        this.city = city;
        this.skill = skill;
        this.status = status;
        this.eventName = eventName;
        this.location = location;
    }

    public String getApplicationId() { return applicationId; }
    public String getVolunteerId() { return volunteerId; }
    public String getName() { return name; }
    public String getCity() { return city; }
    public String getSkill() { return skill; }
    public String getStatus() { return status; }
    public String getEventName() { return eventName; }
    public String getLocation() { return location; }
}