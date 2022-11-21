package Algoritmo;

import static Algoritmo.Constantes.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import Elementos.Dato;
import Elementos.Hipotesis;

public class candidateElimination {
	private ArrayList<Hipotesis> h = null;
	private ArrayList<Dato> dataset = null;
	ArrayList<Set<String>> lista_atributos = null;

	public candidateElimination(ArrayList<Dato> dataset) {

		this.dataset = dataset;
		h = createHypothesis();
		lista_atributos = listaAtributos();
	}

	/**
	 * Inicializa la hipótesis a vacío (0)
	 */
	private ArrayList<Hipotesis> createHypothesis() {

		ArrayList<Hipotesis> hypothesis = new ArrayList<>();

		for (int i = 0; i < this.dataset.get(0).getSize() - 1; i++) {
			ArrayList<String> vacio = new ArrayList<>();
			vacio.add(VACIO);
			hypothesis.add(new Hipotesis(vacio));
		}

		return hypothesis;
	}

	private ArrayList<Set<String>> listaAtributos() {

		ArrayList<Set<String>> lista_atributos = new ArrayList<>(); // Para almacenar los tipos de atributos que hay

		for (int i = 0; i < dataset.get(0).getSize(); i++) {
			Set<String> a = new HashSet<>();
			lista_atributos.add(a);
		}

		for (Dato d : dataset) {
			for (int i = 0; i < d.getSize(); i++) {
				lista_atributos.get(i).add(d.getAtributo(i));
			}
		}

		return lista_atributos;
	}

	private Set<Hipotesis> createSet(String patron) {

		Set<Hipotesis> conjunto = new HashSet<>();
		ArrayList<String> aux = new ArrayList<String>();

		for (int i = 0; i < dataset.get(0).getSize(); i++) {
			aux.add(patron);
		}

		Hipotesis nueva = new Hipotesis(aux);
		conjunto.add(nueva);

		return conjunto;

	}

	public void algorithm() {

		Set<Hipotesis> G = createSet(TODO);
		Set<Hipotesis> S = createSet(VACIO);
		for (Dato dato : dataset) {
			
			Set<Hipotesis> S_copia = new HashSet<>(S); // Realizamos una copia del conjunto
			Set<Hipotesis> G_copia = new HashSet<>(G);
			System.out.println("===========================================================");
			System.out.println("S: " + S);
			System.out.println("G: " + G);
			System.out.println("===========================================================\n");
			
			System.out.println("Instancia: " + dato);
			if (dato.esPositivo()) { // Si dato es un ejemplo positivo,

				// Eliminar de G cualquier hipótesis inconsistente con d
				G_copia = eliminarInconsistencias(G, dato);

				// para cada hipótesis de S inconsistente con dato:

				for (Hipotesis s : S) {
					if (!esConsistente(s, dato)) {
						// eliminar s de S
						S_copia.remove(s);

						// Incluir en S las generalizaciones minimales h de s, tal que h es consistente
						// con d y existe una hipótesis en G más general que h.

						Hipotesis h = generaliza(s, dato);

						// tal que h es consistente con d y
						// existe una hipótesis en G más general que h. 

						if (esConsistente(h, dato) && masGeneral(G_copia, h)) S_copia.add(h);

						// Eliminar de S las hipótesis tal que exista en S otra hipótesis más general. //// TO-DO ////
						
						
						
						
					}
				}

			} else { // Si dato es un ejemplo negativo,

				// Eliminar de S cualquier hipótesis inconsistente con d
				S = eliminarInconsistencias(S, dato);
				// para cada hipotesis g de G inconsistente con d,
				for (Hipotesis g : G) {

					if (!esConsistente(g, dato)) {
						// eliminar g de G
						G_copia.remove(g);

						// incluir en G las especializaciones minimales h de g,
						ArrayList<Hipotesis> hesp = especializa(g, dato);
										
						// tal que h es consistente con d y existe una hipótesis en S mas especifica que h.
						
						for (Hipotesis hg : hesp) 
							for (Hipotesis hs : S_copia) 
								for (int i = 0; i < hs.getHypothesisSize(); i++) 
									if (!hg.getPatron(i).equals(TODO) && 
											hg.getPatron(i).equals(hs.getPatron(i)) &&
											esConsistente(hg, dato) &&
											!masGeneral(S_copia,hg)) {
										
										G_copia.add(hg);						
									}
						
						// Eliminar de G aquellas hipótesis tales que exista en G una hipótesis más
						// específica. //// TO-DO ////
					}
				}

			}

			S = S_copia;
			G = G_copia;

		}
		
		System.out.println("S: " + S);
		System.out.println("G: " + G);
	}

	private boolean masGeneral(Set<Hipotesis> G, Hipotesis h2) {
		for (Hipotesis hipo : G) {
			if (hipo.generalidad() > h2.generalidad())
				return true;
		}

		return false;
	}

	private Set<Hipotesis> eliminarInconsistencias(Set<Hipotesis> G, Dato dato) {

		// Recorrer las hipótesis de G y comprobar si son inconsistentes con d.
		// si es inconsistente, se elimina.

		Set<Hipotesis> G_copia = new HashSet<>(G); // hacemos una copia del conjunto G
		for (Hipotesis h : G) {
			if (!esConsistente(h, dato)) {
				G_copia.remove(h);
				System.out.println("Hipotesis No Consistente: " + h);
			}

		}

		return G_copia;

	}

	public boolean esConsistente(Hipotesis h, Dato d) {

		if (!satisface(h, d) && d.esPositivo()) {
			// Si algún atributo de un ejemplo positivo no satisface la hipótesis, el
			// ejemplo no es consistente.
			return false;
		} else if (!satisface(h, d) && !d.esPositivo()) {
			// Si algún atributo de un ejemplo negativo no satisface la hipótesis, el
			// ejemplo es consistente.
			return true;
		}
		// Si satisface todos los patrones de la hipotesis y es positivo, entonces es consistente.
		// Si satisface todos los patrones de la hipótesis y es negativo, entonces no es consistente.
		return (d.esPositivo()); 

	}

	private boolean satisface(Hipotesis patron, Dato d) {
		
		for (int i = 0; i < patron.getHypothesisSize(); i++) {
			if (d.getAtributo(i).equals(TODO) || 
					d.getAtributo(i).equals(patron.getPatron(i)) ||
					patron.getPatron(i).equals(TODO)) { /* Comprueba el siguiente*/ }
			
			// En otro caso, no lo satisface
			else return false;
		}
		
		return true;

	}

	public Hipotesis generaliza(Hipotesis s, Dato dato) {

		ArrayList<String> hipotesis_retorno = new ArrayList<>();
		ArrayList<String> hipotesis_s = s.getHypothesisList();

		for (int i = 0; i < hipotesis_s.size(); i++) {

			if (hipotesis_s.get(i).equals(VACIO) || hipotesis_s.get(i).equals(dato.getAtributo(i))) {

				hipotesis_retorno.add(dato.getAtributo(i));

			} else {
				hipotesis_retorno.add(TODO);
			}

		}
		return new Hipotesis(hipotesis_retorno);

	}

	public ArrayList<Hipotesis> especializa(Hipotesis s, Dato dato) {

		ArrayList<Hipotesis> hipotesis_retorno = new ArrayList<>();
		ArrayList<String> patrones = s.getHypothesisList();
		
		for (int i = 0; i < patrones.size(); i++) {
			
			// Si h_i es ? y el atributo es e_i, entonces genera una hipotesis por cada g_i
			// != e_i
			if (patrones.get(i).equals(TODO)) {

				String e_i = dato.getAtributo(i);
				ArrayList<String> ejemplos_distintos = new ArrayList<>();

				for (String e_j : lista_atributos.get(i)) {
					if (!e_j.equals(e_i)) {
						ejemplos_distintos.add(e_j);
					}
				}

				for (String ejemplo : ejemplos_distintos) {
					ArrayList<String> hipotesis_lista = new ArrayList<>();
					for (int j = 0; j < patrones.size(); j++) {
						if (j == i) {
							hipotesis_lista.add(ejemplo);
						}
						else {
							hipotesis_lista.add(TODO);
						}
						

					}
					Hipotesis hipotesis = new Hipotesis(hipotesis_lista);
					hipotesis_retorno.add(hipotesis);
				}

			} else if ( (!patrones.get(i).equals(dato.getAtributo(i)) && !patrones.get(i).equals(TODO)) || patrones.get(i).equals(dato.getAtributo(i))) {
				
				// No genera ninguna hipótesis
				// Si h_i != e_i != ?. entonces no se hace nada (no se genera hipotesis)
				// Si h_i = e_i entonces g_i = VACIO (no se genera hipotesis)
				
				
			} else if (patrones.get(i).equals(VACIO)) {
				// Si h_i = empty entonces no se genera hipotesis.
				// no tiene sentido seguir.
				break;
			}

		}

		return hipotesis_retorno;

	}

	public ArrayList<Hipotesis> getH() {
		return h;
	}

	public static void main(String[] args) {
		/* DATASET */

		ArrayList<Dato> dataset = new ArrayList<>();

		String[] x1 = { "SOL", "TEMPL", "NORMAL", "FUERTE", "TEMPL", "IGUAL", POSITIVO };
		String[] x2 = { "SOL", "TEMPL", "ALTA", "FUERTE", "TEMPL", "IGUAL", POSITIVO };
		String[] x3 = { "LLUVIA", "FRIA", "ALTA", "FUERTE", "TEMPL", "CAMBIO", NEGATIVO };
		String[] x4 = { "SOL", "TEMPL", "ALTA", "FUERTE", "FRIA", "CAMBIO", POSITIVO };
		
		String[] e1 = { "rojo", "dulce", "mediano", POSITIVO };
		String[] e2 = { "rojo", "dulce", "pequeño", POSITIVO };
		String[] e3 = { "rojo", "amargo", "mediano", NEGATIVO };
		String[] e4 = { "negro", "dulce", "pequeño", NEGATIVO };
		
 		dataset.add(new Dato(x1));
		dataset.add(new Dato(x2));
		dataset.add(new Dato(x3));
		dataset.add(new Dato(x4));

		candidateElimination ce = new candidateElimination(dataset);
		ce.algorithm();

	}
}
