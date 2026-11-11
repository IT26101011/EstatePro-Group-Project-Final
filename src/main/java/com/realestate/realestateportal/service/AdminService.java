package com.realestate.realestateportal.service;

import com.realestate.realestateportal.model.Admin;
import com.realestate.realestateportal.model.Appointment;
import com.realestate.realestateportal.model.Property;
import com.realestate.realestateportal.model.Review;
import com.realestate.realestateportal.model.User;
import com.realestate.realestateportal.repository.AdminRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AdminService {

    private final AdminRepository adminRepository = new AdminRepository();
    private final UserService userService = new UserService();
    private final PropertyService propertyService = new PropertyService();
    private final AppointmentService appointmentService = new AppointmentService();
    private final ReviewService reviewService = new ReviewService();
    private final AgentService agentService = new AgentService();

    // =====================================
    // DASHBOARD STATISTICS
    // =====================================

    public Map<String, Object> getDashboardStats() {

        Map<String, Object> stats = new LinkedHashMap<>();

        // Users
        int clients = 0;
        int agents = 0;
        int admins = 0;

        List<User> users = userService.getAllUsers();

        for (User user : users) {

            if (user.getRole().equals("CLIENT")) {
                clients++;
            } else if (user.getRole().equals("AGENT")) {
                agents++;
            } else if (user.getRole().equals("ADMIN")) {
                admins++;
            }
        }

        stats.put("totalUsers", users.size());
        stats.put("totalClients", clients);
        stats.put("totalAgents", agents);
        stats.put("totalAdmins", admins);

        // Agent profiles
        stats.put("agentProfiles", agentService.getAllAgents().size());

        // Properties
        stats.put("totalProperties", propertyService.getAllProperties().size());

        // Appointments
        int pendingAppointments = 0;

        List<Appointment> appointments = appointmentService.getAllAppointments();

        for (Appointment appointment : appointments) {

            if ("PENDING".equals(appointment.getStatus())) {
                pendingAppointments++;
            }
        }

        stats.put("totalAppointments", appointments.size());
        stats.put("pendingAppointments", pendingAppointments);

        // Reviews
        int pendingReviews = 0;

        List<Review> reviews = reviewService.getAllReviews();

        for (Review review : reviews) {

            if ("PENDING".equals(review.getStatus())) {
                pendingReviews++;
            }
        }

        stats.put("totalActivities", adminRepository.getAllActivities().size());
        stats.put("totalReviews", reviews.size());
        stats.put("pendingReviews", pendingReviews);

        return stats;
    }

    // =====================================
    // PROPERTY MANAGEMENT
    // =====================================

    public List<Property> getAllProperties() {

        return propertyService.getAllProperties();
    }

    public String deleteProperty(String adminEmail, String propertyId) {

        if (propertyId == null || propertyId.isBlank()) {
            return "Invalid property ID.";
        }

        if (propertyService.getPropertyById(propertyId) == null) {
            return "Property not found.";
        }

        String result = propertyService.deleteProperty(propertyId);

        if (result.contains("successfully")) {
            logActivity(adminEmail, "DELETE_PROPERTY", "Property ID " + propertyId);
        }

        return result;
    }

    // =====================================
    // APPOINTMENT MANAGEMENT
    // =====================================

    public List<Appointment> getAllAppointments() {

        return appointmentService.getAllAppointments();
    }

    public String deleteAppointment(String adminEmail, String appointmentId) {

        if (appointmentId == null || appointmentId.isBlank()) {
            return "Invalid appointment ID.";
        }

        if (appointmentService.getAppointmentById(appointmentId) == null) {
            return "Appointment not found.";
        }

        String result = appointmentService.deleteAppointment(appointmentId);

        if (result.contains("successfully")) {
            logActivity(adminEmail, "DELETE_APPOINTMENT", "Appointment ID " + appointmentId);
        }

        return result;
    }

    // =====================================
    // ADMIN ACTIVITY LOG
    // =====================================

    // Save one admin action
    public void logActivity(String adminEmail, String action, String details) {

        String dateTime = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        adminRepository.saveActivity(
                new Admin(adminEmail, action, details, dateTime)
        );
    }

    // Get all admin actions (newest first)
    public List<Admin> getActivityLog() {

        List<Admin> activities = adminRepository.getAllActivities();

        java.util.Collections.reverse(activities);

        return activities;
    }
}
