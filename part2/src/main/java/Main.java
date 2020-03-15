import benchmarkGenerator.TestInstance;
import testers.SolutionTester;
import votingRules.PTA_Copeland;

/**
 * This is the Main class of the project, from which everything will be run
 */
public class Main {

    public static void main(String[] args){

        int numAgents = 500;
        int numJobs = 100;

        TestInstance testInstance = SolutionTester.generateRandom(numAgents,  numJobs);

        System.out.println("PREFERENCES: " + testInstance.toString());


        System.out.println("SCORES:");
        new PTA_Copeland().schedule(testInstance);

    }

}
