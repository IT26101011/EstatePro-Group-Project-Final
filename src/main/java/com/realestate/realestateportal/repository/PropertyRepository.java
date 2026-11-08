package com.realestate.realestateportal.repository;

import com.realestate.realestateportal.model.Property;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class PropertyRepository {

    private static final String FILE_PATH = "data/properties.txt";

    // Prevent simultaneous file access
    private static final Object FILE_LOCK = new Object();

    // Save a new property
    public boolean saveProperty(Property property) {

        if (property == null) {
            return false;
        }

        synchronized (FILE_LOCK) {
            try {
                File file = new File(FILE_PATH);
                File parent = file.getParentFile();

                if (parent != null && !parent.exists()) {
                    if (!parent.mkdirs() && !parent.exists()) {
                        System.out.println("Unable to create data directory.");
                        return false;
                    }
                }

                try (BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(
                                new FileOutputStream(file, true),
                                StandardCharsets.UTF_8))) {

                    writer.write(convertToLine(property));
                    writer.newLine();
                }

                return true;

            } catch (IOException e) {
                System.out.println("Error saving property: " + e.getMessage());
                return false;
            }
        }
    }

    // Get all properties
    public List<Property> getAllProperties() {

        List<Property> properties = new ArrayList<>();

        synchronized (FILE_LOCK) {

            File file = new File(FILE_PATH);

            if (!file.exists()) {
                return properties;
            }

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(file),
                            StandardCharsets.UTF_8))) {

                String line;

                while ((line = reader.readLine()) != null) {

                    String[] parts = line.split("\\|", -1);

                    if (parts.length == 11) {

                        try {
                            Property property = new Property(
                                    parts[0],
                                    parts[1],
                                    parts[2],
                                    parts[3],
                                    parts[4],
                                    parts[5],
                                    Double.parseDouble(parts[6]),
                                    Integer.parseInt(parts[7]),
                                    Integer.parseInt(parts[8]),
                                    Double.parseDouble(parts[9]),
                                    parts[10]
                            );

                            properties.add(property);

                        } catch (NumberFormatException e) {
                            System.out.println("Invalid property data: " + line);
                        }

                    } else {
                        System.out.println("Invalid property record: " + line);
                    }
                }

            } catch (IOException e) {
                System.out.println("Error reading properties: " + e.getMessage());
            }
        }

        return properties;
    }

    // Find property by ID
    public Property findById(String propertyId) {

        if (propertyId == null || propertyId.trim().isEmpty()) {
            return null;
        }

        for (Property property : getAllProperties()) {

            if (property.getPropertyId() != null &&
                    property.getPropertyId().equalsIgnoreCase(propertyId)) {

                return property;
            }
        }

        return null;
    }

    // Find properties belonging to an agent
    public List<Property> findByAgentEmail(String email) {

        List<Property> agentProperties = new ArrayList<>();

        if (email == null || email.trim().isEmpty()) {
            return agentProperties;
        }

        for (Property property : getAllProperties()) {

            if (property.getAgentEmail() != null &&
                    property.getAgentEmail().equalsIgnoreCase(email)) {

                agentProperties.add(property);
            }
        }

        return agentProperties;
    }

    // Update an existing property
    public boolean updateProperty(Property updatedProperty) {

        if (updatedProperty == null ||
                updatedProperty.getPropertyId() == null) {
            return false;
        }

        synchronized (FILE_LOCK) {

            List<Property> properties = getAllProperties();
            boolean found = false;

            for (int i = 0; i < properties.size(); i++) {

                String existingId = properties.get(i).getPropertyId();

                if (existingId != null &&
                        existingId.equalsIgnoreCase(updatedProperty.getPropertyId())) {

                    properties.set(i, updatedProperty);
                    found = true;
                    break;
                }
            }

            if (!found) {
                return false;
            }

            return rewriteFile(properties);
        }
    }

    // Delete a property
    public boolean deleteProperty(String propertyId) {

        if (propertyId == null || propertyId.trim().isEmpty()) {
            return false;
        }

        synchronized (FILE_LOCK) {

            List<Property> properties = getAllProperties();

            boolean removed = properties.removeIf(property ->
                    property.getPropertyId() != null &&
                            property.getPropertyId().equalsIgnoreCase(propertyId)
            );

            if (!removed) {
                return false;
            }

            return rewriteFile(properties);
        }
    }

    // Rewrite the properties file
    private boolean rewriteFile(List<Property> properties) {

        synchronized (FILE_LOCK) {

            try {
                File file = new File(FILE_PATH);
                File parent = file.getParentFile();

                if (parent != null && !parent.exists()) {
                    if (!parent.mkdirs() && !parent.exists()) {
                        System.out.println("Unable to create data directory.");
                        return false;
                    }
                }

                try (BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(
                                new FileOutputStream(file, false),
                                StandardCharsets.UTF_8))) {

                    for (Property property : properties) {

                        writer.write(convertToLine(property));
                        writer.newLine();
                    }
                }

                return true;

            } catch (IOException e) {
                System.out.println("Error updating property file: " + e.getMessage());
                return false;
            }
        }
    }

    // Convert property object into a text-file record
    private String convertToLine(Property property) {

        return property.getPropertyId() + "|" +
                property.getAgentEmail() + "|" +
                property.getTitle() + "|" +
                property.getPropertyType() + "|" +
                property.getListingType() + "|" +
                property.getLocation() + "|" +
                property.getPrice() + "|" +
                property.getBedrooms() + "|" +
                property.getBathrooms() + "|" +
                property.getArea() + "|" +
                property.getDescription();
    }
}