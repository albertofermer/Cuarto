package QLearning;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Politica {

	ArrayList<Float> steers = new ArrayList<>();
	public void loadPolitica(String politica_name) {
		String directory = "Politicas";
		File file = new File(directory, politica_name + ".txt");
		Scanner myReader;
		try {
			myReader = new Scanner(file);
			while (myReader.hasNextLine()) {
		        String data = myReader.nextLine();
		        String[] datos = data.split("\t");
		        System.out.println(data);
		        steers.add(Float.parseFloat(datos[1]));
		      }
		} catch (FileNotFoundException e) {
			System.err.println("No existe una politica definida.");
		}
	}
	
	public static void savePolitica(String politica_name, QTable qTable) {
		String directory = "Politicas";
		File file = new File(directory, politica_name + ".txt");
		try {
			FileWriter writer = new FileWriter(file.getAbsolutePath());
								
			for(int i = 0 ; i < qTable.size(); i++) {
				writer.write(i + "\t" + Constantes.STEER_VALUES[qTable.getBestRewardPosition(i)][0] + "\n");
			}
			
			writer.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public float getSteer(Integer state) {
		return steers.get(state);
	}
}
