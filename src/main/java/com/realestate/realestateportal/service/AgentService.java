
package com.realestate.realestateportal.service;

import com.realestate.realestateportal.model.Agent;
import com.realestate.realestateportal.model.User;
import com.realestate.realestateportal.repository.AgentRepository;
import com.realestate.realestateportal.repository.UserRepository;
import com.realestate.realestateportal.algorithm.AgentBST;
import com.realestate.realestateportal.algorithm.AgentSelectionSort;
import com.realestate.realestateportal.exception.InvalidAgentDataException;

import java.util.List;
import java.util.UUID;

public class AgentService extends BaseAgentService {

    private final AgentRepository agentRepository = new AgentRepository();
    private final UserRepository userRepository = new UserRepository();

    private AgentBST agentBST;
    private final AgentSelectionSort selectionSort = new AgentSelectionSort();

    // Constructor
    public AgentService() {
        rebuildAgentBST();
    }

    // Rebuild BST using saved agents
    @Override
    protected void rebuildAgentBST() {

        agentBST = new AgentBST();

        List<Agent> agents = agentRepository.getAllAgents();

        for (Agent agent : agents) {
            agentBST.insert(agent);
        }
    }

    // Attach registered user's full name to agent
    private void attachAgentName(Agent agent) {

        if (agent == null) {
            return;
        }

        User user = userRepository.findByEmail(agent.getEmail());

        if (user != null) {
            agent.setFullName(user.getFullName());
        }
    }
    @Override
    public String createAgentProfile(Agent agent) {

        if (agent == null ||
                !hasValidText(agent.getEmail()) ||
                !hasValidText(agent.getAgencyName()) ||
                !hasValidText(agent.getSpecialization()) ||
                !hasValidText(agent.getLocation())) {

            throw new InvalidAgentDataException(
                    "Please fill all required fields.");
        }

        if (!isValidExperience(agent.getExperienceYears())) {
            throw new InvalidAgentDataException(
                    "Invalid experience years.");
        }

        if (agentRepository.findByEmail(agent.getEmail()) != null) {
            throw new InvalidAgentDataException(
                    "Agent profile already exists.");
        }

        String agentId = "AGT-" +
                UUID.randomUUID().toString().substring(0, 8);

        agent.setAgentId(agentId);
        agent.setRating(0.0);

        if (agentRepository.saveAgent(agent)) {
            rebuildAgentBST();
            return "Agent profile created successfully!";
        }

        return "Failed to create agent profile.";
    }


    // Get All Agents - Sorted by Rating
    @Override
    public List<Agent> getAllAgents() {

        List<Agent> agents = agentRepository.getAllAgents();

        for (Agent agent : agents) {
            attachAgentName(agent);
        }

        selectionSort.sortByRating(agents);

        return agents;
    }

    // Find Agent by ID using BST
    @Override
    public Agent getAgentById(String agentId) {

        if (!hasValidText(agentId)) {
            return null;
        }

        Agent agent = agentBST.search(agentId);
        attachAgentName(agent);

        return agent;
    }

    // Find Agent by Email
    @Override
    public Agent getAgentByEmail(String email) {

        if (!hasValidText(email)) {
            return null;
        }

        Agent agent = agentRepository.findByEmail(email);
        attachAgentName(agent);

        return agent;
    }

    // Update Agent Profile
    @Override
    public String updateAgentProfile(Agent agent) {

        if (agent == null || !hasValidText(agent.getAgentId())) {
            return "Invalid agent details.";
        }

        if (agentBST.search(agent.getAgentId()) == null) {
            return "Agent not found.";
        }

        if (!isValidExperience(agent.getExperienceYears())) {
            return "Invalid experience years.";
        }

        if (agentRepository.updateAgent(agent)) {
            rebuildAgentBST();
            return "Agent profile updated successfully!";
        }

        return "Failed to update agent profile.";
    }

    // Update Agent Average Rating
    @Override
    public boolean updateAgentRating(String agentEmail, double rating) {

        if (!hasValidText(agentEmail) || !isValidRating(rating)) {
            return false;
        }

        Agent agent = agentRepository.findByEmail(agentEmail);

        if (agent == null) {
            return false;
        }

        agent.setRating(rating);

        if (agentRepository.updateAgent(agent)) {
            rebuildAgentBST();
            return true;
        }

        return false;
    }

    // Delete Agent Profile and Related User Account
    @Override
    public String deleteAgentProfile(String agentId) {

        if (!hasValidText(agentId)) {
            return "Invalid agent ID.";
        }

        // Search Agent using BST
        Agent agent = agentBST.search(agentId);

        if (agent == null) {
            return "Agent not found.";
        }

        // Get agent email before deletion
        String agentEmail = agent.getEmail();

        // Delete related User account first
        boolean userDeleted = userRepository.deleteUser(agentEmail);

        if (!userDeleted) {
            return "Failed to delete user account.";
        }

        // Delete Agent profile
        if (agentRepository.deleteAgent(agentId)) {
            rebuildAgentBST();
            return "Agent profile and user account deleted successfully!";
        }

        return "User account deleted, but agent profile deletion failed.";
    }
}
