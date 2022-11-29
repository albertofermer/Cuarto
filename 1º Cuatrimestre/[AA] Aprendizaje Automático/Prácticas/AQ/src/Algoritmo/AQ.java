package Algoritmo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import Elementos.*;

public class AQ {

	 private Set<String> conjunto_atributos = null; // done
	 private HashMap<String, Set<String>> conjunto_valores_atributo = null; // done
	 private Set<String> conjunto_clases = null; // done
	 private Set<Dato> conjunto_ejemplos_entrenamiento = null; // done
	 private Recubrimiento conjunto_reglas = null;
	 private ArrayList<Regla> LEF = null; // lista de criterios de preferencia de reglas TODO
	 
	 private Dataset dataset = null;

	public AQ(Dataset dataset) {

		// Inicialización de los elementos del AQ
		conjunto_atributos = new HashSet<>(dataset.getNombreAtributos());
		conjunto_valores_atributo = dataset.getLista_Atributos_Valores();
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
			
			// Generar complejos que cubran la semilla y excluyan a los ejemplos de N.
			
			// Elegir de entre todos los complejos el que optimice el criterio de selección (LEF)
			
			// Eliminar de P los ejemplos cubiertos por la nueva regla
			
			// Añadir el complejo al recubrimiento.
		
		}
		
		return conjunto_reglas;
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
