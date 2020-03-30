import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.*;


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
		CMDP cmdp = UserGenerator.getCMDPAdult();

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
			FileWriter writer = new FileWriter("/home/andreaalf/Documents/AIDM/AIDM/part3/dataAnalysis/task2dataAdult.csv");
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
	
	// Solve constrained problem for 2 agents with trivial budget split
	public static void task4() {		
		// Get CMDP models
//		CMDP cmdpChild = UserGenerator.getCMDPChild();
//		CMDP cmdpAdult = UserGenerator.getCMDPAdult();
//
//		// Assign cost child
//		for(int s = 0; s < cmdpChild.getNumStates(); s++)
//			for(int a = 0; a < cmdpChild.getNumActions(); a++)
//				cmdpChild.assignCost(s, a, 2 * a);
//
//		// Assign cost adult
//		for(int s = 0; s < cmdpAdult.getNumStates(); s++)
//			for(int a = 0; a < cmdpAdult.getNumActions(); a++)
//				cmdpAdult.assignCost(s, a, 2 * a);
//
//		PlanningAlgorithm alg = new PlanningAlgorithm();
//		Simulator sim = new Simulator(rnd);
		
		// Solve both problems separately without constraints and print expectations
//		System.out.println("=========== UNCONSTRAINED ===========");
//		for(int i=0; i<2; i++) {
//			CMDP cmdp = (i==0) ? cmdpChild : cmdpAdult;
//			Solution sol = alg.solveUnconstrained(new CMDP[]{cmdp});
//			double expectedReward0 = sol.getExpectedReward();
//			double expectedCost0 = sol.getExpectedCost();
//			System.out.println("Expected reward agent "+i+": "+expectedReward0);
//			System.out.println("Expected cost agent "+i+": "+expectedCost0);
//		}
		
		// trivial budget split: invest 10 in each agent
//		System.out.println();
//		System.out.println("=========== SEPARATE PLANNING ===========");
//
//		double expectedReward = 0.0;
//		double expectedCost = 0.0;
//		int budget = 20;
//		for(int i=0; i<2; i++) {
//			CMDP cmdp = (i==0) ? cmdpChild : cmdpAdult;
//			Solution sol = alg.solve(new CMDP[]{cmdp}, budget / 2.0);
//			double expectedReward0 = sol.getExpectedReward();
//			double expectedCost0 = sol.getExpectedCost();
//			System.out.println("Expected reward agent "+i+": "+expectedReward0);
//			System.out.println("Expected cost agent "+i+": "+expectedCost0);
//			expectedReward += expectedReward0;
//			expectedCost += expectedCost0;
//		}
//		System.out.println("Expected reward: "+expectedReward);
//		System.out.println("Expected cost: "+expectedCost);
		
		// This represents the % of children in the tested group
		double[] childrenComposition = new double[]{0.75};
//		double[] childrenComposition = new double[]{0.5};
		PlanningAlgorithm alg = new PlanningAlgorithm();

		for(int index = 0; index < childrenComposition.length; index++) {
			double composition = childrenComposition[index];
			System.out.println("=======================================");
			System.out.println("==========TESTING FOR " + composition + " / " + (1 - composition) + " =====");
			System.out.println("=======================================");

			try {

				FileWriter writer = new FileWriter("/home/andreaalf/Documents/AIDM/AIDM/part3/dataAnalysis/task4/task4_" + index + ".csv");
				writer.write("numAgents,runtime\n");
				for (int nAgents = 2; nAgents <= 50; nAgents += 2) {
					int totalBudget = 10 * nAgents;
					CMDP[] agents = new CMDP[nAgents];

					int i;

					// First we create the children
					for (i = 0; i < (int) Math.round(nAgents * composition); i++) {
						agents[i] = UserGenerator.getCMDPChild();

						for (int s = 0; s < agents[i].getNumStates(); s++)
							for (int a = 0; a < agents[i].getNumActions(); a++)
								agents[i].assignCost(s, a, 2 * a);
					}

					// Then we create the adults
					for (; i < nAgents; i++) {
						agents[i] = UserGenerator.getCMDPAdult();

						for (int s = 0; s < agents[i].getNumStates(); s++)
							for (int a = 0; a < agents[i].getNumActions(); a++)
								agents[i].assignCost(s, a, 2 * a);
					}

					try{
						FutureTask<Solution> task = new FutureTask<>(() -> alg.solve(agents, totalBudget));
						long start = System.currentTimeMillis();
						new Thread(task).start();
						Solution solution = task.get(5L, TimeUnit.MINUTES);
						long end = System.currentTimeMillis();
						System.out.println(nAgents + " agents: " + (end - start) + "ms");
						writer.write(nAgents + "," + (end - start) + "\n");
					}
					catch (InterruptedException e) {
						writer.close();
						System.err.println("Received an interrupted exception");
					}
					catch (ExecutionException e){
						writer.close();
						e.printStackTrace();
					}
					catch (TimeoutException e){
						System.out.println(nAgents + " agents: " + "TIMED OUT	");
						writer.write(nAgents + ",0\n");
					}

//					alg.solve(agents, totalBudget);

//					writer.write(nAgents + "," + (end - start) + "\n");

				}

				writer.close();
			}
			catch (IOException e){
				e.printStackTrace();
			}
		}

//		Solution combinedSolution = alg.solve(new CMDP[]{cmdpChild, cmdpAdult}, budget);
//		System.out.println();
//		System.out.println("=========== MULTI-AGENT PLANNING ===========");
//		System.out.println("Expected reward: "+combinedSolution.getExpectedReward());
//		System.out.println("Expected reward agent 0: "+combinedSolution.getExpectedReward(0));
//		System.out.println("Expected reward agent 1: "+combinedSolution.getExpectedReward(1));
//		System.out.println("Expected cost total: "+combinedSolution.getExpectedCost());
//		System.out.println("Expected cost agent 0: "+combinedSolution.getExpectedCost(0));
//		System.out.println("Expected cost agent 1: "+combinedSolution.getExpectedCost(1));
//
		// simulate
//		sim.simulate(new CMDP[]{cmdpChild, cmdpAdult}, combinedSolution, 10000);

		System.exit(0);
	}
	
	public static void main(String[] args) {
		  task4();
	}
}
