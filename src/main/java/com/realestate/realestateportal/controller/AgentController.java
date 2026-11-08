
package com.realestate.realestateportal.controller;

import com.realestate.realestateportal.model.Agent;
import com.realestate.realestateportal.model.User;
import com.realestate.realestateportal.service.AgentManagement;
import com.realestate.realestateportal.service.AgentService;
import com.realestate.realestateportal.exception.InvalidAgentDataException;

import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/agent")
public class AgentController {

    // Polymorphism - Interface reference
    private final AgentManagement agentService = new AgentService();

    // Create Agent Profile
    @PostMapping("/create-profile")
    public String createProfile(
            @ModelAttribute Agent agent,
            HttpSession session) {

        User user = (User) session.getAttribute("loggedInUser");

        if (user == null || !"AGENT".equals(user.getRole())) {
            return "Please login as an Agent.";
        }

        agent.setEmail(user.getEmail());

        try {
            return agentService.createAgentProfile(agent);
        } catch (InvalidAgentDataException e) {
            return e.getMessage();
        }
    }

    // Get Logged-in Agent Profile
    @GetMapping("/my-profile")
    public Object getMyProfile(HttpSession session) {

        User user = (User) session.getAttribute("loggedInUser");

        if (user == null || !"AGENT".equals(user.getRole())) {
            return "Access denied.";
        }

        Agent agent = agentService.getAgentByEmail(user.getEmail());

        if (agent == null) {
            return "Agent profile not found.";
        }

        return agent;
    }

    // Update Agent Profile
    @PostMapping("/update-profile")
    public String updateProfile(
            @ModelAttribute Agent updatedAgent,
            HttpSession session) {

        User user = (User) session.getAttribute("loggedInUser");

        if (user == null || !"AGENT".equals(user.getRole())) {
            return "Please login as an Agent.";
        }

        Agent existingAgent =
                agentService.getAgentByEmail(user.getEmail());

        if (existingAgent == null) {
            return "Agent profile not found.";
        }

        updatedAgent.setAgentId(existingAgent.getAgentId());
        updatedAgent.setEmail(existingAgent.getEmail());
        updatedAgent.setRating(existingAgent.getRating());

        return agentService.updateAgentProfile(updatedAgent);
    }

    // Get All Agent Profiles
    @GetMapping("/all")
    public Object getAllAgents(HttpSession session) {

        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "Please login first.";
        }

        List<Agent> agents = agentService.getAllAgents();

        return agents;
    }

    // Delete Agent Profile - Admin Only
    @DeleteMapping("/{agentId}")
    public String deleteAgent(
            @PathVariable String agentId,
            HttpSession session) {

        User user = (User) session.getAttribute("loggedInUser");

        if (user == null || !"ADMIN".equals(user.getRole())) {
            return "Access denied. Admin only.";
        }

        return agentService.deleteAgentProfile(agentId);
    }

    // Search Agent by ID using BST
    @GetMapping("/search/{agentId}")
    public Object searchAgentById(
            @PathVariable String agentId,
            HttpSession session) {

        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "Please login first.";
        }

        Agent agent = agentService.getAgentById(agentId);

        if (agent == null) {
            return "Agent not found.";
        }

        return agent;
    }
}
