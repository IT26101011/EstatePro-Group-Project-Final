package com.realestate.realestateportal.controller;

import com.realestate.realestateportal.model.User;
import com.realestate.realestateportal.service.AdminService;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService = new AdminService();

    // Check whether the logged-in user is an Admin
    private boolean isAdmin(HttpSession session) {

        User user = (User) session.getAttribute("loggedInUser");

        return user != null && "ADMIN".equals(user.getRole());
    }

    // Dashboard Statistics
    @GetMapping("/stats")
    public Object getStats(HttpSession session) {

        if (!isAdmin(session)) {
            return "Access denied. Admin only.";
        }

        return adminService.getDashboardStats();
    }

    // View All Properties
    @GetMapping("/properties")
    public Object getAllProperties(HttpSession session) {

        if (!isAdmin(session)) {
            return "Access denied. Admin only.";
        }

        return adminService.getAllProperties();
    }

    // Delete Property
    @PostMapping("/delete-property")
    public String deleteProperty(
            @RequestParam("propertyId") String propertyId,
            HttpSession session) {

        if (!isAdmin(session)) {
            return "Access denied. Admin only.";
        }

        User admin = (User) session.getAttribute("loggedInUser");

        return adminService.deleteProperty(admin.getEmail(), propertyId);
    }

    // View All Appointments
    @GetMapping("/appointments")
    public Object getAllAppointments(HttpSession session) {

        if (!isAdmin(session)) {
            return "Access denied. Admin only.";
        }

        return adminService.getAllAppointments();
    }

    // Delete Appointment
    @PostMapping("/delete-appointment")
    public String deleteAppointment(
            @RequestParam("appointmentId") String appointmentId,
            HttpSession session) {

        if (!isAdmin(session)) {
            return "Access denied. Admin only.";
        }

        User admin = (User) session.getAttribute("loggedInUser");

        return adminService.deleteAppointment(admin.getEmail(), appointmentId);
    }

    // View Admin Activity Log
    @GetMapping("/activity")
    public Object getActivityLog(HttpSession session) {

        if (!isAdmin(session)) {
            return "Access denied. Admin only.";
        }

        return adminService.getActivityLog();
    }
}
