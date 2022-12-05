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
	public Set<Complejo> aplicar(Set<Complejo> conjunto_complejos) {
		Set<Complejo> complejo_seleccionado = new HashSet<>();

		switch (indicador) {
		case ">":
			// Obtiene el máximo del criterio seleccionado
			switch (criterio) {
			case "cobertura":
				int cobertura_max = 0;
				for (Complejo c : conjunto_complejos) {
					if (c.getCobertura() > cobertura_max) {
						cobertura_max = c.getCobertura();
					}
				}
				
				for (Complejo c : conjunto_complejos) {
					if (c.getCobertura() == cobertura_max) {
						complejo_seleccionado.add(c);
					}
				}
				break;
			case "simplicidad":
				float simplicidad_max = 0;
				for (Complejo c : conjunto_complejos) {
					if (c.getSimplicidad() > simplicidad_max) {
						simplicidad_max = c.getSimplicidad();
					}
				}
				for (Complejo c : conjunto_complejos) {
					if (c.getSimplicidad() == simplicidad_max) {
						complejo_seleccionado.add(c);
					}
				}
				break;
			case "coste":
				int coste_max = 0;
				for (Complejo c : conjunto_complejos) {
					if (c.getCoste() > coste_max) {
						coste_max = c.getCoste();
					}
				}
				for (Complejo c : conjunto_complejos) {
					if (c.getCoste() == coste_max) {
						complejo_seleccionado.add(c);
					}
				}
				break;
			case "generalidad":
				float generalidad_max = 0;
				for (Complejo c : conjunto_complejos) {
					if (c.getGeneralidad() > generalidad_max) {
						generalidad_max = c.getGeneralidad();
					}
				}
				
				for (Complejo c : conjunto_complejos) {
					if (c.getGeneralidad() == generalidad_max) {
						complejo_seleccionado.add(c);
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
						cobertura_min = c.getCobertura();
					}
				}
				for (Complejo c : conjunto_complejos) {
					if (c.getCobertura() == cobertura_min) {
						complejo_seleccionado.add(c);
					}
				}
				break;
			case "simplicidad":
				float simplicidad_min = Float.MAX_VALUE;
				for (Complejo c : conjunto_complejos) {
					if (c.getSimplicidad() < simplicidad_min) {
						simplicidad_min = c.getSimplicidad();
					}
				}
				for (Complejo c : conjunto_complejos) {
					if (c.getSimplicidad() == simplicidad_min) {
						complejo_seleccionado.add(c);
					}
				}
				break;
			case "coste":
				int coste_min = Integer.MAX_VALUE;
				for (Complejo c : conjunto_complejos) {
					if (c.getCoste() < coste_min) {
						coste_min = c.getCoste();
					}
				}
				for (Complejo c : conjunto_complejos) {
					if (c.getCoste() == coste_min) {
						complejo_seleccionado.add(c);
					}
				}
				break;
			case "generalidad":
				float generalidad_min = 1; // Como mucho tendrá cociente 1 porque será totalmente general.
				for (Complejo c : conjunto_complejos) {
					if (c.getGeneralidad() < generalidad_min) {
						generalidad_min = c.getGeneralidad();
					}
				}
				for (Complejo c : conjunto_complejos) {
					if (c.getGeneralidad() == generalidad_min) {
						complejo_seleccionado.add(c);
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
