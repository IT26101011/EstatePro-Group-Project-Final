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

    private final PropertyService propertyService =
            new PropertyService();

    // Create Property
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

    // Get Properties Of Logged-in Agent
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

    // Get All Properties
    @GetMapping("/all")
    public Object getAllProperties(HttpSession session) {

        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "Please login first.";
        }

        return propertyService.getAllProperties();
    }

    // Update Property
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
                !existingProperty.getAgentEmail().equalsIgnoreCase(user.getEmail())) {
            return "Property not found or access denied.";
        }

        property.setAgentEmail(user.getEmail());

        return propertyService.updateProperty(property);
    }

    // Delete Property
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
                !existingProperty.getAgentEmail().equalsIgnoreCase(user.getEmail())) {
            return "Property not found or access denied.";
        }

        return propertyService.deleteProperty(propertyId);
    }
}
