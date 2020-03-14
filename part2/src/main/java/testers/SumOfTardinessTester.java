package testers;

import votingRules.Preference;

import java.util.HashMap;


public class SumOfTardinessTester {

    private int numJobs;
    private int numAgents;

    private int[] agentTardiness;
    private int[] completionTimes;
    private int[] completionTimesPreference;

    public int[] getAgentTardiness(){
        return this.agentTardiness;
    }

    public SumOfTardinessTester(int n, int a){
        this.numJobs = n;
        this.numAgents = a;
        agentTardiness = new int[numAgents];
        completionTimes = new int[numJobs];
        completionTimesPreference = new int[numJobs];
    }

    public void calculateCompletionTimes(Object[] schedule, boolean isPreferenceOfAgent, int[] processingTimes){
        int[] compTimes = completionTimes;
        if (isPreferenceOfAgent) compTimes = completionTimesPreference;
        compTimes[0] = processingTimes[(Integer)schedule[0]];
        for(int i = 1; i < numJobs; i++) {
            int job =  (Integer) schedule[i];
            compTimes[i] = compTimes[i-1] + processingTimes[job];
        }
    }

    public int calculateSumOfTardiness(Object[] schedule, Preference[] preferences, int[] processingTimes){
        int sum = 0;
        calculateCompletionTimes(schedule, false, processingTimes);
        for (int i=0; i<numAgents; i++){
            HashMap<Integer, Integer> preferenceList = preferences[i].getList();
            Integer[] temp = new Integer[numJobs];
            for(int jobID = 0; jobID < numJobs; jobID++) {
                int position = preferenceList.get(jobID);
                temp[position] = jobID;
            }
            calculateCompletionTimes(temp, true, processingTimes);
            agentTardiness[i] = 0;
            for (int pos=0; pos<numJobs; pos++){
                int completedTimeSchedule = completionTimes[pos];
                int jobID = (Integer) schedule[pos];
                int position = preferenceList.get(jobID);
                int completedTimePref = completionTimesPreference[position];
                agentTardiness[i] += Integer.max(0, completedTimeSchedule - completedTimePref);
            }
            sum += agentTardiness[i];
        }

        return sum;
    }

}
