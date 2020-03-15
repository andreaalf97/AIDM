package votingRules;

import benchmarkGenerator.TestInstance;
import testers.CondorcetConsistencyTester;
import testers.GiniIndexTester;
import testers.ParetoEfficiencyTester;
import testers.SumOfTardinessTester;

import java.util.ArrayList;
import java.util.Arrays;

public class PTA_Copeland implements VotingRule {

    @Override
    public int[] schedule(TestInstance testInstance) {
        double start = System.currentTimeMillis();

        int numAgents = testInstance.numAgents;
        int numJobs = testInstance.numJobs;
        CondorcetConsistencyTester condorsetTests = new CondorcetConsistencyTester(numJobs, numAgents);
        SumOfTardinessTester sumOfTardTests = new SumOfTardinessTester(numJobs, numAgents);
        ParetoEfficiencyTester paretoTests = new ParetoEfficiencyTester(numJobs, numAgents);

        // Initialize the scores with all 0
        Scores scores = new Scores(numJobs);

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
                        counter_i++;  // Increment the counter
                    else counter_j++;


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

        System.out.println(scores);
        System.out.println("SORTED BY SCORE:");
        ArrayList scheduleArrayList = scores.sorted();
        System.out.println(scheduleArrayList);

        double end = System.currentTimeMillis();
        double part1 = end - start;
        System.out.println("part1: " + part1);

        int[] schedule = new int[numJobs];
        for(int i = 0; i < numJobs; i++){
            schedule[i] = (int)scheduleArrayList.get(i);
        }

        boolean isThereWinner = condorsetTests.isThereCondorcetWinner();
        System.out.println("isThereWinner: " + isThereWinner);
        boolean isCondorcetWinner = condorsetTests.testCondorcetWinner(schedule);
        System.out.println("isCondorcetWinner: " + isCondorcetWinner);

        System.out.println("--------------------------------------------------------------------");
        System.out.println("PTA violations before: " + condorsetTests.countPTACondorcetViolations(schedule, testInstance.processingTimes));
        System.out.println("Sum of Tardiness before: " + sumOfTardTests.calculateSumOfTardiness(schedule, testInstance.preferences, testInstance.processingTimes));
        System.out.println("--------------------------------------------------------------------");
        int[] agentTardiness = sumOfTardTests.getAgentTardiness();
        System.out.println("Agent Tardiness: " + Arrays.toString(agentTardiness));
        System.out.println(GiniIndexTester.getIndex(agentTardiness, numAgents));

        int[] results = new int[2];
        results[0] = 0;
        results[1] = 0;

        double start2 = System.currentTimeMillis();

        while (results[0] == 0) {
            //for (int i=0; i<schedule.length; i++) System.out.print(schedule[i] + " ");
            //System.out.println();
            results = condorsetTests.testCondorcetConsistency(schedule, results[1]);
            if (results[0] == 1)
                System.out.println(Arrays.toString(schedule));
        }

        double end2 = System.currentTimeMillis();
        double part2 = end2 - start2;
        System.out.println("part2: " + part2);
        System.out.println("total: " + (part1 + part2));

        System.out.println("--------------------------------------------------------------------");
        System.out.println("PTA violations after: " + condorsetTests.countPTACondorcetViolations(schedule, testInstance.processingTimes));

        System.out.println("--------------------------------------------------------------------");
        System.out.println("Sum of Tardiness after: " + sumOfTardTests.calculateSumOfTardiness(schedule, testInstance.preferences, testInstance.processingTimes));

        System.out.println("Pareto Efficient schedule: " + paretoTests.isScheduleParetoEfficient(schedule));
        agentTardiness = sumOfTardTests.getAgentTardiness();
        System.out.println("Agent Tardiness: " + Arrays.toString(agentTardiness));
        //System.out.println("Pareto Efficient per agent: " + Arrays.toString(paretoTests.agentParetoEfficiency(agentTardiness)));

        System.out.println(GiniIndexTester.getIndex(agentTardiness, numAgents));

        return schedule;


    }

}
