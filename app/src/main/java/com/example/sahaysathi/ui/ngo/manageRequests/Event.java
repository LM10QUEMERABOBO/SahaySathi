package com.example.sahaysathi.ui.ngo.manageRequests;
public class Event {

    public String title, location, date;
    public int appliedCount, selectedCount;

    public Event(String title, String location, String date, int appliedCount, int selectedCount) {
        this.title = title;
        this.location = location;
        this.date = date;
        this.appliedCount = appliedCount;
        this.selectedCount = selectedCount;
    }
}