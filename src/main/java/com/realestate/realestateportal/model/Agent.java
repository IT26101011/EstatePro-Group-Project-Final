package com.realestate.realestateportal.model;

public class Agent {

    private String agentId;
    private String email;
    private String fullName;
    private String agencyName;
    private String specialization;
    private String location;
    private int experienceYears;
    private String description;
    private double rating;

    // Default Constructor
    public Agent() {
    }

    // Parameterized Constructor
    public Agent(String agentId, String email, String agencyName,
                 String specialization, String location,
                 int experienceYears, String description,
                 double rating) {

        this.agentId = agentId;
        this.email = email;
        this.agencyName = agencyName;
        this.specialization = specialization;
        this.location = location;
        this.experienceYears = experienceYears;
        this.description = description;
        this.rating = rating;
    }

    // Getters and Setters

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAgencyName() {
        return agencyName;
    }

    public void setAgencyName(String agencyName) {
        this.agencyName = agencyName;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getExperienceYears() {
        return experienceYears;
    }

    public void setExperienceYears(int experienceYears) {
        this.experienceYears = experienceYears;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
