package votingRules;

public class PTA_Copeland implements VotingRule {

    @Override
    public void schedule(Preference[] preferences) {

        int numPreferences = preferences.length;
        int numJobs = preferences[0].getSize();

        // Initialize the scores with all 0
        Scores scores = new Scores(numJobs);

        // This double loop compares each job with all the others
        for(int i = 1; i <= numJobs - 1; i++) {
            for (int j = i + 1; j <= numJobs; j++){

                int counter = 0;
                // Now we count how many times the job with ID=i comes before the job with ID=j
                // For all preferences
                // If job with ID=i comes before the job with ID=j
                for (Preference preference : preferences)
                    if (preference.isBefore(i, j))
                        counter++;  // Increment the counter

                int p_i = preferences[0].getProcessingTime(i);
                int p_j = preferences[0].getProcessingTime(j);

                float threshold = ((float)(p_i * numPreferences))/(p_i + p_j);
                if(counter > threshold)
                    scores.addOne(i);
                else
                    scores.addOne(j);

            }
        }

        System.out.println(scores);
        System.out.println("SORTED BY SCORE:");
        System.out.println(scores.sorted());
    }

}
