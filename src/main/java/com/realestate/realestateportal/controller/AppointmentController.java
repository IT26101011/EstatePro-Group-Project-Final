package com.realestate.realestateportal.controller;

import com.realestate.realestateportal.model.Appointment;
import com.realestate.realestateportal.model.User;
import com.realestate.realestateportal.model.Property;
import com.realestate.realestateportal.service.AppointmentService;
import com.realestate.realestateportal.service.PropertyService;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/appointment")
public class AppointmentController {

    private final AppointmentService appointmentService =
            new AppointmentService();

    private final PropertyService propertyService =
            new PropertyService();

    // Book Appointment - Client Only
    @PostMapping("/book")
    public String bookAppointment(
            @ModelAttribute Appointment appointment,
            HttpSession session) {

        User user = (User) session.getAttribute("loggedInUser");

        if (user == null || !"CLIENT".equals(user.getRole())) {
            return "Access denied. Please login as a Client.";
        }

        Property property = propertyService.getPropertyById(
                appointment.getPropertyId()
        );

        if (property == null) {
            return "Property not found.";
        }

        if (!property.getAgentEmail().equalsIgnoreCase(
                appointment.getAgentEmail())) {
            return "Invalid Agent for this property.";
        }

        appointment.setClientEmail(user.getEmail());
        appointment.setAgentEmail(property.getAgentEmail());

        return appointmentService.bookAppointment(appointment);
    }

    // Get Client Appointments
    @GetMapping("/my-appointments")
    public Object getClientAppointments(HttpSession session) {

        User user = (User) session.getAttribute("loggedInUser");

        if (user == null || !"CLIENT".equals(user.getRole())) {
            return "Access denied. Please login as a Client.";
        }

        return appointmentService.getClientAppointments(user.getEmail());
    }

    // Get Agent Appointments
    @GetMapping("/agent-appointments")
    public Object getAgentAppointments(HttpSession session) {

        User user = (User) session.getAttribute("loggedInUser");

        if (user == null || !"AGENT".equals(user.getRole())) {
            return "Access denied. Please login as an Agent.";
        }

        return appointmentService.getAgentAppointments(user.getEmail());
    }

    // Update Appointment - Client Only
    @PostMapping("/update")
    public String updateAppointment(
            @ModelAttribute Appointment appointment,
            HttpSession session) {

        User user = (User) session.getAttribute("loggedInUser");

        if (user == null || !"CLIENT".equals(user.getRole())) {
            return "Access denied.";
        }

        Appointment existing = appointmentService.getAppointmentById(
                appointment.getAppointmentId()
        );

        if (existing == null ||
                !existing.getClientEmail().equalsIgnoreCase(user.getEmail())) {
            return "Appointment not found or access denied.";
        }

        if (!"PENDING".equals(existing.getStatus())) {
            return "Only pending appointments can be updated.";
        }

        appointment.setClientEmail(existing.getClientEmail());
        appointment.setAgentEmail(existing.getAgentEmail());
        appointment.setPropertyId(existing.getPropertyId());

        return appointmentService.updateAppointment(appointment);
    }

    // Update Appointment Status - Agent Only
    @PostMapping("/update-status")
    public String updateAppointmentStatus(
            @RequestParam String appointmentId,
            @RequestParam String status,
            HttpSession session) {

        User user = (User) session.getAttribute("loggedInUser");

        if (user == null || !"AGENT".equals(user.getRole())) {
            return "Access denied. Please login as an Agent.";
        }

        Appointment appointment =
                appointmentService.getAppointmentById(appointmentId);

        if (appointment == null ||
                !appointment.getAgentEmail().equalsIgnoreCase(user.getEmail())) {
            return "Appointment not found or access denied.";
        }

        if (!"PENDING".equals(appointment.getStatus())) {
            return "This appointment has already been processed.";
        }

        return appointmentService.updateAppointmentStatus(
                appointmentId, status
        );
    }

    // Delete Appointment
    @PostMapping("/delete")
    public String deleteAppointment(
            @RequestParam String appointmentId,
            HttpSession session) {

        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "Please login first.";
        }

        Appointment appointment =
                appointmentService.getAppointmentById(appointmentId);

        if (appointment == null) {
            return "Appointment not found.";
        }

        boolean isClient = "CLIENT".equals(user.getRole()) &&
                appointment.getClientEmail().equalsIgnoreCase(user.getEmail());

        boolean isAgent = "AGENT".equals(user.getRole()) &&
                appointment.getAgentEmail().equalsIgnoreCase(user.getEmail());

        if (!isClient && !isAgent) {
            return "Access denied.";
        }

        return appointmentService.deleteAppointment(appointmentId);
    }
}