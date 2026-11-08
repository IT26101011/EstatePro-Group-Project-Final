package com.realestate.realestateportal.model;

public class Review {

    private String reviewId;
    private String agentEmail;
    private String clientEmail;
    private int rating;
    private String comment;
    private String date;
    private String status;

    public Review() {
    }

    public Review(String reviewId, String agentEmail,
                  String clientEmail, int rating,
                  String comment, String date, String status) {

        this.reviewId = reviewId;
        this.agentEmail = agentEmail;
        this.clientEmail = clientEmail;
        this.rating = rating;
        this.comment = comment;
        this.date = date;
        this.status = status;
    }

    public String getReviewId() {
        return reviewId;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }

    public String getAgentEmail() {
        return agentEmail;
    }

    public void setAgentEmail(String agentEmail) {
        this.agentEmail = agentEmail;
    }

    public String getClientEmail() {
        return clientEmail;
    }

    public void setClientEmail(String clientEmail) {
        this.clientEmail = clientEmail;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
