package Datos;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import QLearning.Constantes;

public class Dato {

	private Double longitud_recorrida;
	private Double posicion_carretera;
	private Double epsilon;
	private Double distancia_punto_comienzo;
	private Double tiempo_vuelta;
	private Integer ticks_duracion;
	private Integer indice_carrera;
	private Float angulo_volante;
	private Double max_speed;
	private boolean finished_lap = false;
	private ArrayList<HashMap<Integer, Integer>> Dict_AccionValor;
	

	public Dato(Integer num_estados, Integer num_acciones) {
		Dict_AccionValor = new ArrayList<>(num_estados);
		for (int i = 0 ; i < num_estados; i++) {
			HashMap<Integer, Integer> hm = new HashMap<>();
			for(int j = 0; j < num_acciones; j++) {
				hm.put(j, 0);
			}
			Dict_AccionValor.add(hm);
		}
	}
	
	public void addAccionValor(Integer estado, Integer accion) {
		
		Integer value = Dict_AccionValor.get(estado).get(accion);
		Dict_AccionValor.get(estado).replace(accion, value+1);
		
	}
	
	public void setMaxSpeed(double max_speed) {
		this.max_speed = max_speed;
	}
	public void setFinishedLap(boolean bool) {
		finished_lap = bool;
	}

	public void setLongitud_recorrida(Double longitud_recorrida) {
		this.longitud_recorrida = longitud_recorrida;
	}

	public void setPosicion_carretera(Double posicion_carretera) {
		this.posicion_carretera = posicion_carretera;
	}

	public void setEpsilon(Double epsilon) {
		this.epsilon = epsilon;
	}

	public void setDistancia_punto_comienzo(Double distancia_punto_comienzo) {
		this.distancia_punto_comienzo = distancia_punto_comienzo;
	}

	public void setTicks_duracion(Integer ticks_duracion) {
		this.ticks_duracion = ticks_duracion;
	}

	public void setIndice_carrera(Integer indice_carrera) {
		this.indice_carrera = indice_carrera;
	}

	public void setAngulo_volante(Float angulo_volante) {
		this.angulo_volante = angulo_volante;
	}
	
	public void setTiempo_vuelta(Double tiempo_vuelta) {
		this.tiempo_vuelta = tiempo_vuelta;
	}

	public void writeHeader(String file_name) {
		String str = "#CARRERA;TICK;TIEMPO_VUELTA;EPSILON;DIST_RACED;DIST_STARTPOINT;MAX_SPEED;FINISHED_LAP"
				+ "\n";
		try {
			File file = new File("Datos", file_name);
			BufferedWriter writer = new BufferedWriter(new FileWriter(file.getAbsolutePath() + ".csv"));
			writer.append(str);
			writer.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	public void write_vel(String file_name) {
		// Escribe los angulos del volante en columnas para representarlo posteriormente
		String str = "";
		str += indice_carrera + ";";
		str += ticks_duracion + ";";
		str += tiempo_vuelta + ";";
		str += epsilon + ";";
		str += longitud_recorrida + ";";
		str += distancia_punto_comienzo + ";";
		str += max_speed + ";";
		str += finished_lap + ";";
		str += "\n";

		try {
			File file = new File("Datos",file_name);
			BufferedWriter writer = new BufferedWriter(new FileWriter(file.getAbsolutePath()+ ".csv", true));
			writer.append(str);
			writer.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void write(String file_name) {
		// Escribe los angulos del volante en columnas para representarlo posteriormente
		String str = "";
		str += indice_carrera + ";";
		str += ticks_duracion + ";";
		str += tiempo_vuelta + ";";
		str += epsilon + ";";
		str += longitud_recorrida + ";";
		
		str += angulo_volante + ";";
		str += posicion_carretera + ";";
		str += distancia_punto_comienzo + ";";
		str += "\n";

		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(file_name + ".csv", true));
			writer.append(str);
			writer.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	public void writeActUse(String file_name) {
		String str = "";
		str += Dict_AccionValor + "\n";
		String directory = "Datos";
		try {
			File file = new File(directory, file_name);
			BufferedWriter writer = new BufferedWriter(new FileWriter(file.getAbsolutePath()+".csv", true));
			writer.append(str);
			System.out.println(str);
			writer.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void writeDistRaced(String file_name) {
		// Escribe los angulos del volante en columnas para representarlo posteriormente
		String str = "";
		str += indice_carrera + ";";
		str += ticks_duracion + ";";
		str += tiempo_vuelta + ";";
		str += epsilon + ";";
		str += longitud_recorrida;
		str += "\n";
		
		String directory = "Datos";
		try {
			File file = new File(directory, file_name);
			BufferedWriter writer = new BufferedWriter(new FileWriter(file_name+".csv", true));
			writer.append(str);
			writer.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
