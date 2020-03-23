package votingRules;

import benchmarkGenerator.TestInstance;
import testers.CondorcetConsistencyTester;
import testers.GiniIndexTester;
import testers.ParetoEfficiencyTester;
import testers.SumOfTardinessTester;

import java.util.ArrayList;
import java.util.Arrays;

public class PTA_Copeland implements VotingRule {

    /**
     * This method does everything: Given a test instance with all the necessary info,
     * it applies our modified PTA-Condorcet algorithm, produces a schedule, and calculates
     * all the properties and statistics about this schedule (and prints them).
     * @param testInstance An object containing every piece of info about the problem we need
     * @param minizincSolution The schedule the Minizinc model decides, was used during testing
     */
    @Override
    public int[] schedule(TestInstance testInstance, int[] minizincSolution) {

        long start = System.currentTimeMillis();

        int numAgents = testInstance.numAgents;
        int numJobs = testInstance.numJobs;
        //Initialize some testers for later
        CondorcetConsistencyTester condorsetTests = new CondorcetConsistencyTester(numJobs, numAgents);
        SumOfTardinessTester sumOfTardTests = new SumOfTardinessTester(numJobs, numAgents);
        ParetoEfficiencyTester paretoTests = new ParetoEfficiencyTester(numJobs, numAgents);

        // Initialize the scores with all 0
        Scores scores = new Scores(numJobs);

        //PART 1
        // This double loop compares each job with all the others
        for(int i = 0; i < numJobs - 1; i++) {
            for (int j = i + 1; j < numJobs; j++) {

                int counter_i = 0;
                int counter_j = 0;
                // Now we count how many times the job with ID=i comes before the job with ID=j
                // For all preferences
                // If job with ID=i comes before the job with ID=j
                for (Preference preference : testInstance.preferences)
                    if (preference.isBefore(i, j))
                        counter_i++;  // Increment the number of agents preferring i over j
                    else counter_j++; // Increment the number of agents preferring j over i


                int p_i = testInstance.processingTimes[i];
                int p_j = testInstance.processingTimes[j];

                // The threshold of job i, as given by our definition
                // This rule is the main and only way the processing times of the
                // jobs are taken into account for the final "true" ranking
                float threshold = (float)(p_i * numAgents) / (float)(p_i + p_j);

                if (counter_i == threshold) {    //If the number of votes equals the threshold for job i,
                    // the same applies for job j and its corresponding threshold, because math, so give both of them a point
                    scores.addOne(i);
                    scores.addOne(j);
                } else if (counter_i > threshold) {  //Give a point to job i for surpassing the threshold
                    scores.addOne(i);
                } else { //Give a point to job j for surpassing its threshold (again, because math)
                    scores.addOne(j);
                }

                // Store the votes each job got against the other
                CondorcetConsistencyTester.votes[i][j] = counter_i;
                CondorcetConsistencyTester.votes[j][i] = counter_j;

            }
        }

        //Sort the jobs in descending order, based on the score they obtained
        ArrayList scheduleArrayList = scores.sorted();

        //Transform the sorted list of jobs into an array
        int[] schedule = new int[numJobs];
        for(int i = 0; i < numJobs; i++){
            schedule[i] = (int)scheduleArrayList.get(i);
        }

//        boolean isThereWinner = condorsetTests.isThereCondorcetWinner();
//        System.out.println("isThereWinner: " + isThereWinner);
//        boolean isCondorcetWinner = condorsetTests.testCondorcetWinner(schedule);
//        System.out.println("isCondorcetWinner: " + isCondorcetWinner);
//        System.out.println("--------------------------------------------------------------------");
//        System.out.println("PTA violations before: " + condorsetTests.countPTACondorcetViolations(schedule, testInstance.processingTimes));
//        System.out.println("Sum of Tardiness before: " + sumOfTardTests.calculateSumOfTardiness(schedule, testInstance.preferences, testInstance.processingTimes));
//        System.out.println("--------------------------------------------------------------------");
//        int[] agentTardiness = sumOfTardTests.getAgentTardiness();
//        System.out.println("Tardiness per agent (before): " + Arrays.toString(agentTardiness));
//       System.out.println("Gini Index (before): " + GiniIndexTester.getIndex(agentTardiness, numAgents));


        //PART 2 - Make our schedule Condorcet-consistent
        //First cell of the "results" array represents consistency,
        //second cell represents index to start the next search from (optimization purposes)
        int[] results = new int[2];
        //While our schedule is not Condorcet-consistent
        while (results[0] == 0) {
            //Search for swaps that will render the schedule Condorcet-consistent
            results = condorsetTests.testCondorcetConsistency(schedule, results[1]);
            //If schedule has become Condorcet-consistent
            if (results[0] == 1)
                System.out.println("Schedule: " + Arrays.toString(schedule));
        }

        //Runtime
        long end = System.currentTimeMillis();
        long ourRuntime = end - start;
        System.out.println("OUR TOTAL RUNTIME: " + ourRuntime + " ms");

        Statistics.myRuntime.add(ourRuntime);
        Statistics.numAgents.add(testInstance.numAgents);
        Statistics.numJobs.add(testInstance.numJobs);

        //PTA-Violations
        double myPercPTA = ((double)condorsetTests.countPTACondorcetViolations(schedule, testInstance.processingTimes)) / (numJobs * numJobs /2);
        System.out.println("PTA violations % for our solution: " + myPercPTA);
        Statistics.myPTAviolations.add(myPercPTA);
        if (minizincSolution != null) {
            double mznPercPTA = ((double) condorsetTests.countPTACondorcetViolations(minizincSolution, testInstance.processingTimes) / (numJobs * numJobs /2));
            System.out.println("PTA violations % for MINIZINC: " + mznPercPTA);
            Statistics.mznPTAviolations.add(mznPercPTA);
        }

        //Sum-of-tardiness and tardiness per agent
        int mySum = sumOfTardTests.calculateSumOfTardiness(schedule, testInstance.preferences, testInstance.processingTimes);
        System.out.println("Sum of Tardiness for our solution: " + mySum);
        int[] agentTardiness = sumOfTardTests.getAgentTardiness();

        int mznSum = 0;
        int[] agentTardinessMinizinc = null;
        if (minizincSolution != null) {
            mznSum = sumOfTardTests.calculateSumOfTardiness(minizincSolution, testInstance.preferences, testInstance.processingTimes);
            System.out.println("Sum of Tardiness for MINIZINC: " + mznSum);
            agentTardinessMinizinc = sumOfTardTests.getAgentTardiness();
        }

        Statistics.mySumOfTardiness.add(mySum);
        if (minizincSolution != null) {
            Statistics.mznSumOfTardiness.add(mznSum);
        }

        System.out.println("Tardiness per agent: " + Arrays.toString(agentTardiness));

        //Pareto efficiency
        System.out.println("Pareto Efficient schedule: " + paretoTests.isScheduleParetoEfficient(schedule));
        //System.out.println("Pareto Efficiency per agent: " + Arrays.toString(paretoTests.agentParetoEfficiency(agentTardiness)));

        //Gini index
        double myIndex = GiniIndexTester.getIndex(agentTardiness, numAgents);
        System.out.println("Gini Index for our solution: " + myIndex);
        Statistics.myGiniIndex.add(myIndex);

        if (minizincSolution != null) {
            double mznIndex = GiniIndexTester.getIndex(agentTardinessMinizinc, numAgents);
            System.out.println("Gini Index for MINIZINC: " + mznIndex);
            Statistics.mznGiniIndex.add(mznIndex);
        }

        return schedule;


    }

}
