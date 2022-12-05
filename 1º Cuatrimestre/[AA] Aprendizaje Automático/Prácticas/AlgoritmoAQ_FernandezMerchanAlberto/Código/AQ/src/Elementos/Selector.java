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

	public String getAtributo() {
		return atributo;
	}

	public String getOperador() {
		return operador;
	}

	public String getValor() {
		return valor;
	}

	@Override
	public String toString() {
		return "(" + atributo + " " + operador + " " + valor + ")";
	}

	@Override
	public int hashCode() {
		return 1;
	}
	@Override
	public boolean equals(Object o) {
		Selector s = (Selector) o;
		
		return (this.atributo.equals(s.getAtributo()) &&
				this.operador.equals(s.getOperador()) &&
				this.valor.equals(s.getValor()));
	}

	public static void main(String[] args) {
		Selector s = new Selector("Atributo", "=", "7", null);

		System.out.println(s);
	}

	public boolean incluidoEn(Complejo c) {
		
		for (Selector s : c.getSelectores()) {
			if (this.equals(s)) return true;
		}
		
		return false;
	
	}
	
	public boolean incluidoEn(Set<Complejo> e) {
		
		for (Complejo c : e) {
			if(this.incluidoEn(c)) return true;
		}
		
		return false;
	}	
	
	public boolean cubre(Dato p) {
		return (p.getAtributo(atributo_identificador.get(this.atributo)).equals(this.valor));
	}


}
