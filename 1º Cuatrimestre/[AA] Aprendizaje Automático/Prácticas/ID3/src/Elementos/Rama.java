package Elementos;

public class Rama {

	Nodo padre = null;
	Nodo hijo = null;
	String valor = "";
	
	public Rama(String valor) {
		this.valor = valor;
	}
	
	public void addHijo(Nodo h) {
		this.hijo = h;
	}
	
	public void addPadre(Nodo p) {
		this.padre = p;
	}
	
}
