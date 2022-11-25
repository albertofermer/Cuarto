package Elementos;

import java.util.ArrayList;

public class Nodo {
	
	String atributo = "";
	ArrayList<Rama> ramas = null;
	
	public Nodo(String atributo) {
		this.atributo = atributo;
		ramas = new ArrayList<>();
	}
	
	public void addRama(Rama r) {
		ramas.add(r);
	}
	
	
	
	@Override
	public String toString() { // Convertir a reglas if-else
		
		String regla_if = "if (" + atributo + " == ";
		String str = ".";
		
		System.out.println(ramas);
		
		if (ramas != null) {
			for (Rama r : ramas) {
				str = regla_if + r + ")\n";
				System.out.println(r.getHijo());
				str += r.getHijo();
				
			}
			
		}
		
		
		return str;
		
	}
	
	

}
