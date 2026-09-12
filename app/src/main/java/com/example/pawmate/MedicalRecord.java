package com.example.pawmate;

public class MedicalRecord {

    private String id;
    private String diagnosis;
    private String date;
    private String vetName;
    private String notes;
    private long createdAt;


    // Required empty constructor for Firestore
    public MedicalRecord() {
    }


    public MedicalRecord(
            String diagnosis,
            String date,
            String vetName,
            String notes,
            long createdAt) {

        this.diagnosis = diagnosis;
        this.date = date;
        this.vetName = vetName;
        this.notes = notes;
        this.createdAt = createdAt;
    }


    // ID

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    // Diagnosis

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }


    // Date

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    // Vet Name

    public String getVetName() {
        return vetName;
    }

    public void setVetName(String vetName) {
        this.vetName = vetName;
    }


    // Notes

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }


    // Created At

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}