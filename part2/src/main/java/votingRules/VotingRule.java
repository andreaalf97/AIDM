package votingRules;

/**
 * This interface represents a voting rule
 */
public interface VotingRule {

    void schedule(int numJobs, Preference[] preferences);

}
