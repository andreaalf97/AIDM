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
     * @param agents The number of agents
     * @param jobs The number of jobs
     * @return A random set of preferences
     */
    static Preference[] generateRandom(int agents, int jobs){
        // Randomly generates the processing times of the jobs
        int[] processingTimes = new int[jobs];
        for(int i = 0; i < processingTimes.length; i++)
            processingTimes[i] = (new Random().nextInt(99)) + 1;

        // Generates the preferences randomly, but the processing times are fixed
        Preference[] preferences = new Preference[agents];
        for(int i = 0; i < agents; i++)
            preferences[i] = new Preference(jobs, processingTimes);

        return preferences;
    }

}
