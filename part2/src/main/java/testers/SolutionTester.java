package testers;

import benchmarkGenerator.TestInstance;
import votingRules.Preference;

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
    static TestInstance generateRandom(int numAgents, int numJobs){

        // Generates the preferences randomly, but the processing times are fixed
        Preference[] preferences = new Preference[numAgents];
        for(int i = 0; i < numAgents; i++)
            preferences[i] = new Preference(numJobs);

        /*
        int[] temp0 = {7, 8, 2, 3, 4, 9, 6, 1, 0, 5};
        preferences[0] = new Preference(temp0);
        int[] temp1 = {5, 8, 2, 6, 1, 9, 7, 4, 0, 3};
        preferences[1] = new Preference(temp1);
        int[] temp2 = {7, 9, 0, 3, 2, 8, 4, 5, 6, 1};
        preferences[2] = new Preference(temp2);
        int[] temp3 = {1, 2, 0, 7, 5, 9, 6, 4, 3, 8};
        preferences[3] = new Preference(temp3);
        int[] temp4 = {7, 9, 0, 3, 1, 4, 6, 8, 2, 5};
        preferences[4] = new Preference(temp4);
        int[] temp5 = {5, 3, 9, 0, 1, 2, 7, 6, 8, 4};
        preferences[5] = new Preference(temp5);
        int[] temp6 = {4, 7, 1, 0, 5, 3, 6, 8, 9, 2};
        preferences[6] = new Preference(temp6);
        int[] temp7 = {4, 1, 0, 2, 8, 9, 6, 7, 3, 5};
        preferences[7] = new Preference(temp7);
        int[] temp8 = {2, 1, 3, 0, 7, 8, 5, 6, 9, 4};
        preferences[8] = new Preference(temp8);
        int[] temp9 = {0, 4, 3, 2, 8, 6, 5, 1, 9, 7};
        preferences[9] = new Preference(temp9);
        */


        return new TestInstance(numAgents, numJobs, preferences);
    }

}
