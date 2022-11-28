package Principal;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import Algoritmo.ID3;
import Elementos.*;

public class Main {

	
	public Main() {	}

	public static void main(String[] args) throws Exception {
		
		lectorCSV lector = new lectorCSV();
		ArrayList<Dato> d = new ArrayList<>();
		try {
			
			ArrayList<List<String>> dataset = lector.leerCSV(args[0].toString());
			
			// Creación del tipo de dato "Dato" para crear el dataset.
			for (List<String> e : dataset) {
				String[] dato = e.toArray(new String[0]);
				d.add(new Dato(dato));
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		ID3 id3 = new ID3();
		
		Nodo n = id3.algoritmo(new Dataset(d));
		System.out.println("---------------- DATASET ----------------");
		System.out.println(new Dataset(d));
		System.out.println("-----------------------------------------");
		System.out.println("----------------- Reglas del ID3 -----------------");
		System.out.println(n);
		

	}
	
}
