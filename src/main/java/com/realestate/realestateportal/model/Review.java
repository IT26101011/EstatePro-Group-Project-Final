package com.realestate.realestateportal.model;

import java.util.Locale;

public class Review {

    private String reviewId;
    private String agentEmail;
    private String clientEmail;
    private int rating;
    private String comment;
    private String date;
    private String status;

    // Default Constructor
    public Review() {
    }

    // Parameterized Constructor
    public Review(String reviewId, String agentEmail,
                  String clientEmail, int rating,
                  String comment, String date, String status) {

        setReviewId(reviewId);
        setAgentEmail(agentEmail);
        setClientEmail(clientEmail);
        setRating(rating);
        setComment(comment);
        setDate(date);
        setStatus(status);
    }

    // Getters and Setters

    public String getReviewId() {
        return reviewId;
    }

    public void setReviewId(String reviewId) {
        if (reviewId == null || reviewId.trim().isEmpty()) {
            throw new IllegalArgumentException("Review ID cannot be empty");
        }
        this.reviewId = reviewId.trim();
    }

    public String getAgentEmail() {
        return agentEmail;
    }

    public void setAgentEmail(String agentEmail) {
        if (agentEmail == null || agentEmail.trim().isEmpty()) {
            throw new IllegalArgumentException("Agent email cannot be empty");
        }
        this.agentEmail = agentEmail.trim();
    }

    public String getClientEmail() {
        return clientEmail;
    }

    public void setClientEmail(String clientEmail) {
        if (clientEmail == null || clientEmail.trim().isEmpty()) {
            throw new IllegalArgumentException("Client email cannot be empty");
        }
        this.clientEmail = clientEmail.trim();
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException(
                    "Rating must be between 1 and 5"
            );
        }
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        if (comment == null || comment.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Review comment cannot be empty"
            );
        }
        this.comment = comment.trim();
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        if (date == null || date.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Review date cannot be empty"
            );
        }
        this.date = date.trim();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {

        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Review status cannot be empty"
            );
        }

        String normalizedStatus = status.trim().toUpperCase(Locale.ROOT);

        if (!normalizedStatus.equals("PENDING")
                && !normalizedStatus.equals("APPROVED")
                && !normalizedStatus.equals("REJECTED")) {

            throw new IllegalArgumentException(
                    "Status must be PENDING, APPROVED or REJECTED"
            );
        }

        this.status = normalizedStatus;
    }
}