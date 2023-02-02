package QLearning;

public final class Constantes {
	
	/* Nombre de Fichero */
	
	public final static String FILE_NAME = "datos_jugador";
	
	
	/* Constantes de la QTable - Volante */
	public final static int NUM_STATES_STEER = 15;
	public final static float[][] STEER_VALUES = {{-0.2f}, {-0.1f}, {0f}, {0.1f}, {0.2f}};
	public final static int NUM_ANGLES = STEER_VALUES.length;
	

	/* Constantes de la QTable - Velocidad */
	public final static int NUM_STATES_VEL = 11; 
	public final static float[][] VEL_VALUES = {{0f, .1f}, {0f, .2f}, {0f, .4f}, {0f, .8f}, {0f, 1f}, 
			{0f, 0f}, {0.1f, 0f}, {0.2f, 0f}, {0.4f, 0f}, {0.8f, 0f}, {1f, 0f}};
	public final static int NUM_VEL = VEL_VALUES.length;
	
	/* Constantes de la QTable - Marchas */
	public final static int NUM_STATES_GEAR = 5; 
	public final static float[][] GEAR_VALUES = {{-1}, {0}, {1}};
	public final static int NUM_GEAR = GEAR_VALUES.length;
	
	/* Constantes del control de entrenamiento*/
	public final static int TICK_ENTRENAMIENTO = 10;
	public final static int TICK_COMIENZO = 120;
	public final static int TICKS_ESPERA = 50;
	public final static int CARRERA_JUGADOR = 6;
	
	
	/* Constantes de control de porcentaje */
	public final static double PORCENTAJE_INICIAL = 0.0;
	public final static float INCREMENTO_PORCENTAJE = 0.004f;
	public final static int MAX_CARRERAS_INCREMENTO_PORCENTAJE = 1;
	public final static double MAX_PORCENTAJE = 0.999;
	
	
	/*Constantes de segmentación de carretera*/
	public final static double CENTRO_MIN = -0.01;
	public final static double CENTRO_MAX = 0.01;
	
	
	/* Constantes de segmentación de angulos de volante */
	public final static double STEER_RECTO_MIN = -0.05;
	public final static double STEER_RECTO_MAX = 0.05;
	public final static double STEER_DERECHA = -0.5;
	public final static double STEER_IZQUIERDA = 0.5;
	
	
	private Constantes() {
		
	}
	
	
}