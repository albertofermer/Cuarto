package QLearning;

public class QCell {

	private Double[] rewards = null;
	
	public QCell(int size) {
		
		rewards = new Double[size];
		
		for (int i = 0; i < rewards.length; i++) {
			rewards[i] = 0.0;
		}
	}

	
	public void setReward(int indice, Double reward) {
		this.rewards[indice] = reward;
	}

	public Double getReward(int accion) {
		return rewards[accion];
	}
	
}
