package benchmarkGenerator;

import testers.SolutionTester;

import java.io.*;

public class RandomTestGenerator implements TestGenerator {

    private String filePath = "/home/andreaalf/Documents/AIDM/AIDM/part2/src/main/resources/tests/randomTest.instances";

    @Override
    public TestInstance[] generateBenchmark() {

        TestInstance[] instances = new TestInstance[30];
        int positionCounter = 0;

        for(int numAgents = 30; numAgents <= 35; numAgents++){
            for(int numJobs = 5; numJobs <= 9; numJobs += 1){

                instances[positionCounter] = SolutionTester.generateRandom(numAgents, numJobs);

                positionCounter++;
            }
        }

        return instances;


    }

    public static void main(String[] args){

        RandomTestGenerator generator = new RandomTestGenerator();
        TestInstance[] tests = generator.generateBenchmark();


        try {
            FileOutputStream file = new FileOutputStream(generator.filePath);
            ObjectOutputStream out = new ObjectOutputStream(file);

            out.writeObject(tests);
            out.close();
            file.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }

//        try {
//            FileInputStream file = new FileInputStream(generator.filePath);
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
