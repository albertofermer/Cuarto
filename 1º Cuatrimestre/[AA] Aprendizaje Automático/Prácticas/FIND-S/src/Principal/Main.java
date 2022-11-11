package Principal;
import java.util.ArrayList;

import Elementos.*;

public class Main {

	public final static String POSITIVO = "+";
	public final static String NEGATIVO = "-";
	public final static String TODO = "?";
	public final static String VACIO = "0";
	private static ArrayList<Dato> dataset = new ArrayList<>();
	// Inicializar como una lista de HIPOTESIS (nueva clase).
	private static ArrayList<Hipotesis> hypothesis = new ArrayList<>();
	
	public Main() {	}

	public static void main(String[] args) throws Exception {
		
		/*	DATASET	 */
		String [] x1 = {"SUNNY","WARM","NORMAL","STRONG","WARM", "SAME",POSITIVO};
		String [] x2 = {"SUNNY","WARM","HIGH","STRONG","WARM", "SAME",POSITIVO};
		String [] x3 = {"RAINY","COLD","HIGH","STRONG","WARM", "CHANGE",NEGATIVO};
		String [] x4 = {"SUNNY","WARM","HIGH","STRONG","COOL", "CHANGE",POSITIVO};

		
		dataset.add(new Dato(x1));
		dataset.add(new Dato(x2));
		dataset.add(new Dato(x3));
		dataset.add(new Dato(x4));
		
		ArrayList<Hipotesis> h = createHypothesis();
		
		// Para cada ejemplo, si es positivo, aplica el algoritmo
			// Funcion satisface -> devuelve bool y entra como parámetro h[j] y dataset[i][j]
		for (Dato d : dataset) {
			
			// Si no es positivo, continue
			
			int pos_clase = d.getSize()-1;
			if( d.getAtributos()[pos_clase].equals(NEGATIVO)) continue;
			
			for (int j = 0; j < d.getSize()-1; j++) {
				//System.out.println(h);
				if(satisface(h.get(j), d.getDato(j))) {
					
					// No hago nada
					
				}
				else {
					
					ArrayList<String> gen = new ArrayList<>();
					// Suponemos que solo hay una hipótesis en la sublista de hipótesis.
					gen.add(generaliza(h.get(j).getPatron(0)  , d.getDato(j)));
					Hipotesis generalizacion = new Hipotesis(gen);
					h.set(j, generalizacion);
					
				}
				
				
				
			}
			
		}
		
		System.out.println(h);
		
		
		/* DUAL-FIND-S*/
		/**
		 * Inicializar h a ?,?,? (TODO)
		 * para todos los ejemplos negativos, si satisface el patrón tengo que especializar.
		 * 
		 * 
		 * 
		 */

	}
	
	/**
	 *  Inicializa la hipótesis a vacío (0)
	 */
	public static ArrayList<Hipotesis> createHypothesis(){

		for (int i = 0; i < dataset.get(0).getSize()-1; i++) {
			ArrayList<String> vacio = new ArrayList<>();
			vacio.add(VACIO);
			hypothesis.add(new Hipotesis(vacio));
		}
		
		return hypothesis;
	}
	
	public static boolean satisface(Hipotesis patron, String atributo) {
		
		// Si el atributo es igual a TODO entonces devuelve verdadero.
		if(atributo.equals(TODO)) return true;
		
		
		// Si alguna de las hipótesis satisface el atributo entonces devuelve verdadero.
		for (String hipotesis : patron.getAllHypothesis()) {
			if(hipotesis.equals(atributo)) return true;
		}
		
		// En otro caso devuelve falso.
		return false;
		
	}
	
	/**
	 * Generaliza la hipótesis. 
	 * @param patron
	 * @param atributo
	 * @return
	 */
	public static String generaliza(String patron, String atributo) {
		
		if ( patron.equals(VACIO) || patron.equals(atributo)) { return atributo;}
		return TODO;
		
	}

	/**
	 * El especificar devuelve una lista de patrones!!
	 * 
	 */
	
	
}
