package testers;


public class CondorcetConsistencyTester {

    private int numJobs;
    private int numAgents;

    public CondorcetConsistencyTester(int n, int a){
        this.numJobs = n;
        this.numAgents = a;
        votes = new int[n][n];
    }

    /**
     * The position [i][j] of this array indicates
     * the number of agents that prefer job i over job j.
     */
    public static int[][] votes;

    /**
     * The following function tests Condorset consistency for a given ranking
     * @param schedule The array-ranking that we will check for consistency
     * @param startIndex The index from which to start checking (only used for optimization purposes)
     */
    public int[] testCondorcetConsistency(int[] schedule, int startIndex){
        // The returnThis array stores the value 0/1 in its first position if the schedule is consistent,
        // and if not, stores the index from where the next consistency-check should start (for optimization purposes)
        int[] returnThis = new int[2];
        if (startIndex < 0) startIndex = 0;
        for(int i = startIndex; i < numJobs - 1; i++) {
                int job_left = schedule[i];
                int job_right =  schedule[i+1];
                    // If the job on the right is preferred by the majority, then the schedule is not consistent
                    // As such, perform a local swap between the left and right jobs
                    if (votes[job_right][job_left] > numAgents/2) {
                        //System.out.println(job_left + " > " + job_right + " but the score of job " + job_right + " is " +
                        //        votes[job_right][job_left] + " , which is greater than half the agents");
                        int temp = schedule[i];
                        schedule[i] = schedule[i+1];
                        schedule[i+1] = temp;

                        // non-Condorset consistent schedule. Also return the index to start the next consistency test
                        returnThis[0] = 0;
                        returnThis[1] = i - 1;
                        return returnThis;
                    }
        }
        // Condorcet consistent schedule
        returnThis[0] = 1;
        returnThis[1] = 0;
        return returnThis;
    }

    /**
     * The following function tests if the winner of the schedule is the Condorcet winner
     * @param schedule The array-ranking
     */
    public boolean testCondorcetWinner(int[] schedule){
        int winner = schedule[0];
        for(int i = 1; i < numJobs; i++) {
            int job_right = schedule[i];
            if (votes[job_right][winner] > numAgents/2) {
                //System.out.println(winner + " > " + job_right + " but the score of job " + job_right + " is " +
                //        votes[job_right][winner] + " , which is greater than half the agents");
                return false;
            }
        }
        return true;
    }

    /**
     * The following function tests if there is a Condorcet winner at all
     */
    public boolean isThereCondorcetWinner(){
        for(int i = 0; i < numJobs; i++) {
            boolean lost = false;
            for(int j = 0; j < numJobs; j++) {
                if (i != j){
                    if (votes[j][i] >= votes[i][j]) {
                        lost = true;
                        break;
                    }
                }
            }
            if (lost == false) {
//                System.out.println("The actual Condorcet winner is " + i);
                return true;
            }
        }
//        System.out.println("No Condorcet winner exists");
        return false;
    }

    /** The following function counts the number of PTA-Condorcet violations
     * in the schedule, as PTA-Condorcet consistency is defined in the paper
     * @param schedule The array-ranking
     * @param processingTimes The processing time of each job
     */
    public int countPTACondorcetViolations(int[] schedule, int[] processingTimes){
        int count = 0;
        //for each pair of jobs in the schedule
        for(int i = 0; i < numJobs - 1; i++) {
            for(int j = i+1; j < numJobs; j++) {
                int job_left = schedule[i];
                int job_right = schedule[j];
                int p_i = processingTimes[job_left];
                int p_j = processingTimes[job_right];
                //If the job on the right ("loser") actually got more votes than the threshold it should surpass,
                //then this is a violation of the PTA-Condorcet rule
                float threshold = (float)(p_j * numAgents) / (float)(p_j + p_i);
                //float threshold2 = (float)(p_i * numAgents) / (float)(p_i + p_j);
                if (votes[job_right][job_left] > threshold) {
                    //System.out.println(job_left + " > " + job_right + " but the score of job " + job_right + " is " +
                    //        votes[job_right][job_left] + " , which is greater than the threshold " + threshold);
                    //System.out.println(job_left + " > " + job_right + " and the score of job " + job_left + " is " +
                    //        votes[job_left][job_right] + " , which is less than the threshold " + threshold2);
                    count++;
                }
            }
        }
        return count;
    }


}
