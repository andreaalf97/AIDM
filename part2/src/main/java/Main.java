import testers.SolutionTester;
import votingRules.PTA_Copeland;
import votingRules.Preference;

/**
 * This is the Main class of the project, from which everything will be run
 */
public class Main {

    public static void main(String[] args){

        int numAgents = 10;
        int numJobs = 10;

        Preference[] preferences = SolutionTester.generateRandom(numAgents, numJobs);

        System.out.println("PREFERENCES:");
        for (Preference preference : preferences) {
            System.out.println(preference);
        }

        System.out.println("SCORES:");
        new PTA_Copeland().schedule(numJobs, preferences);

    }

}
