package Algoritmo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import Elementos.*;

public class AQ {

	private HashMap<Integer, String> identificador_atributo = null;
	private HashMap<String, Integer> atributo_identificador = null;
	private Recubrimiento conjunto_reglas = null;
	private ArrayList<Regla> LEF = null; // lista de criterios de preferencia de reglas

	private Set<Dato> P = null;
	private Set<Dato> N = null;

	public static Dataset dataset = null;	

	public AQ(Dataset dataset) {

		// Inicialización de los elementos del AQ
		identificador_atributo = dataset.get_Atributos_Identificador();
		atributo_identificador = dataset.getIdentificador_atributo_cabecera();

		// Inicialización de LEF
		LEF = new ArrayList<>();

		AQ.dataset = dataset;
	}
	
	public boolean addCriterio(Regla r) {
		if(!LEF.contains(r)) {
			LEF.add(r);
			System.out.println("¡Criterio añadido!");
			return true;
		}
		System.out.println("Ya existe ese criterio en LEF");
		return false;
			
	}
	
	public boolean removeCriterio(Regla r) {
		if (LEF.contains(r)) {
			LEF.remove(r);
			System.out.println("¡Criterio eliminado!");
			return true;
		}
		System.out.println("Ese criterio no existe");
		return false;
	}

	public Recubrimiento algoritmo() {
		// Inicialmente, el recubrimiento está vacío.
		conjunto_reglas = new Recubrimiento();

		// Se considera el conjunto P de ejemplos positivos
		P = new HashSet<>(dataset.filtrarDatasetPorClase().get(0).toArrayList());
		// Se considera el conjunto N de ejemplos negativos.
		N = new HashSet<>(dataset.filtrarDatasetPorClase().get(1).toArrayList());

		// Mientas queden ejemplos positivos en P:
		while (P.iterator().hasNext()) {
			// Elegir un ejemplo de P que será la semilla de la proxima regla
			Dato semilla = P.iterator().next();
			Complejo complejo_semilla = Complejo.generarComplejo(semilla, identificador_atributo,
					atributo_identificador);

			// Generar complejos que cubran la semilla y excluyan a los ejemplos de N.
			Set<Complejo> complejos = algoritmo_star(complejo_semilla.getSelectores());
			// Elegir de entre todos los complejos el que optimice el criterio de selección
			// (LEF)
			//System.out.println("Fin: " + complejos);

			Set<Complejo> complejo_optimo = elegirComplejo(complejos, LEF);
			//System.out.println("Elección de reglas: " + complejo_optimo);
			// Eliminar de P los ejemplos cubiertos por la nueva regla
			P = eliminarEjemplosCubiertos(complejo_optimo, P);
			// Añadir el complejo al recubrimiento.
			conjunto_reglas.addAll(complejo_optimo);
		}

		return conjunto_reglas;
	}

	private Set<Dato> eliminarEjemplosCubiertos(Set<Complejo> complejo_optimo, Set<Dato> positivos) {

		Set<Dato> positivos_copia = new HashSet<>(positivos);
		for (Dato p : positivos) {
			for (Complejo c : complejo_optimo)
				if (c.cubre(p)) {
					positivos_copia.remove(p);
				}
		}

		return positivos_copia;
	}

	private Set<Complejo> elegirComplejo(Set<Complejo> complejos, ArrayList<Regla> LEF) {

		Set<Complejo> complejos_reducidos = new HashSet<>(complejos);

		for (Regla r : LEF) {

			// aplicar la regla a la copia_complejos
			complejos_reducidos = r.aplicar(complejos_reducidos);
			// aplicar la siguiente regla sobre el resultado de la lista complejos_reducidos

		}

		return complejos_reducidos;
	}

	private Set<Complejo> algoritmo_star(Set<Selector> S) {
		Set<Complejo> E = new HashSet<>();
		Set<Complejo> L = new HashSet<>();
		int iteracion = 0;
		L.add(new Complejo());
		while (!L.isEmpty()) {
			//System.out.println("\nIteracion: " + ++iteracion);
			// Crear un conjunto E' con complejos creados por conjuncion de un elemento de L
			// y un selector de S.
			//System.out.println("L: " + L);
			//System.out.println("S: " + S);
			Set<Complejo> E_prima = Complejo.combinar(L, S);
			//System.out.println("E_prima: " + E_prima);

			// Eliminar de E' los elementos ya incluidos en E
			E_prima = eliminarElementosRepetidos(E_prima, E);
			//System.out.println("E_prima sin repetidos: " + E_prima);
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
			//System.out.println("E: " + E);
			// Actualizar la lista L a los elementos de E'
			//System.out.println("E_prima nueva = " + E_prima);
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

		String[] cabecera = { "ANTENAS", "COLAS", "NUCLEOS", "CUERPO", "CLASE" };
		String[] e1 = { "1", "0", "2", "RAYADO", "NORMAL" };
		String[] e2 = { "1", "2", "0", "RAYADO", "NORMAL" };
		String[] e3 = { "0", "2", "1", "RAYADO", "NORMAL" };
		String[] e4 = { "0", "2", "2", "RAYADO", "NORMAL" };
		String[] e6 = { "1", "0", "1", "BLANCO", "CANCERIGENA" };
		String[] e7 = { "1", "1", "1", "RAYADO", "CANCERIGENA" };
		String[] e8 = { "2", "2", "1", "RAYADO", "CANCERIGENA" };

		ds.add(new Dato(cabecera));
		ds.add(new Dato(e1));
		ds.add(new Dato(e2));
		ds.add(new Dato(e3));
		ds.add(new Dato(e4));
		ds.add(new Dato(e6));
		ds.add(new Dato(e7));
		ds.add(new Dato(e8));

		Dataset d = new Dataset(ds);

		System.out.println(d);
		
		AQ alg = new AQ(d);
		
		alg.menuLEF();
		
		System.out.println(alg.algoritmo());

	}

	public void menuLEF() {
		Scanner sc = new Scanner(System.in);
		int opc = 0;
		do {
			System.out.println("================ Menú LEF ========================");
			
			System.out.println("\t 1. Por defecto (>cobertura,>simplicidad).");
			System.out.println("\t 2. Añadir criterio de selección.");
			System.out.println("\t 3. Eliminar criterio de selección.");
			System.out.println("\t 4. Ejecutar algoritmo");
			System.out.println("LEF: " + LEF);
			System.out.println("==================================================");
			
			opc = sc.nextInt();
			
			if (! (opc < 1 || opc > 3) ) {
				switch (opc) {
				case 1:
					LEF.clear();
					LEF.add(new Regla(">", "cobertura"));
					LEF.add(new Regla(">", "simplicidad"));
					break;
				case 2:
					LEF.add(menuaddCriterio());
					break;
				case 3:
					int id = (menuremoveCriterio());
					if (id != LEF.size()) {
						LEF.remove(id);
					}
					break;
				default:
					throw new IllegalArgumentException("Unexpected value: " + opc);
				}
			}
			
			
			
		}while(opc != 4);
		

		
	}

	private int menuremoveCriterio() {
		int id = -1;
		Scanner sc = new Scanner(System.in);
			do {
				System.out.println("================ Eliminar Criterio =======================");
				
				for (int i = 1; i <= LEF.size(); i++) {
					System.out.println("\t" + i + ". " + LEF.get(i-1).toString());
				}
				System.out.println("\t" + (LEF.size()+1) + ". Volver");
				System.out.println("===================================================================");
				
				id = sc.nextInt();
				
			}while(id < 1 || id > (LEF.size()+1));
		return (id-1);
	}

	private Regla menuaddCriterio() {
		Scanner sc = new Scanner(System.in);
		String indicador = "";
		String criterio = "";
		int opc_criterio = 0;
		int opc_minmax = 0;
		do {
			
			do {
				System.out.println("================ Añadir Criterio (I) =======================");
				System.out.println("\t 1. Máximo (>)");
				System.out.println("\t 2. Mínimo (<)");
				System.out.println("===================================================================");
				
				opc_minmax = sc.nextInt();
				
			}while(opc_minmax < 1 || opc_minmax > 2);
					
			System.out.println("================ Añadir Criterio (II) ======================");
			System.out.println("\t 1. Cobertura");
			System.out.println("\t 2. Simplicidad");
			System.out.println("\t 3. Coste");
			System.out.println("\t 4. Generalidad");
			System.out.println("\t otro. Volver atrás");
			System.out.println("===================================================================");		
			
			opc_criterio = sc.nextInt();
			
		}while(opc_criterio < 1 || opc_criterio > 4);

		switch (opc_minmax) {
		case 1:
			indicador = ">";
			break;
		case 2:
			indicador = "<";
			break;
		default:
			throw new IllegalArgumentException("Unexpected value: " + opc_minmax);
		}
		
		switch (opc_criterio) {
		case 1:
			criterio = "cobertura";
			break;
		case 2:
			criterio = "simplicidad";
			break;
		case 3:
			criterio = "coste";
			break;
		case 4:
			criterio = "generalidad";
			break;
		default:
			throw new IllegalArgumentException("Unexpected value: " + opc_criterio);
		}
		
		return (new Regla(indicador,criterio));
		
	}

}
