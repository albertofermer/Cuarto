package Elementos;
public class Dato {

	private String [] atributos;
	public final String POSITIVO = "+";
	public final String NEGATIVO = "-";
	
	public Dato(String [] atributos) {
		
		this.setAtributos(atributos);
		
	}

	public String [] getAtributos() {
		return atributos;
	}

	public void setAtributos(String [] atributos) {
		this.atributos = atributos;
	}
	
	public int getSize() {
		return atributos.length;
	}
	
	public String getDato(int i) {
		return atributos[i];
	}

}
