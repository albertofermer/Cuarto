package Elementos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Complejo {

	private Set<Selector> conjuncion_selectores = null;

	public Complejo() {
		// acepta todo
		this.conjuncion_selectores = new HashSet<>();
	}
	
	public Complejo(Complejo c) {
		conjuncion_selectores = new HashSet<>(c.getSelectores());
	}
	
	public void add(Selector s) {
		conjuncion_selectores.add(s);
	}

	@Override
	public int hashCode() {
		return 1;
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
		
		if (!conjuncion_selectores.isEmpty()) {
			String str = "";
			ArrayList<Selector> conjuncion_selectores = new ArrayList<>();
			conjuncion_selectores.addAll(this.conjuncion_selectores);

			for (int i = 0; i < conjuncion_selectores.size() - 1; i++) {
				str += conjuncion_selectores.get(i).toString() + "^";
			}

			str += conjuncion_selectores.get(conjuncion_selectores.size() - 1).toString();

			return str;			
		} else {
			return conjuncion_selectores.toString();
		}

	}

	@Override
	public boolean equals(Object o) {
		Complejo c = (Complejo) o;
		return this.conjuncion_selectores.equals(c.conjuncion_selectores);
		
	}
	
	public static void main(String[] args) {
		Selector s = new Selector("Atributo", "=", "7",null);
		Selector s2 = new Selector("Atributo", "=", "8",null);
		Set<Selector> selectores = new HashSet<>();
		selectores.add(s);
		selectores.add(s2);
		Complejo c = new Complejo(selectores);

		System.out.println(c);
	}

	public static Complejo generarComplejo(Dato semilla, HashMap<Integer, String> identificador_atributo,
			HashMap<String, Integer> atributo_identificador) {
		
		Set<Selector> selectores = new HashSet<>();
		for (int id = 0 ; id < semilla.getAtributos().size() ; id++) {
			Selector s = new Selector(identificador_atributo.get(id), "=", semilla.getAtributo(id),atributo_identificador);
			selectores.add(s);			
		}
		
		return new Complejo(selectores);
	}

	public Set<Selector> getSelectores() {
		
		return conjuncion_selectores;
	}

	public static Set<Complejo> combinar(Set<Complejo> L, Set<Selector> S) {

		Set<Complejo> combinacion = new HashSet<>();
		for (Selector ss : S) {
		
			Complejo complejo_copia = null;
			for (Complejo cl : L) {
				
				complejo_copia = new Complejo(cl);
				complejo_copia.add(ss);
				
			}
			
			combinacion.add(complejo_copia);
			
		}
		
		
		return combinacion;
	}

	/**
	 * 
	 * @param n
	 * @return
	 */
	public boolean noCubreNingun(Set<Dato> n) {
		
		for(Dato d : n) {
			if(this.cubre(d)) return false;
		}
		
		return true;
	}

	public boolean incluidoEn(Set<Complejo> e) {
		
		for (Selector s : this.conjuncion_selectores) {
			if (!s.incluidoEn(e)) return false;
		}
		return true;
	}

}
