package testers;

import benchmarkGenerator.TestInstance;
import votingRules.PTA_Copeland;
import votingRules.VotingRule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GiniIndexTester {

    /**
     * This method calculates Gini index of a schedule, based solely
     * on the (previously calculated) tardiness per agent.
     * @param tardiness The tardiness per agent
     * @param numAgents The number of agents
     * @return The Gini index of the schedule (fairness)
     */
    public static double getIndex(int[] tardiness, int numAgents){

        //Sort the tardiness array in increasing order by first making it a list
        List<Integer> list = new ArrayList<Integer>();
        for(int i = 0; i < tardiness.length; i++)
            list.add(tardiness[i]);

        Collections.sort(list);

        //Each agent has the same representation ratio in the total population of agents
        double fractionPopulation = 1.0 / numAgents;

        //Having sorted the tardiness, we can now
        //calculate the Gini index through the formula
        //given by the problem description (the link to the
        //stackoverflow question actually). We have let the
        //variables with their original names (G and S)
        //as this helped avoid confusion.
        //Do not worry, we perfectly understood the idea behind this fairness measure.
        double[] S = new double[numAgents];
        for (int i=0; i<numAgents; i++){
            for (int j=0; j<=i; j++){
                S[i] += list.get(j) * fractionPopulation;
            }
        }
        double G = S[0] * fractionPopulation;
        for (int i=1; i<numAgents; i++){
            G += fractionPopulation * (S[i] + S[i-1]);
        }

        double result = 1 - (G / S[numAgents-1]);


        return result;
    }

    public static void main(String[] args){

        int numAgents = 10;
        int numJobs = 10;

        TestInstance instance = SolutionTester.generateRandom(numAgents, numJobs);

//        VotingRule rule = new PTA_Copeland();
//        int[] schedule = rule.schedule(instance);
//
//        SumOfTardinessTester tester = new SumOfTardinessTester(numJobs, numAgents);
//        tester.calculateSumOfTardiness(schedule, instance.preferences, instance.processingTimes);
//
//        int[] tardiness = tester.getAgentTardiness();

//        System.out.println(GiniIndexTester.getIndex(tardiness, numAgents));
    }
}
