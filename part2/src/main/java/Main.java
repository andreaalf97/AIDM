import benchmarkGenerator.TestInstance;
import testers.CondorcetConsistencyTester;
import testers.SolutionTester;
import votingRules.PTA_Copeland;

import java.io.IOException;

/**
 * This is the Main class of the project, from which everything will be run
 */
public class Main {

    public static void main(String[] args){

//        if(args.length != 2)
//            throw new RuntimeException("I am expecting 2 args: numAgents and numJobs");

//        int numAgents = Integer.parseInt(args[0]);
//        int numJobs = Integer.parseInt(args[1]);

        int numAgents = 10;
        int numJobs = 10;

        TestInstance testInstance = SolutionTester.generateRandom(numAgents,  numJobs);

//        try {
//            Process p = Runtime.getRuntime().exec("minizinc model.mzn example.dzn");
//        }
//        catch (IOException e){
//            e.printStackTrace();
//        }

//        System.out.println("PREFERENCES: " + testInstance.toString());

//        System.out.println("SCORES:");

        long start = System.currentTimeMillis();

        int[] schedule = new PTA_Copeland().schedule(testInstance);

        long end = System.currentTimeMillis();

        CondorcetConsistencyTester condTester = new CondorcetConsistencyTester(testInstance.numJobs, testInstance.numAgents);



        System.out.println("Found solution of lenght " + schedule.length);
        System.out.println("Solution found in " + (end - start) + "ms");


    }

}
