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
	 * La hip�tesis ser� m�s general que h2 si todos los patrones de
	 * h son m�s generales que los de h2.
	 * @param h2
	 * @return
	 */
	public boolean esMasGeneral(Hipotesis h2) {
		
		for (int i = 0; i < patrones.size(); i++) {
			
			if (patrones.get(i).equals(TODO) || patrones.get(i).equals(h2.getPatron(i))) {
				// El patr�n de h es m�s general que el de h2 si:
				//	1. El patr�n h es ?
				//	2. El patr�n h es igual al de h2
				continue;
			}
			else {
				// En otro caso, la hip�tesis no ser� m�s general que h2.
				return false;
			}
			
			
		}
		return true;
	}
	
	/**
	 * La hip�tesis ser� m�s espec�fica que h2 si todos los patrones de
	 * h son m�s espec�ficos que los de h2.
	 * @param h2
	 * @return
	 */
	public boolean esMasEspecifica(Hipotesis h2) {
		for (int i = 0; i < patrones.size(); i++) {
			
			if (patrones.get(i).equals(VACIO) ||
					patrones.get(i).equals(h2.getPatron(i)) ||
					h2.getPatron(i).equals(TODO)) {
				// El patr�n de h es m�s espec�fico que el de h2 si:
				//	1. El patr�n h es VACIO
				//	2. El patr�n h es igual al de h2
				continue;
			}
			else {
				// En otro caso, la hip�tesis no ser� m�s general que h2.
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
