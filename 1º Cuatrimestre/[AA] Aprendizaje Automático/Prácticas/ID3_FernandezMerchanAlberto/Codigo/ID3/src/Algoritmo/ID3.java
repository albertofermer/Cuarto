package Algoritmo;

import java.util.ArrayList;
import java.util.HashMap;

import Elementos.*;

public class ID3 {

	public Nodo raiz = null;

	public ID3() {

	}

	public Nodo algoritmo(Dataset dataset) {

		if (!dataset.getInstancias().isEmpty() && mismaClase(dataset)) { // Si todas las instancias son de la misma
																			// clase
			return new Nodo(dataset.getInstancias().get(0).getClase());

		} else if (dataset.getInstancias().size() == 0) { // Si no quedan más instancias para estudiar.
			return new Nodo("Por Defecto");

		} else if (dataset.getInstancias().get(0).getSize() == 0) { // Si no quedan atributos por estudiar.

			return claseMayoritaria(dataset);

		} else {

			String atributo = calculaMayorGanancia(dataset);
			Nodo raiz = new Nodo(atributo);

			for (String valor : dataset.getListaCabecera_Valores().get(atributo)) {

				Rama branch = new Rama(valor);
				branch.addPadre(raiz);
				Dataset dataset_reducido = dataset.filtrarDatasetPorAtributoValor(dataset, atributo, valor);
				branch.addHijo(algoritmo(dataset_reducido));

			}

			return raiz;
		}

	}

	private String calculaMayorGanancia(Dataset dataset) {
		// Debemos devolver el atributo que menor ganancia de información aporte.
		ArrayList<String> atributos = new ArrayList<>();
		ArrayList<Double> informacion = new ArrayList<>();
		atributos.addAll(dataset.getNombreAtributos());
		
		//System.out.println(dataset.getNombreAtributos());

		for (String atributo : atributos) {
			informacion.add(calculaInformacionAtributo(dataset, atributo));
		}

		// Suponemos que el máximo es el primer valor
		double min = informacion.get(0);
		String atributo_max = atributos.get(0);

		for (int i = 1; i < informacion.size(); i++) {
			if (informacion.get(i) < min) {
				min = informacion.get(i);
				atributo_max = atributos.get(i);
			}

		}
		
		System.out.println("Atr_Min: " + atributo_max);
		return atributo_max;
	}

	private Double calculaInformacionAtributo(Dataset dataset, String atributo) {
		double suma = 0;
		// para cada valor del atributo pasado por parámetro, se suma sus
		// contribuciones.
		for (String valor : dataset.getListaCabecera_Valores().get(atributo)) {

			int nij = dataset.filtrarDatasetPorAtributoValor(dataset, atributo, valor).getInstancias().size();

			int n = dataset.getInstancias().size();
			suma += ((double)nij / (double)n) * calculaInformacionValor(dataset, atributo, valor);
		}
		
		System.out.println("I(" + atributo + ") = " + suma);
		return suma;
	}

	private double calculaInformacionValor(Dataset dataset, String atributo, String valor) {

		double suma = 0;
		int nij = dataset.filtrarDatasetPorAtributoValor(dataset, atributo, valor).getInstancias().size();
		for (String clase : dataset.getLista_clases()) {
			
			int nijc = dataset.filtrarDatasetPorAtributoValorClase(dataset, atributo, valor, clase).getInstancias()
					.size();
			suma += ( ( (double) nijc / (double) nij)) * log(( ( (double) nijc / (double) nij)), 2);
		}
		
		return -suma;
	}

	private static Double log(double num, int base) {
		if (num == 0) {
			return (double) 0;
		}
		return (Math.log10(num) / Math.log10(base));
	}

	private Nodo claseMayoritaria(Dataset dataset) {

		// Hacer un diccionario que relacione cada clase del conjunto lista_clases con
		// un valor.
		HashMap<String, Integer> contador = new HashMap<String, Integer>();
		// Inicializamos el contador de cada clase.
		for (String clase : dataset.getLista_clases()) {
			contador.put(clase, 0);
		}

		// Elegimos como clase mayoritaria la primera que aparezca.
		String clase_mayoritaria = dataset.getInstancias().get(0).getClase();

		// Calcula cual es la clase mayoritaria
		for (Dato d : dataset.getInstancias()) {

			// Aumenta el contador de la clase de d.
			contador.put(d.getClase(), contador.get(d.getClase()) + 1);

			// Actualiza la clase mayoritaria
			if (contador.get(clase_mayoritaria) < contador.get(d.getClase())) {
				clase_mayoritaria = d.getClase();
			}

		}

		return new Nodo(clase_mayoritaria);
	}

	private boolean mismaClase(Dataset dataset2) {
		// Comparamos con la clase de la primera instancia
		String clase_aux = dataset2.getInstancias().get(0).getClase();
		for (Dato d : dataset2.getInstancias()) {

			if (!clase_aux.equals(d.getClase())) { // En el caso de que sea distintas, entonces no son todas de la misma
													// clase.
				return false;
			}
		}
		return true;
	}
}
