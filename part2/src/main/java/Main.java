import benchmarkGenerator.BenchmarkReader;
import benchmarkGenerator.TestInstance;
import testers.SolutionTester;
import votingRules.PTA_Copeland;
import votingRules.Statistics;

import java.io.*;

/**
 * This is the Main class of the project, from which everything will be run
 */
public class Main {

    public static void main(String[] args){


        TestInstance[] instances = BenchmarkReader.readTests("/home/andreaalf/Documents/AIDM/AIDM/part2/src/main/resources/tests/processingTimes.instances");


        // Create the file if it doesn't exist yet and put the minizinc data in
        for(TestInstance test : instances) {

            try {
                File minizincData = new File("/home/andreaalf/Documents/AIDM/AIDM/part2/minizinc/temp.dzn");
                minizincData.createNewFile();

                FileWriter writer = new FileWriter(minizincData);
                writer.write(BenchmarkReader.toMinizincFile(test));
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }


            Runtime runtime = Runtime.getRuntime();
            String[] commands = {"minizinc",
                    "/home/andreaalf/Documents/AIDM/AIDM/part2/minizinc/model.mzn",
                    "/home/andreaalf/Documents/AIDM/AIDM/part2/minizinc/temp.dzn"};

            try {
                long start = System.currentTimeMillis();
                Process process = runtime.exec(commands);

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream()));

                int exitVal = process.waitFor();

                String line = reader.readLine();
                String scheduleString = line.split("\\[")[1].split("\\]")[0];

                int[] solutionSchedule = BenchmarkReader.toSchedule(scheduleString);

                long end = System.currentTimeMillis();
                if (exitVal != 0) {
                    throw new RuntimeException("Entered wrong else statement");
                }

                System.out.println("EXECUTION TIME MINIZINC: " + (end - start) + " ms");
                Statistics.mznRuntime.add(end-start);

                new PTA_Copeland().schedule(test, solutionSchedule);

                System.out.println("*********************************************");
                System.out.println("*********************************************");
                System.out.println("*********************************************");

            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Statistics.toFile("/home/andreaalf/Documents/Other/statisticsProcessing.txt");

        //System.out.println("PREFERENCES: " + testInstance.toString());

        //System.out.println("SCORES:");

    }

}
