package maze.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import maze.model.Datos;
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

	public Datos explore(Integer nmEpisodes, Integer startState, Integer targetState,  Double porcentaje) throws InterruptedException {
		Datos datos = new Datos();
		this.startPositionCoordinates = maze.getCoordinates(startState);
		this.endPositionCoordinates = maze.getCoordinates(targetState);
		// Inicializa la ventana del mapa.
		this.mf.updateMap(this.maze.getMap(), startPositionCoordinates, startPositionCoordinates,
				endPositionCoordinates);

		// Por cada episodio
		for (Integer episodes = 0; episodes < nmEpisodes; episodes++) {
			
			
			double recompensa_acumulada = 0;
			int long_camino = 0;
			
			Integer currentState = startState;
	
			long inicio = System.nanoTime();		
			// Mientras no se llegue al estado objetivo
			while (!currentState.equals(targetState)) {
				
				
				
				
				// Imprime el mapa.
				this.mf.updateMap(this.maze.getMap(), maze.getCoordinates(currentState), startPositionCoordinates,
						endPositionCoordinates);
				//Thread.sleep(1);

				// Paso 1. Escoger un movimiento.
				
				// Elige la posici�n que obtenga una mayor recompensa a partir del estado actual. //EXPLOTA
				MovePosition movePosition = qTable.getBestRewardPosition(currentState, new ArrayList<MovePosition>());
				
				// Calculamos el estado siguiente
				Integer nextState = null;
				do {
					// 30% Explora nuevos estados
					if (this.randomGenerator.nextDouble() >= porcentaje) { // EXPLORA
						// Elige un movimiento aleatorio
						MovePosition sorted = MovePosition.values()[this.randomGenerator.nextInt(4)];
						
						// El estado sorted debe ser diferente al mejor estado calculado anteriormente
						while (sorted == movePosition) {
							sorted = MovePosition.values()[this.randomGenerator.nextInt(4)];
						}
						
						movePosition = sorted;
					}					
					nextState = maze.move(currentState, movePosition);
					
					// Si es un movimiento no v�lido, se repite el bucle.
				} while (nextState == -1);
				
				
				
				// Obtiene las coordenadas del estado siguiente
				Integer[] targetCoordinates = maze.getCoordinates(nextState);
				
				// Obtiene la recompensa del destino
				Double targetReward = maze.getMap()[targetCoordinates[0]][targetCoordinates[1]] * 1.0;

				// Step 2 (Sets Q-Table reward and move
				// Paso 2. Establece la recompensa y el movimiento en la Q-tabla
				Double reward = qTable.setReward(currentState, nextState, movePosition, targetReward,
						getBestMoveFromTarget(nextState), episodes);
				
				
				recompensa_acumulada += qTable.getReward(currentState, movePosition);
				

				
				
				// Actualiza la ventana de la Q-Tabla
				this.qTableFrame.setQTable(qTable);
				
				// Actualiza el estado actual.
				currentState = nextState;
				long_camino ++;
			}
			
			long fin = System.nanoTime();
			//////////////////////////////////////
			
			System.out.println("Iteracion " + episodes + ":" + (double) ((fin - inicio))/100000);
			System.out.println("Recompensa" +episodes + "=" + (recompensa_acumulada));
			System.out.println("Longitud Camino = " + long_camino);
			System.out.println("-----------------------------");
			datos.addLongitudCaminos(long_camino);
			datos.addTiempo((long) ((fin - inicio))/10000000);
			datos.addRecompensa((recompensa_acumulada));
		}
		
		return datos;
	}
	
	public List<Integer> getPathFinal(Integer currentState, Integer targetState) {
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
			//System.out.println(maze.getPosition(coordinates[0], coordinates[1]) + " " + maze.validateMovement(coordinates[0], coordinates[1]));
		} while (maze.validateMovement(coordinates[0], coordinates[1]).equals(Boolean.FALSE));
		return bestPosition;
	}

}
