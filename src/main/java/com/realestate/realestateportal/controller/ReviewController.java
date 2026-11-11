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

    // Encapsulation:
    // HAS-A RELATIONSHIP: ReviewController has ReviewService,
    // AppointmentService and AgentService objects.
    private final ReviewService reviewService = new ReviewService();

    private final AppointmentService appointmentService =
            new AppointmentService();

    private final AgentService agentService = new AgentService();

    // Submit Review - (client )
    @PostMapping("/submit")
    public String submitReview(
            @RequestParam String agentEmail,
            @RequestParam int rating,
            @RequestParam String comment,
            HttpSession session) {

        // Object type casting: Retrieves the logged-in User object
        // from the session and casts it to the User type.
        User user = (User) session.getAttribute("loggedInUser");

        if (user == null || !"CLIENT".equals(user.getRole())) {
            return "Access denied. Please login as a Client.";
        }

        // Object interaction: Uses AppointmentService to retrieve
        // appointments belonging to the logged-in client.
        List<Appointment> appointments =
                appointmentService.getClientAppointments(user.getEmail());

        boolean validAppointment = false;

        for (Appointment appointment : appointments) {

            boolean correctAgent = appointment.getAgentEmail() != null
                    && appointment.getAgentEmail()
                    .equalsIgnoreCase(agentEmail);

            // Only completed appointments are eligible
            boolean validStatus =
                    "COMPLETED".equalsIgnoreCase(appointment.getStatus());

            if (correctAgent && validStatus) {
                validAppointment = true;
                break;
            }
        }

        if (!validAppointment) {
            return "You must have a completed appointment with this Agent to submit a review.";
        }

        try {
            // Abstraction: Calls the ReviewService method instead of
            // handling review submission logic directly in the controller.
            reviewService.submitReview(
                    agentEmail,
                    user.getEmail(),
                    rating,
                    comment
            );

            return "Review submitted successfully. Waiting for Admin approval.";

        } catch (IllegalArgumentException e) {
            return e.getMessage();

        } catch (IllegalStateException e) {
            return "Unable to save review. Please try again.";
        }
    }

    // Get Client Reviews
    @GetMapping("/my-reviews")
    public Object getClientReviews(HttpSession session) {

        User user = (User) session.getAttribute("loggedInUser");

        if (user == null || !"CLIENT".equals(user.getRole())) {
            return "Access denied. Please login as a Client.";
        }

        // Delegates review retrieval to the ReviewService.
        return reviewService.getReviewsByClient(user.getEmail());
    }

    // Get Agent Reviews (Agent ge)
    @GetMapping("/agent-reviews")
    public Object getAgentReviews(HttpSession session) {

        User user = (User) session.getAttribute("loggedInUser");

        if (user == null || !"AGENT".equals(user.getRole())) {
            return "Access denied. Please login as an Agent.";
        }

        List<Review> reviews =
                reviewService.getReviewsByAgent(user.getEmail());

        // Object creation: Creates an ArrayList object to store
        // approved Review objects.
        List<Review> approvedReviews = new ArrayList<>();

        for (Review review : reviews) {

            if ("APPROVED".equals(review.getStatus())) {
                approvedReviews.add(review);
            }
        }

        return approvedReviews;
    }

    // Update Review (Client ge)
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
            // OBJECT Intreaction: Calls ReviewService to update
            // the selected review.
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

    // Delete Review (Client ge)
    @PostMapping("/delete")
    public String deleteReview(
            @RequestParam String reviewId,
            HttpSession session) {

        User user = (User) session.getAttribute("loggedInUser");

        if (user == null || !"CLIENT".equals(user.getRole())) {
            return "Access denied. Please login as a Client.";
        }

        // Delegates review deletion to the ReviewService.
        boolean deleted = reviewService.deleteReview(
                reviewId,
                user.getEmail()
        );

        return deleted
                ? "Review deleted successfully."
                : "Review not found or access denied.";
    }

    // Get All Reviews ( Admin )
    @GetMapping("/admin/all")
    public Object getAllReviews(HttpSession session) {

        User user = (User) session.getAttribute("loggedInUser");

        if (user == null || !"ADMIN".equals(user.getRole())) {
            return "Access denied. Admin access required.";
        }

        // Uses ReviewService to retrieve all Review objects.
        return reviewService.getAllReviews();
    }

    // Approve or Reject Review (Admin)
    @PostMapping("/admin/update-status")
    public String updateReviewStatus(
            @RequestParam String reviewId,
            @RequestParam String status,
            HttpSession session) {

        User user = (User) session.getAttribute("loggedInUser");

        if (user == null || !"ADMIN".equals(user.getRole())) {
            return "Access denied. Admin access required.";
        }

        if (status == null ||
                (!"APPROVED".equalsIgnoreCase(status)
                        && !"REJECTED".equalsIgnoreCase(status))) {

            return "Invalid review status.";
        }

        // Uses ReviewService to update the review status.
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

                // Object interaction: Uses ReviewService to calculate
                // the average rating for the relevant agent.
                double averageRating =
                        reviewService.calculateAverageRating(
                                review.getAgentEmail()
                        );

                // Uses AgentService to update the agent's average rating.
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