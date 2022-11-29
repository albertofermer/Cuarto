package Elementos;

import java.util.Set;

public class Selector {
	
	private String atributo = null;
	private String operador = null;
	private String valor = null;
	
	public Selector(String atributo, String operador, String valor) {
		this.atributo = atributo;
		this.operador = operador;
		this.valor = valor;
	}
	
	
	@Override
	public String toString() {
		return "(" + atributo + " " + operador + " " + valor + ")";
	}
	
	public static void main(String[] args) {
		Selector s = new Selector("Atributo", ">", "7");
		
		System.out.println(s);
	}
}
