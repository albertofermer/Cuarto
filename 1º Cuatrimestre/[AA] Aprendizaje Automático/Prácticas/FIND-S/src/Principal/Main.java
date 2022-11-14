package Principal;
import java.util.ArrayList;

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
		
		FindS algorithm_findS = new FindS(dataset);
		System.out.println(algorithm_findS.algoritmo());
		

	}	
	
}
