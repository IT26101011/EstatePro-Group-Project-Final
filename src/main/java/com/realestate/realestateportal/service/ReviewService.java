package com.realestate.realestateportal.service;

import com.realestate.realestateportal.model.Review;
import com.realestate.realestateportal.repository.ReviewRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class ReviewService {


    private final ReviewRepository reviewRepository;

    public ReviewService() {
        this.reviewRepository = new ReviewRepository();
    }

    // Submit Review
    public Review submitReview(String agentEmail,
                               String clientEmail,
                               int rating,
                               String comment) {

        if (agentEmail == null || agentEmail.isBlank()
                || clientEmail == null || clientEmail.isBlank()) {
            throw new IllegalArgumentException("Invalid user details.");
        }

        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5.");
        }

        if (comment == null || comment.isBlank()) {
            throw new IllegalArgumentException("Review comment cannot be empty.");
        }
        if (comment.contains("|")
                || comment.contains("\n")
                || comment.contains("\r")) {
            throw new IllegalArgumentException(
                    "Review comment cannot contain | or line breaks."
            );
        }

        String reviewId = "REV-" + UUID.randomUUID();

        Review review = new Review(
                reviewId,
                agentEmail,
                clientEmail,
                rating,
                comment.trim(),
                LocalDate.now().toString(),
                "PENDING"
        );

        reviewRepository.saveReview(review);

        return review;
    }

    // Get All Reviews
    public List<Review> getAllReviews() {
        return reviewRepository.getAllReviews();
    }

    // Get Reviews By Agent
    public List<Review> getReviewsByAgent(String agentEmail) {
        return reviewRepository.findByAgentEmail(agentEmail);
    }

    // Get Reviews By Client
    public List<Review> getReviewsByClient(String clientEmail) {
        return reviewRepository.findByClientEmail(clientEmail);
    }

    // Update Review
    public boolean updateReview(String reviewId,
                                String clientEmail,
                                int rating,
                                String comment) {

        Review review = reviewRepository.findById(reviewId);

        if (review == null) {
            return false;
        }

        if (!review.getClientEmail().equalsIgnoreCase(clientEmail)) {
            return false;
        }

        if (!"PENDING".equals(review.getStatus())) {
            return false;
        }

        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5.");
        }

        if (comment == null || comment.isBlank()) {
            throw new IllegalArgumentException("Review comment cannot be empty.");
        }
        if (comment.contains("|")
                || comment.contains("\n")
                || comment.contains("\r")) {
            throw new IllegalArgumentException(
                    "Review comment cannot contain | or line breaks."
            );
        }


        review.setRating(rating);
        review.setComment(comment.trim());

        return reviewRepository.updateReview(review);
    }

    // Delete Review
    public boolean deleteReview(String reviewId, String clientEmail) {

        Review review = reviewRepository.findById(reviewId);

        if (review == null) {
            return false;
        }

        if (!review.getClientEmail().equalsIgnoreCase(clientEmail)) {
            return false;
        }

        return reviewRepository.deleteReview(reviewId);
    }

    // Update Review Status
    public boolean updateReviewStatus(String reviewId, String status) {

        if (!status.equals("APPROVED") && !status.equals("REJECTED")) {
            return false;
        }

        Review review = reviewRepository.findById(reviewId);

        if (review == null) {
            return false;
        }

        review.setStatus(status);

        return reviewRepository.updateReview(review);
    }

    // Calculate Average Rating
    public double calculateAverageRating(String agentEmail) {

        List<Review> reviews = reviewRepository.findByAgentEmail(agentEmail);

        int totalRating = 0;
        int count = 0;

        for (Review review : reviews) {

            if ("APPROVED".equals(review.getStatus())) {
                totalRating += review.getRating();
                count++;
            }
        }

        if (count == 0) {
            return 0.0;
        }

        return Math.round((double) totalRating / count * 100.0) / 100.0;
    }

}
