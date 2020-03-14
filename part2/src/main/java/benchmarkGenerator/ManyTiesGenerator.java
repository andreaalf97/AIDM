package benchmarkGenerator;

import votingRules.Preference;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ManyTiesGenerator implements TestGenerator {

    private String filePath = "/home/andreaalf/Documents/AIDM/AIDM/part2/src/main/resources/tests/manyTies.instances";

    @Override
    public TestInstance[] generateBenchmark() {

        List<TestInstance> testInstances = new ArrayList<>();  // The array which will contain all the test instances

        // The number of jobs:
        // This value can't be too high because the number of agents is then
        // the number of all permutations of this numJobs and of its
        // subsets
        int numJobs = 6;

        // The number of agents, which will depend on how many subsets and their permutations
        // we are going to use in each TestInstance
        int numAgents;

        // The input vector for the permutation function
        int[] permInput = new int[numJobs];
        for(int i = 0; i < numJobs; i++)
            permInput[i] = i;

        // All the permutations of the input vector
        List<int[]> permutations = allPermutations(permInput);

        // System.out.println(permutations.size());

        // The number of agent is the number of the permutations here because we don't have any subsets in this test
        numAgents = permutations.size();

        // Let's create the first test
        Preference[] preferences = new Preference[numAgents];
        for(int i = 0; i < numAgents; i++)
            preferences[i] = new Preference(permutations.get(i));

        // Let's create 10 test instances with the same preferences but random processing times
        for(int i = 0; i < 10; i++)
            testInstances.add(
                    new TestInstance(numAgents, numJobs, preferences)
            );

        //********************************************************************
        // Now we create test with two subsets of 6 jobs
        //********************************************************************

        List<int[]> permutationsLeft = allPermutations(
                new int[]{0, 1, 2}
        );

        List<int[]> permutationsRight = allPermutations(
                new int[]{3, 4, 5}
        );

        permutations = new ArrayList<>();

        for(int[] pLeft : permutationsLeft){
            for(int[] pRight : permutationsRight){

                int[] p = new int[6];
                for(int i = 0; i < 3; i++)
                    p[i] = pLeft[i];
                for(int i = 3; i < 6; i++)
                    p[i] = pRight[i-3];

                permutations.add(p);
            }
        }

        numAgents = permutations.size();

        preferences = new Preference[numAgents];
        for(int i = 0; i < numAgents; i++)
            preferences[i] = new Preference(permutations.get(i));

        for(int i = 0; i < 10; i++)
            testInstances.add(
                    new TestInstance(numAgents, numJobs, preferences)
            );


        //********************************************************************
        // Now we create the permutations of 3 subsets of jobs
        //********************************************************************

        List<int[]> permutationsA = allPermutations(
                new int[]{0, 1}
        );

        List<int[]> permutationsB = allPermutations(
                new int[]{2, 3}
        );

        List<int[]> permutationsC = allPermutations(
                new int[]{4, 5}
        );

        permutations = new ArrayList<>();

        for(int[] pA : permutationsA)
            for(int[] pB : permutationsB)
                for(int[] pC: permutationsC) {

                    int[] p = new int[6];
                    for (int i = 0; i < 2; i++)
                        p[i] = pA[i];
                    for (int i = 2; i < 4; i++)
                        p[i] = pB[i - 2];
                    for(int i = 4; i < 6; i++)
                        p[i] = pC[i - 4];

                    permutations.add(p);
                }


        numAgents = permutations.size();

        preferences = new Preference[numAgents];
        for(int i = 0; i < numAgents; i++)
            preferences[i] = new Preference(permutations.get(i));

        for(int i = 0; i < 10; i++)
            testInstances.add(
                    new TestInstance(numAgents, numJobs, preferences)
            );


//        System.out.println(permutations.size());
//
//        for(int[] p : permutations){
//            String builder = "";
//            for(int e : p)
//                builder += e + ", ";
//            System.out.println(builder);
//        }

        // System.out.println(testInstances.size());

        TestInstance[] returnable = new TestInstance[testInstances.size()];
        for(int i = 0; i < returnable.length; i++)
            returnable[i] = testInstances.get(i);

        return returnable;

    }

    public List<int[]> allPermutations(int[] elements) {

        List list = new ArrayList();
        int n = elements.length;

        int[] indexes = new int[n];
        for (int i = 0; i < n; i++) {
            indexes[i] = 0;
        }
        list.add(elements.clone());

        int i = 0;
        while (i < n) {
            if (indexes[i] < i) {
                swap(elements, i % 2 == 0 ?  0: indexes[i], i);
                list.add(elements.clone());
                indexes[i]++;
                i = 0;
            }
            else {
                indexes[i] = 0;
                i++;
            }
        }

        return list;
    }

    private void swap(int[] input, int a, int b) {
        int tmp = input[a];
        input[a] = input[b];
        input[b] = tmp;
    }

    public static void main(String[] args){

        ManyTiesGenerator generator = new ManyTiesGenerator();
        TestInstance[] tests = generator.generateBenchmark();


        try {
            File f = new File(generator.filePath);
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
//            File f = new File(generator.filePath);
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
