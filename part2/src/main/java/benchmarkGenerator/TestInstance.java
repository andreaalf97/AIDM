package benchmarkGenerator;

import votingRules.Preference;

import java.io.Serializable;
import java.util.Arrays;

/**
 * This is a single test instance from any test benchmark
 */
public class TestInstance implements Serializable {

    private int numAgents;

    private int numJobs;

    private Preference[] preferences;

    public TestInstance(int numAgents, int numJobs, Preference[] preferences) {
        this.numAgents = numAgents;
        this.numJobs = numJobs;
        this.preferences = preferences;
    }

    @Override
    public String toString() {
        return "TestInstance{" +
                "numAgents=" + numAgents +
                ", numJobs=" + numJobs +
                ", preferences=" + Arrays.toString(preferences) +
                '}';
    }
}
