package testers;

import benchmarkGenerator.TestInstance;
import votingRules.PTA_Copeland;
import votingRules.VotingRule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GiniIndexTester {

    public static float getIndex(int[] tardiness){

        List<Integer> list = new ArrayList<Integer>();
        for(int i = 0; i < tardiness.length; i++)
            list.add(tardiness[i]);

        Collections.sort(list);

        System.out.println(list);

        return 0;
    }

    public static void main(String[] args){

        int numAgents = 50;
        int numJobs = 10;

        TestInstance instance = SolutionTester.generateRandom(numAgents, numJobs);

        VotingRule rule = new PTA_Copeland();
        int[] schedule = rule.schedule(instance);

        SumOfTardinessTester tester = new SumOfTardinessTester(numJobs, numAgents);
        tester.calculateSumOfTardiness(schedule, instance.preferences, instance.processingTimes);

        int[] tardiness = new SumOfTardinessTester(numJobs, numAgents).getAgentTardiness();

        System.out.println(GiniIndexTester.getIndex(tardiness));
    }
}
