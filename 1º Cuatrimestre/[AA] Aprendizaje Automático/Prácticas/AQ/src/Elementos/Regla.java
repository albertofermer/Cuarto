package Elementos;

import java.util.HashSet;
import java.util.Set;

public class Regla {

	private String indicador = null; // mayor o menor
	private String criterio = null; // cobertura, simplicidad, coste, generalidad

	public Regla(String indicador, String criterio) {

		this.indicador = indicador;
		this.criterio = criterio;
	}

	/**
	 * Devuelve el subconjunto de complejos de conjunto_complejos que cumplen la
	 * regla
	 * 
	 * @param conjunto_complejos
	 * @return
	 */
	public Complejo aplicar(Set<Complejo> conjunto_complejos) {
		Complejo complejo_seleccionado = new Complejo();

		switch (indicador) {
		case ">":
			// Obtiene el máximo del criterio seleccionado
			switch (criterio) {
			case "cobertura":
				int cobertura_max = 0;
				for (Complejo c : conjunto_complejos) {
					if (c.getCobertura() > cobertura_max) {
						complejo_seleccionado = c;
						cobertura_max = c.getCobertura();
					}
				}
				break;
			case "simplicidad":
				float simplicidad_max = 0;
				for (Complejo c : conjunto_complejos) {
					if (c.getSimplicidad() > simplicidad_max) {
						complejo_seleccionado = c;
						simplicidad_max = c.getSimplicidad();
					}
				}
				break;
			case "coste":
				int coste_max = 0;
				for (Complejo c : conjunto_complejos) {
					if (c.getCoste() > coste_max) {
						complejo_seleccionado = c;
						coste_max = c.getCoste();
					}
				}
				break;
			case "generalidad":
				float generalidad_max = 0;
				for (Complejo c : conjunto_complejos) {
					if (c.getGeneralidad() > generalidad_max) {
						complejo_seleccionado = c;
						generalidad_max = c.getGeneralidad();
					}
				}
				break;
			default:
				throw new IllegalArgumentException("Unexpected value: " + criterio);
			}
			break;

		case "<":
			// Obtiene el minimo del criterio seleccionado
			switch (criterio) {
			case "cobertura":
				int cobertura_min = Integer.MAX_VALUE;
				for (Complejo c : conjunto_complejos) {
					if (c.getCobertura() < cobertura_min) {
						complejo_seleccionado = c;
						cobertura_min = c.getCobertura();
					}
				}
				break;
			case "simplicidad":
				float simplicidad_min = Integer.MAX_VALUE;
				for (Complejo c : conjunto_complejos) {
					if (c.getSimplicidad() < simplicidad_min) {
						complejo_seleccionado = c;
						simplicidad_min = c.getSimplicidad();
					}
				}
				break;
			case "coste":
				int coste_min = Integer.MAX_VALUE;
				for (Complejo c : conjunto_complejos) {
					if (c.getCobertura() < coste_min) {
						complejo_seleccionado = c;
						coste_min = c.getCoste();
					}
				}
				break;
			case "generalidad":
				float generalidad_min = 1;
				for (Complejo c : conjunto_complejos) {
					if (c.getGeneralidad() < generalidad_min) {
						complejo_seleccionado = c;
						generalidad_min = c.getGeneralidad();
					}
				}
				break;
			default:
				throw new IllegalArgumentException("Unexpected value: " + criterio);
			}
			break;
		default:
			throw new IllegalArgumentException("Unexpected value: " + indicador);
		}

		return complejo_seleccionado;
	}

}
