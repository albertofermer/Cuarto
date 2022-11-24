package Algoritmo;

import static Algoritmo.Constantes.NEGATIVO;
import static Algoritmo.Constantes.POSITIVO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import Elementos.*;

public class ID3 {

	private ArrayList<Dato> dataset = null;
	public Nodo raiz = null;
	ArrayList<Set<String>> lista_atributos = null;
	Set<String> lista_clases = null;

	public ID3(ArrayList<Dato> dataset) {

		this.dataset = dataset;
		generarListaAtributosyClases();

	}

	private void generarListaAtributosyClases() {

		lista_atributos = new ArrayList<>(); // Para almacenar los tipos de atributos que hay
		lista_clases = new HashSet<>();
		if (!dataset.isEmpty()) {

			for (int i = 0; i < dataset.get(0).getSize(); i++) {
				Set<String> a = new HashSet<>();
				lista_atributos.add(a);
			}

			for (Dato d : dataset) {
				lista_clases.add(d.getClase());
				for (int i = 0; i < d.getSize(); i++) {
					lista_atributos.get(i).add(d.getAtributo(i));
				}
			}
		}

	}

	public Nodo algoritmo(ArrayList<Dato> dataset) {

		if (!dataset.isEmpty() && mismaClase(dataset)) {
			System.out.println(new Nodo(dataset.get(0).getClase()));
			return new Nodo(dataset.get(0).getClase());
		} else if (dataset.size() == 0) {
			System.out.println(new Nodo("Por Defecto"));
			return new Nodo("Por Defecto");
		} else if (dataset.get(0).getSize() == 0) {
			System.out.println(claseMayoritaria(dataset));
			return claseMayoritaria(dataset);
		} else {

			int id_atributo = calculaMayorGanancia(dataset);
			Nodo raiz = new Nodo(Integer.toString(id_atributo));

			for (String valor : lista_atributos.get(id_atributo)) {

				Rama branch = new Rama(valor);
				branch.addPadre(raiz);
				ArrayList<Dato> dataset_reducido = reducirDataset(dataset, id_atributo, valor);
				branch.addHijo(algoritmo(dataset_reducido));

			}
			return raiz;
		}

	}

	private ArrayList<Dato> reducirDataset(ArrayList<Dato> dataset2, int id_atributo, String valor) {
		
		ArrayList<Dato> dataset_reducido = new ArrayList<>();
		for (Dato d : dataset2) {
			
		}
		
		return null;
	}

	private int calculaMayorGanancia(ArrayList<Dato> dataset2) {
		// TODO Auto-generated method stub
		return -1;
	}

	private Nodo claseMayoritaria(ArrayList<Dato> dataset2) {

		// Hacer un diccionario que relacione cada clase del conjunto lista_clases con
		// un valor.
		HashMap<String, Integer> contador = new HashMap<String, Integer>();
		// Inicializamos el contador de cada clase.
		for (String clase : lista_clases) {
			contador.put(clase, 0);
		}

		// Elegimos como clase mayoritaria la primera que aparezca.
		String clase_mayoritaria = dataset2.get(0).getClase();

		// Calcula cual es la clase mayoritaria
		for (Dato d : dataset2) {

			// Aumenta el contador de la clase de d.
			contador.put(d.getClase(), contador.get(d.getClase()) + 1);

			// Actualiza la clase mayoritaria
			if (contador.get(clase_mayoritaria) < contador.get(d.getClase())) {
				clase_mayoritaria = d.getClase();
			}

		}

		return new Nodo(clase_mayoritaria);
	}

	private boolean mismaClase(ArrayList<Dato> dataset2) {
		String clase_aux = dataset2.get(0).getClase();
		for (Dato d : dataset2) {
			if (!clase_aux.equals(d.getClase())) {
				return false;
			}
		}
		return true;
	}

	public static void main(String[] args) {
		ArrayList<Dato> dataset = new ArrayList<>();
		ArrayList<Dato> dataset_empty = new ArrayList<>();
		ArrayList<Dato> dataset_no_atributos = new ArrayList<>();
		/* DATASET */
		String [] cabecera = {"TIEMPO", "TEMPERATURA", "VIENTO", "TIPO", "PARTIDO", "CLASE"};
		String [] x1 = {"SUNNY","WARM","NORMAL","STRONG","WARM", "SAME", POSITIVO};
		String [] x2 = {"SUNNY","WARM","HIGH","STRONG","WARM", "SAME", POSITIVO};
		String [] x3 = {"RAINY","COLD","HIGH","STRONG","WARM", "CHANGE", NEGATIVO};
		String [] x4 = {"SUNNY","WARM","HIGH","STRONG","COOL", "CHANGE", POSITIVO};
		
		dataset.add(new Dato(cabecera));
		dataset.add(new Dato(x1));
		dataset.add(new Dato(x2));
		dataset.add(new Dato(x3));
		dataset.add(new Dato(x4));

		/* DATASET */
		String[] a1 = { POSITIVO };
		String[] a2 = { NEGATIVO };
		String[] a3 = { NEGATIVO };
		String[] a4 = { NEGATIVO };

		dataset_no_atributos.add(new Dato(a1));
		dataset_no_atributos.add(new Dato(a2));
		dataset_no_atributos.add(new Dato(a3));
		dataset_no_atributos.add(new Dato(a4));

		ID3 id3 = new ID3(dataset);
		id3.algoritmo(dataset);
	}
}
