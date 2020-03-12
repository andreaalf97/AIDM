package votingRules;

import java.util.*;

public class Scores {

    /**
     * This Map maps each job ID with the corresponding score
     */
    private Map<Integer, Integer> scores;

    /**
     * Constructs the Score object assigning 0 to all jobs
     * @param nJobs The number of existing jobs
     */
    public Scores(int nJobs){
        this.scores = new HashMap<>();

        for(int i = 1; i <= nJobs; i++)
            this.scores.put(i, 0);
    }

    /**
     * @param jobId The ID of the job
     * @return The score of the given job
     */
    public int getScore(int jobId){
        if( ! this.scores.containsKey(jobId) )
            throw new RuntimeException("Job ID out of bounds");

        return this.scores.get(jobId);
    }

    /**
     * Sets the score of the job
     * @param jobId The ID of the job
     * @param score The score we want to set
     */
    public void setScore(int jobId, int score){
        if( ! this.scores.containsKey(jobId) )
            throw new RuntimeException("Job ID out of bounds");

        this.scores.replace(jobId, score);
    }

    /**
     * Adds 1 to the score of the given job
     * @param jobId The ID of the job
     */
    public void addOne(int jobId){
        if( ! this.scores.containsKey(jobId) )
            throw new RuntimeException("Job ID out of bounds");

        int oldValue = this.scores.get(jobId);
        this.scores.replace(jobId, oldValue + 1);
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();

        for(Map.Entry<Integer, Integer> entry : this.scores.entrySet())
            output.append("Job ")
                    .append(entry.getKey())
                    .append(": ")
                    .append(entry.getValue())
                    .append("\n");

        return output.toString();
    }

    public ArrayList<Integer> sorted(){

        List<Map.Entry<Integer, Integer>> list = new LinkedList<>(scores.entrySet());

        list.sort((o1, o2) -> -(o1.getValue()).compareTo(o2.getValue()));

        ArrayList<Integer> sorted = new ArrayList<>();
        for(Map.Entry<Integer, Integer> entry : list)
            sorted.add(entry.getKey());

        return sorted;
    }
}
