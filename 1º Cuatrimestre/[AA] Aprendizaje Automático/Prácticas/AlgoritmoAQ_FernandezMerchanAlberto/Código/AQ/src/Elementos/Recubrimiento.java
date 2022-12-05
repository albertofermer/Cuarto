package Elementos;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import Algoritmo.Constantes;

public class Recubrimiento {
	
	Set<Complejo> disyuncion_complejos = null;
	
	public Recubrimiento() {
		disyuncion_complejos = new HashSet<>();
	}
	
	public Recubrimiento(Set<Complejo> disyuncion_complejos) {
		this.disyuncion_complejos = disyuncion_complejos;
	}
	
	@Override
	public String toString() {
		String str = "";
		
		for (Complejo c : disyuncion_complejos) {
			str += "SI " + c + " ENTONCES " + Constantes.POSITIVO + "\n";
		}
		
		return str;
	}
	
	public void add(Complejo c) {
		disyuncion_complejos.add(c);
	}
	
	public void addAll(Collection<Complejo> c) {
		for (Complejo comp : c) {
			disyuncion_complejos.add(comp);
		}
	}
	
	public static void main(String[] args) {
		
		Selector s = new Selector("Atributo", "==", "7",null);
		
		Set<Selector> selectores = new HashSet<>();
		selectores.add(s);
		selectores.add(s);
		Complejo c = new Complejo(selectores);
		Complejo c2 = new Complejo(selectores);
		Set<Complejo> complejos = new HashSet<>();
		complejos.add(c);
		complejos.add(c2);
		
		System.out.println(c.equals(c2));
		
		Recubrimiento r = new Recubrimiento(complejos);
		
		System.out.println(r);
		
	}
}
