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

        double totalTardiness = 0;
        for(int t : list)
            totalTardiness += t;

        double fractionPopulation = 1.0 / numAgents;
        double total = 0;

        for(int i = 0; i < list.size(); i++){

//            System.out.println("fractionPopulation: " + fractionPopulation);

            double fractionIncome = ((double)tardiness[i]) / totalTardiness;
//            System.out.println("fractionIncome: " + fractionIncome);

            double fractionRicher = ((double)(i)) / list.size();
//            System.out.println("fractionRicher: " + fractionRicher);

            total += fractionIncome * (fractionPopulation + (2 * fractionRicher));
//            System.out.println("totalTardiness: " + totalTardiness);

        }

        return 1 - total;
    }

    public static void main(String[] args){

        int numAgents = 500;
        int numJobs = 30;

        TestInstance instance = SolutionTester.generateRandom(numAgents, numJobs);

        VotingRule rule = new PTA_Copeland();
        int[] schedule = rule.schedule(instance);

        SumOfTardinessTester tester = new SumOfTardinessTester(numJobs, numAgents);
        tester.calculateSumOfTardiness(schedule, instance.preferences, instance.processingTimes);

        int[] tardiness = tester.getAgentTardiness();

        System.out.println(GiniIndexTester.getIndex(tardiness, numAgents));
    }
}
