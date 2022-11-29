package Elementos;

import java.util.ArrayList;

public class Complejo {
	ArrayList<Selector> conjuncion_selectores = null;
	
	public Complejo(ArrayList<Selector> conjuncion_selectores) {
		this.conjuncion_selectores = conjuncion_selectores;
	}
	
	
	
	@Override
	public String toString() {
		String str = "";
		
		for (int i = 0; i < conjuncion_selectores.size() - 1 ; i++) {
			str += conjuncion_selectores.get(i).toString() + "^";
		}
		
		str += conjuncion_selectores.get(conjuncion_selectores.size()-1).toString();
		
		return str;
	}
	
	public static void main(String[] args) {
		Selector s = new Selector("Atributo", ">", "7");
		ArrayList<Selector> selectores = new ArrayList<>();
		selectores.add(s);
		selectores.add(s);
		Complejo c = new Complejo(selectores);
		
		System.out.println(c);
	}
	
}
