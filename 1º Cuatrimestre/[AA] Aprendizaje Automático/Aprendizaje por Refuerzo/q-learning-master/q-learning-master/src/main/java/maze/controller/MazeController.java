package maze.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import maze.model.Maze;
import maze.model.MovePosition;
import maze.model.QTable;
import maze.view.MainFrame;
import maze.view.QTableFrame;

public class MazeController {

	private Maze maze;
	private QTable qTable;
	private Random randomGenerator;
	private MainFrame mf;
	private QTableFrame qTableFrame;

	private Integer[] startPositionCoordinates, endPositionCoordinates;

	public MazeController(Maze maze, QTable qTable) {
		this.maze = maze;
		this.qTable = qTable;
		this.randomGenerator = new Random();
		this.mf = new MainFrame();
		this.qTableFrame = new QTableFrame(qTable);
	}

	public void explore(Integer nmEpisodes, Integer startState, Integer targetState) throws InterruptedException {
		this.startPositionCoordinates = maze.getCoordinates(startState);
		this.endPositionCoordinates = maze.getCoordinates(targetState);
		this.mf.updateMap(this.maze.getMap(), startPositionCoordinates, startPositionCoordinates,
				endPositionCoordinates);

		for (Integer episodes = nmEpisodes; episodes > 0; episodes--) {
			Integer currentState = startState;
			while (!currentState.equals(targetState)) {

				// Print out map
				this.mf.updateMap(this.maze.getMap(), maze.getCoordinates(currentState), startPositionCoordinates,
						endPositionCoordinates);
				Thread.sleep(1);

				// Step 1 (Choose a Movement)
				MovePosition movePosition = qTable.getBestRewardPosition(currentState, new ArrayList<MovePosition>());
				Integer nextState = null;
				do {
					if (this.randomGenerator.nextDouble() >= 0.7) {
						MovePosition sorted = MovePosition.values()[this.randomGenerator.nextInt(4)];
						while (sorted == movePosition) {
							sorted = MovePosition.values()[this.randomGenerator.nextInt(4)];
						}
						movePosition = sorted;
					}
					nextState = maze.move(currentState, movePosition);
				} while (nextState == -1);
				Integer[] targetCoordinates = maze.getCoordinates(nextState);
				Double targetReward = maze.getMap()[targetCoordinates[0]][targetCoordinates[1]] * 1.0;

				// Step 2 (Sets Q-Table reward and move)
				Double reward = qTable.setReward(currentState, nextState, movePosition, targetReward,
						getBestMoveFromTarget(nextState));
				this.qTableFrame.setQTable(qTable);
				currentState = nextState;
			}
		}
	}

	public List<Integer> getPath(Integer currentState, Integer targetState) {
		Integer[][] finalMap = this.maze.getMap();
		ArrayList<Integer> path = new ArrayList<Integer>();

		// Walk in Map (goes to best position from Q Table)
		while (!currentState.equals(targetState)) {
			path.add(currentState);
			currentState = maze.move(currentState, getBestMoveFromTarget(currentState));
		}
		path.add(currentState);

		// Print out best path in the Map
		for (Integer integer : path) {
			Integer[] coordinates = this.maze.getCoordinates(integer);
			finalMap[coordinates[0]][coordinates[1]] = -1;
		}
		this.mf.updateMap(finalMap, maze.getCoordinates(currentState), startPositionCoordinates,
				endPositionCoordinates);
		return path;
	}

	private MovePosition getBestMoveFromTarget(Integer currentPosition) {
		MovePosition bestPosition = null;
		ArrayList<MovePosition> invalidMovements = new ArrayList<MovePosition>();
		Integer[] coordinates = null;
		do {
			invalidMovements.add(bestPosition);
			bestPosition = qTable.getBestRewardPosition(currentPosition, invalidMovements);
			coordinates = maze.getCoordinates(maze.move(currentPosition, bestPosition));
		} while (maze.validateMovement(coordinates[0], coordinates[1]).equals(Boolean.FALSE));
		return bestPosition;
	}

}
