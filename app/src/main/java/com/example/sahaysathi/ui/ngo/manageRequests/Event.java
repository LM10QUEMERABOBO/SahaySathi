package com.example.sahaysathi.ui.ngo.manageRequests;
public class Event {

    public String id;
    public String title, location, date;
    public int appliedCount, selectedCount;

    public Event() {}

    public Event(String id, String title, String location, String date,
                 int appliedCount, int selectedCount) {

        this.id = id;
        this.title = title;
        this.location = location;
        this.date = date;
        this.appliedCount = appliedCount;
        this.selectedCount = selectedCount;
    }
}