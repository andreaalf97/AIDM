package testers;

public class ParetoEfficiencyTester {

    private int numJobs;
    private int numAgents;

    public ParetoEfficiencyTester(int n, int a){
        this.numJobs = n;
        this.numAgents = a;
    }

    /**
     * This method decides whether the given schedule is Pareto-efficient,
     * based on the definition in the paper.
     * @param schedule The schedule for which the tardiness will be calculated
     */
    public boolean isScheduleParetoEfficient(int[] schedule){
        for (int i=0; i<numJobs; i++){
            for (int j=0; j<numJobs; j++) {
                //If some job i got all the votes compared to another job j (preferred by all)
                if (i != j && CondorcetConsistencyTester.votes[i][j] == numAgents) {
                    //if i comes after job j, the schedule is not Pareto efficient
                    for (int c = 0; c < numJobs; c++) {
                        if (schedule[c] == i) break;
                        if (schedule[c] == j) return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Please ignore this function, it was used to calculate the Pareto efficiency per agent
     * @param agentTardiness The tardiness of each agent based on the last schedule used
     */
    public boolean[] agentParetoEfficiency(int[] agentTardiness){
        int[] optimalTardiness = {616, 858, 488, 509, 590, 679, 570, 625, 215, 653};
        boolean[] returnThis = new boolean[numAgents];
        for (int i=0; i<numAgents; i++)
            returnThis[i] = (agentTardiness[i] <= optimalTardiness[i]);
        return returnThis;
    }

}
