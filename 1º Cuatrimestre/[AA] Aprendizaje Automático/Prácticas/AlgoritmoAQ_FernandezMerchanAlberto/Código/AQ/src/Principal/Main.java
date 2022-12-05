package Principal;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import Algoritmo.AQ;
import Algoritmo.Constantes;
import Elementos.*;

public class Main {
	public Main() {
	}

	/**
	 * Función principal del programa. 
	 * El primer argumento debe ser la ruta del dataset.
	 * El segundo argumento es la clase positiva. 
	 * En caso de dejarlo vacío, se tomará como positiva la clase del primer ejemplo del dataset.
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		lectorCSV lector = new lectorCSV();
		ArrayList<Dato> lista_datos = new ArrayList<>();
		
		try {
		
		ArrayList<List<String>> dataset = lector.leerCSV(args[0].toString());
		
		// Creación del tipo de dato "Dato" para crear el dataset.
		for (List<String> e : dataset) {
			String[] dato = e.toArray(new String[0]);
			lista_datos.add(new Dato(dato));
		}

	} catch (FileNotFoundException e) {
		e.printStackTrace();
	}
		Dataset d = new Dataset(lista_datos);
		Constantes.setClasePositiva(d.get(0).getClase(), d.getLista_clases());
		if (args.length == 2) Constantes.setClasePositiva(args[1].toString(), d.getLista_clases());

		AQ alg = new AQ(d);
		Recubrimiento solucion = alg.algoritmo();
		
		System.out.println("=========================================");
		System.out.println(solucion);
		System.out.println("=========================================");


	}

}
