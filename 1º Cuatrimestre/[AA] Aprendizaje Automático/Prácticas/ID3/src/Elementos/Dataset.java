package Elementos;

import static Algoritmo.Constantes.NEGATIVO;
import static Algoritmo.Constantes.POSITIVO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Dataset {

	private ArrayList<String> atributos_cabecera = null; // done
	private ArrayList<Dato> instancias = null; // done
	private Set<String> lista_clases = null; // done
	private ArrayList<Set<String>> valores_atributos = null; // done
	private HashMap<String, Set<String>> lista_cabecera_valores = null; // done
	private HashMap<String, Integer> identificador_atributo_cabecera = null; // done

	public Dataset(ArrayList<Dato> dataset) {

		atributos_cabecera = inicializarCabecera(dataset.get(0));
		
		instancias = new ArrayList<>();

		for (int i = 1; i < dataset.size(); i++) {
			instancias.add(dataset.get(i));
		}

		generarListaAtributosyClases(dataset);

		lista_cabecera_valores = new HashMap<String, Set<String>>();
		for (int i = 0; i < atributos_cabecera.size()-1; i++) {
			lista_cabecera_valores.put(atributos_cabecera.get(i), valores_atributos.get(i));
		}
		
		identificador_atributo_cabecera = new HashMap<String,Integer>();
		for (int id = 0; id < atributos_cabecera.size(); id++) {
			identificador_atributo_cabecera.put(atributos_cabecera.get(id), id);
		}
		
	}

	private ArrayList<String> inicializarCabecera(Dato dato) {

		ArrayList<String> cabecera = new ArrayList<>();
		for (String s : dato.getAtributos()) {
			cabecera.add(s);
		}
		cabecera.add(dato.getClase());
		return cabecera;
	}

	private void generarListaAtributosyClases(ArrayList<Dato> dataset) {

		valores_atributos = new ArrayList<>(); // Para almacenar los tipos de atributos que hay
		lista_clases = new HashSet<>();

		
		
		for (int i = 0; i < dataset.get(0).getSize(); i++) { // Inicializamos la lista a M conjuntos vacios
			
			Set<String> valores = new HashSet<>();
			valores_atributos.add(valores);
		}

		// Empieza en 1 porque el 0 es la cabecera.
		for (int i = 1 ; i < dataset.size(); i++) {
			lista_clases.add(dataset.get(i).getClase());
			for (int j = 0; j < dataset.get(i).getSize(); j++) {
				valores_atributos.get(j).add(dataset.get(i).getAtributo(j)); // añado al conjunto el atributo ij
			}
		}

	}

	public ArrayList<String> getAtributos_cabecera() {
		return atributos_cabecera;
	}
	
	public ArrayList<String> getNombreAtributos(){
		ArrayList<String> nombre_atributos = new ArrayList<>();
		for (int i = 0 ; i < atributos_cabecera.size() - 1 ; i++) {
			nombre_atributos.add(atributos_cabecera.get(i));
		}
		return nombre_atributos;
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
	
	public HashMap<String,Set<String>> getListaCabecera_Valores(){
		return lista_cabecera_valores;
	}
	
	public Dataset filtrarDatasetPorAtributoValor(Dataset d, String atributo, String valor_atributo) {
		
		ArrayList<Dato> dataset_reducido = new ArrayList<>();
		int identificador_atributo = d.getIdentificador_atributo_cabecera().get(atributo);

		
		Dato cabecera = new Dato((d.getAtributos_cabecera().toArray(new String[0])));
		cabecera.eliminarAtributo(d.getIdentificador_atributo_cabecera().get(atributo));
		dataset_reducido.add(cabecera);
		
		for (Dato dato : d.getInstancias()) {
			Dato dato_aux = new Dato(dato);
			if(dato_aux.getAtributo(identificador_atributo).equals(valor_atributo)) {
				
				dato_aux.eliminarAtributo(identificador_atributo);
				dataset_reducido.add(dato_aux);
			}
				
		}
		
		return new Dataset(dataset_reducido);
	}
	
	public Dataset filtrarDatasetPorAtributoValorClase(Dataset d, String atributo, String valor_atributo, String clase) {
		
		ArrayList<Dato> dataset_reducido = new ArrayList<>();
		int identificador_atributo = d.getIdentificador_atributo_cabecera().get(atributo);

		Dato cabecera = new Dato((d.getAtributos_cabecera().toArray(new String[0])));
				
		dataset_reducido.add(cabecera);
		
		for (Dato dato : d.getInstancias()) {
			Dato dato_aux = new Dato(dato);
			if(dato_aux.getAtributo(identificador_atributo).equals(valor_atributo) && dato.getClase().equals(clase)) {
				dato_aux.eliminarAtributo(identificador_atributo);
				dataset_reducido.add(dato_aux);
			}
				
		}
		
		return new Dataset(dataset_reducido);
	}
	
	
	public static void main(String[] args) {
		ArrayList<Dato> dataset = new ArrayList<>();
		/*	DATASET	 */
		String [] c = {"Antenas","Colas","Nucleos","Cuerpo","Clase"};
		String [] x1 = {"1","0","2","Rayado", NEGATIVO};
		String [] x2 = {"1","0","1","Blanco", POSITIVO};
		String [] x3 = {"1","2","0","Rayado", NEGATIVO};
		String [] x4 = {"0","2","1","Rayado", NEGATIVO};
		String [] x5 = {"1","1","1","Rayado", POSITIVO};
		String [] x6 = {"2","2","1","Rayado", POSITIVO};
		
		dataset.add(new Dato(c));
		dataset.add(new Dato(x1));
		dataset.add(new Dato(x2));
		dataset.add(new Dato(x3));
		dataset.add(new Dato(x4));
		dataset.add(new Dato(x5));
		dataset.add(new Dato(x6));
		
		Dataset d = new Dataset(dataset);
		
		System.out.println(d.getAtributos_cabecera());
		System.out.println(d.getInstancias());
		System.out.println(d.getIdentificador_atributo_cabecera());
		System.out.println(d.getLista_clases());
		System.out.println(d.getLista_valores_atributos());
		System.out.println(d.getListaCabecera_Valores());
		
		
		System.out.println("===============================================");
		Dataset dr = d.filtrarDatasetPorAtributoValor(d, "Colas", "0");
		System.out.println(dr.getInstancias());
		System.out.println("===============================================");
		
		
		
		
		
	}

}
