package Elementos;
import java.util.ArrayList;

public class Hipotesis {

	private ArrayList<String> patrones = new ArrayList<>();
	
	public Hipotesis(ArrayList<String> h) {
		
		this.patrones = h;
		
	}
	
	@Override
	public String toString(){
		
		return patrones.toString();
		
	}
	
	@Override
	public boolean equals(Object o) {
		
		return patrones.contains(o);
	}
	
	public ArrayList<String> getAllHypothesis(){
		return patrones;
	}
	
	public String getPatron(int j) throws Exception{
		try {
			
			return patrones.get(j);
			
			
		} catch (Exception e) {
			System.out.println("¡Error. No hay tantas hipótesis!");
			return null;
		}
	}

	
//	public static void main(String[] args) {
//		ArrayList<String> h1 = new ArrayList<>();
//		h1.add("W"); h1.add("X");
//		Hipotesis h = new Hipotesis(h1);
//		
//		System.out.println(h.equals("O"));
//	}
}
