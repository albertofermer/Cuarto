package Principal;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import Algoritmo.candidateElimination;
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

		// Eliminamos la cabecera del dataset.
		ArrayList<Dato> instancias = new ArrayList<>(d);
		instancias.remove(0);

		// Le pasamos las instancias al algoritmo.
		candidateElimination ce = new candidateElimination(instancias);
		
		// Se define la clase positiva como la clase del primer ejemplo.
		ce.constantes.setClasePositiva(instancias.get(0).getClase(),ce.getListaClases());
		
		// En caso de que haya un segundo argumento, este será la clase positiva.
		if (args.length == 2) ce.constantes.setClasePositiva(args[1].toString(),ce.getListaClases());
		
		// Se aplica el algoritmo.
		ArrayList<Set<Hipotesis>> solucion = ce.algorithm();
		System.out.println("===========================================================");
		System.out.println("S: " + solucion.get(1));
		System.out.println("G: " + solucion.get(0));
		System.out.println("===========================================================\n");

	}

}
