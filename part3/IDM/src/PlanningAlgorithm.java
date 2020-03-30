import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;


import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.linear.LinearConstraint;
import org.apache.commons.math3.optim.linear.LinearConstraintSet;
import org.apache.commons.math3.optim.linear.LinearObjectiveFunction;
import org.apache.commons.math3.optim.linear.Relationship;
import org.apache.commons.math3.optim.linear.SimplexSolver;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;


public class PlanningAlgorithm {
	
	public PlanningAlgorithm() {
		
	}
	
	public Solution solveUnconstrained(CMDP[] cmdps) {
		return solve(cmdps, Double.MAX_VALUE);
	}
	
	public Solution solve(CMDP[] cmdps, double costLimit) {
		int nAgents = cmdps.length;
		
		// assign variable IDs to state-action pairs
		int[][][] varIDs = new int[nAgents][][];
		int varCounter = 0;
		for(int i=0; i<nAgents; i++) {
			varIDs[i] = new int[cmdps[i].getNumStates()][cmdps[i].getNumActions()];
			for(int s=0; s<cmdps[i].getNumStates(); s++) {
				for(int a=0; a<cmdps[i].getNumActions(); a++) {
					varIDs[i][s][a] = varCounter;
					varCounter++;
				}
			}
		}
		int numVars = varCounter;
		
		// create objective function
		RealVector objectiveCoefficients = new ArrayRealVector(numVars);
		for(int i=0; i<nAgents; i++) {
			CMDP cmdp = cmdps[i];
			
			for(int s=0; s<cmdp.getNumStates(); s++) {
				for(int a=0; a<cmdp.getNumActions(); a++) {
					objectiveCoefficients.setEntry(varIDs[i][s][a], cmdps[i].getReward(s, a));
				}
			}
		}
		LinearObjectiveFunction objectiveFunction = new LinearObjectiveFunction(objectiveCoefficients, 0);
		
		// create constraint set
		Collection<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
		
		// add flow conservation constraints
		for(int i=0; i<nAgents; i++) {
			CMDP cmdp = cmdps[i];
			
			for(int sNext=0; sNext<cmdp.getNumStates(); sNext++) {
				RealVector lhs = new ArrayRealVector(numVars);
				
				for(int aPrime=0; aPrime<cmdp.getNumActions(); aPrime++) {
					lhs.setEntry(varIDs[i][sNext][aPrime], 1.0);
				}
				
				for(int s=0; s<cmdp.getNumStates(); s++) {
					for(int a=0; a<cmdp.getNumActions(); a++) {
						lhs.addToEntry(varIDs[i][s][a], -1.0 * (cmdp.getDiscountFactor() * cmdp.getTransitionProbability(s, a, sNext)));
					}
				}
				
				double rhs = cmdp.getInitialState() == sNext ? 1.0 : 0.0;
				LinearConstraint constr = new LinearConstraint(lhs, Relationship.EQ, rhs);
				constraints.add(constr);
			}
		}
		
		// add cost constraint
		RealVector costLHS = new ArrayRealVector(numVars);
		for(int i=0; i<nAgents; i++) {
			CMDP cmdp = cmdps[i];
			
			for(int s=0; s<cmdp.getNumStates(); s++) {
				for(int a=0; a<cmdp.getNumActions(); a++) {
					costLHS.setEntry(varIDs[i][s][a], cmdp.getCost(s, a));
				}
			}
		}
		LinearConstraint costConstraint = new LinearConstraint(costLHS, Relationship.LEQ, costLimit);
		constraints.add(costConstraint);
		
		// add non-negative constraints
		for(int i=0; i<nAgents; i++) {
			CMDP cmdp = cmdps[i];
			
			for(int s=0; s<cmdp.getNumStates(); s++) {
				for(int a=0; a<cmdp.getNumActions(); a++) {
					RealVector lhs = new ArrayRealVector(numVars);
					lhs.setEntry(varIDs[i][s][a], 1.0);
					LinearConstraint constr = new LinearConstraint(lhs, Relationship.GEQ, 0.0);
					constraints.add(constr);
				}
			}	
		}
		
		// solve the problem using simplex
		SimplexSolver solver = new SimplexSolver();
		LinearConstraintSet lcs = new LinearConstraintSet(constraints);
		PointValuePair solution = solver.optimize(objectiveFunction, lcs, GoalType.MAXIMIZE);
		
		// compute expected reward and cost
		double expectedReward = 0.0;
		double expectedCost = 0.0;
		double[] expectedRewardAgent = new double[nAgents];
		double[] expectedCostAgent = new double[nAgents];
		for(int i=0; i<nAgents; i++) {
			CMDP cmdp = cmdps[i];
			
			for(int s=0; s<cmdp.getNumStates(); s++) {
				for(int a=0; a<cmdp.getNumActions(); a++) {
					int varID = varIDs[i][s][a];
					double flow = solution.getPoint()[varID];
					expectedReward += flow * cmdp.getReward(s, a);
					expectedCost += flow * cmdp.getCost(s, a);
					expectedRewardAgent[i] += flow * cmdp.getReward(s, a);
					expectedCostAgent[i] += flow * cmdp.getCost(s, a);
				}
			}
		}
		
		// get solution
		double[] solutionValues = new double[numVars];
		for(int v=0; v<numVars; v++) {
			solutionValues[v] = solution.getPoint()[v];
			
			if(Math.abs(solutionValues[v]) < 0.00000001) {
				solutionValues[v] = 0.0;
			}
		}
		
		// construct policy
		double[][][] policy = new double[nAgents][][];
		for(int i=0; i<nAgents; i++) {
			CMDP cmdp = cmdps[i];
			
			policy[i] = new double[cmdp.getNumStates()][cmdp.getNumActions()];
			
			for(int s=0; s<cmdp.getNumStates(); s++) {

				double divisor = 0.0;
				for(int aPrime=0; aPrime<cmdp.getNumActions(); aPrime++) {
					int varID = varIDs[i][s][aPrime];
					divisor += solutionValues[varID];
				}

				for(int a=0; a<cmdp.getNumActions(); a++) {
					int varID = varIDs[i][s][a];
					policy[i][s][a] = solutionValues[varID] / divisor;
				}
			}
			
		}


		
		ArrayList<double[][]> policies = new ArrayList<double[][]>();
		for(int i=0; i<nAgents; i++) {
			policies.add(policy[i]);
		}
		
		return new Solution(policies, expectedReward, expectedCost, expectedRewardAgent, expectedCostAgent);
	}
	
	public Solution solveVI(CMDP[] cmdps) {
		CMDP cmdp = cmdps[0];
		
		double[] V = new double[cmdp.getNumStates()];
		int nStates = cmdp.getNumStates();
		int nActions = cmdp.getNumActions();
		double e = 0.00005;
		
		// TODO compute an optimal value function for the cmdp object
		// Check out slide #25 of the first lecture for this algorithm
		double[][] Q = new double[nStates][nActions];
		double d;
		do {
			d = 0;
			for(int s=0; s<nStates; s++) {
				for(int a=0; a<nActions; a++) {
					
					double sum = 0;
					for(int sNext=0; sNext<nStates; sNext++) {
						if (cmdp.getTransitionProbability(s, a, sNext) > 0){
							
							double max_Q_nextStep = 0;
							for(int aNext=0; aNext<nActions; aNext++) {
								if (Q[sNext][aNext] > max_Q_nextStep)
									max_Q_nextStep = Q[sNext][aNext];
							}
							sum += cmdp.getTransitionProbability(s, a, sNext) * max_Q_nextStep;
						}
					}
					
					double oldQ = Q[s][a];
					Q[s][a] = cmdp.getReward(s, a) + cmdp.getDiscountFactor() * sum;
					d = Double.max(d, Math.abs(Q[s][a] - oldQ));
					
				}
			}
			
		} while (d >= e);


		//Print Q
		System.out.println("Q - VALUE FUNCTION");
		System.out.println("--------------------------------");
		for(int s=0; s<nStates; s++) {
			if (s < 10)	System.out.print("state " + s + "  :");
			else System.out.print("state " + s + " :");
			for(int a=0; a<nActions; a++) {
				System.out.printf("%6.2f ", Q[s][a]);
			}
			System.out.println();
		}
		System.out.println();

		for(double v : V)
			System.out.println(v);
		
		
		double[][] policy = new double[cmdp.getNumStates()][cmdp.getNumActions()];
		
		// TODO fill the policy array with probabilities
		for(int s=0; s<nStates; s++) {
			 double maxUtil = 0;
			 int chosen_action = 0;
			 for(int a=0; a<nActions; a++) {
				 if (Q[s][a] > maxUtil){
					 maxUtil = Q[s][a];
					 chosen_action = a;
				 }
			 }
			 policy[s][chosen_action] = 1;
			 V[s] = maxUtil;
		}
		
		ArrayList<double[][]> policies = new ArrayList<double[][]>();
		policies.add(policy);
		
		double expectedReward = V[cmdp.getInitialState()];
		
		return new Solution(policies, expectedReward, 0.0, new double[]{expectedReward}, new double[1]);
	}
}
