package QLearning;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

// QTable para controlar el volante
public class QTable {

	QCell[] qTable;
	Random randomGenerator;
	int num_acciones;
	float[][] acciones;
	
	String nombre = "";

	public QTable(String name, Integer maxPositions, int num_acciones, float[][] acciones) {
		nombre = name;
		this.num_acciones = num_acciones;
		this.acciones = acciones;
		this.qTable = new QCell[maxPositions];
		for (int i = 0; i < maxPositions; i++) {
			qTable[i] = new QCell(num_acciones); // hay 9 posibles �ngulos para controlar el volante
		}
			
		this.randomGenerator = new Random();
	}
	
	public Integer size() {
		return this.qTable.length;
	}

	public Double getReward(Integer estado, int accion) {
		return this.qTable[estado].getReward(accion);
	}

	/**
	 * Obtiene la posici�n con mayor recompensa a partir de la posici�n actual.
	 * @param estado
	 * @param blackList
	 * @return
	 */
	public int getBestRewardPosition(Integer estado) {
		// Escoge un movimiento aleatorio.
		int maxAction = this.randomGenerator.nextInt(num_acciones);
		// Obtenemos la recompensa actual del mejor movimiento.
		Double maxValue = this.getReward(estado, maxAction);
		
		// Elegimos la acci�n que proporciona m�xima recompensa en el estado.
		
		for (int accion = 0; accion < num_acciones; accion++) {
			Double reward = getReward(estado, accion);
			if (reward > maxValue) {
				// Actualizamos la mejor acci�n y mejor valor.
				maxAction = accion;
				maxValue = reward;
			}
		}
		return maxAction;
	}

	/**
	 * Calcula el Q-valor
	 * @param estado_anterior
	 * @param estado_actual
	 * @param accion
	 * @param targetReward
	 * @param targetBestMove
	 * @return
	 */
	public Double setReward(Integer estado_anterior, Integer estado_actual, Integer accion_actual, Integer accion_anterior, Double targetReward,
		Integer targetBestMove) {
		
		Double previuousQ = this.getReward(estado_anterior, accion_anterior);
		Double maxCurrentQ = this.getReward(estado_actual, targetBestMove);
		Double learningRate = 0.15;
		Double discountFactor = 0.3;
		
		previuousQ = (1-learningRate)*previuousQ + learningRate*(targetReward + discountFactor * maxCurrentQ);

		this.qTable[estado_anterior].setReward(accion_anterior, previuousQ);
		return previuousQ;

	}


	
	public QCell getQCell(Integer position) {
		return this.qTable[position];
	}
	
	public void saveQTable(String qtable_name) {
		String directory = "QTable";
		File qtable = new File(directory, qtable_name);
		try {
			System.out.println(qtable.getAbsolutePath() + ".csv");
			FileWriter writer = new FileWriter(qtable.getAbsolutePath() + ".csv");
			
			String cabecera = "";
			cabecera += "Estado \t ";
			for (float[] accion : acciones) {
				for(float value : accion)
					cabecera += Float.toString(value) + "\t";
			}
			cabecera += "\n";
			
			// Escribimos la cabecera Estado - valores_steer
			writer.write(cabecera);
			
			for(int i = 0 ; i < qTable.length; i++) {
				
				writer.write(i+1 + "\t" + qTable[i].toString() + "\n");
				
			}
			
			writer.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void loadQTable(String qtable_name) {
		String directory = "QTable";
		File qtable = new File(directory, qtable_name+".csv");
		Scanner myReader;
		try {
			myReader = new Scanner(qtable);
			if(myReader.hasNextLine()) {
				myReader.nextLine(); // Nos saltamos la primera linea (cabecera)
			}
			while (myReader.hasNextLine()) {
		        String data = myReader.nextLine();
		        String[] datos = data.strip().split("\t");
		        for (int i = 1 ; i < datos.length; i++) {
		        	int estado = Integer.parseInt(datos[0]);
		        	this.getQCell(estado-1).setReward(i-1, Double.parseDouble(datos[i]));
		        	
		        }
		        System.out.println(data);
		      }
		} catch (FileNotFoundException e) {
			System.err.println("No existe una tabla definida. Se creara de 0.");
		}
	      
	}
}