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
	
	public static void main(String[] args) {
		Nodo n = new Nodo("raiz");
		
		Nodo h1 = new Nodo("H1");
		Nodo h2 = new Nodo("H2");
		Nodo h3 = new Nodo("H3");
		
		Rama r1 = new Rama("V1");
		Rama r2 = new Rama("V2");
		Rama r3 = new Rama("V3");
		
		Rama r4 = new Rama("V1.1");
		Nodo h4 = new Nodo("H4");
		
		r1.addPadre(n); r1.addHijo(h1);
		r2.addPadre(n); r2.addHijo(h2);
		r3.addPadre(n); r3.addHijo(h3);
		
		System.out.println(n);
		
		
		
	}
	
	

}
