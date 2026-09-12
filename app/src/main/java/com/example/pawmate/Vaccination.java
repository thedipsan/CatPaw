package com.example.pawmate;

public class Vaccination {

    private String id;
    private String name;
    private String date;
    private String nextDate;
    private String notes;

    public Vaccination() {
        // Required for Firebase
    }

    public Vaccination(
            String id,
            String name,
            String date,
            String nextDate,
            String notes) {

        this.id = id;
        this.name = name;
        this.date = date;
        this.nextDate = nextDate;
        this.notes = notes;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getNextDate() {
        return nextDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setNextDate(String nextDate) {
        this.nextDate = nextDate;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}