package com.realestate.realestateportal.repository;

import com.realestate.realestateportal.model.Admin;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class AdminRepository {

    private static final String FILE_PATH = "data/admin_activity.txt";

    // Save a new activity record
    public boolean saveActivity(Admin activity) {

        try {
            File file = new File(FILE_PATH);

            File parent = file.getParentFile();

            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }

            try (BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(file, true),
                            StandardCharsets.UTF_8))) {

                writer.write(
                        clean(activity.getAdminEmail()) + "," +
                                clean(activity.getAction()) + "," +
                                clean(activity.getDetails()) + "," +
                                clean(activity.getDateTime())
                );

                writer.newLine();
            }

            return true;

        } catch (IOException e) {
            System.out.println("Error saving admin activity: " + e.getMessage());
            return false;
        }
    }

    // Get all activity records
    public List<Admin> getAllActivities() {

        List<Admin> activities = new ArrayList<>();

        File file = new File(FILE_PATH);

        if (!file.exists()) {
            return activities;
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(file),
                        StandardCharsets.UTF_8))) {

            String line;

            while ((line = reader.readLine()) != null) {

                String[] parts = line.split(",", -1);

                if (parts.length == 4) {

                    activities.add(new Admin(
                            parts[0],
                            parts[1],
                            parts[2],
                            parts[3]
                    ));
                }
            }

        } catch (IOException e) {
            System.out.println("Error reading admin activity: " + e.getMessage());
        }

        return activities;
    }

    // Remove commas and new lines so the file format never breaks
    private String clean(String text) {

        if (text == null) {
            return "";
        }

        return text.replace(",", " ").replace("\n", " ").replace("\r", " ");
    }
}
