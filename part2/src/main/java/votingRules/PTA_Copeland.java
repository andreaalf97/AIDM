package votingRules;

import testers.CondorcetConsistencyTester;

import java.util.ArrayList;

import static votingRules.Preference.processingTimes;

public class PTA_Copeland implements VotingRule {

    @Override
    public void schedule(int numJobs, Preference[] preferences) {

        int numAgents = preferences.length;
        CondorcetConsistencyTester condorsetTests = new CondorcetConsistencyTester(numJobs, numAgents);

        // Initialize the scores with all 0
        Scores scores = new Scores(numJobs);

        // This double loop compares each job with all the others
        for(int i = 0; i < numJobs - 1; i++) {
            for (int j = i + 1; j < numJobs; j++) {

                int counter1 = 0;
                int counter2 = 0;
                // Now we count how many times the job with ID=i comes before the job with ID=j
                // For all preferences
                // If job with ID=i comes before the job with ID=j
                for (Preference preference : preferences)
                    if (preference.isBefore(i, j))
                        counter1++;  // Increment the counter
                    else counter2++;


                int p_i = processingTimes[i];
                int p_j = processingTimes[j];

                // The threshold of job i, as given by our definition
                // This rule is the main and only way the processing times of the
                // jobs are taken into account for the final "true" ranking
                float threshold = (p_i * numAgents) / (p_i + p_j);

                if (counter1 == threshold) {    //If the number of votes equals the threshold for job i,
                    // the same applies for job j and its corresponding threshold, because math, so give both of them a point
                    scores.addOne(i);
                    scores.addOne(j);
                } else if (counter1 > threshold) {  //Give a point to job i for surpassing the threshold
                    scores.addOne(i);
                } else { //Give a point to job j for surpassing its threshold (again, because math)
                    scores.addOne(j);
                }

                // Store the votes each job got against the other
                CondorcetConsistencyTester.votes[i][j] = counter1;
                CondorcetConsistencyTester.votes[j][i] = counter2;

            }
        }

        System.out.println(scores);
        System.out.println("SORTED BY SCORE:");
        ArrayList scheduleArrayList = scores.sorted();
        System.out.println(scheduleArrayList);
        Object[] schedule = scheduleArrayList.toArray();
        boolean isThereWinner = condorsetTests.isThereCondorcetWinner();
        System.out.println("isThereWinner: " + isThereWinner);
        boolean isCondorcetWinner = condorsetTests.testCondorcetWinner(schedule);
        System.out.println("isCondorcetWinner: " + isCondorcetWinner);
        int[] results = new int[2];
        results[0] = 0;
        results[1] = 0;
        while (results[0] == 0) {
            for (int i=0; i<schedule.length; i++) System.out.print(schedule[i] + " ");
            System.out.println();
            results = condorsetTests.testCondorcetConsistency(schedule, results[1]);
            System.out.println("isCondorcetConsistent (1==YES):" + results[0]);
        }
    }

}
