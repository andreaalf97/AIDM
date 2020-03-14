package testers;

public class ParetoEfficiencyTester {

    private int numJobs;
    private int numAgents;

    public ParetoEfficiencyTester(int n, int a){
        this.numJobs = n;
        this.numAgents = a;
    }

    public boolean isScheduleParetoEfficient(Object[] schedule){
        for (int i=0; i<numJobs; i++){
            for (int j=0; j<numJobs; j++) {
                if (i != j && CondorcetConsistencyTester.votes[i][j] == numAgents) {
                    for (int c = 0; c < numJobs; c++) {
                        if ((Integer) schedule[c] == i) break;
                        if ((Integer) schedule[c] == j) return false;
                    }
                }
            }
        }
        return true;
    }

    public boolean[] agentParetoEfficiency(int[] agentTardiness){
        int[] optimalTardiness = {616, 858, 488, 509, 590, 679, 570, 625, 215, 653};
        boolean[] returnThis = new boolean[numAgents];
        for (int i=0; i<numAgents; i++)
            returnThis[i] = (agentTardiness[i] <= optimalTardiness[i]);
        return returnThis;
    }

}
