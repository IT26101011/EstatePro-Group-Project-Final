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
    public String bookAppointment(Appointment appointment) {

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

            LocalDate date = LocalDate.parse(appointment.getAppointmentDate());
            LocalTime time = LocalTime.parse(appointment.getAppointmentTime());

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
    public String updateAppointment(Appointment appointment) {

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

        if (appointment.getAppointmentDate() == null ||
                appointment.getAppointmentDate().isBlank() ||
                appointment.getAppointmentTime() == null ||
                appointment.getAppointmentTime().isBlank()) {

            return "Please enter a valid date and time.";
        }

        try {

            LocalDate date = LocalDate.parse(appointment.getAppointmentDate());
            LocalTime time = LocalTime.parse(appointment.getAppointmentTime());

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
    public String updateAppointmentStatus(String appointmentId, String status) {

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

        appointment.setStatus(status);

        boolean updated =
                appointmentRepository.updateAppointment(appointment);

        return updated
                ? "Appointment status updated successfully!"
                : "Unable to update appointment status.";
    }

    // Delete Appointment
    public String deleteAppointment(String appointmentId) {

        boolean deleted =
                appointmentRepository.deleteAppointment(appointmentId);

        return deleted
                ? "Appointment deleted successfully!"
                : "Appointment not found.";
    }
}