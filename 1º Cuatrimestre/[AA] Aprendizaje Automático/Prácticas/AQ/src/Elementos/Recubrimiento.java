package Elementos;

import java.util.ArrayList;

public class Recubrimiento {
	
	ArrayList<Complejo> disyuncion_complejos = null;
	
	public Recubrimiento() {
		disyuncion_complejos = new ArrayList<>();
	}
	
	public Recubrimiento(ArrayList<Complejo> disyuncion_complejos) {
		this.disyuncion_complejos = disyuncion_complejos;
	}
	
	@Override
	public String toString() {
		String str = "";
		
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
		
		Selector s = new Selector("Atributo", "==", "7");
		
		ArrayList<Selector> selectores = new ArrayList<>();
		selectores.add(s);
		selectores.add(s);
		Complejo c = new Complejo(selectores);
		ArrayList<Complejo> complejos = new ArrayList<>();
		complejos.add(c);
		complejos.add(c);
		
		
		
		Recubrimiento r = new Recubrimiento(complejos);
		
		System.out.println(r);
		
	}
}
