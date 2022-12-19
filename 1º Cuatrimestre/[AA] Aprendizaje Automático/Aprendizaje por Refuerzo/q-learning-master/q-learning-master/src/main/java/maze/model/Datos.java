package maze.model;

import java.util.ArrayList;
import java.util.List;

public class Datos {

	private List<Integer> longitud_caminos = null;
	private List<Long> tiempo = null;
	private List<Double> recompensa_acumulada = null;
	
	public Datos() {
		longitud_caminos = new ArrayList<>();
		tiempo = new ArrayList<>();
		recompensa_acumulada = new ArrayList<>();
	}

	public List<Integer> getLongitud_caminos() {
		return longitud_caminos;
	}

	public List<Long> getTiempo() {
		return tiempo;
	}

	public List<Double> getRecompensa_acumulada() {
		return recompensa_acumulada;
	}
	
	public void addLongitudCaminos(Integer lc) {
		longitud_caminos.add(lc);
	}
	
	public void addTiempo(Long t) {
		tiempo.add(t);
	}
	
	public void addRecompensa(Double recompensa) {
		recompensa_acumulada.add(recompensa);
	}
	
	
}
