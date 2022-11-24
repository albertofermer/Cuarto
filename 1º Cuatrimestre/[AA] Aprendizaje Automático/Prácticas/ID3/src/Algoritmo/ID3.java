package Algoritmo;
import java.util.ArrayList;
import Elementos.*;

public class ID3 {

	private ArrayList<Dato> dataset = null;
	public Nodo raiz = null;
	
	public ID3(ArrayList<Dato> dataset) {
		
		this.dataset = dataset;
		
	}
	
	public Nodo algoritmo(ArrayList<Dato> dataset) {
	
		if (mismaClase(dataset))
			return new Nodo(dataset.get(0).getClase());
		else if ()
		
	}

	private boolean mismaClase(ArrayList<Dato> dataset2) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
