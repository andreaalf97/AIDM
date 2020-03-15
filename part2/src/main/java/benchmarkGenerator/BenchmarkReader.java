package benchmarkGenerator;

import votingRules.Preference;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

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

        for(Preference preference : testInstance.preferences){

            int[] intPreference = preference.toArray();
            int position;

            for(position = 0; position < intPreference.length - 1; position++)
                builder.append(intPreference[position] + ",");

            builder.append(intPreference[position] + "|\n");
        }

        builder.append("];");

        return builder.toString();

    }

    public static void main(String[] args){

        TestInstance[] instances = BenchmarkReader.readTests("/home/andreaalf/Documents/AIDM/AIDM/part2/src/main/resources/tests/randomTest.instances");

        System.out.println(BenchmarkReader.toMinizincFile(instances[21]));



    }


}
