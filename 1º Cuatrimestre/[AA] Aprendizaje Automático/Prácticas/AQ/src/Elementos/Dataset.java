package Elementos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Esta clase representa el dataset pasado al algoritmo. El dataset estará
 * compuesto por:
 * 
 * La cabecera (atributos_cabecera) representada como una lista de String. Las
 * instancias (instancias) representadas como una lista de "Datos". La lista de
 * las clases (lista_clases) representada como un conjunto de String. Los
 * valores de los atributos (valores_atributos) representados como una lista de
 * conjuntos de String. Un diccionario que relaciona cada atributo de la
 * cabecera con su lista de valores (lista_cabecera_valores) Un diccionario que
 * relaciona cada atributo de la cabecera con un identificador.
 * 
 * @author Alberto Fernández
 *
 */
public class Dataset {

	private ArrayList<String> atributos_cabecera = null; // done
	private ArrayList<Dato> instancias = null; // done
	private Set<String> lista_clases = null; // done
	private ArrayList<Set<String>> valores_atributos = null; // done
	private HashMap<String, Set<String>> lista_cabecera_valores = null; // done
	private HashMap<String, Integer> identificador_atributo_cabecera = null; // done
	private HashMap<Integer, String> atributo_identificador = null; // done
	
	private ArrayList<Dato> dataset_ArrayList = null;
	/**
	 * Constructor de la clase.
	 * 
	 * @param dataset
	 */
	public Dataset(ArrayList<Dato> dataset) {

		// Obtenemos los atributos de la cabecera.
		atributos_cabecera = inicializarCabecera(dataset.get(0));

		// Inicializamos el arraylist de instancias.
		instancias = new ArrayList<>();

		// Añadimos las instancias del dataset a la lista de instancias.
		// Empieza a contar en 1 porque el índice 0 corresponde con el de la cabecera.
		for (int i = 1; i < dataset.size(); i++) {
			instancias.add(dataset.get(i));
		}

		// Generamos los conjuntos de los valores de los atributos y de las clases.
		generarListaAtributosyClases(dataset);

		// Asociamos cada atributo de la cabecera con su lista de valores.
		lista_cabecera_valores = new HashMap<String, Set<String>>();
		for (int i = 0; i < atributos_cabecera.size() - 1; i++) {
			lista_cabecera_valores.put(atributos_cabecera.get(i), valores_atributos.get(i));
		}

		// Asociamos cada atributo de la cabecera con un identificador.
		identificador_atributo_cabecera = new HashMap<>();
		atributo_identificador = new HashMap<>();
		for (int id = 0; id < atributos_cabecera.size(); id++) {
			identificador_atributo_cabecera.put(atributos_cabecera.get(id), id);
			atributo_identificador.put(id, atributos_cabecera.get(id));
		}
		
		dataset_ArrayList = dataset;

	}

	/**
	 * Inicializa la cabecera con los atributos de la primera instancia del dataset.
	 * 
	 * @param dato Es la primera instancia del dataset que corresponde con la
	 *             cabecera del mismo.
	 * @return
	 */
	private ArrayList<String> inicializarCabecera(Dato dato) {

		ArrayList<String> cabecera = new ArrayList<>();
		// Por cada atributo de la cabecera se añade a la lista.
		for (String s : dato.getAtributos()) {
			cabecera.add(s);
		}

		// El método getAtributos() solo obtiene los atributos y no el nombre de la
		// clase a la que pertenece
		// tenemos que añadir, por último, la clase.
		cabecera.add(dato.getClase());

		// Devuelve la lista de los atributos.
		return cabecera;
	}

	/**
	 * Inicializa los conjuntos de valores de atributos y de las clases.
	 * 
	 * @param dataset
	 */
	private void generarListaAtributosyClases(ArrayList<Dato> dataset) {

		valores_atributos = new ArrayList<>(); // Para almacenar los valores de los atributos.
		lista_clases = new HashSet<>(); // Para almacenar los tipos de clase del dataset.

		// Inicializamos la lista a tantos conjuntos vacios como atributos haya.
		for (int i = 0; i < dataset.get(0).getSize(); i++) {
			Set<String> valores = new HashSet<>();
			valores_atributos.add(valores);
		}

		// Empieza en 1 porque el 0 es la cabecera.
		for (int i = 1; i < dataset.size(); i++) {
			// Añadimos la clase al conjunto de clases.
			lista_clases.add(dataset.get(i).getClase());
			// Por cada valor del ejemplo, se añade al conjunto
			for (int j = 0; j < dataset.get(i).getSize(); j++) {
				valores_atributos.get(j).add(dataset.get(i).getAtributo(j));
			}
		}

	}

	public ArrayList<String> get_cabecera() {
		return atributos_cabecera;
	}

	public ArrayList<String> getNombreAtributos() {
		ArrayList<String> nombre_atributos = new ArrayList<>();
		for (int i = 0; i < atributos_cabecera.size() - 1; i++) {
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
	
	public HashMap<Integer, String> get_Atributos_Identificador() {
		return atributo_identificador;
	}

	public HashMap<String, Set<String>> getLista_Atributos_Valores() {
		return lista_cabecera_valores;
	}

	/**
	 * Devuelve un dataset reducido en el que se mantienen las instancias que tenían
	 * el atributo atributo con el valor valor_atributo eliminando dicha columna.
	 * 
	 * @param d
	 * @param atributo
	 * @param valor_atributo
	 * @return
	 */
	public Dataset filtrarDatasetPorAtributoValor(Dataset d, String atributo, String valor_atributo) {

		// Inicialización del dataset reducido.
		ArrayList<Dato> dataset_reducido = new ArrayList<>();

		// Obtenemos el identificador del atributo.
		int identificador_atributo = d.getIdentificador_atributo_cabecera().get(atributo);

		// Convertimos la cabecera en un nuevo dato para no modificar el parámetro.
		Dato cabecera = new Dato((d.get_cabecera().toArray(new String[0])));

		// Eliminamos el atributo de la cabecera.
		cabecera.eliminarAtributo(d.getIdentificador_atributo_cabecera().get(atributo));

		// Añadimos al dataset_reducido la nueva cabecera reducida.
		dataset_reducido.add(cabecera);

		// Por cada dato del dataset original,
		for (Dato dato : d.getInstancias()) {
			// Creamos una copia del dato para no modificar el anterior.
			Dato dato_aux = new Dato(dato);
			// Si el dato tiene como valor el pasado por parámetro
			if (dato_aux.getAtributo(identificador_atributo).equals(valor_atributo)) {
				// Se elimina el atributo y se añade el dato reducido al nuevo dataset.
				dato_aux.eliminarAtributo(identificador_atributo);
				dataset_reducido.add(dato_aux);
			}

		}

		// Se devuelve un nuevo dataset con la lista reducida.
		return new Dataset(dataset_reducido);
	}

	/**
	 * Devuelve un dataset reducido en el que se mantienen las instancias que tenían
	 * el atributo atributo con el valor valor_atributo eliminando dicha columna y,
	 * además, pertenecen a la clase pasada por parámetro.
	 * 
	 * @param d
	 * @param atributo
	 * @param valor_atributo
	 * @param clase
	 * @return
	 */
	public Dataset filtrarDatasetPorAtributoValorClase(Dataset d, String atributo, String valor_atributo,
			String clase) {

		ArrayList<Dato> dataset_reducido = new ArrayList<>();
		int identificador_atributo = d.getIdentificador_atributo_cabecera().get(atributo);

		Dato cabecera = new Dato((d.get_cabecera().toArray(new String[0])));

		dataset_reducido.add(cabecera);

		for (Dato dato : d.getInstancias()) {
			Dato dato_aux = new Dato(dato);
			if (dato_aux.getAtributo(identificador_atributo).equals(valor_atributo) && dato.getClase().equals(clase)) {

				dato_aux.eliminarAtributo(identificador_atributo);
				dataset_reducido.add(dato_aux);
			}

		}

		return new Dataset(dataset_reducido);
	}
	
	public ArrayList<Dato> toArrayList(){
		return dataset_ArrayList;		
	}

	/**
	 * La primera posición es el dataset positivo y la segunda posición el negativo.
	 * @param d
	 * @return
	 */
	public ArrayList<Dataset> filtrarDatasetPorClase() {
		ArrayList<Dataset> datasets_positivos_y_negativos = new ArrayList<>();

		ArrayList<Dato> datos_positivos = new ArrayList<>();
		ArrayList<Dato> datos_negativos = new ArrayList<>();

		for (Dato dato : this.getInstancias()) {
			if (dato.esPositivo())
				datos_positivos.add(dato);
			else
				datos_negativos.add(dato);

		}
		
		datasets_positivos_y_negativos.add(new Dataset(datos_positivos));
		datasets_positivos_y_negativos.add(new Dataset(datos_negativos));

		return datasets_positivos_y_negativos;

	}

	@Override
	public String toString() {
		String str = atributos_cabecera.toString() + "\n";
		for (Dato d : instancias)
			str += d.toString() + "\n";

		return str;
	}

}
