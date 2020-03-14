package votingRules;

import benchmarkGenerator.TestInstance;

/**
 * This interface represents a voting rule
 */
public interface VotingRule {

    void schedule(TestInstance testInstance);

}
