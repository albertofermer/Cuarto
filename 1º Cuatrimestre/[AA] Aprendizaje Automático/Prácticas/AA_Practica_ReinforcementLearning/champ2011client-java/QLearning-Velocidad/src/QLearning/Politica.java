package QLearning;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Politica {

	ArrayList<float[]> accion = new ArrayList<>();
	
	public void loadPolitica(String politica_name) {
		String directory = "Politicas";
		File file = new File(directory, politica_name + ".txt");
		Scanner myReader;
		try {
			myReader = new Scanner(file);
			while (myReader.hasNextLine()) {
		        String data = myReader.nextLine();
		        String[] datos = data.split("\t");
		        float[] d = new float[datos.length-1];
		        for(int i = 1; i < datos.length; i++) {
		        	d[i-1] = Float.parseFloat(datos[i]);
		        }
		        System.out.println(data);
		        accion.add(d);
		      }
		} catch (FileNotFoundException e) {
			System.err.println("No existe una politica definida.");
		}
	}
	
	public static void savePolitica(String politica_name, QTable qTable, float[][] acciones) {
		String directory = "Politicas";
		File file = new File(directory, politica_name + ".txt");
		try {
			FileWriter writer = new FileWriter(file.getAbsolutePath());
								
			for(int i = 0 ; i < qTable.size(); i++) {
				writer.write(i + "\t" + acciones[qTable.getBestRewardPosition(i)][0]);
				for(int j = 1; j < acciones[qTable.getBestRewardPosition(i)].length; j++)
					writer.write("\t" + acciones[qTable.getBestRewardPosition(i)][j]);
				
				writer.write("\n");
			}
			
			writer.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public float[] getAccion(Integer state) {
		return accion.get(state);
	}
}
