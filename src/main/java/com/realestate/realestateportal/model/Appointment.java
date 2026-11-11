package com.realestate.realestateportal.model;

public class Appointment {

    private String appointmentId;
    private String clientEmail;
    private String agentEmail;
    private String propertyId;
    private String appointmentDate;
    private String appointmentTime;
    private String message;
    private String status;

    public Appointment() {
    }

    public Appointment(String appointmentId, String clientEmail,
                       String agentEmail, String propertyId,
                       String appointmentDate, String appointmentTime,
                       String message, String status) {

        this.appointmentId = appointmentId;
        this.clientEmail = clientEmail;
        this.agentEmail = agentEmail;
        this.propertyId = propertyId;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.message = message;
        this.status = status;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getClientEmail() {
        return clientEmail;
    }

    public void setClientEmail(String clientEmail) {
        this.clientEmail = clientEmail;
    }

    public String getAgentEmail() {
        return agentEmail;
    }

    public void setAgentEmail(String agentEmail) {
        this.agentEmail = agentEmail;
    }

    public String getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }

    public String getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(String appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(String appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}