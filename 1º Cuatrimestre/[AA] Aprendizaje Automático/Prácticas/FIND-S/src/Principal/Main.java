package Principal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import Algoritmo.FindS;
import Elementos.*;
import static Algoritmo.Constantes.*;

public class Main {

	private static ArrayList<Dato> dataset = new ArrayList<>();
	
	public Main() {	}

	public static void main(String[] args) throws Exception {
		
		/*	DATASET	 */
		String [] x1 = {"SUNNY","WARM","NORMAL","STRONG","WARM", "SAME", POSITIVO};
		String [] x2 = {"SUNNY","WARM","HIGH","STRONG","WARM", "SAME", POSITIVO};
		String [] x3 = {"RAINY","COLD","HIGH","STRONG","WARM", "CHANGE", NEGATIVO};
		String [] x4 = {"SUNNY","WARM","HIGH","STRONG","COOL", "CHANGE", POSITIVO};
		
		dataset.add(new Dato(x1));
		dataset.add(new Dato(x2));
		dataset.add(new Dato(x3));
		dataset.add(new Dato(x4));
		
		ArrayList<Set<String>> lista_atributos = listaAtributos(dataset);
		
		FindS algorithm_findS = new FindS(dataset);
		System.out.println(algorithm_findS.algoritmo());
		

	}
	
	public static ArrayList<Set<String>> listaAtributos(ArrayList<Dato> dataset) {
		
		ArrayList<Set<String>> lista_atributos = new ArrayList<>(); // Para almacenar los tipos de atributos que hay
		
		for (int i=0; i< dataset.get(0).getSize(); i++) {
			Set<String>	a = new HashSet<>();
			lista_atributos.add(a);
		}
		
		for (Dato d: dataset) {
			for (int i = 0; i < d.getSize(); i++) {
				lista_atributos.get(i).add(d.getAtributo(i));
			}
		}
		
		return lista_atributos;
	}
	
}
