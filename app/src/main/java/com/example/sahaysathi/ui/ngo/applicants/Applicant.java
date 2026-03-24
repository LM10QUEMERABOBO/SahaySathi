package com.example.sahaysathi.ui.ngo.applicants;

public class Applicant {

    private String applicationId;
    private String volunteerId;
    private String name;
    private String city;
    private String skill;
    private String status;

    public Applicant() {} // required for Firestore

    public Applicant(String applicationId, String volunteerId,
                     String name, String city, String skill, String status) {
        this.applicationId = applicationId;
        this.volunteerId = volunteerId;
        this.name = name;
        this.city = city;
        this.skill = skill;
        this.status = status;
    }

    public String getApplicationId() { return applicationId; }
    public String getVolunteerId() { return volunteerId; }
    public String getName() { return name; }
    public String getCity() { return city; }
    public String getSkill() { return skill; }
    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }
}