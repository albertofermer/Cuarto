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
	
	public boolean esHoja() {
		return ramas.isEmpty();
	}
	
	
	
	@Override
	public String toString() { // Convertir a reglas if-else
		
		String regla_if = "if (" + atributo + " == ";
		String str = "";
				
		if (!ramas.isEmpty()) {
			for (Rama r : ramas) {
				
				if (!r.getHijo().esHoja()) {
					str += "\n" + regla_if + r + ") then \n{\n";
					str += "" + r.getHijo() + "\n\n}\n";
				}
				else{
					str += "\n" + regla_if + r + ") then ";
					str += "" + r.getHijo() + "";
				}

				
			}
			
		}else {
			str = atributo + ";";
		}
		
		
		return str;
		
	}	

}
