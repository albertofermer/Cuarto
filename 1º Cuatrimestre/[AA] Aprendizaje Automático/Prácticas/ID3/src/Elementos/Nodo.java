package Elementos;

import java.util.ArrayList;

public class Nodo {
	
	String atributo = "";
	ArrayList<Rama> ramas = null;
	
	public Nodo(String atributo) {
		this.atributo = atributo;
		ramas = new ArrayList<>();
	}
	
	public void addRama(Rama r) {
		ramas.add(r);
	}
	

}
