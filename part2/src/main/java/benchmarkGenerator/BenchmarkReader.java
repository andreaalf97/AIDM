package benchmarkGenerator;

import votingRules.Preference;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class BenchmarkReader {

    public static TestInstance[] readTests(String path){

        TestInstance[] solution = null;
        try {
            FileInputStream file = new FileInputStream(path);
            ObjectInputStream in = new ObjectInputStream(file);

            solution = (TestInstance[])in.readObject();
            in.close();
            file.close();

//            System.out.println(solution[0]);
        }
        catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
        }

        return solution;

    }

    public static String toMinizincFile(TestInstance testInstance){

        StringBuilder builder = new StringBuilder();

        builder.append("numJobs = ")
                .append(testInstance.numJobs + ";\n")
                .append("numAgents = " + testInstance.numAgents + ";\n")
                .append("processingTimes = [");

        int i;
        for(i = 0; i < testInstance.processingTimes.length - 1; i++)
            builder.append(testInstance.processingTimes[i] + ", ");


        builder.append(testInstance.processingTimes[i] + "];\n")
                .append("preferences = [|");

        for(i = 0; i < testInstance.preferences.length - 1; i++){

            int[] intPreference = testInstance.preferences[i].toArray();
            int position;

            for(position = 0; position < intPreference.length - 1; position++)
                builder.append((intPreference[position] + 1) + ",");

            builder.append((intPreference[position] + 1) + "|\n");
        }

        int[] intPreference = testInstance.preferences[i].toArray();
        int position;

        for(position = 0; position < intPreference.length - 1; position++)
            builder.append((intPreference[position]+1) + ",");

        builder.append((intPreference[position]+1) + "|];");

        return builder.toString();

    }

    public static void main(String[] args){

        TestInstance[] instances = BenchmarkReader.readTests("/home/andreaalf/Documents/AIDM/AIDM/part2/src/main/resources/tests/randomTest.instances");

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

                StringBuilder output = new StringBuilder();

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream()));



                int exitVal = process.waitFor();

                String line = reader.readLine();
                String scheduleString = line.split("\\[")[1].split("\\]")[0];

                int[] solutionSchedule = BenchmarkReader.toSchedule(scheduleString);

                for(int i = 0; i < solutionSchedule.length; i++)
                    System.out.print(solutionSchedule[i]);
                System.out.println();

                long end = System.currentTimeMillis();
                if (exitVal == 0) {
//                    Scanner scanner = new Scanner(output.toString());
//                    String solution = scanner.nextLine().split(":")[1].split("]")[0];
//                    System.out.println(output);
                } else {
                    throw new RuntimeException("Entered wrong else statement");
                }

                System.out.println("EXECUTION TIME: " + (end - start) + " ms");
                System.out.println("********************************************************");
                System.out.println("********************************************************");

            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }



    }

    public static int[] toSchedule(String scheduleString) {


        scheduleString = scheduleString.replaceAll("[ ]", "");
        String[] splits = scheduleString.split(",");

        ArrayList<Integer> list = new ArrayList<>();

        for(String split : splits){

            list.add((Integer.parseInt(split)) - 1);

        }

        int[] schedule = new int[list.size()];
        for(int i = 0; i < list.size(); i++) {
            schedule[i] = list.get(i);
        }

        return schedule;

    }


}
