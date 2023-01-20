package Datos;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import QLearning.Constantes;

public class Dato {

	private ArrayList<Double> recompensa_acumulada;
	private ArrayList<Integer> ticks_duracion;
	private ArrayList<Double> longitud_recorrida;
	private ArrayList<Float> angulos_volante;
	private ArrayList<Double> posicion_carretera;
	private ArrayList<Double> epsilon;

	public Dato() {
		recompensa_acumulada = new ArrayList<>();
		ticks_duracion = new ArrayList<>();
		longitud_recorrida = new ArrayList<>();
		angulos_volante = new ArrayList<>();
		posicion_carretera = new ArrayList<>();
		epsilon = new ArrayList<>();
	}

	public void addRecompensa(Double recompensa) {
		this.recompensa_acumulada.add(recompensa);
	}

	public void addTick(Integer tick) {
		this.ticks_duracion.add(tick);
	}

	public void addLongitudRecorrida(Double longitud) {
		this.longitud_recorrida.add(longitud);
	}

	public void addAnguloVolante(float steer_angle) {
		this.angulos_volante.add(steer_angle);
	}

	public void addTrackPosition(Double tp) {
		this.posicion_carretera.add(tp);
	}

	public void addEpsilon(Double epsilon) {
		this.epsilon.add(epsilon);
	}
	
	public void write(String file_name) {
		// Escribe los angulos del volante en columnas para representarlo posteriormente
		String str = "";
		for(int i = 0; i<angulos_volante.size(); i++) {
			str += i + ";"; // Tick
			str += angulos_volante.get(i) + ";";
			str += posicion_carretera.get(i) + ";";
			
			str += "\n"; // Salto de linea
		}
		
		try {
			FileWriter writer = new FileWriter(file_name+".csv");
			writer.write(str);
			writer.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
