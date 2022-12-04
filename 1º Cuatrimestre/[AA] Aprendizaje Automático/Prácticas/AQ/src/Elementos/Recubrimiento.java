package Elementos;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

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
		ArrayList<Complejo> disyuncion_complejos = new ArrayList<>();
		disyuncion_complejos.addAll(this.disyuncion_complejos);
		
		if(disyuncion_complejos.size()>1)
		for (int i = 0; i < disyuncion_complejos.size() - 1 ; i++) {
			str += disyuncion_complejos.get(i).toString() + "V";
		}
				
		str += disyuncion_complejos.get(disyuncion_complejos.size()-1).toString();
		
		return str;
	}
	
	public void add(Complejo c) {
		disyuncion_complejos.add(c);
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
