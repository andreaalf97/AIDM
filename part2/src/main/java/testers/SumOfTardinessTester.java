package testers;

import votingRules.Preference;
import java.util.HashMap;


public class SumOfTardinessTester {

    private int numJobs;
    private int numAgents;

    private int[] agentTardiness; //tardiness per agent
    private int[] completionTimes; //the completion time of each job based on the given schedule.
    // index 0 = first scheduled job, NOT job with ID=0
    private int[] completionTimesPreference; //the completion time of each job based on the preference of an agent.
    // index 0 = first scheduled job by agent, NOT job with ID=0


    public SumOfTardinessTester(int numJobs, int numAgents){
        this.numJobs = numJobs;
        this.numAgents = numAgents;
        agentTardiness = new int[this.numAgents];
        completionTimes = new int[this.numJobs];
        completionTimesPreference = new int[this.numJobs];
    }

    /**
     * This method calculates the completion time of each job based on a schedule
     * @param schedule The array on which the completion times will be based
     * @param isPreferenceOfAgent is the array given (schedule) a preference list or the final schedule?
     * @param processingTimes The processing times of the jobs
     */
    public void calculateCompletionTimes(int[] schedule, boolean isPreferenceOfAgent, int[] processingTimes){
        //Decide which object (array) you will fill
        int[] compTimes = completionTimes;
        if (isPreferenceOfAgent) compTimes = completionTimesPreference;
        //The completion time of the first job IN THE SCHEDULE is the processing time of that job
        compTimes[0] = processingTimes[schedule[0]];
        //Calculate the rest of the completion times by adding the processing time of each next job to the current value.
        for(int i = 1; i < numJobs; i++) {
            int job =  schedule[i];
            compTimes[i] = compTimes[i-1] + processingTimes[job];
        }
    }

    /**
     * This method calculates the sum of tardiness of a schedule
     * @param schedule The schedule for which the tardiness will be calculated
     * @param preferences The preference lists of the agents
     * @param processingTimes The processing times of the jobs
     * @return The sum-of-tardiness
     */
    public int calculateSumOfTardiness(int[] schedule, Preference[] preferences, int[] processingTimes){
        int sum = 0;
        //calculate the completion times based on the schedule
        calculateCompletionTimes(schedule, false, processingTimes);
        //for each agent
        for (int i=0; i<numAgents; i++){
            //Transform the preference list of that agent (hashmap) into an array (temp)
            HashMap<Integer, Integer> preferenceList = preferences[i].getList();
            int[] temp = new int[numJobs];
            for(int jobID = 0; jobID < numJobs; jobID++) {
                int position = preferenceList.get(jobID);
                temp[position] = jobID;
            }
            //calculate the completion times based on the preference list of that agent
            calculateCompletionTimes(temp, true, processingTimes);
            agentTardiness[i] = 0;
            //for each job in the actual schedule
            for (int pos=0; pos<numJobs; pos++){
                //get its completion time based on the schedule
                int completedTimeSchedule = completionTimes[pos];
                //get its completion time based on the preference list of the agent
                int jobID = schedule[pos];
                int position = preferenceList.get(jobID);
                int completedTimePref = completionTimesPreference[position];
                //calculate the tardiness of the agent
                agentTardiness[i] += Integer.max(0, completedTimeSchedule - completedTimePref);
            }
            sum += agentTardiness[i];
        }

        return sum;
    }

    public int[] getAgentTardiness(){
        return this.agentTardiness.clone();
    }

}
