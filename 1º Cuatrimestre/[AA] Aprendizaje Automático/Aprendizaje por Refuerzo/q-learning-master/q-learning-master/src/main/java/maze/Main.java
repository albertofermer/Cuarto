package maze;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.github.sh0nk.matplotlib4j.Plot;
import com.github.sh0nk.matplotlib4j.PythonExecutionException;

import maze.controller.MazeController;
import maze.model.Datos;
import maze.model.Maze;
import maze.model.QTable;

import java.io.FileWriter;

public class Main {

	
//    private static Integer[][] map = {
//            {Integer.MAX_VALUE, 1, 1, 1},
//            {1, 1, Integer.MIN_VALUE, 1},
//            {1, Integer.MIN_VALUE,1, 1, 1},
//            {1, Integer.MIN_VALUE, 1, 1, 1},
//            {Integer.MIN_VALUE ,1,  1, 1}
//        };
	private static Integer[][] map = { 
			{ Integer.MAX_VALUE, 1, 1, 1, Integer.MIN_VALUE, 1, 1, 1, 1, 1, 1, Integer.MIN_VALUE },
			{ 1, Integer.MIN_VALUE, 1, 1, 1, 1, 1, 1, 1, 1, 1, Integer.MIN_VALUE }, 
			{ Integer.MIN_VALUE, 1, Integer.MIN_VALUE, 1, 1, 1, Integer.MIN_VALUE, 1, Integer.MIN_VALUE, Integer.MIN_VALUE, 1, 1 },
			{ 1, Integer.MIN_VALUE, 1, 1, 1, 1, 1, 1, Integer.MIN_VALUE, 1, 1, 1 }, 
			{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
			{ Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, 1, 1, Integer.MIN_VALUE, 1, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE }, 
			{ Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, 1, 1, 1, 1, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE },
			{ Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, 1, 1, 1, 1, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE }, 
			{ Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, 1, 1, Integer.MIN_VALUE, 1, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE },
			{ Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, 1, 1, 1, 1, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE }
	};
	public static void main(String[] args) throws InterruptedException {
		// Create Maze and Q-Table
		Maze maze = new Maze(map);
		QTable qTable = new QTable(map.length*map[0].length);
		// Set start and target Positions
		Integer startState = 115;
		Integer targetState = 0;

		// Make Episodes
		MazeController mazeController = new MazeController(maze, qTable);
		int numEpisodes = 200;
		
		Double porcentaje_explotacion = 0.7;
			long inicio = System.currentTimeMillis();
			Datos datos = mazeController.explore(numEpisodes, startState, targetState,porcentaje_explotacion);
			long fin = System.currentTimeMillis();
			System.out.println("Tiempo " + ":" + (double) ((fin - inicio)));
			
		
		// Print out best path
		List<Integer> path = mazeController.getPathFinal(startState, targetState);
		for (Integer integer : path) {
			System.out.println(integer);
		}
		
//		try {
//			writeCSV(normalizar(datos.getRecompensa_acumulada()));
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		
		
		
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
		    .add((datos.getLongitud_caminos()))
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
			.add((datos.getTiempo()))
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
			.add(normalizarDouble(datos.getRecompensa_acumulada()))
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

private static List<? extends Number> normalizarLong(List<Long> datos) {
	double media = 0;
	double std = 0;
	List<Double> normalized_list = new ArrayList<>();

	for (Long a : datos){
		media += a;
	}
	
	media = media/datos.size();
	
	//System.out.println("mean: " + media);
	
	for (Long a : datos){
		std += Math.pow((a - media),2);
	}
	
	std = Math.sqrt(std/datos.size());
	
	//System.out.println("std: " + std);
	long min = Integer.MAX_VALUE;
	for (Long a : datos){
		if (a < min)
			min = a;
	}
	
	long max = Integer.MIN_VALUE;
	for (Long a : datos){
		if (a > max)
			max = a;
	}
	
	
	
	for (Long a : datos) {
//		double normalized_data = (a - media);
//		normalized_data = normalized_data/std;
//		normalized_list.add(normalized_data);
		
		double normalized_data = (a - min)/(max-min);
		normalized_list.add(normalized_data);
	}
	
	return normalized_list;
	
	
	
}

private static void writeCSV(List<Double> datos) throws IOException {

	FileWriter writer = new FileWriter("C:\\Users\\afmhu\\Desktop\\output.txt"); 
	for(Double d: datos) {
	  writer.write(d.toString() + ",");
	}
	writer.close();
}
private static List<? extends Number> listEpisodes(int numEpisodes) {
	List<Integer> eje = new ArrayList<>();
	for (int i = 0; i < numEpisodes; i++) {
		eje.add(i);
	}
	return eje;
}

private static List<Double> normalizarDouble(List<Double> datos){
	
	double media = 0;
	double std = 0;
	List<Double> normalized_list = new ArrayList<>();

	for (Double a : datos){
		media += a;
	}
	
	media = media/datos.size();
	
	//System.out.println("mean: " + media);
	
	for (Double a : datos){
		std += Math.pow((a - media),2);
	}
	
	std = Math.sqrt(std/datos.size());
	
	//System.out.println("std: " + std);
	double min = Double.MAX_VALUE;
	for (Double a : datos){
		if (a < min)
			min = a;
	}
	
	double max = Double.MIN_VALUE;
	for (Double a : datos){
		if (a > max)
			max = a;
	}
	
	
	
	for (Double a : datos) {
//		double normalized_data = (a - media);
//		normalized_data = normalized_data/std;
//		normalized_list.add(normalized_data);
		
		double normalized_data = (a - min)/(max-min);
		normalized_list.add(normalized_data);
	}
	
	return normalized_list;
	
	
	
	
}

}
