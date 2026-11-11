
        package com.realestate.realestateportal.controller;

import com.realestate.realestateportal.model.Property;
import com.realestate.realestateportal.model.User;
import com.realestate.realestateportal.service.PropertyService;

import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/property")
public class PropertyController {

    private final PropertyService propertyService;

    // Constructor Dependency Injection
    public PropertyController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    // CREATE PROPERTY
    @PostMapping("/create")
    public String createProperty(
            @ModelAttribute Property property,
            HttpSession session) {

        User user = (User) session.getAttribute("loggedInUser");

        if (user == null || !"AGENT".equals(user.getRole())) {
            return "Please login as an Agent.";
        }

        property.setAgentEmail(user.getEmail());

        return propertyService.createProperty(property);
    }

    // GET PROPERTIES OF LOGGED-IN AGENT
    @GetMapping("/my-properties")
    public Object getMyProperties(HttpSession session) {

        User user = (User) session.getAttribute("loggedInUser");

        if (user == null || !"AGENT".equals(user.getRole())) {
            return "Access denied.";
        }

        List<Property> properties =
                propertyService.getPropertiesByAgent(user.getEmail());

        return properties;
    }

    // GET ALL PROPERTIES
    @GetMapping("/all")
    public Object getAllProperties(HttpSession session) {

        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "Please login first.";
        }

        return propertyService.getAllProperties();
    }

    // FILTER PROPERTIES BY MULTIPLE CRITERIA
    // Public endpoint - Login is not required for searching
    @GetMapping("/filter")
    public Object filterProperties(

            @RequestParam(required = false) String propertyType,

            @RequestParam(required = false) String listingType,

            @RequestParam(required = false) String location,

            @RequestParam(required = false) Double minPrice,

            @RequestParam(required = false) Double maxPrice,

            @RequestParam(required = false) Integer bedrooms,

            @RequestParam(required = false) Integer bathrooms,

            @RequestParam(required = false) Double minArea,

            @RequestParam(required = false) Double maxArea) {

        return propertyService.filterProperties(
                propertyType,
                listingType,
                location,
                minPrice,
                maxPrice,
                bedrooms,
                bathrooms,
                minArea,
                maxArea
        );
    }

    // UPDATE PROPERTY
    @PostMapping("/update")
    public String updateProperty(
            @ModelAttribute Property property,
            HttpSession session) {

        User user = (User) session.getAttribute("loggedInUser");

        if (user == null || !"AGENT".equals(user.getRole())) {
            return "Please login as an Agent.";
        }

        Property existingProperty =
                propertyService.getPropertyById(property.getPropertyId());

        if (existingProperty == null ||
                existingProperty.getAgentEmail() == null ||
                !existingProperty.getAgentEmail().equalsIgnoreCase(user.getEmail())) {

            return "Property not found or access denied.";
        }

        property.setAgentEmail(user.getEmail());

        return propertyService.updateProperty(property);
    }

    // DELETE PROPERTY
    @PostMapping("/delete")
    public String deleteProperty(
            @RequestParam("propertyId") String propertyId,
            HttpSession session) {

        User user = (User) session.getAttribute("loggedInUser");

        if (user == null || !"AGENT".equals(user.getRole())) {
            return "Please login as an Agent.";
        }

        Property existingProperty =
                propertyService.getPropertyById(propertyId);

        if (existingProperty == null ||
                existingProperty.getAgentEmail() == null ||
                !existingProperty.getAgentEmail().equalsIgnoreCase(user.getEmail())) {

            return "Property not found or access denied.";
        }

        return propertyService.deleteProperty(propertyId);
    }
}
