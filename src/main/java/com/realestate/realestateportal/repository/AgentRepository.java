package com.realestate.realestateportal.repository;

import com.realestate.realestateportal.model.Agent;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class AgentRepository {

    private static final String FILE_PATH = "data/agents.txt";

    // Save Agent
    public boolean saveAgent(Agent agent) {

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

                writer.write(convertToLine(agent));
                writer.newLine();
            }

            return true;

        } catch (IOException e) {

            System.out.println("Error saving agent: " + e.getMessage());
            return false;
        }
    }

    // Find Agent by ID
    public Agent findById(String agentId) {

        for (Agent agent : getAllAgents()) {

            if (agent.getAgentId().equalsIgnoreCase(agentId)) {
                return agent;
            }
        }

        return null;
    }

    // Find Agent by Email
    public Agent findByEmail(String email) {

        for (Agent agent : getAllAgents()) {

            if (agent.getEmail().equalsIgnoreCase(email)) {
                return agent;
            }
        }

        return null;
    }

    // Get All Agents
    public List<Agent> getAllAgents() {

        List<Agent> agents = new ArrayList<>();

        File file = new File(FILE_PATH);

        if (!file.exists()) {
            return agents;
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(file),
                        StandardCharsets.UTF_8))) {

            String line;

            while ((line = reader.readLine()) != null) {

                String[] parts = line.split("\\|", -1);

                if (parts.length == 8) {

                    try {

                        Agent agent = new Agent(
                                parts[0],
                                parts[1],
                                parts[2],
                                parts[3],
                                parts[4],
                                Integer.parseInt(parts[5]),
                                parts[6],
                                Double.parseDouble(parts[7])
                        );

                        agents.add(agent);

                    } catch (NumberFormatException e) {

                        System.out.println("Invalid agent data: " + line);
                    }
                }
            }

        } catch (IOException e) {

            System.out.println("Error reading agents: " + e.getMessage());
        }

        return agents;
    }

    // Update Agent
    public boolean updateAgent(Agent updatedAgent) {

        List<Agent> agents = getAllAgents();

        boolean found = false;

        for (int i = 0; i < agents.size(); i++) {

            if (agents.get(i).getAgentId()
                    .equalsIgnoreCase(updatedAgent.getAgentId())) {

                agents.set(i, updatedAgent);
                found = true;
                break;
            }
        }

        if (!found) {
            return false;
        }

        return rewriteFile(agents);
    }

    // Delete Agent
    public boolean deleteAgent(String agentId) {

        List<Agent> agents = getAllAgents();

        boolean removed = agents.removeIf(
                agent -> agent.getAgentId().equalsIgnoreCase(agentId)
        );

        if (!removed) {
            return false;
        }

        return rewriteFile(agents);
    }

    // Rewrite File
    private boolean rewriteFile(List<Agent> agents) {

        try {

            File file = new File(FILE_PATH);

            File parent = file.getParentFile();

            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }

            try (BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(file, false),
                            StandardCharsets.UTF_8))) {

                for (Agent agent : agents) {

                    writer.write(convertToLine(agent));
                    writer.newLine();
                }
            }

            return true;

        } catch (IOException e) {

            System.out.println("Error updating agent file: " + e.getMessage());
            return false;
        }
    }

    // Convert Agent Object to Text Line
    private String convertToLine(Agent agent) {

        return agent.getAgentId() + "|" +
                agent.getEmail() + "|" +
                agent.getAgencyName() + "|" +
                agent.getSpecialization() + "|" +
                agent.getLocation() + "|" +
                agent.getExperienceYears() + "|" +
                agent.getDescription() + "|" +
                agent.getRating();
    }
}
