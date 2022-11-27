package Algoritmo;

import java.util.Set;

public class Constantes {

	public Constantes() {
		// restrict instantiation
	}

	public static final String VACIO = "0";
	public static final String TODO = "?";
	public static  String POSITIVO = "+";
	public static  String NEGATIVO = "-";
	
	public void setClasePositiva(String clasePositiva, Set<String> lista_clases) {
		POSITIVO = clasePositiva;
		for (String s : lista_clases) {
			if (!s.equals(POSITIVO)) {
				NEGATIVO = s;
			}
		}
		
	}
	

	
}