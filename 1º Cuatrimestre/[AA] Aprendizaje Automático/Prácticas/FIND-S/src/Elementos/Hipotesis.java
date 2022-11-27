package Elementos;

import java.util.ArrayList;
import static Algoritmo.Constantes.*;

public class Hipotesis {

	private ArrayList<String> patrones = new ArrayList<>();

	public Hipotesis(ArrayList<String> h) {

		this.patrones = h;

	}

	@Override
	public String toString() {

		return patrones.toString();

	}

	@Override
	public boolean equals(Object o) {

		Hipotesis h2 = (Hipotesis) o;
		if (h2.getHypothesisList().size() != this.patrones.size())
			return false;

		for (int i = 0; i < this.patrones.size(); i++) {
			if (!this.patrones.get(i).equals(h2.getHypothesisList().get(i)))
				return false;
		}

		return true;
	}

	public ArrayList<String> getHypothesisList() {
		return patrones;
	}

	public int getHypothesisSize() {
		return patrones.size();
	}

	public int generalidad() {
		int count = 0;
		for (String patron : patrones) {
			if (patron.equals(TODO))
				count++;
		}

		return count;
	}

	public String getPatron(int j) {

		return patrones.get(j);

	}
	
	/**
	 * La hipótesis será más general que h2 si todos los patrones de
	 * h son más generales que los de h2.
	 * @param h2
	 * @return
	 */
	public boolean esMasGeneral(Hipotesis h2) {
		
		for (int i = 0; i < patrones.size(); i++) {
			
			if (patrones.get(i).equals(TODO) || patrones.get(i).equals(h2.getPatron(i))) {
				// El patrón de h es más general que el de h2 si:
				//	1. El patrón h es ?
				//	2. El patrón h es igual al de h2
				continue;
			}
			else {
				// En otro caso, la hipótesis no será más general que h2.
				return false;
			}
			
			
		}
		return true;
	}
	
	/**
	 * La hipótesis será más específica que h2 si todos los patrones de
	 * h son más específicos que los de h2.
	 * @param h2
	 * @return
	 */
	public boolean esMasEspecifica(Hipotesis h2) {
		for (int i = 0; i < patrones.size(); i++) {
			
			if (patrones.get(i).equals(VACIO) ||
					patrones.get(i).equals(h2.getPatron(i)) ||
					h2.getPatron(i).equals(TODO)) {
				// El patrón de h es más específico que el de h2 si:
				//	1. El patrón h es VACIO
				//	2. El patrón h es igual al de h2
				continue;
			}
			else {
				// En otro caso, la hipótesis no será más general que h2.
				return false;
			}
			
			
		}
		return true;
	}

//	public static void main(String[] args) {
//		ArrayList<String> h1 = new ArrayList<>();
//		h1.add("W"); h1.add("X");
//		Hipotesis h = new Hipotesis(h1);
//		
//		System.out.println(h.equals("O"));
//	}
}
