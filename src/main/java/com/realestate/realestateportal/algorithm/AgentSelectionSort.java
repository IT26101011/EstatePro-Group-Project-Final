
package com.realestate.realestateportal.algorithm;

import com.realestate.realestateportal.model.Agent;

import java.util.List;

public class AgentSelectionSort {
    public void sortByRating(List<Agent> agents) {

        if (agents == null) {
            throw new IllegalArgumentException("Agent list cannot be null");
        }

        int n = agents.size();

        for (int i = 0; i < n - 1; i++) {

            int maxIndex = i;

            for (int j = i + 1; j < n; j++) {

                if (agents.get(j).getRating() >
                        agents.get(maxIndex).getRating()) {

                    maxIndex = j;
                }
            }

            Agent temp = agents.get(i);
            agents.set(i, agents.get(maxIndex));
            agents.set(maxIndex, temp);
        }
    }

}
