package votingRules;

import java.util.Random;

public class Job {

    private int id;
    private int processingTime;

    /**
     * Regular constructor
     * @param id The ID of the job
     * @param processingTime The processing time of the job (p)
     */
    public Job(int id, int processingTime){
        this.id = id;
        this.processingTime = processingTime;
    }

    /**
     * Getter
     * @return The job ID
     */
    public int getId() {
        return id;
    }

    /**
     * Getter
     * @return The processing time of the job
     */
    public int getProcessingTime() {
        return processingTime;
    }
}
