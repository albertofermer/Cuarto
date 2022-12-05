package Elementos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import Algoritmo.AQ;

public class Complejo {

	private Set<Selector> conjuncion_selectores = null;
	private int cobertura = -1; // Numero de ejemplos positivos cubiertos
	private float simplicidad = -1; // Inversa de Size de conjuncion_selectores
	private int coste = -1;
	private float generalidad = -1; // numero de ejemplos observados / numero de ejemplos posibles.

	public Complejo() {
		// acepta todo
		this.conjuncion_selectores = new HashSet<>();
		calculaCriterios();
	}

	public Complejo(Complejo c) {
		conjuncion_selectores = new HashSet<>(c.getSelectores());
	}

	public void add(Selector s) {
		conjuncion_selectores.add(s);
		calculaCriterios();
		
	}

	@Override
	public int hashCode() {
		return 1;
	}

	public Complejo(Set<Selector> conjuncion_selectores) {
		this.conjuncion_selectores = conjuncion_selectores;
		calculaCriterios();
	}

	public boolean cubre(Dato p) {

		for (Selector s : conjuncion_selectores) {
			// System.out.println("\tSelector: " + s + " -- " + s.cubre(p));
			if (!s.cubre(p))
				return false;
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
		Selector s = new Selector("Atributo", "=", "7", null);
		Selector s2 = new Selector("Atributo", "=", "8", null);
		Set<Selector> selectores = new HashSet<>();
		selectores.add(s);
		selectores.add(s2);
		Complejo c = new Complejo(selectores);

		System.out.println(c);
	}

	public static Complejo generarComplejo(Dato semilla, HashMap<Integer, String> identificador_atributo,
			HashMap<String, Integer> atributo_identificador) {

		Set<Selector> selectores = new HashSet<>();
		for (int id = 0; id < semilla.getAtributos().size(); id++) {
			Selector s = new Selector(identificador_atributo.get(id), "=", semilla.getAtributo(id),
					atributo_identificador);
			selectores.add(s);
		}

		return new Complejo(selectores);
	}

	public Set<Selector> getSelectores() {

		return conjuncion_selectores;
	}

	public static Set<Complejo> combinar(Set<Complejo> L, Set<Selector> S) {

		boolean incluido = false;
		Set<Complejo> combinacion = new HashSet<>();
		for (Selector ss : S) {

			Complejo complejo_copia = null;
			for (Complejo cl : L) {
				if (!cl.getSelectores().contains(ss)) {
					complejo_copia = new Complejo(cl);
					complejo_copia.add(ss);
				} else
					incluido = true;

				if (!incluido)
					combinacion.add(complejo_copia);
			}

			incluido = false;

		}

		return combinacion;
	}

	/**
	 * 
	 * @param n
	 * @return
	 */
	public boolean noCubreNingun(Set<Dato> n) {
		// System.out.println("Complejo: " + this);
		for (Dato d : n) {
			// System.out.println("Dato: " + d);
			if (this.cubre(d))
				return false;
		}

		return true;
	}

	public boolean incluidoEn(Set<Complejo> e) {

		for (Complejo c : e) {
			if (this.incluidoEn(c))
				return true;
		}

		return false;
	}

	private boolean incluidoEn(Complejo c) {
		for (Selector s : this.conjuncion_selectores) {
			// System.out.println(" --- Selector " + s + " incluido en " + e + ": " +
			// s.incluidoEn(e));
			if (!s.incluidoEn(c))
				return false;
		}
		return true;

	}

	private void calculaCriterios() {
		// Cobertura
		cobertura = 0;
		// Conjunto de datos positivos
		Set<Dato> P = new HashSet<>(AQ.dataset.filtrarDatasetPorClase().get(0).toArrayList());

		for (Dato d : P) {
			if (this.cubre(d))
				cobertura = getCobertura() + 1;
		}

		// Simplicidad
		simplicidad = 0;
		// Numero de atributos diferentes
		// Como son conjunciones, un atributo no podrá aparecer dos veces en ella.
		// Por lo tanto, será lo mismo que calcular el numero de atributos.
		simplicidad = 1/(float)this.getSelectores().size();

		// Coste
		// Como solo utilizamos un operador (=) asumimos coste 1 para cada selector.
		// Por lo tanto, será lo mismo que calcular el numero de atributos.
		coste = this.getSelectores().size();

		// Generalidad
		generalidad = 0;
		// numero de ejemplos observados / numero de ejemplos posibles
		// numero de ejemplos observados : los ejemplos a los que cubre el complejo
		// numero de ejemplos posibles : numero de ejemplos totales.
		int num_ejemplos_observados = 0;
		
		for (Dato d : AQ.dataset.getInstancias()) {
			if (this.cubre(d)) num_ejemplos_observados++;
		}
		
		generalidad = (float)num_ejemplos_observados/(float)AQ.dataset.getInstancias().size();

	}

	public int getCobertura() {
		return cobertura;
	}

	public float getSimplicidad() {
		return simplicidad;
	}

	public int getCoste() {
		return coste;
	}

	public float getGeneralidad() {
		return generalidad;
	}

}
