package testers;

import votingRules.Preference;

import java.util.Random;

/**
 * This interface represents a tester which takes in a solution (and possibly the set of preferences)
 * and compares them
 */
public interface SolutionTester {

    Preference[] generate(int agents, int jobs);

    /**
     * This generates a set of random preferences
     * @param numAgents The number of agents
     * @param numJobs The number of jobs
     * @return A random set of preferences
     */
    static Preference[] generateRandom(int numAgents, int numJobs){
        // Randomly generates the processing times of the jobs
        int[] processingTimes = new int[numJobs];
        for(int i = 0; i < processingTimes.length; i++)
            processingTimes[i] = (new Random().nextInt(99)) + 1;

        // Generates the preferences randomly, but the processing times are fixed
        Preference[] preferences = new Preference[numAgents];
        for(int i = 0; i < numAgents; i++)
            preferences[i] = new Preference(processingTimes);

        return preferences;
    }

}
