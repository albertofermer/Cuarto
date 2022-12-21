package maze.model;

import java.util.List;
import java.util.Random;

public class QTable {

	QCell[] qTable;
	Random randomGenerator;

	public QTable(Integer maxPositions) {
		this.qTable = new QCell[maxPositions];
		for (int i = 0; i < maxPositions; i++) {
			qTable[i] = new QCell();
		}
		this.randomGenerator = new Random();
	}
	
	public Integer size() {
		return this.qTable.length;
	}

	public Double getReward(Integer source, MovePosition movement) {
		return this.qTable[source].getReward(movement);
	}

	/**
	 * Obtiene la posición con mayor recompensa a partir de la posición actual.
	 * @param source
	 * @param blackList
	 * @return
	 */
	public MovePosition getBestRewardPosition(Integer source, List<MovePosition> blackList) {
		// Escoge un movimiento aleatorio.
		MovePosition bestPosition = MovePosition.values()[this.randomGenerator.nextInt(4)];
		// Obtenemos la recompensa actual del mejor movimiento.
		Double bestReward = this.getReward(source, bestPosition);
		
		// Por cada movimiento posible.
		for (MovePosition move : MovePosition.values()) {
			// Se obtiene la recompensa del estado siguiente
			Double reward = getReward(source, move);
			
			// Si la recompensa del movimiento es mejor que la del mejor movimiento calculado
			// anteriormente y el movimiento no está contenido en la blacklist
			if (reward > bestReward && !blackList.contains(move)) {
				// Se actualiza la mejor posición
				bestPosition = move;
				// Se actualiza la recompensa
				bestReward = reward;
			}
		}
		return bestPosition;
	}

	/**
	 * Calcula el Q-valor
	 * @param source
	 * @param target
	 * @param move
	 * @param targetReward
	 * @param targetBestMove
	 * @return
	 */
	public Double setReward(Integer source, Integer target, MovePosition move, Double targetReward,
		MovePosition targetBestMove, Integer episode) {
		
		Double currentQ = this.getReward(source, move);
		Double maxFutureQ = this.getReward(target, targetBestMove);
		Double learningRate = 0.5;
		Double discountFactor = 0.1;
		
		currentQ = (1-learningRate)*currentQ + learningRate*(targetReward + discountFactor * maxFutureQ);

		switch (move) {
		case UP: {
			this.qTable[source].setUpReward(currentQ);
			break;
		}
		case DOWN: {
			this.qTable[source].setBelowReward(currentQ);
			break;
		}
		case LEFT: {
			this.qTable[source].setLeftReward(currentQ);
			break;
		}
		case RIGHT: {
			this.qTable[source].setRightReward(currentQ);
			break;
		}
		}
		return currentQ;

	}

	public void printTable() {
		for (int i = 0; i < this.qTable.length; i++) {
			System.out.println(
					"POSITION " + i + " , TOP: " + qTable[i].getUpReward() + " , DOWN: " + qTable[i].getBelowReward()
							+ " , LEFT: " + qTable[i].getLeftReward() + " , RIGHT: " + qTable[i].getRightReward());
		}
	}
	
	public QCell getQCell(Integer position) {
		return this.qTable[position];
	}

}
