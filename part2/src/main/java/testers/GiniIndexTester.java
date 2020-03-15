package testers;

import benchmarkGenerator.TestInstance;
import votingRules.PTA_Copeland;
import votingRules.VotingRule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GiniIndexTester {

    public static double getIndex(int[] tardiness, int numAgents){

        List<Integer> list = new ArrayList<Integer>();
        for(int i = 0; i < tardiness.length; i++)
            list.add(tardiness[i]);

        Collections.sort(list);

        double fractionPopulation = 1.0 / numAgents;

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


        /*
        double total = 0;
        double totalTardiness = 0;
        for(int t : list)
            totalTardiness += t;

        for(int i = 0; i < list.size(); i++){

//            System.out.println("fractionPopulation: " + fractionPopulation);

            double fractionIncome = ((double)list.get(i)) / totalTardiness;
//            System.out.println("fractionIncome: " + fractionIncome);

            double fractionRicher = ((double)(i)) / list.size();
//            System.out.println("fractionRicher: " + fractionRicher);

            double score = fractionIncome * (fractionPopulation + (2 * fractionRicher));
//            System.out.println("totalTardiness: " + totalTardiness);

            total += score;

        }

        return 1 - total;

         */

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
