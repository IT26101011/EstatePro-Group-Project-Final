
package com.realestate.realestateportal.service;

public abstract class BaseAgentService implements AgentManagement {

    // Validate required text fields
    protected boolean hasValidText(String value) {
        return value != null && !value.isBlank();
    }

    // Validate experience years
    protected boolean isValidExperience(int years) {
        return years >= 0;
    }

    // Validate agent rating
    protected boolean isValidRating(double rating) {
        return Double.isFinite(rating)
                && rating >= 0
                && rating <= 5;
    }

    // Abstract method for rebuilding the BST
    protected abstract void rebuildAgentBST();
}
