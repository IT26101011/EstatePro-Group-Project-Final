
package com.realestate.realestateportal.service;

import com.realestate.realestateportal.model.Property;
import com.realestate.realestateportal.repository.PropertyRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;
import java.util.UUID;

@Service
public class PropertyService {

    private final PropertyRepository propertyRepository;

    // Constructor used by Spring Boot
    public PropertyService(PropertyRepository propertyRepository) {
        this.propertyRepository = propertyRepository;
    }

    // Default constructor
    // Kept for compatibility with existing controllers
    // that use: new PropertyService()
    public PropertyService() {
        this.propertyRepository = new PropertyRepository();
    }

    // =========================================================
    // CREATE PROPERTY
    // =========================================================

    public String createProperty(Property property) {

        if (property == null) {
            return "Invalid property details.";
        }

        if (property.getTitle() == null || property.getTitle().isBlank() ||
                property.getPropertyType() == null || property.getPropertyType().isBlank() ||
                property.getListingType() == null || property.getListingType().isBlank() ||
                property.getLocation() == null || property.getLocation().isBlank() ||
                property.getDescription() == null || property.getDescription().isBlank()) {

            return "Please fill all required fields.";
        }

        if (property.getPrice() <= 0) {
            return "Invalid property price.";
        }

        if (property.getBedrooms() < 0 ||
                property.getBathrooms() < 0 ||
                property.getArea() <= 0) {

            return "Invalid property details.";
        }

        // Generate unique property ID
        property.setPropertyId(
                "PRP-" + UUID.randomUUID().toString().substring(0, 8)
        );

        if (propertyRepository.saveProperty(property)) {
            return "Property created successfully!";
        }

        return "Failed to create property.";
    }

    // =========================================================
    // READ ALL PROPERTIES
    // =========================================================

    public List<Property> getAllProperties() {

        return propertyRepository.getAllProperties();
    }

    // =========================================================
    // READ PROPERTIES BY AGENT
    // =========================================================

    public List<Property> getPropertiesByAgent(String email) {

        return propertyRepository.findByAgentEmail(email);
    }

    // =========================================================
    // READ PROPERTY BY ID
    // =========================================================

    public Property getPropertyById(String propertyId) {

        return propertyRepository.findById(propertyId);
    }

    // =========================================================
    // CRITERIA BASED FILTERING
    // =========================================================

    public List<Property> filterProperties(
            String propertyType,
            String listingType,
            String location,
            Double minPrice,
            Double maxPrice,
            Integer bedrooms,
            Integer bathrooms,
            Double minArea,
            Double maxArea) {

        List<Property> allProperties =
                propertyRepository.getAllProperties();

        List<Property> filteredProperties = new ArrayList<>();

        // Validate price range
        if (minPrice != null &&
                maxPrice != null &&
                minPrice > maxPrice) {

            return filteredProperties;
        }

        // Validate area range
        if (minArea != null &&
                maxArea != null &&
                minArea > maxArea) {

            return filteredProperties;
        }

        // Check every property
        for (Property property : allProperties) {

            // -------------------------------------------------
            // 1. Filter by Property Type
            // -------------------------------------------------

            if (propertyType != null && !propertyType.isBlank()) {

                if (property.getPropertyType() == null ||
                        !property.getPropertyType()
                                .equalsIgnoreCase(propertyType.trim())) {

                    continue;
                }
            }

            // -------------------------------------------------
            // 2. Filter by Listing Type
            // -------------------------------------------------

            if (listingType != null && !listingType.isBlank()) {

                if (property.getListingType() == null ||
                        !property.getListingType()
                                .equalsIgnoreCase(listingType.trim())) {

                    continue;
                }
            }

            // -------------------------------------------------
            // 3. Filter by Location
            // -------------------------------------------------

            if (location != null && !location.isBlank()) {

                if (property.getLocation() == null ||
                        !property.getLocation()
                                .toLowerCase()
                                .contains(location.trim().toLowerCase())) {

                    continue;
                }
            }

            // -------------------------------------------------
            // 4. Filter by Minimum Price
            // -------------------------------------------------

            if (minPrice != null &&
                    property.getPrice() < minPrice) {

                continue;
            }

            // -------------------------------------------------
            // 5. Filter by Maximum Price
            // -------------------------------------------------

            if (maxPrice != null &&
                    property.getPrice() > maxPrice) {

                continue;
            }

            // -------------------------------------------------
            // 6. Filter by Bedrooms
            // -------------------------------------------------

            if (bedrooms != null &&
                    property.getBedrooms() < bedrooms) {

                continue;
            }

            // -------------------------------------------------
            // 7. Filter by Bathrooms
            // -------------------------------------------------

            if (bathrooms != null &&
                    property.getBathrooms() < bathrooms) {

                continue;
            }

            // -------------------------------------------------
            // 8. Filter by Minimum Area
            // -------------------------------------------------

            if (minArea != null &&
                    property.getArea() < minArea) {

                continue;
            }

            // -------------------------------------------------
            // 9. Filter by Maximum Area
            // -------------------------------------------------

            if (maxArea != null &&
                    property.getArea() > maxArea) {

                continue;
            }

            // All selected criteria matched
            filteredProperties.add(property);
        }

        return filteredProperties;
    }

    // =========================================================
    // UPDATE PROPERTY
    // =========================================================

    public String updateProperty(Property property) {

        if (property == null ||
                property.getPropertyId() == null ||
                property.getPropertyId().isBlank()) {

            return "Invalid property ID.";
        }

        if (property.getTitle() == null ||
                property.getTitle().isBlank() ||

                property.getPropertyType() == null ||
                property.getPropertyType().isBlank() ||

                property.getListingType() == null ||
                property.getListingType().isBlank() ||

                property.getLocation() == null ||
                property.getLocation().isBlank() ||

                property.getDescription() == null ||
                property.getDescription().isBlank()) {

            return "Please fill all required fields.";
        }

        if (property.getPrice() <= 0 ||
                property.getBedrooms() < 0 ||
                property.getBathrooms() < 0 ||
                property.getArea() <= 0) {

            return "Invalid property details.";
        }

        if (propertyRepository.updateProperty(property)) {

            return "Property updated successfully!";
        }

        return "Failed to update property.";
    }

    // =========================================================
    // DELETE PROPERTY
    // =========================================================

    public String deleteProperty(String propertyId) {

        if (propertyId == null ||
                propertyId.isBlank()) {

            return "Invalid property ID.";
        }

        if (propertyRepository.deleteProperty(propertyId)) {

            return "Property deleted successfully!";
        }

        return "Property not found.";
    }
}
