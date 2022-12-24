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

public class Main {

	private static Integer[][] map = {
			{ Integer.MAX_VALUE, 1, 1, 1, -10000, 1, 1, 1, 1, 1, 1, -10000 },
			{ 1, -10000, 1, 1, 1, 1, 1, 1, 1, 1, 1, -10000 },
			{ -10000, 1, -10000, 1, 1, 1, -10000, 1, -10000,
					-10000, 1, 1 },
			{ 1, -10000, 1, 1, 1, 1, 1, 1, -10000, 1, 1, 1 },
			{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
			{ -10000, -10000, -10000, -10000, 1, 1, -10000, 1,
					-10000, -10000, -10000, -10000 },
			{ -10000, -10000, -10000, -10000, 1, 1, 1, 1, -10000,
					-10000, -10000, -10000 },
			{ -10000, -10000, -10000, -10000, 1, 1, 1, 1, -10000,
					-10000, -10000, -10000 },
			{ -10000, -10000, -10000, -10000, 1, 1, -10000, 1,
					-10000, -10000, -10000, -10000 },
			{ -10000, -10000, -10000, -10000, 1, 1, 1, 1, -10000,
					-10000, -10000, -10000 } };

	public static void main(String[] args) throws InterruptedException {
		// Create Maze and Q-Table
		Maze maze = new Maze(map);
		// Set start and target Positions
		Integer startState = 115;
		Integer targetState = 0;

		// Número de Épocas de Entrenamiento
		int numEpisodes = 500;
		
		// Número de Repeticiones del algoritmo para sacar resultados representativos
		int numRepeticiones = 1;
		
		// Porcentaje de explotación de comienzo del algoritmo
		Double porcentaje_explotacion = 0.0;
		
		ArrayList<Datos> lista_datos = new ArrayList<>();
		Datos datos = new Datos();
		MazeController mazeController = null;
		QTable qTable = null;

		// Algoritmo Q-Learning //
		for(int i = 0; i < numRepeticiones-1; i++) {
			System.out.println("=================> Repetición " + i + " <=================");
			// Inicialización de la Q-Table
			qTable = new QTable(map.length * map[0].length);
			
			// Entrenamiento del algoritmo
			mazeController = new MazeController(maze, qTable, true);
			datos = mazeController.train(numEpisodes, startState, targetState, porcentaje_explotacion);
			
			// Obtención de datos para graficar
			lista_datos.add(datos);
			mazeController.getMainFrame().getFrame().dispose();
			mazeController.getQTableFrame().dispose();
			mazeController.getCaptionFrame().dispose();
		
		}
		
		System.out.println("=================> Repetición " + (numRepeticiones-1) + " <=================");
		qTable = new QTable(map.length * map[0].length);
		mazeController = new MazeController(maze, qTable, true);
		datos = mazeController.train(numEpisodes, startState, targetState, porcentaje_explotacion);
		lista_datos.add(datos);
		
		mazeController.setMapVisible(true);
		
		// Mostramos la Política estado->acción //
		for (int estado = 0 ; estado < (map.length * map[0].length) ; estado++) {
			System.out.println(estado + " -> " + qTable.getBestRewardPosition(estado, new ArrayList<>()));
		}
		
		
		
		
		// Print out best path
		mazeController.getPathFinal(startState, targetState);


		// Calculamos las medias de los datos //
		List<List<Double>> datos_media = media(lista_datos);
		
		
		Plot valores_medios = Plot.create();
		valores_medios.plot().add(logaritmoDouble(datos_media.get(0))).label("Longitud del camino media").linestyle("-")
				.color("red");
		valores_medios.plot().add(escalarDoubleDouble(logaritmoDouble(datos_media.get(0)),datos.getPorcentaje_explotacion())).label("% Exploración").linestyle("-").color("blue");
		valores_medios.plot().add(escalarDoubleDouble(logaritmoDouble(datos_media.get(0)),normalizarDouble(datos_media.get(2)))).label("Recompensa Media").linestyle("-")
		.color("green");

		valores_medios.xlabel("Epochs");
		valores_medios.ylabel("log(Longitud del Camino)");
		valores_medios.title("Evolución de la Longitud media del camino y Recompensa media");
		valores_medios.legend();

		try {
			valores_medios.show();
		} catch (IOException | PythonExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		


	}

	private static List<List<Double>> media(ArrayList<Datos> lista_datos) {
		
		List<List<Double>> datos_media  = new ArrayList<>();
		List<Double> longCamino_media = new ArrayList<>();
		List<Double> tiempo_media = new ArrayList<>();
		List<Double> recompensa_media = new ArrayList<>();
		
		for (int i = 0; i < lista_datos.get(0).getLongitud_caminos().size(); i++) {
			longCamino_media.add(0.0);
			tiempo_media.add(0.0);
			recompensa_media.add(0.0);
		}
		
		
		for (Datos d : lista_datos) {
			
			List<Integer> longCamino_i = d.getLongitud_caminos();
			List<Long> tiempo_i = d.getTiempo();
			List<Double> recompensa_i = d.getRecompensa_acumulada();
			
			for (int i = 0; i < d.getLongitud_caminos().size(); i++) {
				longCamino_media.set(i, longCamino_i.get(i) + longCamino_media.get(i));
				tiempo_media.set(i, tiempo_i.get(i) + tiempo_media.get(i));
				recompensa_media.set(i,recompensa_i.get(i) + recompensa_media.get(i));
			}
			
		}
		
		
		for (int i = 0; i < longCamino_media.size(); i++) {
			
			longCamino_media.set(i, longCamino_media.get(i)/lista_datos.size());
			tiempo_media.set(i, tiempo_media.get(i)/lista_datos.size());
			recompensa_media.set(i,recompensa_media.get(i)/lista_datos.size());
			
		}
		
		System.out.println("Longitud Media del Camino : " + longCamino_media.get(longCamino_media.size()-1));
		datos_media.add(longCamino_media);
		datos_media.add(tiempo_media);
		datos_media.add(recompensa_media);
		
		
		
		return datos_media;
	}

	private static List<Double> escalarDoubleDouble(List<Double> longitud_caminos, List<Double> tiempo) {
		
		double max_long = Double.MIN_VALUE;
		// Calculamos el camino máximo
		for(Double l : longitud_caminos) {
			if(l > max_long) {
				max_long = l;
			}
		}
		
		// Calcuamos el tiempo máximo
		double max_tiempo = Double.MIN_VALUE;
		for(Double t : tiempo) {
			if(t > max_tiempo) {
				max_tiempo = t;
			}
		}
		
		// Constante de proporcionalidad
		double k = max_long/max_tiempo;
		
		List<Double> tiempo_escalado = new ArrayList<>();
		// Aplicar al segundo array
		for (Double t : tiempo) {
			double new_value = t*k;
			tiempo_escalado.add(new_value);
		}
		
		return tiempo_escalado;
	}

	private static List<Double> logaritmoDouble(List<Double> datos) {
		List<Double> log_list = new ArrayList<>();
		for (Double a : datos) {
			double log_value = Math.log(a);
			log_list.add(log_value);
		}

		return log_list;
	}

	
	private static List<Double> logaritmoInt(List<Integer> datos) {
		List<Double> log_list = new ArrayList<>();
		for (Integer a : datos) {
			double log_value = Math.log(a);
			log_list.add(log_value);
		}

		return log_list;
	}

	
	private static List<Double> normalizarDouble(List<Double> datos) {

		List<Double> normalized_list = new ArrayList<>();

		double min = Double.MAX_VALUE;
		for (Double a : datos) {
			if (a < min)
				min = a;
		}

		double max = Double.MIN_VALUE;
		for (Double a : datos) {
			if (a > max)
				max = a;
		}

		for (Double a : datos) {
			double normalized_data = (a - min) / (max - min);
			normalized_list.add(normalized_data);
		}

		return normalized_list;

	}

}
