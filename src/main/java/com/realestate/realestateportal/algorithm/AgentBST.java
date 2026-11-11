
package com.realestate.realestateportal.algorithm;

import com.realestate.realestateportal.model.Agent;

public class AgentBST {

    private static class Node {
        Agent agent;
        Node left;
        Node right;

        Node(Agent agent) {
            this.agent = agent;
            left = null;
            right = null;
        }
    }

    private Node root;

    public AgentBST() {
        root = null;
    }

    // Insert an agent into the BST
    public void insert(Agent agent) {

        if (agent == null) {
            throw new IllegalArgumentException("Agent cannot be null");
        }

        root = insertRec(root, agent);
    }

    private Node insertRec(Node current, Agent agent) {

        if (current == null) {
            return new Node(agent);
        }

        int comparison = agent.getAgentId()
                .compareTo(current.agent.getAgentId());

        if (comparison < 0) {
            current.left = insertRec(current.left, agent);
        } else if (comparison > 0) {
            current.right = insertRec(current.right, agent);
        } else {
            throw new IllegalArgumentException("Duplicate Agent ID");
        }

        return current;
    }

    // Search an agent by ID
    public Agent search(String agentId) {

        if (agentId == null || agentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Agent ID cannot be empty");
        }

        Node current = root;

        while (current != null) {

            int comparison = agentId.compareTo(
                    current.agent.getAgentId());

            if (comparison == 0) {
                return current.agent;
            } else if (comparison < 0) {
                current = current.left;
            } else {
                current = current.right;
            }
        }

        return null;
    }
}
