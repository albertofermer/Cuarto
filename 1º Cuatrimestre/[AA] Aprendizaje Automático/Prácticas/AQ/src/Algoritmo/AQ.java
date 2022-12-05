package Algoritmo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import Elementos.*;

public class AQ {

	private Set<String> conjunto_atributos = null; // done
	private HashMap<String, Set<String>> conjunto_valores_atributo = null; // done
	private HashMap<Integer, String> identificador_atributo = null;
	private HashMap<String, Integer> atributo_identificador = null;
	private Set<String> conjunto_clases = null; // done
	private Set<Dato> conjunto_ejemplos_entrenamiento = null; // done
	private Recubrimiento conjunto_reglas = null;
	private ArrayList<Regla> LEF = null; // lista de criterios de preferencia de reglas TODO

	private Set<Dato> P = null;
	private Set<Dato> N = null;

	public static Dataset dataset = null;

	public AQ(Dataset dataset) {

		// Inicialización de los elementos del AQ
		conjunto_atributos = new HashSet<>(dataset.getNombreAtributos());
		conjunto_valores_atributo = dataset.getLista_Atributos_Valores();
		identificador_atributo = dataset.get_Atributos_Identificador();
		atributo_identificador = dataset.getIdentificador_atributo_cabecera();
		conjunto_clases = dataset.getLista_clases();
		conjunto_ejemplos_entrenamiento = new HashSet<>(dataset.getInstancias());

		// Inicialización de LEF
		LEF = new ArrayList<>();
		LEF.add(new Regla(">","cobertura"));
		LEF.add(new Regla(">","simplicidad"));
		AQ.dataset = dataset;
	}

	public Recubrimiento algoritmo() {
		// Inicialmente, el recubrimiento está vacío.
		conjunto_reglas = new Recubrimiento();

		// Se considera el conjunto P de ejemplos positivos
		P = new HashSet<>(dataset.filtrarDatasetPorClase().get(0).toArrayList());
		// Se considera el conjunto N de ejemplos negativos.
		N = new HashSet<>(dataset.filtrarDatasetPorClase().get(1).toArrayList());

		// Mientas queden ejemplos positivos en P:
		while (!P.isEmpty()) {
			// Elegir un ejemplo de P que será la semilla de la proxima regla
			Dato semilla = P.iterator().next();
			Complejo complejo_semilla = Complejo.generarComplejo(semilla, identificador_atributo,
					atributo_identificador);

			// Generar complejos que cubran la semilla y excluyan a los ejemplos de N.
			Set<Complejo> complejos = algoritmo_star(complejo_semilla.getSelectores());
			// Elegir de entre todos los complejos el que optimice el criterio de selección
			// (LEF)
			System.out.println("Fin: " + complejos);
//			
//			ArrayList<Complejo> ctest = new ArrayList<>(complejos);
//			System.out.println(ctest.get(0).getGeneralidad());
			
			
			
			Complejo complejo_optimo = elegirComplejo(complejos, LEF);
			// Eliminar de P los ejemplos cubiertos por la nueva regla
			P = eliminarEjemplosCubiertos(complejo_optimo, P);
			// Añadir el complejo al recubrimiento.
			conjunto_reglas.add(complejo_optimo);
		}

		return conjunto_reglas;
	}

	private Set<Dato> eliminarEjemplosCubiertos(Complejo complejo_optimo, Set<Dato> positivos) {

		Set<Dato> positivos_copia = new HashSet<>(positivos);
		for (Dato p : positivos) {
			if (complejo_optimo.cubre(p)) {
				positivos_copia.remove(p);
			}
		}

		return positivos_copia;
	}

	private Complejo elegirComplejo(Set<Complejo> complejos, ArrayList<Regla> LEF) {

		ArrayList<Complejo> copia_complejos = new ArrayList<>(complejos);
		for (Regla r : LEF) {
			
			// aplicar la regla a la copia_complejos
			// añadir el resultado a una lista nueva (complejos_reducidos)
			
			/**
			 * Cambiar el metodo aplicar regla para que devuelva un conjunto
			 * de complejos porque, en caso de igualdad, deberá devolver todos.
			 * 
			 * 
			 */
			
			// aplicar la siguiente regla sobre el resultado de la lista complejos_reducidos
			
		}
		
		return null;
	}

	private Set<Complejo> algoritmo_star(Set<Selector> S) {
		Set<Complejo> E = new HashSet<>();
		Set<Complejo> L = new HashSet<>();
		int iteracion = 0;
		L.add(new Complejo());
		while (!L.isEmpty()) {
			System.out.println("\nIteracion: " + ++iteracion);
			// Crear un conjunto E' con complejos creados por conjuncion de un elemento de L
			// y un selector de S.
			System.out.println("L: " + L);
			System.out.println("S: " + S);
			Set<Complejo> E_prima = Complejo.combinar(L, S);
			System.out.println("E_prima: " + E_prima);
			
			// Eliminar de E' los elementos ya incluidos en E
			E_prima = eliminarElementosRepetidos(E_prima, E);
			System.out.println("E_prima sin repetidos: " + E_prima);
			Set<Complejo> E_prima_copia = new HashSet<>(E_prima);
			// Para cada complejo de E', si no cubre ningún ejemplo negativo, entonces:
			for (Complejo c : E_prima) {
				if (c.noCubreNingun(N)) {
					// Añadir el complejo a E
					E.add(c);
					// Eliminar el complejo de E'
					E_prima_copia.remove(c);
				}
			}
			
			E_prima = E_prima_copia;
			System.out.println("E: " + E);
			// Actualizar la lista L a los elementos de E'
			System.out.println("E_prima nueva = " + E_prima);
			L = E_prima;
			
		}
	
		return E;
	}

	private Set<Complejo> eliminarElementosRepetidos(Set<Complejo> E_prima, Set<Complejo> E) {

		Set<Complejo> e_prima = new HashSet<>();
		for (Complejo cep : E_prima) {
			if (!cep.incluidoEn(E)) {
				e_prima.add(cep);
			}
		}

		return e_prima;
	}

	public static void main(String[] args) {

		ArrayList<Dato> ds = new ArrayList<>();

		String[] cabecera = { "Antenas", "Colas", "Nucleos", "Cuerpo", "CLASE" };
		String[] e1 = { "1", "0", "2", "Rayado", "Normal" };
		String[] e2 = { "1", "2", "0", "Rayado", "Normal" };
		String[] e3 = { "0", "2", "1", "Rayado", "Normal" };
		String[] e4 = { "0", "2", "2", "Rayado", "Normal" };
		String[] e5 = { "1", "0", "2", "Rayado", "Normal" };
		String[] e6 = { "1", "0", "1", "Blanco", "Cancerigena" };
		String[] e7 = { "1", "1", "1", "Rayado", "Cancerigena" };
		String[] e8 = { "2", "2", "1", "Rayado", "Cancerigena" };

		Dato d4 = new Dato(e4);
		Dato d5 = new Dato(e5);

		ds.add(new Dato(cabecera));
		ds.add(new Dato(e1));
		ds.add(new Dato(e2));
		ds.add(new Dato(e3));
		ds.add(new Dato(e4));
		ds.add(new Dato(e5));
		ds.add(new Dato(e6));
		ds.add(new Dato(e7));
		ds.add(new Dato(e8));

		Dataset d = new Dataset(ds);

		AQ alg = new AQ(d);
		alg.algoritmo();

	}

}
