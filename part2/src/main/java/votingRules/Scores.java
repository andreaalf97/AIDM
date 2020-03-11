package votingRules;

import java.util.Arrays;

public class Scores {

    /**
     * This array contains the scores of all jobs
     * scores[i-1] contains the score of job with ID = i
     */
    private int[] scores;

    public Scores(int nJobs){
        this.scores = new int[nJobs];

        for(int i = 0; i < nJobs; i++)
            this.scores[i] = 0;
    }

    /**
     * @param jobId The ID of the job
     * @return The score of the given job
     */
    public int getScore(int jobId){
        if(jobId <= 0 || jobId > scores.length)
            throw new RuntimeException("Job ID out of bounds");

        return this.scores[jobId - 1];
    }

    /**
     * Sets the score of the job
     * @param jobId The ID of the job
     * @param score The score we want to set
     */
    public void setScore(int jobId, int score){
        if(jobId <= 0 || jobId > scores.length)
            throw new RuntimeException("Job ID out of bounds");

        this.scores[jobId-1] = score;
    }

    /**
     * Adds 1 to the score of the given job
     * @param jobId The ID of the job
     */
    public void addOne(int jobId){
        if(jobId <= 0 || jobId > scores.length)
            throw new RuntimeException("Job ID out of bounds");

        this.scores[jobId-1]++;
    }

    @Override
    public String toString() {
        String output = "";

        for(int i = 0; i < scores.length; i++)
            output += "Job " + (i+1) + ": " + scores[i] + "\n";

        return output;
    }
}
