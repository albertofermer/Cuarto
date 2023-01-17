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

	public QTable(Integer maxPositions) {
		this.qTable = new QCell[maxPositions];
		for (int i = 0; i < maxPositions; i++) {
			qTable[i] = new QCell(Constantes.NUM_ANGLES); // hay 9 posibles ángulos para controlar el volante
		}
			
		this.randomGenerator = new Random();
	}
	
	public Integer size() {
		return this.qTable.length;
	}

	public Double getReward(Integer source, int movement) {
		return this.qTable[source].getReward(movement);
	}

	/**
	 * Obtiene la posición con mayor recompensa a partir de la posición actual.
	 * @param estado
	 * @param blackList
	 * @return
	 */
	public int getBestRewardPosition(Integer estado) {
		// Escoge un movimiento aleatorio.
		int bestPosition = this.randomGenerator.nextInt(Constantes.NUM_ANGLES);
		// Obtenemos la recompensa actual del mejor movimiento.
		Double bestReward = this.getReward(estado, bestPosition);
		
		// Por cada accion posible.
		for (int accion = 0; accion < Constantes.NUM_ANGLES; accion++) {
			// Se obtiene la recompensa del estado siguiente
			Double reward = getReward(estado, accion);
			
			// Si la recompensa del movimiento es mejor que la del mejor movimiento calculado
			// anteriormente y el movimiento no está contenido en la blacklist
			if (reward > bestReward) {
				// Se actualiza la mejor posición
				bestPosition = accion;
				// Se actualiza la recompensa
				bestReward = reward;
			}
		}
		return bestPosition;
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
	public Double setReward(Integer estado_anterior, Integer estado_actual, Integer accion, Double targetReward,
		Integer targetBestMove) {
		
		Double previuousQ = this.getReward(estado_anterior, accion);
		Double maxCurrentQ = this.getReward(estado_actual, targetBestMove);
		Double learningRate = 0.15;
		Double discountFactor = 0.3;
		
		previuousQ = (1-learningRate)*previuousQ + learningRate*(targetReward + discountFactor * maxCurrentQ);

		this.qTable[estado_anterior].setReward(accion, previuousQ);
		return previuousQ;

	}


	
	public QCell getQCell(Integer position) {
		return this.qTable[position];
	}
	
	public void saveQTable() {
		File qtable = new File("qtable.csv");
		try {
			FileWriter writer = new FileWriter("qtable.csv");
			String cabecera = "";
			cabecera += "Estado \t ";
			for (float value : Constantes.STEER_VALUES) {
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
	
	public void loadQTable() {
		File qtable = new File("qtable.csv");
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
			System.err.println("No existe una tabla definida. Se creará de 0.");
		}
	      
	}

}
