package com.realestate.realestateportal.model;

// Stores one action done by an Admin (Admin Activity Log record)
public class Admin {

    private String adminEmail;
    private String action;
    private String details;
    private String dateTime;

    public Admin() {
    }

    public Admin(String adminEmail, String action,
                 String details, String dateTime) {

        this.adminEmail = adminEmail;
        this.action = action;
        this.details = details;
        this.dateTime = dateTime;
    }

    public String getAdminEmail() {
        return adminEmail;
    }

    public void setAdminEmail(String adminEmail) {
        this.adminEmail = adminEmail;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
}
