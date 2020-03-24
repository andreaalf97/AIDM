
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//Represents one of two things:
//1) Possible actions and their probabilities of being chosen
//   for the current state (not visible in this class)
//2) Possible destination states and the transition probability to them
//	 P(s',s,a) - again, current state s not visible in this class
public class ProbabilitySample {
	private List<Item> items;
	private Random rnd;
	private double probabilitySum = 0.0;
	
	public ProbabilitySample(Random rnd) {
		items = new ArrayList<Item>();
		this.rnd = rnd;
	}
	
	public void addItem(int item, double probability) {
		assert probability >= 0.0 && probability <= 1.0 : "PROB: "+probability;		
		if(probability > 0.0) {
			items.add(new Item(item,probability));
			probabilitySum += probability;
		}
	}
	
	public int sampleItem() {
		//probability-sum must be less than/equal to 1
		assert Math.abs(probabilitySum-1.0) < 0.001 : "No valid probability distribution: "+probabilitySum;
		assert items.size() > 0 : "No items added";
		
		double cumulative = 0.0;
		double randomNumber = rnd.nextDouble();
		//Initialise with last item of this list, for safety
		int retItem = items.get(items.size()-1).item;
		
		//Stupid random choice algorithm
		for(Item item : items) {
			cumulative += item.probability;
			
			if(randomNumber <= cumulative) {
				retItem = item.item;
				break;
			}
		}
		
		return retItem;
	}
	
	//Item = action + probability of executing that action in current state
	private class Item {
		public int item;
		public double probability;
		
		public Item(int item, double probability) {
			this.item = item;
			this.probability = probability;
		}
	}
}
