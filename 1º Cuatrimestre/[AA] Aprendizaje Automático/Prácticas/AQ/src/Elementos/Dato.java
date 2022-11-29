package Elementos;
import static Algoritmo.Constantes.*;

import java.util.ArrayList;

public class Dato {

	private ArrayList<String> atributos;
	private String clase;
	
	public Dato(Dato d) {
		this.atributos = new ArrayList<>();
		this.atributos.addAll(d.getAtributos());
		this.clase = d.getClase();
	}
	
	public Dato(String [] atributos) {
		
		this.atributos = new ArrayList<>();
		
		for (int i=0; i< atributos.length-1; i++) {
			this.atributos.add(atributos[i]);		
		}
		
		this.clase = atributos[atributos.length-1];
		
		
		
	}

	public ArrayList<String> getAtributos() {
		return atributos;
	}
	
	public String getClase() {
		return clase;
	}
	
	public int getSize() {
		return atributos.size();
	}
	
	public String getAtributo(int i) {
		return atributos.get(i);
	}
	
	public boolean esPositivo() {
		return clase == POSITIVO;
	}
	
	public void eliminarAtributo(int id) {
		atributos.remove(id);
	}
	
	@Override
	public boolean equals(Object o) {
		Dato d = (Dato) o;
		
		if (d.getClase().equals(this.getClase()) && this.atributos.equals(d.getAtributos())) return true;
		else return false;
	}
	
	@Override
	public int hashCode() {
		return 1;
	}
	
	@Override
	public String toString() {
		
		String str = "[";
		for (String s : atributos) {
			str += s + ", ";
		}
		str += clase + "]";
		
		return str;
		
	}

}
