package Datos;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import QLearning.Constantes;

public class Dato {

	private Double longitud_recorrida;
	private Double posicion_carretera;
	private Double epsilon;
	private Double distancia_punto_comienzo;
	private Integer ticks_duracion;
	private Integer indice_carrera;
	private Float angulo_volante;

	public Dato() {
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

	public void writeHeader(String file_name) {
		String str = "#CARRERA;TICK;EPSILON;DIST_RACED"
				+ "\n";
		try {
			File file = new File("Datos", file_name);
			System.out.println(file.getAbsolutePath());
			BufferedWriter writer = new BufferedWriter(new FileWriter(file.getAbsolutePath() + ".csv"));
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
		str += angulo_volante + ";";
		str += posicion_carretera + ";";
		str += epsilon + ";";
		str += longitud_recorrida + ";";
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

	public void writeDistRaced(String file_name) {
		// Escribe los angulos del volante en columnas para representarlo posteriormente
		String str = "";
		str += indice_carrera + ";";
		str += ticks_duracion + ";";
		str += epsilon + ";";
		str += longitud_recorrida;
		str += "\n";
		
		String directory = "Datos";
		try {
			File file = new File(directory, file_name);
			BufferedWriter writer = new BufferedWriter(new FileWriter("file_name.csv", true));
			writer.append(str);
			writer.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
