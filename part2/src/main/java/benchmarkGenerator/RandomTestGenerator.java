package benchmarkGenerator;

import testers.SolutionTester;

import java.io.*;

public class RandomTestGenerator implements TestGenerator, Serializable {

    private String filePath = this.getClass().getClassLoader().getResource("tests/randomTest.txt").getPath();;

    @Override
    public TestInstance[] generateBenchmark() {

        TestInstance[] instances = new TestInstance[30];
        int positionCounter = 0;

        for(int numAgents = 30; numAgents <= 35; numAgents++){
            for(int numJobs = 5; numJobs <= 25; numJobs += 5){

                instances[positionCounter] = new TestInstance(
                        numAgents,
                        numJobs,
                        SolutionTester.generateRandom(numAgents, numJobs)
                );

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

    }

}
