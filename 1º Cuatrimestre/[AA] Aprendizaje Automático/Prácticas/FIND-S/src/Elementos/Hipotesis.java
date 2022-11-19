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

//	public static void main(String[] args) {
//		ArrayList<String> h1 = new ArrayList<>();
//		h1.add("W"); h1.add("X");
//		Hipotesis h = new Hipotesis(h1);
//		
//		System.out.println(h.equals("O"));
//	}
}
