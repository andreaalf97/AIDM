import java.util.Random;


public class Simulator {
	private Random rnd;
	private boolean printActions = false;
	
	public Simulator(Random rnd) {
		this.rnd = rnd;
	}

	public void printActions() {
		this.printActions = true;
	}
	
	public void simulate(CMDP[] cmdps, Solution solution, int numRuns) {
		double meanReward = 0.0;
		double meanRewardTest = 0.0;
		double meanCost = 0.0;
		double meanCostTest = 0.0;
		
		for(int run=0; run<numRuns; run++) {
			double runReward = 0.0;
			double runCost = 0.0;
			
			//for each agent
			for(int i=0; i<cmdps.length; i++) {
				//get agent
				CMDP cmdp = cmdps[i];
				//get policy of agent
				double[][] policy = solution.getPolicy(i);
				//number of steps depend on the discount factor g??
				int numSteps = (int) Math.ceil(Math.log(0.00000001) / Math.log(cmdp.getDiscountFactor()));
				
				int state = cmdp.getInitialState();
				for(int step=0; step<numSteps; step++) {
					// select random action based on policy (e.g. a1=0.2 a2=0.7 a3=0.1)
					ProbabilitySample ps = new ProbabilitySample(rnd);
					for(int a=0; a<cmdp.getNumActions(); a++) {
						ps.addItem(a, policy[state][a]);
					}
					int a = ps.sampleItem();
					
					// get reward
					// R(s,a) * g ^ step
					double r = cmdp.getReward(state, a) * Math.pow(cmdp.getDiscountFactor(), ((double) step));
					runReward += r;
					
					if(printActions) System.out.println("Step "+step+": state "+state+", execute "+a+", get reward "+r);
					
					// get cost
					// C(s,a) * g ^ step
					double c = cmdp.getCost(state, a) * Math.pow(cmdp.getDiscountFactor(), ((double) step));
					runCost += c;
					
					// transition to next state
					// select random next state based on transition probability P(s',s,a)
					ps = new ProbabilitySample(rnd);
					for(int sNext=0; sNext<cmdp.getNumStates(); sNext++) {
						ps.addItem(sNext, cmdp.getTransitionProbability(state, a, sNext));
					}
					state = ps.sampleItem();
				}
			}
			
			meanReward = ((meanReward * ((double) run)) + runReward) / ((double) (run+1));
			meanRewardTest += runReward;
			meanCost = ((meanCost * ((double) run)) + runCost) / ((double) (run+1));
			meanCostTest += runCost;
		}
		meanRewardTest = meanRewardTest / numRuns;
		meanCostTest = meanCostTest / numRuns;
		
		System.out.println("Mean reward: "+meanReward);
		System.out.println("Mean reward Test: "+meanRewardTest);
		System.out.println("Mean cost: "+meanCost);
		System.out.println("Mean cost Test: "+meanCostTest);
		
	}
}
