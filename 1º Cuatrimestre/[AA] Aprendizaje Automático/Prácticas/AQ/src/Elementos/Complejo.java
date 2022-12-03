package Elementos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Complejo {

	private Set<Selector> conjuncion_selectores = null;

	public Complejo() {
		// acepta todo
		this.conjuncion_selectores = new HashSet<>();
	}

	public Complejo(Set<Selector> conjuncion_selectores) {
		this.conjuncion_selectores = conjuncion_selectores;
	}

	public boolean cubre(Dato p) {
		for (Selector s : conjuncion_selectores) {
			if (!s.cubre(p)) return false;
		}
		
		return true;
	}

	@Override
	public String toString() {
		String str = "";
		ArrayList<Selector> conjuncion_selectores = new ArrayList<>();
		conjuncion_selectores.addAll(this.conjuncion_selectores);

		for (int i = 0; i < conjuncion_selectores.size() - 1; i++) {
			str += conjuncion_selectores.get(i).toString() + "^";
		}

		str += conjuncion_selectores.get(conjuncion_selectores.size() - 1).toString();

		return str;
	}

	@Override
	public boolean equals(Object o) {
		Complejo c = (Complejo) o;
		return this.conjuncion_selectores.equals(c.conjuncion_selectores);
		
	}
	
	public static void main(String[] args) {
		Selector s = new Selector("Atributo", ">", "7",null);
		Selector s2 = new Selector("Atributo", ">", "8",null);
		Set<Selector> selectores = new HashSet<>();
		selectores.add(s);
		selectores.add(s2);
		Complejo c = new Complejo(selectores);

		System.out.println(c);
	}

	public static Complejo generarComplejo(Dato semilla, HashMap<String, Integer> atributo_identificador) {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<Selector> getSelectores() {
		return conjuncion_selectores;
	}

	public static Set<Complejo> combinar(Set<Complejo> l, ArrayList<Selector> s) {
		// TODO Auto-generated method stub
		return null;
	}

}
