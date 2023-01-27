package QLearning;

public class QCell {

	private Double[] rewards = null;
	
	public QCell(int size) {
		
		rewards = new Double[size];
		
		for (int i = 0; i < rewards.length; i++) {
			rewards[i] = -1.0;
		}
	}

	
	public void setReward(int indice, Double reward) {
		this.rewards[indice] = reward;
	}

	public Double getReward(int accion) {
		return rewards[accion];
	}
	
	@Override
	public String toString() {
		
		String str = "";
		for (double reward : rewards) {
			str += Double.toString(reward) + " \t ";
		}
		
		return str;
	}
}
