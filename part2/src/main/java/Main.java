import testers.SolutionTester;
import votingRules.PTA_Copeland;
import votingRules.Preference;

/**
 * This is the Main class of the project, from which everything will be run
 */
public class Main {

    public static void main(String[] args){

        int nAgents = 5;
        int nJobs = 10;

        Preference[] preferences = SolutionTester.generateRandom(nAgents, nJobs);
        System.out.println("PREFERENCES:");
        for (Preference preference : preferences) {
            System.out.println(preference);
        }

        System.out.println("SCORES:");
        new PTA_Copeland().schedule(preferences);

    }

}
