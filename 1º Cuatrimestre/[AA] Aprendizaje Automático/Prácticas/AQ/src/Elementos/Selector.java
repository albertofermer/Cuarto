package Elementos;

import java.util.HashMap;
import java.util.Set;

public class Selector {
	
	private String atributo = null;
	private String operador = null;
	private String valor = null;
	private HashMap<String, Integer> atributo_identificador = null;
	
	public Selector(String atributo, String operador, String valor, HashMap<String, Integer> atributo_identificador) {
		this.atributo = atributo;
		this.operador = operador;
		this.valor = valor;
		this.atributo_identificador = atributo_identificador;
	}
	
	
	@Override
	public String toString() {
		return "(" + atributo + " " + operador + " " + valor + ")";
	}
	
	public static void main(String[] args) {
		Selector s = new Selector("Atributo", "=", "7", null);
		
		System.out.println(s);
	}


	public boolean cubre(Dato p) {
		return (p.getAtributo(atributo_identificador.get(this.atributo)) == this.valor);
	}
}
