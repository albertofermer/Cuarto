package Velocidad;

import java.util.ArrayList;
import java.util.Random;
import Datos.Dato;
import QLearning.*;
import champ2011client.Action;
import champ2011client.Controller;
import champ2011client.SensorModel;
import champ2011client.SocketHandler;

public class TrainVelocidad extends Controller {

	/* Gear Changing Constants */
	final int[] gearUp = { 5000, 6000, 6000, 6500, 7000, 0 };
	final int[] gearDown = { 0, 2500, 3000, 3000, 3500, 3500 };

	/* Stuck constants */
	final int stuckTime = 25;
	final float stuckAngle = (float) 0.523598775; // PI/6

	/* Accel and Brake Constants */
	final float maxSpeedDist = 7;
	final float maxSpeed = 50;
	final float sin5 = (float) 0.08716;
	final float cos5 = (float) 0.99619;

	/* Steering constants */
	final float steerLock = (float) 0.785398;
	final float steerSensitivityOffset = (float) 80.0;
	final float wheelSensitivityCoeff = 1;

	/* ABS Filter Constants */
	final float wheelRadius[] = { (float) 0.3179, (float) 0.3179, (float) 0.3276, (float) 0.3276 };
	final float absSlip = (float) 2.0;
	final float absRange = (float) 3.0;
	final float absMinSpeed = (float) 3.0;

	/* Clutching Constants */
	final float clutchMax = (float) 0.5;
	final float clutchDelta = (float) 0.05;
	final float clutchRange = (float) 0.82;
	final float clutchDeltaTime = (float) 0.02;
	final float clutchDeltaRaced = 10;
	final float clutchDec = (float) 0.01;
	final float clutchMaxModifier = (float) 1.3;
	final float clutchMaxTime = (float) 1.5;

	Integer oldState;
	Integer oldAction;

	Integer iRestart = 0;
	Integer contador_entrenamientos = 0; // se resetea
	Double recompensa_acumulada = 0.0;
	Integer indice_carreras = 0; // no se resetea

	Integer lastLap = 0;
	Integer tick = 0;

	float oldSteer;
	float oldAccel;
	float oldBrake;
	double oldTrackPosition = 0.0;
	int count_tick = 0;

	double porcentaje = Constantes.PORCENTAJE_INICIAL;
	boolean isStuck = false;
	double bestLapTick = Double.MAX_VALUE;

	// Datos
	Dato datos;

	// current clutch
	private float clutch = 0;

	/* Q-Table - Volante */
	/////////////////////////////////////////////////////////////////////////
	private static QTable qtable_velocidad = new QTable("Velocidad", Constantes.NUM_STATES_VEL, Constantes.NUM_VEL,
			Constantes.VEL_VALUES);
	private static QTableFrame qTableFrame_velocidad = new QTableFrame(qtable_velocidad, Constantes.VEL_VALUES,
			Constantes.NUM_VEL);
	private Random randomGenerator = new Random();
	/////////////////////////////////////////////////////////////////////////

	private float last_steer;
	private double last_trackPosition;
	private double last_distRaced;
	private double last_distFromStartLine;

	private boolean carrera_terminada = false;

	private String name_qtable = "qtable_velocidad";
	private String name_politica = "volante";
	String name_datos = Constantes.FILE_NAME + "_velocidad";

	ArrayList<float[]> recompensa = new ArrayList<>();
	
	SocketHandler mySocket;
	Politica politica_volante;

	public TrainVelocidad() {
		politica_volante = new Politica();
		politica_volante.loadPolitica(name_politica);

		datos = new Dato();
		qtable_velocidad.loadQTable(name_qtable);
		qTableFrame_velocidad.setQTable(qtable_velocidad);
		datos.writeHeader(name_datos); // escribe el header.

		qtable_velocidad.saveQTable(name_qtable);
	}

	public void reset() {

		if (contador_entrenamientos == Constantes.CARRERA_JUGADOR) {
			/* Escribimos los datos que vamos a sacar para hacer grÃ¡ficas */
			datos.setIndice_carrera(indice_carreras);
			datos.setTicks_duracion(tick);
			datos.setLongitud_recorrida(last_distRaced);
			datos.setEpsilon(1 - porcentaje);
			datos.writeDistRaced(name_datos);
		}

		iRestart++;
		contador_entrenamientos++;
		indice_carreras++;
		tick = 0;
		recompensa_acumulada = 0.0;
		//contador_vueltas = 0;
		oldTrackPosition = 0.0;

		qtable_velocidad.saveQTable(name_qtable);

		if (contador_entrenamientos == Constantes.CARRERA_JUGADOR + 1)
			contador_entrenamientos = 0;

	}

	public void shutdown() {
		qtable_velocidad.saveQTable(name_qtable);
		// Politica.savePolitica(name_politica, qtable_velocidad);

		if (contador_entrenamientos == Constantes.CARRERA_JUGADOR) {
			/* Escribimos los datos que vamos a sacar para hacer grÃ¡ficas */
			datos.setIndice_carrera(indice_carreras);
			datos.setTicks_duracion(tick);
			datos.setLongitud_recorrida(last_distRaced);
			datos.setEpsilon(1 - porcentaje);
			datos.writeDistRaced(name_datos);
		}

		System.out.println("Bye bye!");
	}

	private int getGear(SensorModel sensors) {
		int gear = sensors.getGear();
		double rpm = sensors.getRPM();

		// if gear is 0 (N) or -1 (R) just return 1
		if (gear < 1)
			return 1;
		// check if the RPM value of car is greater than the one suggested
		// to shift up the gear from the current one
		if (gear < 6 && rpm >= gearUp[gear - 1])
			return gear + 1;
		else
		// check if the RPM value of car is lower than the one suggested
		// to shift down the gear from the current one
		if (gear > 1 && rpm <= gearDown[gear - 1])
			return gear - 1;
		else // otherwhise keep current gear
			return gear;
	}


	public Action control(SensorModel sensors, SocketHandler mySocket) {
		System.out.println("Mejor Vuelta: " + bestLapTick);
		if (sensors.getLastLapTime() > 0.0) {
			System.out.println("VUELTA TERMINADA!");
			train(getSpeedState(sensors), getPorcentaje(sensors), sensors, true);
			
			recompensa = new ArrayList<>();
			Action restart = new Action();
			restart.restartRace = true;
			return restart;
		}

		this.mySocket = mySocket;

		// compute gear
		int gear = getGear(sensors);

		// compute steering
		float steer = politica_volante.getSteer(getSteerState(sensors));
		float accel = 0.0f;
		float brake = 0.0f;

		System.out.println("Tick: " + tick);
		System.out.println("Entrenamiento: " + contador_entrenamientos);
		System.out.println("Carrera #" + indice_carreras);

		if (tick >= Constantes.TICK_COMIENZO && (tick % Constantes.TICK_ENTRENAMIENTO == 0)
				&& contador_entrenamientos < Constantes.CARRERA_JUGADOR) {

			/**
			 * A partir del tick de comienzo, comenzamos a entrenar el coche. Lo haremos
			 * cada TICK_ENTRENAMIENTO ticks para no aprender cada tick.
			 */
			System.out.println("TRAIN");
			/* TODO : AQUI HAY QUE PONER VELOCIDAD */
			float[] accel_and_brake = train(getSpeedState(sensors), getPorcentaje(sensors), sensors, false);
			accel = accel_and_brake[0];
			brake = accel_and_brake[1];
			
		} else if (tick >= Constantes.TICK_COMIENZO && contador_entrenamientos == Constantes.CARRERA_JUGADOR) {
			/**
			 * Cada 10 entrenamientos, probamos a jugar con el jugador para ver su progreso
			 * y poder sacar resultados consistentes.
			 */
			System.out.println("--JUGADOR--");
			float[] accel_and_brake = play(sensors);
			// Si el coche se sale de la pista, reiniciamos la partida.
			if (isStuck) {
				Action reset = new Action();
				reset.restartRace = true;
				isStuck = false;
				return reset;
			}

			accel = accel_and_brake[0];
			brake = accel_and_brake[1];

		} else {
			accel = oldAccel;
			brake = oldBrake;
		}

		tick++;

		clutch = clutching(sensors, clutch);

		
		/**
		 * Si el coche no se mueve, al menos, una diferencia
		 * de 5 metros en 10 ticks, se reinicia el juego y se puntúa
		 * negativamente.
		 */
		System.out.println(Math.abs(sensors.getTrackPosition() - oldTrackPosition));
		if (Math.abs(sensors.getTrackPosition() - oldTrackPosition) <=0.00001 ) {
			// Si hay una diferencia minima aumenta en uno el contador.
			count_tick++;
		}else {
			// Si aumenta dicha diferencia, se resetea el contador.
			count_tick = 0;
		}
		
		// Actualiza la posición de referencia cada X ticks.
		if(tick > Constantes.TICK_COMIENZO && tick % Constantes.TICKS_ESPERA == 0) {
			oldTrackPosition = sensors.getTrackPosition();
		}
		
		// build a CarControl variable and return it
		Action action = new Action();

		action.gear = gear;
		action.steering = steer;
		action.accelerate = accel;
		action.brake = 0;
		action.clutch = 0;

		oldAccel = accel;
		oldBrake = brake;

		return action;
	}

	private void recompensar(ArrayList<float[]> recompensa, char modo) {
		Double reward = 2.0;
		if(modo == '-')
			reward = -1.0;
		
//		accion_recompensa[0] = oldState;
//		accion_recompensa[1] = newState;
//		accion_recompensa[2] = accion;
//		accion_recompensa[3] = oldAction;
			
		for(int i = recompensa.size()-1; i >= 0; i--) {
			float[] accion_reward = recompensa.get(i);
			qtable_velocidad.setReward(Integer.valueOf((int)accion_reward[0]), Integer.valueOf((int)accion_reward[1]), Integer.valueOf((int)accion_reward[2]), 
					Integer.valueOf((int)accion_reward[3]), reward, getBestMoveFromTarget((int) accion_reward[1]));
		}		
	}

	private float[] play(SensorModel sensors) {

		Integer state = getSpeedState(sensors);
		if (state == 9 || count_tick >= 10) {
			isStuck = true;
			float[] default_value = { 0f, 0f };
			return default_value;
		}

		int vel = qtable_velocidad.getBestRewardPosition(state);

		last_distRaced = sensors.getDistanceRaced();
		System.out.println("PLAY -> " + Constantes.VEL_VALUES[vel][0]);
		return Constantes.VEL_VALUES[vel];
	}

	private double getPorcentaje(SensorModel sensors) {

		if (iRestart == Constantes.MAX_CARRERAS_INCREMENTO_PORCENTAJE) {
			porcentaje += Constantes.INCREMENTO_PORCENTAJE;
			iRestart = 0;
		}

		if (porcentaje > Constantes.MAX_PORCENTAJE)
			porcentaje = Constantes.MAX_PORCENTAJE;

		return porcentaje;
	}

	private boolean estaEntre(double valor, double minimo, double maximo) {
		return (minimo <= valor && valor <= maximo);

	}

	private Integer getSpeedState(SensorModel sensors) {

		double distVec9 = sensors.getTrackEdgeSensors()[9];

		if (estaEntre(distVec9, 20, 40))
			return 0;
		if (estaEntre(distVec9, 40, 60))
			return 1;
		if (estaEntre(distVec9, 60, 80))
			return 2;
		if (estaEntre(distVec9, 80, 100))
			return 3;
		if (estaEntre(distVec9, 100, 120))
			return 4;
		if (estaEntre(distVec9, 120, 140))
			return 5;
		if (estaEntre(distVec9, 140, 160))
			return 6;
		if (estaEntre(distVec9, 160, 180))
			return 7;
		if (estaEntre(distVec9, 180, 200))
			return 8;

		return 9;
	}

	private Integer getSteerState(SensorModel sensors) {
		// derecha negativo izquierda positivo
		double trackPosition = sensors.getTrackPosition();
		double carAngle = sensors.getAngleToTrackAxis();

		if (estaEntre(trackPosition, Constantes.CENTRO_MIN, Constantes.CENTRO_MAX)) {
			if (estaEntre(carAngle, Constantes.STEER_RECTO_MIN, Constantes.STEER_RECTO_MAX))
				return 0; // centro - coche mira recto
			else if (estaEntre(carAngle, Constantes.STEER_RECTO_MAX, Constantes.STEER_IZQUIERDA))
				return 1; // centro - coche mira der
			else if (estaEntre(carAngle, Constantes.STEER_DERECHA, Constantes.STEER_RECTO_MIN))
				return 2; // centro - coche mira izq
			else if (estaEntre(carAngle, Constantes.STEER_IZQUIERDA, 1))
				return 3;
			else if (estaEntre(carAngle, -1, Constantes.STEER_DERECHA))
				return 4;

		} else if (trackPosition < Constantes.CENTRO_MIN) { // derecha
			if (estaEntre(carAngle, Constantes.STEER_RECTO_MIN, Constantes.STEER_RECTO_MAX))
				return 5; // centro - coche mira recto
			else if (estaEntre(carAngle, Constantes.STEER_RECTO_MAX, Constantes.STEER_IZQUIERDA))
				return 6; // centro - coche mira der
			else if (estaEntre(carAngle, Constantes.STEER_DERECHA, Constantes.STEER_RECTO_MIN))
				return 7; // centro - coche mira izq
			else if (estaEntre(carAngle, Constantes.STEER_IZQUIERDA, 1))
				return 8;
			else if (estaEntre(carAngle, -1, Constantes.STEER_DERECHA))
				return 9;

		} else if (trackPosition > Constantes.CENTRO_MAX) { // Izq
			if (estaEntre(carAngle, Constantes.STEER_RECTO_MIN, Constantes.STEER_RECTO_MAX))
				return 10; // centro - coche mira recto
			else if (estaEntre(carAngle, Constantes.STEER_RECTO_MAX, Constantes.STEER_IZQUIERDA))
				return 11; // centro - coche mira der
			else if (estaEntre(carAngle, Constantes.STEER_DERECHA, Constantes.STEER_RECTO_MIN))
				return 12; // centro - coche mira izq
			else if (estaEntre(carAngle, Constantes.STEER_IZQUIERDA, 1))
				return 13;
			else if (estaEntre(carAngle, -1, Constantes.STEER_DERECHA))
				return 14;
		}

		return null;
	}

	public float[] train(Integer newState, Double porcentaje, SensorModel sensors, boolean vuelta_terminada) {

		// Paso 1. Escoger un movimiento.

		// Elige la posiciï¿½n que obtenga una mayor recompensa a partir del estado
		// actual. //EXPLOTA
		Integer accion = qtable_velocidad.getBestRewardPosition(newState);

		if (porcentaje > 1.0)
			porcentaje = 1.0;

		// Explora nuevos estados
		if (this.randomGenerator.nextDouble() > porcentaje) {
			// Elige un movimiento aleatorio
			System.out.println("EXPLORA");
			accion = this.randomGenerator.nextInt(Constantes.NUM_VEL);
		}

		// Si el estado anterior es nulo (es la primera evaluaciï¿½n) entonces lo hace
		// con el mismo estado actual.
		if (oldState == null)
			oldState = newState;

		if (oldAction == null)
			oldAction = accion;


		if (Math.abs(sensors.getTrackPosition()) > 1 || newState == 9 || tick >= bestLapTick) {
			/**
			 * Si el coche se sale de la carretera, entonces se recompensa negativamente.
			 */
			Double targetReward = -1000.0 * Math.abs(sensors.getAngleToTrackAxis());

			/**
			 * Antes de reiniciar deberiamos actualizar la tabla con una recompensa
			 * negativa.
			 */

			Double reward = qtable_velocidad.setReward(oldState, newState, accion, oldAction, targetReward,
					getBestMoveFromTarget(newState));

			System.out.println("Porcentaje: " + porcentaje);
			System.out.println("Estado: " + getSpeedState(sensors));
			System.out.println("Posicion: " + sensors.getTrackPosition());
			System.out.println("Angulo: " + sensors.getAngleToTrackAxis());
			System.out.println("Distancia Vector#9: " + sensors.getTrackEdgeSensors()[9]);
			System.out.println("Recompensa Actual: " + targetReward);
			System.out.println("Recompensa Previa: " + reward);
			System.out.println("Distancia Recorrida: " + sensors.getDistanceRaced());
			System.out.println("Distancia desde el inicio: " + sensors.getDistanceFromStartLine());
			System.out.println("-----------------------------");
			// Actualiza la ventana de la Q-Tabla
			recompensar(recompensa, '-');
//			qtable_velocidad.setReward(oldState, newState, accion, oldAction, targetReward,
//					getBestMoveFromTarget(newState));
			qTableFrame_velocidad.setQTable(qtable_velocidad);

			recompensa_acumulada += reward;

			Action action = new Action();
			action.restartRace = true;
			mySocket.send(action.toString());

		} else {
			
			/**
			 * La recompensa serï¿½ proporcional a la distancia recorrida (cuanto mayor
			 * distancia, mayor recompensa) e inversamente proporcional a la distancia al
			 * centro de la carretera (cuanto mï¿½s cercano a 0, mï¿½s recompensa).
			 */
			double rewardTrackPosition = Math.pow(1 / ((Math.abs(sensors.getTrackPosition())) + 1), 4) * 0.7;
			double rewardAngle = Math.pow(1 / ((Math.abs(sensors.getAngleToTrackAxis())) + 1), 4) * 0.25;
			double rewardSpeed = Math.pow((sensors.getSpeed()), 1) * 0.05;

			Double targetReward = rewardTrackPosition + rewardAngle + rewardSpeed;

			// Se establece la recompensa para el estado anterior en funciï¿½n del estado
			// actual.

//			Double reward = qtable_velocidad.setReward(oldState, newState, accion, oldAction, targetReward,
//					getBestMoveFromTarget(newState));

			System.out.println("Porcentaje: " + porcentaje);
			System.out.println("Estado: " + getSpeedState(sensors));
			System.out.println("Estado Antiguo: " + oldState);
			System.out.println("Accion_Actual : " + Constantes.VEL_VALUES[accion]);
			System.out.println("Distancia Vector#9: " + sensors.getTrackEdgeSensors()[9]);
			System.out.println("Acelerador: ");
			System.out.println("Recompensa Actual: " + targetReward);
			//System.out.println("Recompensa Previa: " + reward);
			System.out.println("Recompensa Acumulada " + recompensa_acumulada);
			System.out.println("-----------------------------");

			//recompensa_acumulada += reward;
			
			float[] accion_recompensa = new float[4];
			accion_recompensa[0] = oldState;
			accion_recompensa[1] = newState;
			accion_recompensa[2] = accion;
			accion_recompensa[3] = oldAction;
			recompensa.add(accion_recompensa);
			if(vuelta_terminada)
				if(tick < bestLapTick) {
					bestLapTick = tick;
					recompensar(recompensa, '+');
				}else
					recompensar(recompensa, '-');

			oldState = newState;
		}

		// Actualiza la ventana de la Q-Tabla

		qTableFrame_velocidad.setQTable(qtable_velocidad);

		// Actualiza el estado previo.

		//////////////////////////////////////

		oldAction = accion;

		return Constantes.VEL_VALUES[accion];

	}

	// Para calcular la maxFutureQ en la QTable
	private Integer getBestMoveFromTarget(Integer nextState) {
		Integer best_angle = null;
		best_angle = qtable_velocidad.getBestRewardPosition(nextState);
		return best_angle;
	}

	private float filterABS(SensorModel sensors, float brake) {
		// convert speed to m/s
		float speed = (float) (sensors.getSpeed() / 3.6);
		// when spedd lower than min speed for abs do nothing
		if (speed < absMinSpeed)
			return brake;

		// compute the speed of wheels in m/s
		float slip = 0.0f;
		for (int i = 0; i < 4; i++) {
			slip += sensors.getWheelSpinVelocity()[i] * wheelRadius[i];
		}
		// slip is the difference between actual speed of car and average speed of
		// wheels
		slip = speed - slip / 4.0f;
		// when slip too high applu ABS
		if (slip > absSlip) {
			brake = brake - (slip - absSlip) / absRange;
		}

		// check brake is not negative, otherwise set it to zero
		if (brake < 0)
			return 0;
		else
			return brake;
	}

	float clutching(SensorModel sensors, float clutch) {

		float maxClutch = clutchMax;

		// Check if the current situation is the race start
		if (sensors.getCurrentLapTime() < clutchDeltaTime && getStage() == Stage.RACE
				&& sensors.getDistanceRaced() < clutchDeltaRaced)
			clutch = maxClutch;

		// Adjust the current value of the clutch
		if (clutch > 0) {
			double delta = clutchDelta;
			if (sensors.getGear() < 2) {
				// Apply a stronger clutch output when the gear is one and the race is just
				// started
				delta /= 2;
				maxClutch *= clutchMaxModifier;
				if (sensors.getCurrentLapTime() < clutchMaxTime)
					clutch = maxClutch;
			}

			// check clutch is not bigger than maximum values
			clutch = Math.min(maxClutch, clutch);

			// if clutch is not at max value decrease it quite quickly
			if (clutch != maxClutch) {
				clutch -= delta;
				clutch = Math.max((float) 0.0, clutch);
			}
			// if clutch is at max value decrease it very slowly
			else
				clutch -= clutchDec;
		}
		return clutch;
	}

	public float[] initAngles() {

		float[] angles = new float[19];

		/*
		 * set angles as
		 * {-90,-75,-60,-45,-30,-20,-15,-10,-5,0,5,10,15,20,30,45,60,75,90}
		 */
		for (int i = 0; i < 5; i++) {
			angles[i] = -90 + i * 15;
			angles[18 - i] = 90 - i * 15;
		}

		for (int i = 5; i < 9; i++) {
			angles[i] = -20 + (i - 5) * 5;
			angles[18 - i] = 20 - (i - 5) * 5;
		}
		angles[9] = 0;
		return angles;
	}
}