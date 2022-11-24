package Elementos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Dataset {
	
	private ArrayList<String> atributos_cabecera = null; // done
	private ArrayList<Dato> instancias = null;			 // done
	private Set<String> lista_clases = null;			 // done
	private ArrayList<Set<String>> valores_atributos = null;
	private HashMap<String, Set<String>> lista_cabecera_valores = null;
	private HashMap<String, Integer> identificador_atributo_cabecera = null;
	
	public Dataset(ArrayList<Dato> dataset) {
		
		atributos_cabecera = inicializarCabecera(dataset.get(0));
		instancias = new ArrayList<>();
		
		for (int i = 1 ; i < dataset.size() ; i++) {
			instancias.add(dataset.get(i));
		}
		
		
	}

	private ArrayList<String> inicializarCabecera(Dato dato) {
		
		ArrayList<String> cabecera = new ArrayList<>();
		for (String s : dato.getAtributos()) {
			cabecera.add(s);
		}
		return cabecera;
	}
	
	private void generarListaAtributosyClases(ArrayList<Dato> dataset) {

		valores_atributos = new ArrayList<>(); // Para almacenar los tipos de atributos que hay
		lista_clases = new HashSet<>();
		if (!dataset.isEmpty()) {

			for (int i = 0; i < dataset.get(0).getSize(); i++) {
				Set<String> valores = new HashSet<>();
				
				valores_atributos.add(valores);
			}

			for (Dato d : dataset) {
				lista_clases.add(d.getClase());
				for (int i = 0; i < d.getSize(); i++) {
					valores_atributos.get(i).add(d.getAtributo(i));
				}
			}
		}

	}

	public ArrayList<String> getAtributos_cabecera() {
		return atributos_cabecera;
	}

	public ArrayList<Dato> getInstancias() {
		return instancias;
	}

	public Set<String> getLista_clases() {
		return lista_clases;
	}

	public ArrayList<Set<String>> getLista_valores_atributos() {
		return valores_atributos;
	}

	public HashMap<String, Integer> getIdentificador_atributo_cabecera() {
		return identificador_atributo_cabecera;
	}
	
	
}
