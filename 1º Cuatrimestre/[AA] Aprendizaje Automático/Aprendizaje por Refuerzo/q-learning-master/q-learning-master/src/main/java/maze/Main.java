package maze;

import java.util.List;

import maze.controller.MazeController;
import maze.model.Maze;
import maze.model.QTable;
import maze.view.MainFrame;

public class Main {

	private static Integer[][] map = { 
			{ 1000, 1, 1, 1, -100, 1, 1, 1, 1, 1, 1, -100 },
			{ 1, -100, 1, 1, 1, 1, 1, 1, 1, 1, 1, -100 }, 
			{ -100, 1, -100, 1, 1, 1, -100, 1, -100, -100, 1, 1 },
			{ 1, -100, 1, 1, 1, 1, 1, 1, -100, 1, 1, 1 }, 
			{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
			{ 0, 0, 0, 0, 1, 1, -100, 1, 0, 0, 0, 0 }, 
			{ 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0 },
			{ 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0 }, 
			{ 0, 0, 0, 0, 1, 1, -100, 1, 0, 0, 0, 0 },
			{ 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0 }
	};

	public static void main(String[] args) throws InterruptedException {
		// Create Maze and Q-Table
		Maze maze = new Maze(map);
		QTable qTable = new QTable(120);

		// Set start and target Positions
		Integer startState = 112;
		Integer targetState = 0;

		// Make Episodes
		MazeController mazeController = new MazeController(maze, qTable);
		int numEpisodes = 200;
		mazeController.explore(numEpisodes, startState, targetState);
		
		// Print out best path
		List<Integer> path = mazeController.getPath(startState, targetState);
		for (Integer integer : path) {
			System.out.println(integer);
		}
		
		
	}

}
