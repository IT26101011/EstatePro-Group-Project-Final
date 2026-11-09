
package com.realestate.realestateportal.service;

import com.realestate.realestateportal.model.Property;
import com.realestate.realestateportal.repository.PropertyRepository;

import java.util.List;
import java.util.ArrayList;
import java.util.UUID;

public class PropertyService {

    private final PropertyRepository propertyRepository =
            new PropertyRepository();

    // Create Property
    public String createProperty(Property property) {

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

        property.setPropertyId(
                "PRP-" + UUID.randomUUID().toString().substring(0, 8)
        );

        if (propertyRepository.saveProperty(property)) {
            return "Property created successfully!";
        }

        return "Failed to create property.";
    }

    // Get All Properties
    public List<Property> getAllProperties() {
        return propertyRepository.getAllProperties();
    }

    // Get Properties By Agent
    public List<Property> getPropertiesByAgent(String email) {
        return propertyRepository.findByAgentEmail(email);
    }

    // Get Property By ID
    public Property getPropertyById(String propertyId) {
        return propertyRepository.findById(propertyId);
    }



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

        // Check every property
        for (Property property : allProperties) {

            // 1. Filter by Property Type
            if (propertyType != null && !propertyType.isBlank()) {

                if (property.getPropertyType() == null ||
                        !property.getPropertyType().equalsIgnoreCase(propertyType)) {
                    continue;
                }
            }

            // 2. Filter by Listing Type
            if (listingType != null && !listingType.isBlank()) {

                if (property.getListingType() == null ||
                        !property.getListingType().equalsIgnoreCase(listingType)) {
                    continue;
                }
            }

            // 3. Filter by Location
            if (location != null && !location.isBlank()) {

                if (property.getLocation() == null ||
                        !property.getLocation().toLowerCase()
                                .contains(location.trim().toLowerCase())) {
                    continue;
                }
            }

            // 4. Filter by Minimum Price
            if (minPrice != null) {

                if (property.getPrice() < minPrice) {
                    continue;
                }
            }

            // 5. Filter by Maximum Price
            if (maxPrice != null) {

                if (property.getPrice() > maxPrice) {
                    continue;
                }
            }

            // 6. Filter by Bedrooms
            if (bedrooms != null) {

                if (property.getBedrooms() < bedrooms) {
                    continue;
                }
            }

            // 7. Filter by Bathrooms
            if (bathrooms != null) {

                if (property.getBathrooms() < bathrooms) {
                    continue;
                }
            }

            // 8. Filter by Minimum Area
            if (minArea != null) {

                if (property.getArea() < minArea) {
                    continue;
                }
            }

            // 9. Filter by Maximum Area
            if (maxArea != null) {

                if (property.getArea() > maxArea) {
                    continue;
                }
            }

            // All selected criteria matched
            filteredProperties.add(property);
        }

        return filteredProperties;
    }


    // UPDATE PROPERTY


    public String updateProperty(Property property) {

        if (property.getTitle() == null || property.getTitle().isBlank() ||
                property.getPropertyType() == null || property.getPropertyType().isBlank() ||
                property.getListingType() == null || property.getListingType().isBlank() ||
                property.getLocation() == null || property.getLocation().isBlank() ||
                property.getDescription() == null || property.getDescription().isBlank()) {

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

    // Delete Property
    public String deleteProperty(String propertyId) {

        if (propertyRepository.deleteProperty(propertyId)) {
            return "Property deleted successfully!";
        }

        return "Property not found.";
    }
}
