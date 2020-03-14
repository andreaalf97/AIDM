package benchmarkGenerator;

import votingRules.Preference;

import java.io.Serializable;
import java.util.*;

/**
 * This is a single test instance from any test benchmark
 */
public class TestInstance implements Serializable {

    public final int numAgents;

    public final int numJobs;

    /**
     * This array contains the processing time for each job.
     * Index i represents job with ID i,
     * so processingTimes[2] is the processing time
     * of job with ID = 2.
     */
    public final int[] processingTimes; //{44, 37, 49, 43, 51, 46, 53, 45, 52, 58};

    /**
     * This array contains all the preferences
     */
    public final Preference[] preferences;

    public TestInstance(int numAgents, int numJobs, int[] processingTimes, Preference[] preferences) {
        this.numAgents = numAgents;
        this.numJobs = numJobs;
        this.processingTimes = processingTimes;
        this.preferences = preferences;
    }

    public TestInstance(int numAgents, int numJobs, Preference[] preferences) {
        this.numAgents = numAgents;
        this.numJobs = numJobs;
        this.preferences = preferences;

        Random rand = new Random();
        this.processingTimes = new int[numJobs];
        for(int i = 0; i < numJobs; i++)
            this.processingTimes[i] = rand.nextInt(50) + 50;
    }

    @Override
    public String toString() {
        return "TestInstance{" +
                "numAgents=" + numAgents +
                ", numJobs=" + numJobs + "\n" +
                ", processing times: " + processingTimes + "\n" +
                ", preferences=" + Arrays.toString(preferences) +
                '}';
    }
}
