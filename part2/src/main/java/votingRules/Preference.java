package votingRules;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * This interface is used to represent a single preference by an agent
 */
public class Preference implements Serializable {

    private final int numJobs;

    /**
     * This hashmap represents the preference list of each agent.
     * The key is the ID of the job, and the value is the position
     * the agent chose for that job. This structure really helps us
     * perform quick lookups later on.
     */
    private final HashMap<Integer, Integer> preferenceList = new HashMap<>();

    public HashMap<Integer, Integer> getList(){
        return this.preferenceList;
    }

    /**
     * This constructor randomly places each
     * job into a unique position in the preference
     * list of the agent. This is achieved
     * through the shuffle method.
     */
    public Preference(int numJobs){
        this.numJobs = numJobs;
        List<Integer> solution = new ArrayList<>();

        for(int i = 0; i < numJobs; i++)
            solution.add(i);

        Collections.shuffle(solution);

        for(int i = 0; i < numJobs; i++)
            preferenceList.put(i, solution.get(i));

    }

    public Preference(int[] preferences){
        this.numJobs = preferences.length;
        for(int i = 0; i < numJobs; i++)
            preferenceList.put(preferences[i], i);
    }


    /**
     * This function tells if job with ID1 comes before job with ID2 in this preference
     * @param id1 The ID of the first job
     * @param id2 The ID of the second job
     * @return T/F
     */
    public boolean isBefore(int id1, int id2){
        int position1 = preferenceList.get(id1);
        int position2 = preferenceList.get(id2);
        return position1 < position2;
    }

    @Override
    public String toString() {

        //Create temporary array representing the position of each job
        //in this preference list. It takes the position from the hashmap
        //and uses it as its index for the currently examined job.
        int[] temp = new int[numJobs];
        for(int jobID = 0; jobID < numJobs; jobID++) {
            int position = preferenceList.get(jobID);
            temp[position] = jobID;
        }

        StringBuilder output = new StringBuilder("[");

        for(int position = 0; position < numJobs; position++)
            output.append(temp[position])
                    .append(", ");

        output.append("]\n");

        return output.toString();
    }
}
