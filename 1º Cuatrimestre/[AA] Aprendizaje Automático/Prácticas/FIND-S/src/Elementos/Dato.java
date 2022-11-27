package Elementos;
import static Algoritmo.Constantes.*;

import java.util.ArrayList;
import java.util.Set;
public class Dato {

	private String [] atributos;
	private String clase;
	
	public Dato(String [] atributos) {
		
		this.atributos = new String[atributos.length-1];
		
		for (int i=0; i< atributos.length-1; i++) {
			this.atributos[i] = atributos[i];			
		}		
		this.clase = atributos[atributos.length-1];
		
		
		
	}

	public String [] getAtributos() {
		return atributos;
	}
	
	public String getClase() {
		return clase;
	}

	public void setAtributos(String [] atributos) {
		this.atributos = atributos;
	}
	
	public int getSize() {
		return atributos.length;
	}
	
	public String getAtributo(int i) {
		return atributos[i];
	}
	
	public boolean esPositivo() {
		return clase.equals(POSITIVO);
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
