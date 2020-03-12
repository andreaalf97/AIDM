package votingRules;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This interface is used to represent a single preference by an agent
 */
public class Preference {

    /**
     * This array contains the job list
     * jobs[0] is the top job
     */
    private Job[] jobs;

    /**
     * This contains all the processing times of all jobs
     * processingTimes[i] contains the p of job with ID = i+1
     */
    private int[] processingTimes;

    /**
     * Constructs the object based on the array
     * @param jobs the array used to create the object
     */
    public Preference(Job[] jobs, int[] processingTimes){
        this.jobs = jobs;
        this.processingTimes = processingTimes;
    }

    /**
     * This contructor randomly generates this object
     * @param nAgents The number of agents
     */
    public Preference(int nAgents, int[] processingTimes){
        List<Job> solution = new ArrayList<>();
        this.processingTimes = processingTimes;

        for(int i = 1; i <= nAgents; i++)
            solution.add(new Job(i, processingTimes[i-1]));

        Collections.shuffle(solution);
        this.jobs = new Job[solution.size()];

        for(int i = 0; i < nAgents; i++)
            this.jobs[i] = solution.get(i);

    }

    /**
     * @return A copy of the job list
     */
    public Job[] getPreferenceAsArray(){
        return this.jobs;
    }

    /**
     * @return The amount of jobs in this Preference
     */
    public int getSize(){
        return this.jobs.length;
    }

    /**
     * This function tells if job with ID1 comes before job with ID2 in this preference
     * @param id1 The ID of the first job
     * @param id2 The ID of the second job
     * @return T/F
     */
    public boolean isBefore(int id1, int id2){
        for (Job job : this.jobs) {
            if (job.getId() == id1)
                return true;
            if (job.getId() == id2)
                return false;
        }

        throw new RuntimeException("No job has been found with the given IDs");
    }

    /**
     * This returns the processing time of job with ID jobId
     * @param jobId The ID of the requested job
     * @return The processing time of the job
     */
    public int getProcessingTime(int jobId){
        if(jobId < 1 || jobId > this.jobs.length)
            throw new RuntimeException("Job ID out of bounds");

        return this.processingTimes[jobId - 1];
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder("[");

        int i;
        for(i = 0; i < jobs.length - 1; i++)
            output.append(jobs[i].getId())
                    .append("(")
                    .append(jobs[i].getProcessingTime())
                    .append("), ");

        output.append(jobs[i].getId())
                .append("(")
                .append(jobs[i].getProcessingTime())
                .append(")")
                .append("]\n");

        return output.toString();
    }
}
