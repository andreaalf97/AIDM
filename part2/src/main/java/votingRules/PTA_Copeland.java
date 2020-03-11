package votingRules;

public class PTA_Copeland implements VotingRule {

    @Override
    public void schedule(Preference[] preferences) {

        int nPreferences = preferences.length;
        int nJobs = preferences[0].getSize();

        // Initialize the scores with all 0
        Scores scores = new Scores(nJobs);

        // This double loop compares each job with all the others
        for(int i = 1; i <= nJobs - 1; i++) {
            for (int j = i + 1; j <= nJobs; j++){

                int counter = 0;
                // Now we count how many times the job with ID=i comes before the job with ID=j
                // For all preferences
                //If job with ID=i comes before the job with ID=j
                for (Preference preference : preferences)
                    if (preference.isBefore(i, j))
                        counter++;  // Increment the counter

                int p_i = preferences[0].getProcessingTime(i);
                int p_j = preferences[0].getProcessingTime(j);

                float threshold = ((float)(p_i * nPreferences))/(p_i + p_j);
                if(counter > threshold)
                    scores.addOne(i);
                else
                    scores.addOne(j);

            }
        }

        System.out.println(scores);
    }

}
