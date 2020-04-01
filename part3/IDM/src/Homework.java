import java.io.FileWriter;
import java.io.IOException;
import java.util.*;


public class Homework {
	private static final Random rnd = new Random(222);
	
	// Example
	public static void task0() {
		// Get CMDP model for 1 agent
		CMDP cmdp = UserGenerator.getCMDPChild();
		CMDP[] cmdps = new CMDP[]{cmdp};
		
		// construct dummy policy that always executes action 4
		ArrayList<double[][]> policies = new ArrayList<double[][]>();
		double[][] policy = new double[cmdp.getNumStates()][cmdp.getNumActions()];
		//always execute action 4, at any state
		for(int s=0; s<cmdp.getNumStates(); s++) {
			policy[s][4] = 1.0;
		}
		policies.add(policy);
		
		// construct dummy solution object with dummy expectations 0.0
		Solution solution = new Solution(policies, 0.0, 0.0, new double[]{0.0}, new double[]{0.0});
		
		// use the simulator to execute on run
		Simulator sim = new Simulator(rnd);
		sim.printActions();
		sim.simulate(cmdps, solution, 1);
	}
	
	// Solve unconstrained problem for 1 agent with value iteration
	public static void task1() {
		// Get CMDP model for 1 agent
		CMDP cmdp = UserGenerator.getCMDPChild();
		CMDP[] cmdps = new CMDP[]{cmdp};
		
		// Solve the problem without constraints
		PlanningAlgorithm alg = new PlanningAlgorithm();		
		Solution solution = alg.solveVI(cmdps);
		System.out.println("Expected reward: "+solution.getExpectedReward());
		System.out.println("Expected cost: "+solution.getExpectedCost());
		
		// Simulate solution
		System.out.println();
		Simulator sim = new Simulator(rnd);
		sim.simulate(cmdps, solution, 1000);
		
		// Print policy of agent 0
		int agentID = 0;
		double[][] policy = solution.getPolicy(agentID);

		System.out.println("\nPOLICY");
		System.out.println("--------------------------------");
		for(int s=0; s<cmdps[agentID].getNumStates(); s++) {
			System.out.print("State "+s+": ");
			if (s < 10) System.out.print(" ");
			for(int a=0; a<cmdps[agentID].getNumActions(); a++) {
				System.out.print(policy[s][a]+" ");
			}
			System.out.println();
		}


		
	}

	// Solve unconstrained problem for 1 agent with cost
	public static void task2() {
		// Get CMDP model for 1 agent
		CMDP cmdp = UserGenerator.getCMDPChild();

		// Assign cost
		for(int s = 0; s < cmdp.getNumStates(); s++)
			for(int a = 0; a < cmdp.getNumActions(); a++)
				cmdp.assignCost(s, a, 2 * a);

		CMDP[] cmdps = new CMDP[]{cmdp};

		// Solve the problem without constraints
		PlanningAlgorithm alg = new PlanningAlgorithm();
		Solution solution = alg.solveUnconstrained(cmdps);
		System.out.println("Expected reward: "+solution.getExpectedReward());
		System.out.println("Expected cost: "+solution.getExpectedCost());

		// Simulate solution
		System.out.println();
		Simulator sim = new Simulator(rnd);
		sim.simulate(cmdps, solution, 1000);

		// Print policy of agent 0
		int agentID = 0;
		double[][] policy = solution.getPolicy(agentID);
		System.out.println();
		for(int s=0; s<cmdps[agentID].getNumStates(); s++) {
			System.out.print("State "+s+": ");
			for(int a=0; a<cmdps[agentID].getNumActions(); a++) {
				System.out.print(policy[s][a]+" ");
			}
			System.out.println();
		}

	}
	
	// Solve unconstrained problem for 1 agent with cost
	public static void task3() {
		// Get CMDP model for 1 agent
		CMDP cmdp = UserGenerator.getCMDPChild();

		// Assign cost
		for(int s = 0; s < cmdp.getNumStates(); s++)
			for(int a = 0; a < cmdp.getNumActions(); a++)
				cmdp.assignCost(s, a, 2 * a);

		CMDP[] cmdps = new CMDP[]{cmdp};

//		cmdp.assignCost(0, 0, 0);

		int testSamples = 50;
		double[] expectedReward = new double[testSamples];

		Solution solution = null;
		for(int budget = 0; budget < testSamples; budget++) {
			// Solve the problem without constraints
			PlanningAlgorithm alg = new PlanningAlgorithm();
			solution = alg.solve(cmdps, budget+1);
			System.out.println("Expected reward: " + solution.getExpectedReward());
//			System.out.println("Expected cost: " + solution.getExpectedCost());

			expectedReward[budget] = solution.getExpectedReward();
		}

		try {
			FileWriter writer = new FileWriter("/home/andreaalf/Documents/AIDM/AIDM/part3/dataAnalysis/task2data.csv");
			writer.write("budgetLimit,expectedReward\n");

			for(int i = 0; i < testSamples; i++){
				writer.write(i+1 + "," + expectedReward[i] + "\n");
			}

			writer.close();
		}
		catch (IOException e){
			e.printStackTrace();
		}

		// Simulate solution
		System.out.println();
		Simulator sim = new Simulator(rnd);
		sim.simulate(cmdps, solution, 1000);
		
		// Print policy of agent 0
		int agentID = 0;
		double[][] policy = solution.getPolicy(agentID);
		System.out.println();
		for(int s=0; s<cmdps[agentID].getNumStates(); s++) {
			System.out.print("State "+s+": ");
			for(int a=0; a<cmdps[agentID].getNumActions(); a++) {
				System.out.print(policy[s][a]+" ");
			}
			System.out.println();
		}
		
	}
	
	// Personal algorithm for solving the special instance
	// This code has not been optimized at all, neither
	// is it properly structured, it is just a proof of concept
	public static void task6() {
		// Get CMDP models
		int limit1 = 1388;
		int limit2 = 3817;
		CMDP[] agents = new CMDP[limit2];
		List<myAgent> listChild = new LinkedList<>();
		List<myAgent> listAdult = new LinkedList<>();
		for (int i=0; i<limit1; i++){
			agents[i] = UserGenerator.getCMDPChild();
			agents[i].budget = 10;
			listChild.add(new myAgent(i, 10));
			// Assign cost child
			for(int s = 0; s < agents[i].getNumStates(); s++)
				for(int a = 0; a < agents[i].getNumActions(); a++)
					agents[i].assignCost(s, a, 2 * a);
		}
		for (int i=limit1; i<limit2; i++){
			agents[i] = UserGenerator.getCMDPAdult();
			agents[i].budget = 10;
			listAdult.add(new myAgent(i, 10));
			// Assign cost child
			for(int s = 0; s < agents[i].getNumStates(); s++)
				for(int a = 0; a < agents[i].getNumActions(); a++)
					agents[i].assignCost(s, a, 2 * a);
		}

		PlanningAlgorithm alg = new PlanningAlgorithm();
		System.out.println("Each array slot represents the number of agents having that budget, where budget = array index");

		for (int j=0; j<11; j++) {

			int[] counter = new int[20];
			for (int i=0; i<limit2; i++){
				counter[(int)agents[i].budget]++;
			}
			System.out.println(Arrays.toString(counter));

			double expectedReward = 0.0;
			for (int i = 0; i < limit2; i++) {
				CMDP cmdp = agents[i];
				Solution sol;
				sol = alg.solve(new CMDP[]{cmdp}, cmdp.budget);
				double expectedReward0 = sol.getExpectedReward();
				expectedReward += expectedReward0;
			}
			System.out.printf("Expected reward: %.2f\n\n", expectedReward);

			for (int i=0; i<limit1; i++){
				listChild.get(i).budget--;
				listAdult.get(i).budget++;
			}

			listChild.sort((o1, o2) -> o2.budget.compareTo(o1.budget));
			listAdult.sort((o1, o2) -> o1.budget.compareTo(o2.budget));

			for (int i=0; i<limit1; i++){
				agents[i].budget = listChild.get(i).budget;
			}
			int c = 0;
			for (int i=limit1; i<limit2; i++){
				agents[i].budget = listAdult.get(c).budget;
				c++;
			}

			/*
			for (int i=0; i<limit1; i++){
				System.out.print(listChild.get(i).budget + " ");
			}
			System.out.println();
			for (int i=0; i<limit2-limit1; i++){
				System.out.print(listAdult.get(i).budget + " ");
			}
			System.out.println();
			for (int i=0; i<limit2; i++){
				System.out.print(agents[i].budget + " ");
			}
			System.out.println();

			 */
		}


	}
	
	public static void main(String[] args) {
		  task6();
	}
}

class myAgent  {
	int ID;
	Double budget;

	myAgent(int id, double budget){
		ID = id;
		this.budget = budget;
	}
}
