package benchmarkGenerator;

import votingRules.Preference;

import java.io.*;
import java.util.Random;

public class ProcessingGenerator implements TestGenerator {

    private static String filePath = "/home/andreaalf/Documents/AIDM/AIDM/part2/src/main/resources/tests/processingTimes.instances";

    @Override
    public TestInstance[] generateBenchmark() {

        int numInstances = 30;

        TestInstance[] testInstances = new TestInstance[numInstances];  // The array which will contain all the test instances
        Random rand = new Random();

        for(int i = 0; i < numInstances; i++){


            int numAgents = rand.nextInt(24) + 5;
            int numJobs = rand.nextInt(8) + 2;

            int[] processingTimes = new int[numJobs];
            for(int j = 0; j < numJobs; j++){

                if(j%2 == 0)
                    processingTimes[j] = rand.nextInt(20) + 80;
                else
                    processingTimes[j] = rand.nextInt(14) + 1;

            }

            Preference[] preferences = new Preference[numAgents];
            for(int j = 0; j < numAgents; j++)
                preferences[j] = new Preference(numJobs);

            testInstances[i] = new TestInstance(
                    numAgents,
                    numJobs,
                    processingTimes,
                    preferences
            );


        }

        return testInstances;

    }


    public static void main(String[] args){

        TestInstance[] tests = new ProcessingGenerator().generateBenchmark();


        try {
            File f = new File(ProcessingGenerator.filePath);

            f.createNewFile();

            FileOutputStream file = new FileOutputStream(f);
            ObjectOutputStream out = new ObjectOutputStream(file);

            out.writeObject(tests);
            out.close();
            file.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }

//        try {
//            File f = new File(ProcessingGenerator.filePath);
//            FileInputStream file = new FileInputStream(f);
//            ObjectInputStream in = new ObjectInputStream(file);
//
//            TestInstance[] readTest = (TestInstance[])in.readObject();
//            in.close();
//            file.close();
//
//            System.out.println(readTest[0]);
//        }
//        catch (IOException | ClassNotFoundException e){
//            e.printStackTrace();
//        }

    }
}
