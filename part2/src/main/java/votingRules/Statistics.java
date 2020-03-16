package votingRules;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Statistics {

    public static ArrayList<Integer> numAgents = new ArrayList<>();
    public static ArrayList<Integer> numJobs = new ArrayList<>();

    public static ArrayList<Integer> mySumOfTardiness = new ArrayList<>();
    public static ArrayList<Integer> mznSumOfTardiness = new ArrayList<>();

    public static ArrayList<Double> myPTAviolations = new ArrayList<>();
    public static ArrayList<Double> mznPTAviolations = new ArrayList<>();

    public static ArrayList<Double> myGiniIndex = new ArrayList<>();
    public static ArrayList<Double> mznGiniIndex = new ArrayList<>();

    public static ArrayList<Long> myRuntime = new ArrayList<>();
    public static ArrayList<Long> mznRuntime = new ArrayList<>();

    public static void toFile(String path){

        StringBuilder builder = new StringBuilder();

        for(int i = 0; i < numAgents.size(); i++){
            builder.append(numAgents.get(i) + " ")
                    .append(numJobs.get(i) + " ")
                    .append(myRuntime.get(i) + " ")
                    .append(mznRuntime.get(i) + " ")
                    .append(mySumOfTardiness.get(i) + " ")
                    .append(mznSumOfTardiness.get(i) + " ")
                    .append(myGiniIndex.get(i) + " ")
                    .append(mznGiniIndex.get(i) + " ")
                    .append(myPTAviolations.get(i) + " ")
                    .append(mznPTAviolations.get(i) + "\n");


        }

        File file = new File(path);

        try {
            file.createNewFile();

            FileWriter writer = new FileWriter(file);

            writer.write(builder.toString());

            writer.close();

        }
        catch (IOException e){
            e.printStackTrace();
        }


    }

}
