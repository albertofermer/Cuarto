package Algoritmo;

import java.util.Set;
/**
 * Valores que se mantendrán constantes una vez iniciado el
 * algoritmo.
 * 
 * @author Alberto Fernández
 *
 */
public class Constantes {

	public Constantes() {}

	public static final String VACIO = "V";
	public static final String TODO = "?";
	public static  String POSITIVO = "+";
	public static  String NEGATIVO = "-";
	
	/**
	 * Toma como clase positiva la pasada por parámetro y como clase negativa
	 * la complementaria.
	 * 
	 * @param clasePositiva
	 * @param lista_clases
	 */
	public void setClasePositiva(String clasePositiva, Set<String> lista_clases) {
		POSITIVO = clasePositiva;
		for (String s : lista_clases) {
			if (!s.equals(POSITIVO)) {
				NEGATIVO = s;
			}
		}
		
	}
	

	
}