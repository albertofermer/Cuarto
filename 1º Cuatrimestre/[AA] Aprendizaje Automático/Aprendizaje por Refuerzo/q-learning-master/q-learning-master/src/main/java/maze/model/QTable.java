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

	public MovePosition getBestRewardPosition(Integer source, List<MovePosition> blackList) {
		MovePosition bestPosition = MovePosition.values()[this.randomGenerator.nextInt(4)];
		Double bestReward = this.getReward(source, bestPosition);
		for (MovePosition move : MovePosition.values()) {
			Double reward = getReward(source, move);
			if (reward > bestReward && !blackList.contains(move)) {
				bestPosition = move;
				bestReward = reward;
			}
		}
		return bestPosition;
	}

	public Double setReward(Integer source, Integer target, MovePosition move, Double targetReward,
			MovePosition targetBestMove) {
		Double currentQ = this.getReward(source, move);
		Double maxFutureQ = this.getReward(target, targetBestMove);
		Double learningRate = 0.5;
		Double discountFactor = 0.1;
		currentQ = currentQ + learningRate * (targetReward + discountFactor * maxFutureQ - currentQ);

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
