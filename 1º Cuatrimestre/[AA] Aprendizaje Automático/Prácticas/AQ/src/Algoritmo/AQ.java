package Algoritmo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import Elementos.*;

public class AQ {

	 private Set<String> conjunto_atributos = null; // done
	 private HashMap<String, Set<String>> conjunto_valores_atributo = null; // done
	 private HashMap<String, Integer> atributo_identificador = null;
	 private Set<String> conjunto_clases = null; // done
	 private Set<Dato> conjunto_ejemplos_entrenamiento = null; // done
	 private Recubrimiento conjunto_reglas = null;
	 private ArrayList<Regla> LEF = null; // lista de criterios de preferencia de reglas TODO
	 
	 private Dataset dataset = null;

	public AQ(Dataset dataset) {

		// Inicialización de los elementos del AQ
		conjunto_atributos = new HashSet<>(dataset.getNombreAtributos());
		conjunto_valores_atributo = dataset.getLista_Atributos_Valores();
		atributo_identificador = dataset.getIdentificador_atributo_cabecera();
		conjunto_clases = dataset.getLista_clases();
		conjunto_ejemplos_entrenamiento = new HashSet<>(dataset.getInstancias());
		
			// Inicialización de LEF
		LEF = new ArrayList<>();
		
		this.dataset = dataset;
	}

	public Recubrimiento algoritmo() {
		// Inicialmente, el recubrimiento está vacío.
		conjunto_reglas = new Recubrimiento();
		
		// Se considera el conjunto P de ejemplos positivos
		Set<Dato> P = new HashSet<>(dataset.filtrarDatasetPorClase().get(0).toArrayList()); 
		// Se considera el conjunto N de ejemplos negativos.
		Set<Dato> N = new HashSet<>(dataset.filtrarDatasetPorClase().get(1).toArrayList());
		
		// Mientas queden ejemplos positivos en P:
		while(!P.isEmpty()) {
			// Elegir un ejemplo de P que será la semilla de la proxima regla
			Dato semilla = P.iterator().next();
			Complejo complejo_semilla = Complejo.generarComplejo(semilla,atributo_identificador);
			
			
			// Generar complejos que cubran la semilla y excluyan a los ejemplos de N.
			Set<Complejo> complejos = algoritmo_star(complejo_semilla.getSelectores());
			// Elegir de entre todos los complejos el que optimice el criterio de selección (LEF)
			Complejo complejo_optimo = elegirComplejos(complejos,LEF);
			// Eliminar de P los ejemplos cubiertos por la nueva regla
			P = eliminarEjemplosCubiertos(complejo_optimo,P);
			// Añadir el complejo al recubrimiento.
			conjunto_reglas.add(complejo_optimo);
		}
		
		return conjunto_reglas;
	}

	private Set<Dato> eliminarEjemplosCubiertos(Complejo complejo_optimo, Set<Dato> positivos) {
		
		Set<Dato> positivos_copia = new HashSet<>(positivos);
		for (Dato p : positivos) {
			if(complejo_optimo.cubre(p)) {
				positivos_copia.remove(p);
			}
		}
		
		return positivos_copia;
	}

	private Complejo elegirComplejos(Set<Complejo> complejos, ArrayList<Regla> LEF) {
		// TODO Auto-generated method stub
		return null;
	}

	private Set<Complejo> algoritmo_star(ArrayList<Selector> S) {
		Set<Complejo> E = new HashSet<>();
		Set<Complejo> L = new HashSet<>();
			L.add(new Complejo());
		while(!L.isEmpty()) {
			// Crear un conjunto E' con complejos creados por conjuncion de un elemento de L y un selector de S.
			Set<Complejo> E_prima = Complejo.combinar(L,S);
			// Eliminar de E' los elementos ya incluidos en E
			
			// Para cada complejo de E', si no cubre ningún ejemplo negativo, entonces:
			
				// Añadir el complejo a E
				// Eliminar el complejo de E'
			
			// Actualizar la lista L a los elementos de E'
		}
		
		
		 	
		return E;
	}

	public static void main(String[] args) {

		ArrayList<Dato> ds = new ArrayList<>();

		String[] cabecera = { "A1", "A2", "A3", "CLASE" };
		String[] e1 = { "1", "2", "3", "+" };
		String[] e2 = { "4", "5", "6", "+" };
		String[] e3 = { "7", "8", "9", "-" };
		String[] e4 = { "1", "10", "8", "+"};
		String[] e5 = { "1", "10", "8", "+"};

		Dato d4 = new Dato(e4);
		Dato d5 = new Dato(e5);
		
		ds.add(new Dato(cabecera));
		ds.add(new Dato(e1));
		ds.add(new Dato(e2));
		ds.add(new Dato(e3));
		ds.add(new Dato(e4));
		ds.add(new Dato(e5));
		
		
		Dataset d = new Dataset(ds);
		
		AQ alg = new AQ(d);
		alg.algoritmo();

	}

}
