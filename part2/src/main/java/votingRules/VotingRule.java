package votingRules;

import benchmarkGenerator.TestInstance;

/**
 * This interface represents a voting rule
 */
public interface VotingRule {

    int[] schedule(TestInstance testInstance);

}
