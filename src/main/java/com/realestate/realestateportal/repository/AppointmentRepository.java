package com.realestate.realestateportal.repository;

import com.realestate.realestateportal.model.Appointment;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class AppointmentRepository {

    private final String FILE_PATH = "data/appointments.txt";

    public AppointmentRepository() {
        try {
            Files.createDirectories(Paths.get("data"));

            File file = new File(FILE_PATH);

            if (!file.exists()) {
                file.createNewFile();
            }

        } catch (IOException e) {
            throw new RuntimeException("Unable to initialize appointments file.", e);
        }
    }

    // Save Appointment
    public void saveAppointment(Appointment appointment) {

        try (BufferedWriter writer = Files.newBufferedWriter(
                Paths.get(FILE_PATH),
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND)) {

            writer.write(convertToLine(appointment));
            writer.newLine();

        } catch (IOException e) {
            throw new RuntimeException("Unable to save appointment.", e);
        }
    }

    // Get All Appointments
    public List<Appointment> getAllAppointments() {

        List<Appointment> appointments = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(
                Paths.get(FILE_PATH), StandardCharsets.UTF_8)) {

            String line;

            while ((line = reader.readLine()) != null) {

                Appointment appointment = convertToAppointment(line);

                if (appointment != null) {
                    appointments.add(appointment);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Unable to read appointments.", e);
        }

        return appointments;
    }

    // Find Appointment By ID
    public Appointment findById(String appointmentId) {

        for (Appointment appointment : getAllAppointments()) {

            if (appointment.getAppointmentId().equals(appointmentId)) {
                return appointment;
            }
        }

        return null;
    }

    // Find Appointments By Client Email
    public List<Appointment> findByClientEmail(String email) {

        List<Appointment> result = new ArrayList<>();

        for (Appointment appointment : getAllAppointments()) {

            if (appointment.getClientEmail().equalsIgnoreCase(email)) {
                result.add(appointment);
            }
        }

        return result;
    }

    // Find Appointments By Agent Email
    public List<Appointment> findByAgentEmail(String email) {

        List<Appointment> result = new ArrayList<>();

        for (Appointment appointment : getAllAppointments()) {

            if (appointment.getAgentEmail().equalsIgnoreCase(email)) {
                result.add(appointment);
            }
        }

        return result;
    }

    // Update Appointment
    public boolean updateAppointment(Appointment updatedAppointment) {

        List<Appointment> appointments = getAllAppointments();

        boolean found = false;

        for (int i = 0; i < appointments.size(); i++) {

            if (appointments.get(i).getAppointmentId()
                    .equals(updatedAppointment.getAppointmentId())) {

                appointments.set(i, updatedAppointment);
                found = true;
                break;
            }
        }

        if (found) {
            rewriteFile(appointments);
        }

        return found;
    }

    // Delete Appointment
    public boolean deleteAppointment(String appointmentId) {

        List<Appointment> appointments = getAllAppointments();

        boolean removed = appointments.removeIf(
                appointment -> appointment.getAppointmentId().equals(appointmentId)
        );

        if (removed) {
            rewriteFile(appointments);
        }

        return removed;
    }

    // Rewrite File
    private void rewriteFile(List<Appointment> appointments) {

        try (BufferedWriter writer = Files.newBufferedWriter(
                Paths.get(FILE_PATH),
                StandardCharsets.UTF_8)) {

            for (Appointment appointment : appointments) {
                writer.write(convertToLine(appointment));
                writer.newLine();
            }

        } catch (IOException e) {
            throw new RuntimeException("Unable to update appointments file.", e);
        }
    }

    // Convert Appointment To Text Line
    private String convertToLine(Appointment appointment) {

        return String.join("|",
                appointment.getAppointmentId(),
                appointment.getClientEmail(),
                appointment.getAgentEmail(),
                appointment.getPropertyId(),
                appointment.getAppointmentDate(),
                appointment.getAppointmentTime(),
                appointment.getMessage(),
                appointment.getStatus()
        );
    }

    // Convert Text Line To Appointment
    private Appointment convertToAppointment(String line) {

        String[] data = line.split("\\|", -1);

        if (data.length != 8) {
            return null;
        }

        return new Appointment(
                data[0],
                data[1],
                data[2],
                data[3],
                data[4],
                data[5],
                data[6],
                data[7]
        );
    }
}
