package com.realestate.realestateportal.service;

import com.realestate.realestateportal.model.Appointment;
import com.realestate.realestateportal.repository.AppointmentRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public class AppointmentService {

    private final AppointmentRepository appointmentRepository =
            new AppointmentRepository();

    // Book Appointment
    public synchronized String bookAppointment(Appointment appointment) {

        if (appointment.getClientEmail() == null ||
                appointment.getAgentEmail() == null ||
                appointment.getPropertyId() == null ||
                appointment.getAppointmentDate() == null ||
                appointment.getAppointmentTime() == null) {

            return "Please fill all required fields.";
        }

        if (appointment.getClientEmail().isBlank() ||
                appointment.getAgentEmail().isBlank() ||
                appointment.getPropertyId().isBlank() ||
                appointment.getAppointmentDate().isBlank() ||
                appointment.getAppointmentTime().isBlank()) {

            return "Please fill all required fields.";
        }

        try {
            LocalDate date = LocalDate.parse(
                    appointment.getAppointmentDate());

            LocalTime time = LocalTime.parse(
                    appointment.getAppointmentTime());

            if (date.isBefore(LocalDate.now())) {
                return "Please select a future date.";
            }

            if (date.equals(LocalDate.now()) &&
                    !time.isAfter(LocalTime.now())) {

                return "Please select a future time.";
            }

        } catch (Exception e) {
            return "Invalid appointment date or time.";
        }

        if (hasConflict(
                appointment.getAgentEmail(),
                appointment.getAppointmentDate(),
                appointment.getAppointmentTime(),
                null)) {

            return "Appointment conflict! This Agent already has an appointment at this time.";
        }

        if (appointment.getMessage() == null) {
            appointment.setMessage("");
        }

        appointment.setAppointmentId(
                "APT-" + UUID.randomUUID().toString().substring(0, 8)
        );

        appointment.setStatus("PENDING");

        appointmentRepository.saveAppointment(appointment);

        return "Appointment booked successfully!";
    }

    // Get All Appointments
    public List<Appointment> getAllAppointments() {
        return appointmentRepository.getAllAppointments();
    }

    // Get Client Appointments
    public List<Appointment> getClientAppointments(String email) {
        return appointmentRepository.findByClientEmail(email);
    }

    // Get Agent Appointments
    public List<Appointment> getAgentAppointments(String email) {
        return appointmentRepository.findByAgentEmail(email);
    }

    // Get Appointment By ID
    public Appointment getAppointmentById(String appointmentId) {
        return appointmentRepository.findById(appointmentId);
    }

    // Update Appointment
    public synchronized String updateAppointment(Appointment appointment) {

        if (appointment.getAppointmentId() == null ||
                appointment.getAppointmentId().isBlank()) {

            return "Invalid appointment ID.";
        }

        Appointment existing = appointmentRepository.findById(
                appointment.getAppointmentId()
        );

        if (existing == null) {
            return "Appointment not found.";
        }

        if (!"PENDING".equals(existing.getStatus())) {
            return "Only pending appointments can be updated.";
        }

        if (appointment.getAppointmentDate() == null ||
                appointment.getAppointmentDate().isBlank() ||
                appointment.getAppointmentTime() == null ||
                appointment.getAppointmentTime().isBlank()) {

            return "Please enter a valid date and time.";
        }

        try {
            LocalDate date = LocalDate.parse(
                    appointment.getAppointmentDate());

            LocalTime time = LocalTime.parse(
                    appointment.getAppointmentTime());

            if (date.isBefore(LocalDate.now())) {
                return "Please select a future date.";
            }

            if (date.equals(LocalDate.now()) &&
                    !time.isAfter(LocalTime.now())) {

                return "Please select a future time.";
            }

        } catch (Exception e) {
            return "Invalid appointment date or time.";
        }

        // Conflict Checking
        if (hasConflict(
                existing.getAgentEmail(),
                appointment.getAppointmentDate(),
                appointment.getAppointmentTime(),
                existing.getAppointmentId())) {

            return "Appointment conflict! This Agent already has an appointment at this time.";
        }

        existing.setAppointmentDate(appointment.getAppointmentDate());
        existing.setAppointmentTime(appointment.getAppointmentTime());

        if (appointment.getMessage() != null) {
            existing.setMessage(appointment.getMessage());
        }

        boolean updated = appointmentRepository.updateAppointment(existing);

        return updated
                ? "Appointment updated successfully!"
                : "Unable to update appointment.";
    }

    // Update Appointment Status
    public synchronized String updateAppointmentStatus(
            String appointmentId, String status) {

        if (appointmentId == null || status == null) {
            return "Invalid appointment details.";
        }

        if (!status.equals("APPROVED") &&
                !status.equals("REJECTED") &&
                !status.equals("COMPLETED")) {

            return "Invalid appointment status.";
        }

        Appointment appointment =
                appointmentRepository.findById(appointmentId);

        if (appointment == null) {
            return "Appointment not found.";
        }

        if (!"PENDING".equals(appointment.getStatus())) {
            return "This appointment has already been processed.";
        }

        // Check Conflict Before Approval
        if ("APPROVED".equals(status) &&
                hasConflict(
                        appointment.getAgentEmail(),
                        appointment.getAppointmentDate(),
                        appointment.getAppointmentTime(),
                        appointment.getAppointmentId())) {

            return "Cannot approve! Another appointment already exists at this time.";
        }

        appointment.setStatus(status);

        boolean updated =
                appointmentRepository.updateAppointment(appointment);

        return updated
                ? "Appointment status updated successfully!"
                : "Unable to update appointment status.";
    }

    // Cancel Appointment - Soft Delete
    public synchronized String cancelAppointment(String appointmentId) {

        Appointment appointment =
                appointmentRepository.findById(appointmentId);

        if (appointment == null) {
            return "Appointment not found.";
        }

        if (!"PENDING".equals(appointment.getStatus())) {
            return "Only pending appointments can be cancelled.";
        }

        appointment.setStatus("CANCELLED");

        boolean updated =
                appointmentRepository.updateAppointment(appointment);

        return updated
                ? "Appointment cancelled successfully!"
                : "Unable to cancel appointment.";
    }

    // Conflict Checking Method
    private boolean hasConflict(
            String agentEmail,
            String date,
            String time,
            String excludeAppointmentId) {

        List<Appointment> appointments =
                appointmentRepository.getAllAppointments();

        for (Appointment existing : appointments) {

            if (excludeAppointmentId != null &&
                    excludeAppointmentId.equals(
                            existing.getAppointmentId())) {

                continue;
            }

            boolean sameAgent =
                    existing.getAgentEmail() != null &&
                            existing.getAgentEmail().equalsIgnoreCase(agentEmail);

            boolean sameDate =
                    existing.getAppointmentDate() != null &&
                            existing.getAppointmentDate().equals(date);

            boolean sameTime = false;

            try {
                if (existing.getAppointmentTime() != null) {
                    sameTime = LocalTime.parse(
                            existing.getAppointmentTime()
                    ).equals(LocalTime.parse(time));
                }
            } catch (Exception e) {
                sameTime = false;
            }

            boolean activeStatus =
                    "PENDING".equalsIgnoreCase(existing.getStatus()) ||
                            "APPROVED".equalsIgnoreCase(existing.getStatus());

            if (sameAgent && sameDate && sameTime && activeStatus) {
                return true;
            }
        }

        return false;
    }
}