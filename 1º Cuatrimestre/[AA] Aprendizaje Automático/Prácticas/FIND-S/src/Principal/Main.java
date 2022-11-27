package Principal;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import Algoritmo.candidateElimination;
import Elementos.*;

public class Main {
	public Main() {
	}

	public static void main(String[] args) throws Exception {

		lectorCSV lector = new lectorCSV();
		ArrayList<Dato> d = new ArrayList<>();
		try {

			ArrayList<List<String>> dataset = lector.leerCSV(args[0].toString());
			for (List<String> e : dataset) {
				String[] dato = e.toArray(new String[0]);
				d.add(new Dato(dato));
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		ArrayList<Dato> instancias = new ArrayList<>(d);
		instancias.remove(0);

		candidateElimination ce = new candidateElimination(instancias);
		
		
		
		ce.constantes.setClasePositiva(instancias.get(0).getClase(),ce.getListaClases());
		if (args.length == 2) ce.constantes.setClasePositiva(args[1].toString(),ce.getListaClases());
		
		
		ce.algorithm();

	}

}
