package com.realestate.realestateportal.controller;

import com.realestate.realestateportal.model.Review;
import com.realestate.realestateportal.model.Appointment;
import com.realestate.realestateportal.model.User;

import com.realestate.realestateportal.service.ReviewService;
import com.realestate.realestateportal.service.AppointmentService;
import com.realestate.realestateportal.service.AgentService;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/review")
public class ReviewController {

    private final ReviewService reviewService = new ReviewService();

    private final AppointmentService appointmentService =
            new AppointmentService();

    private final AgentService agentService = new AgentService();

    // Submit Review - Client Only
    @PostMapping("/submit")
    public String submitReview(
            @RequestParam String agentEmail,
            @RequestParam int rating,
            @RequestParam String comment,
            HttpSession session) {

        User user = (User) session.getAttribute("loggedInUser");

        if (user == null || !"CLIENT".equals(user.getRole())) {
            return "Access denied. Please login as a Client.";
        }

        List<Appointment> appointments =
                appointmentService.getClientAppointments(user.getEmail());

        boolean validAppointment = false;

        for (Appointment appointment : appointments) {

            boolean correctAgent = appointment.getAgentEmail()
                    .equalsIgnoreCase(agentEmail);

            boolean validStatus = "APPROVED".equals(appointment.getStatus())
                    || "COMPLETED".equals(appointment.getStatus());

            if (correctAgent && validStatus) {
                validAppointment = true;
                break;
            }
        }

        if (!validAppointment) {
            return "You must have an approved appointment with this Agent to submit a review.";
        }

        try {
            reviewService.submitReview(
                    agentEmail,
                    user.getEmail(),
                    rating,
                    comment
            );

            return "Review submitted successfully. Waiting for Admin approval.";

        } catch (IllegalArgumentException e) {
            return e.getMessage();
        }
    }

    // Get Client Reviews
    @GetMapping("/my-reviews")
    public Object getClientReviews(HttpSession session) {

        User user = (User) session.getAttribute("loggedInUser");

        if (user == null || !"CLIENT".equals(user.getRole())) {
            return "Access denied. Please login as a Client.";
        }

        return reviewService.getReviewsByClient(user.getEmail());
    }

    // Get Agent Reviews - Agent Only
    @GetMapping("/agent-reviews")
    public Object getAgentReviews(HttpSession session) {

        User user = (User) session.getAttribute("loggedInUser");

        if (user == null || !"AGENT".equals(user.getRole())) {
            return "Access denied. Please login as an Agent.";
        }

        List<Review> reviews =
                reviewService.getReviewsByAgent(user.getEmail());

        List<Review> approvedReviews = new ArrayList<>();

        for (Review review : reviews) {

            if ("APPROVED".equals(review.getStatus())) {
                approvedReviews.add(review);
            }
        }

        return approvedReviews;
    }

    // Update Review - Client Only
    @PostMapping("/update")
    public String updateReview(
            @RequestParam String reviewId,
            @RequestParam int rating,
            @RequestParam String comment,
            HttpSession session) {

        User user = (User) session.getAttribute("loggedInUser");

        if (user == null || !"CLIENT".equals(user.getRole())) {
            return "Access denied. Please login as a Client.";
        }

        try {
            boolean updated = reviewService.updateReview(
                    reviewId,
                    user.getEmail(),
                    rating,
                    comment
            );

            return updated
                    ? "Review updated successfully."
                    : "Review not found or cannot be updated.";

        } catch (IllegalArgumentException e) {
            return e.getMessage();
        }
    }

    // Delete Review - Client Only
    @PostMapping("/delete")
    public String deleteReview(
            @RequestParam String reviewId,
            HttpSession session) {

        User user = (User) session.getAttribute("loggedInUser");

        if (user == null || !"CLIENT".equals(user.getRole())) {
            return "Access denied. Please login as a Client.";
        }

        boolean deleted = reviewService.deleteReview(
                reviewId,
                user.getEmail()
        );

        return deleted
                ? "Review deleted successfully."
                : "Review not found or access denied.";
    }

    // Get All Reviews - Admin Only
    @GetMapping("/admin/all")
    public Object getAllReviews(HttpSession session) {

        User user = (User) session.getAttribute("loggedInUser");

        if (user == null || !"ADMIN".equals(user.getRole())) {
            return "Access denied. Admin access required.";
        }

        return reviewService.getAllReviews();
    }

    // Approve or Reject Review - Admin Only
    @PostMapping("/admin/update-status")
    public String updateReviewStatus(
            @RequestParam String reviewId,
            @RequestParam String status,
            HttpSession session) {

        User user = (User) session.getAttribute("loggedInUser");

        if (user == null || !"ADMIN".equals(user.getRole())) {
            return "Access denied. Admin access required.";
        }

        if (!"APPROVED".equals(status) && !"REJECTED".equals(status)) {
            return "Invalid review status.";
        }

        boolean updated = reviewService.updateReviewStatus(
                reviewId,
                status
        );

        if (!updated) {
            return "Unable to update review status.";
        }

        // Recalculate Agent Average Rating
        List<Review> reviews = reviewService.getAllReviews();

        for (Review review : reviews) {

            if (review.getReviewId().equals(reviewId)) {

                double averageRating =
                        reviewService.calculateAverageRating(
                                review.getAgentEmail()
                        );

                boolean ratingUpdated = agentService.updateAgentRating(
                        review.getAgentEmail(),
                        averageRating
                );

                if (!ratingUpdated) {
                    return "Review status updated, but Agent rating update failed.";
                }

                break;
            }
        }

        return "Review status updated successfully. Agent rating updated.";
    }
}