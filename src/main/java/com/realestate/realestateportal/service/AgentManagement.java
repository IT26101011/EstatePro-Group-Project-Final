
package com.realestate.realestateportal.service;

import com.realestate.realestateportal.model.Agent;
import java.util.List;

public interface AgentManagement {

    String createAgentProfile(Agent agent);

    List<Agent> getAllAgents();

    Agent getAgentById(String agentId);

    Agent getAgentByEmail(String email);

    String updateAgentProfile(Agent agent);

    boolean updateAgentRating(String agentEmail, double rating);

    String deleteAgentProfile(String agentId);
}
