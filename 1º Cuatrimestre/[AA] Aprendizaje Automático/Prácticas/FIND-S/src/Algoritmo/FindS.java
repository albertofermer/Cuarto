package Algoritmo;

import java.util.ArrayList;

import Elementos.Dato;
import Elementos.Hipotesis;
import static Algoritmo.Constantes.*;

public class FindS {

	private ArrayList<Hipotesis> h = null;
	private ArrayList<Dato> dataset = null;

	public FindS(ArrayList<Dato> dataset) {

		this.dataset = dataset;
		h = createHypothesis();
		

	}

	/**
	 * Inicializa la hipótesis a vacío (0)
	 */
	private ArrayList<Hipotesis> createHypothesis() {

		ArrayList<Hipotesis> hypothesis = new ArrayList<>();

		for (int i = 0; i < getDataset().get(0).getSize() - 1; i++) {
			ArrayList<String> vacio = new ArrayList<>();
			vacio.add(VACIO);
			hypothesis.add(new Hipotesis(vacio));
		}

		return hypothesis;
	}
	
	
	private boolean satisface(Hipotesis patron, String atributo) {
		
		// Si el atributo es igual a TODO entonces devuelve verdadero.
		if(atributo.equals(TODO)) return true;
		
		
		// Si alguna de las hipótesis satisface el atributo entonces devuelve verdadero.
		for (String hipotesis : patron.getHypothesisList()) {
			if(hipotesis.equals(atributo)) return true;
		}
		
		// En otro caso devuelve falso.
		return false;
		
	}
	
	public static String generaliza(String patron, String atributo) {
		
		if ( patron.equals(VACIO) || patron.equals(atributo)) { return atributo;}
		return TODO;
		
	}

	public ArrayList<Hipotesis> algoritmo() throws Exception {

		for (Dato d : dataset) {

			// Si no es positivo, continue

			int pos_clase = d.getSize() - 1;
			if (d.getAtributos()[pos_clase].equals(NEGATIVO))
				continue;

			for (int j = 0; j < d.getSize() - 1; j++) {
				// System.out.println(h);
				if (satisface(h.get(j), d.getAtributo(j))) {

					// No hago nada

				} else {

					ArrayList<String> gen = new ArrayList<>();
					// Suponemos que solo hay una hipótesis en la sublista de hipótesis.
					gen.add(generaliza(h.get(j).getPatron(0), d.getAtributo(j)));
					Hipotesis generalizacion = new Hipotesis(gen);
					h.set(j, generalizacion);

				}
			}
		}
		
		return h;

	}

	public ArrayList<Dato> getDataset() {
		return dataset;
	}

	public ArrayList<Hipotesis> getH() {
		return h;
	}

}
