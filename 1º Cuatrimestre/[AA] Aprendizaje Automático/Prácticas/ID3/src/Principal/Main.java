package Principal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import Algoritmo.ID3;
import Elementos.*;
import static Algoritmo.Constantes.*;

public class Main {

	private static ArrayList<Dato> dataset = new ArrayList<>();
	
	public Main() {	}

	public static void main(String[] args) throws Exception {
		
		/*	DATASET	 */
		String [] x1 = {"1","0","2","Rayado", NEGATIVO};
		String [] x2 = {"1","0","1","Blanco", POSITIVO};
		String [] x3 = {"1","2","0","Rayado", NEGATIVO};
		String [] x4 = {"0","2","1","Rayado", NEGATIVO};
		String [] x5 = {"1","1","1","Rayado", POSITIVO};
		String [] x6 = {"2","2","1","Rayado", POSITIVO};
		
		dataset.add(new Dato(x1));
		dataset.add(new Dato(x2));
		dataset.add(new Dato(x3));
		dataset.add(new Dato(x4));
		
		ArrayList<Set<String>> lista_atributos = listaAtributos(dataset);
		
		//ID3 algorithm_findS = new ID3(dataset);
		//System.out.println(algorithm_findS.algoritmo());
		

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
