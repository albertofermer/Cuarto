package maze;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.sh0nk.matplotlib4j.Plot;
import com.github.sh0nk.matplotlib4j.PlotImpl;
import com.github.sh0nk.matplotlib4j.PythonExecutionException;
import com.github.sh0nk.matplotlib4j.builder.HistBuilder.Orientation;

import maze.controller.MazeController;
import maze.model.Datos;
import maze.model.Maze;
import maze.model.QTable;
import maze.view.MainFrame;

public class Main {

	
    private static Integer[][] map = {
            {1, 1, 1, 1},
            {1, 1, Integer.MIN_VALUE, 1},
            {1, Integer.MIN_VALUE,1, 1, 1},
            {1, -10000, 1, 1, 1},
            {Integer.MIN_VALUE ,1,  100000, 1}
        };
//	private static Integer[][] map = { 
//			{ 1000, 1, 1, 1, -100, 1, 1, 1, 1, 1, 1, -100 },
//			{ 1, -100, 1, 1, 1, 1, 1, 1, 1, 1, 1, -100 }, 
//			{ -100, 1, -100, 1, 1, 1, -100, 1, -100, -100, 1, 1 },
//			{ 1, -100, 1, 1, 1, 1, 1, 1, -100, 1, 1, 1 }, 
//			{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
//			{ 0, 0, 0, 0, 1, 1, -100, 1, 0, 0, 0, 0 }, 
//			{ 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0 },
//			{ 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0 }, 
//			{ 0, 0, 0, 0, 1, 1, -100, 1, 0, 0, 0, 0 },
//			{ 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0 }
//	};

	public static void main(String[] args) throws InterruptedException {
		// Create Maze and Q-Table
		Maze maze = new Maze(map);
		QTable qTable = new QTable(20);

		// Set start and target Positions
		Integer startState = 0;
		Integer targetState = 18;

		// Make Episodes
		MazeController mazeController = new MazeController(maze, qTable);
		int numEpisodes = 2500;
		
		Double porcentaje_explotacion = 0.99;
			long inicio = System.currentTimeMillis();
			Datos datos = mazeController.explore(numEpisodes, startState, targetState,porcentaje_explotacion);
			long fin = System.currentTimeMillis();
			System.out.println("Tiempo " + ":" + (double) ((fin - inicio)));
			
		
		// Print out best path
		List<Integer> path = mazeController.getPathFinal(startState, targetState);
		for (Integer integer : path) {
			System.out.println(integer);
		}
		
		
//		Plot plt2 = Plot.create();
//		plt2.plot().add(listEpisodes(numEpisodes), datos.getLongitud_caminos(), ".r").label("sin");
//		plt2.legend().loc("upper right");
//		plt2.title("scatter");
//		try {
//			plt2.show();
//		} catch (IOException | PythonExecutionException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
		
		// Evolución del Camino //
		Plot evolucion_camino = Plot.create();
		evolucion_camino.plot()
		    .add(datos.getLongitud_caminos())
		    .label("Longitud del camino")
		    .linestyle("-")
		    .color("red");
		
//		evolucion_camino.plot()
//			.add(datos.getRecompensa_acumulada())
//			.label("Recompensa Acumulada")
//			.linestyle("-")
//			.color("green");
		
		evolucion_camino.xlabel("Epochs");
		evolucion_camino.ylabel("Longitud del Camino");
		evolucion_camino.title("Evolución de la Longitud del Camino");
		evolucion_camino.legend();
		
		
		try {
			evolucion_camino.show();
		} catch (IOException | PythonExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Plot tiempo = Plot.create();

		tiempo.plot()
			.add(datos.getTiempo())
			.label("Tiempo")
			.linestyle("-")
			.color("blue");
		
		tiempo.xlabel("Epochs");
		tiempo.ylabel("Tiempo");
		tiempo.title("Evolución del tiempo en encontrar la salida");
		tiempo.legend();
		
		
		try {
			tiempo.show();
		} catch (IOException | PythonExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		Plot recompensa = Plot.create();

		recompensa.plot()
			.add(datos.getRecompensa_acumulada())
			.label("Recompensa")
			.linestyle("-")
			.color("green");
		
		recompensa.xlabel("Epochs");
		recompensa.ylabel("Recompensa");
		recompensa.title("Recompensa acumulada en cada epoch");
		recompensa.legend();
		
		
		try {
			recompensa.show();
		} catch (IOException | PythonExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
//		Plot plt = Plot.create();
//		plt.hist().add(datos.getLongitud_caminos()).orientation(Orientation.vertical);
//		plt.xlim(0, numEpisodes);
//		plt.title("histogram");		
//		try {
//			plt.show();
//		} catch (IOException | PythonExecutionException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		
	}
private static List<? extends Number> listEpisodes(int numEpisodes) {
	List<Integer> eje = new ArrayList<>();
	for (int i = 0; i < numEpisodes; i++) {
		eje.add(i);
	}
	return eje;
}

}
