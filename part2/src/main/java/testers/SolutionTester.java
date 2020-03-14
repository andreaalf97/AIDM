package testers;

import votingRules.Preference;

import java.util.Random;

import static votingRules.Preference.processingTimes;

/**
 * This interface represents a tester which takes in a solution (and possibly the set of preferences)
 * and compares them
 */
public interface SolutionTester {

    /**
     * This method generates a set of preferences.
     * The number of jobs is given by Preference.processingTimes
     * @param numAgents The number of agents
     * @param numJobs The number of jobs per preference
     * @return A set of preferences
     */
    Preference[] generate(int numAgents, int numJobs);

    /**
     * This generates a set of random preferences
     * @param numAgents The number of agents
     * @param numJobs The number of jobs
     * @return A random set of preferences
     */
    static Preference[] generateRandom(int numAgents, int numJobs){

        // Initialize the static value of all preferences
        Preference.processingTimes = new int[numJobs];

        // Randomly generates the processing times of the jobs
        for(int i = 0; i < processingTimes.length; i++)
            processingTimes[i] = (new Random().nextInt(99)) + 1;

        // Generates the preferences randomly, but the processing times are fixed
        Preference[] preferences = new Preference[numAgents];
        for(int i = 0; i < numAgents; i++)
            preferences[i] = new Preference();

        return preferences;
    }

}
