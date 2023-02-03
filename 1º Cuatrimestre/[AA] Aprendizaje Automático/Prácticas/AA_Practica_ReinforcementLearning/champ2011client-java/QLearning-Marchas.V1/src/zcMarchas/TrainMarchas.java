package zcMarchas;

import java.util.ArrayList;
import java.util.Random;
import Datos.Dato;
import QLearning.*;
import champ2011client.Action;
import champ2011client.Controller;
import champ2011client.SensorModel;
import champ2011client.SocketHandler;

public class TrainMarchas extends Controller {

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
	double oldSpeed;
	int oldGearT;
	int oldGearP;

	Integer iRestart = 0;
	Integer contador_entrenamientos = 0; // se resetea
	Double recompensa_acumulada = 0.0;
	Integer indice_carreras = 0; // no se resetea

	Integer lastLap = 0;
	Integer tick = 0;
	float oldSteer;
	double aceleracion_actual = 0.0;
	double aceleracion_anterior = 0.0;
	float oldAccel;
	float oldBrake;
	double last_lapTime = 0.0;

	double oldTrackPosition = 0.0;
	int count_tick = 0;

	double porcentaje = Constantes.PORCENTAJE_INICIAL;
	boolean isStuck = false;
	double bestLapTick = Double.MAX_VALUE;

	// Datos
	Dato datos;

	// current clutch
	private float clutch = 0;

	/* Q-Table - Marchas */
	/////////////////////////////////////////////////////////////////////////
	private static QTable qtable_marchas = new QTable("Marchas", Constantes.NUM_STATES_GEAR, Constantes.NUM_GEAR,
			Constantes.GEAR_VALUES);
	private static QTableFrame qTableFrame_velocidad = new QTableFrame(qtable_marchas, Constantes.GEAR_VALUES,
			Constantes.NUM_GEAR);
	private Random randomGenerator = new Random();
	/////////////////////////////////////////////////////////////////////////

	private double last_distRaced;
	private double max_speed = 0.0f;

	private String name_qtable = "qtable_marchas";
	private String name_politica = "marchas";
	String name_datos = Constantes.FILE_NAME + "_marchas";

	ArrayList<float[]> recompensa = new ArrayList<>();

	SocketHandler mySocket;

	/* Politicas */
	Politica politica_volante;
	Politica politica_velocidad;
	private int last_action;

	public TrainMarchas() {
		politica_volante = new Politica();
		politica_volante.loadPolitica("volante");

		politica_velocidad = new Politica();
		politica_velocidad.loadPolitica("velocidad");

		datos = new Dato(Constantes.NUM_STATES_GEAR, Constantes.NUM_GEAR);
		qtable_marchas.loadQTable(name_qtable);
		qTableFrame_velocidad.setQTable(qtable_marchas);
		datos.writeHeader(name_datos); // escribe el header.

		qtable_marchas.saveQTable(name_qtable);
	}

	public void reiniciar() {
		Action action = new Action();
		action.restartRace = true;
		mySocket.send(action.toString());
	}

	public void mostrar(SensorModel sensors) {
		System.out.println("Mejor Vuelta: " + bestLapTick);
		System.out.println("Tick: " + tick);
		System.out.println("Carrera #" + indice_carreras);
		System.out.println("Porcentaje: " + porcentaje);
		System.out.println("Velocidad: " + sensors.getSpeed());
		System.out.println("Marcha: " + sensors.getGear());
		System.out.println("Accel: " + aceleracion_actual);
		System.out.println("Distancia Recorrida: " + sensors.getDistanceRaced());
		System.out.println("Distancia a la meta: " + sensors.getDistanceFromStartLine());
		System.out.println("-----------------------------");
	}

	public void reset() {

		if (contador_entrenamientos == Constantes.CARRERA_JUGADOR) {
			/* Escribimos los datos que vamos a sacar para hacer gr√°ficas */
			datos.setIndice_carrera(indice_carreras);
			datos.setTicks_duracion(tick);
			datos.setLongitud_recorrida(last_distRaced);
			datos.setEpsilon(1 - porcentaje);
			datos.writeDistRaced(name_datos);
			datos.setTiempo_vuelta(last_lapTime);
			datos.setMaxSpeed(max_speed);
		}

		iRestart++;
		contador_entrenamientos++;
		indice_carreras++;
		tick = 0;
		recompensa_acumulada = 0.0;
		oldTrackPosition = 0.0;
		max_speed = 0.0;
		
		aceleracion_actual = 0.0;
		aceleracion_anterior = 0.0;

		// Marcha inicial es N(0) øPunto Muerto?
		oldGearT = 0;
		oldGearP = 0;

		qtable_marchas.saveQTable(name_qtable);

		if (contador_entrenamientos == Constantes.CARRERA_JUGADOR + 1)
			contador_entrenamientos = 0;

	}

	public void shutdown() {
		qtable_marchas.saveQTable(name_qtable);
		Politica.savePolitica(name_politica, qtable_marchas, Constantes.VEL_VALUES);

		if (contador_entrenamientos == Constantes.CARRERA_JUGADOR) {
			/* Escribimos los datos que vamos a sacar para hacer gr√°ficas */
			datos.setIndice_carrera(indice_carreras);
			datos.setTicks_duracion(tick);
			datos.setLongitud_recorrida(last_distRaced);
			datos.setEpsilon(1 - porcentaje);
			datos.write_vel(name_datos);
			datos.setTiempo_vuelta(last_lapTime);
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
		this.mySocket = mySocket;
		
		if (sensors.getLastLapTime() > 0.0) {
			System.out.println("VUELTA TERMINADA!");
			Action restart = new Action();
			restart.restartRace = true;
			return restart;
		}

		aceleracion_actual = sensors.getSpeed() - oldSpeed;
		
		
		int gear = 0;
		float steer = politica_volante.getAccion(getSteerState(sensors))[0];
		float accel = politica_velocidad.getAccion(getSpeedState(sensors))[0];
		float brake = politica_velocidad.getAccion(getSpeedState(sensors))[1];

		if (tick >= Constantes.TICK_COMIENZO && (tick % Constantes.TICK_ENTRENAMIENTO == 0)
				&& contador_entrenamientos < Constantes.CARRERA_JUGADOR) {

			/**
			 * A partir del tick de comienzo, comenzamos a entrenar el coche. Lo haremos
			 * cada TICK_ENTRENAMIENTO ticks para no aprender cada tick.
			 */
			System.out.println("TRAIN");
			// compute gear
			System.out.println(getGearState(sensors));
			int acc_gear = (int) train(getGearState(sensors), getPorcentaje(sensors), sensors);
			gear = oldGearT;

			System.out.println("Estado: ");
			System.out.println("Accion GEAR: " + acc_gear);
			last_action = acc_gear;
			gear = aplicaAccionGear(acc_gear, gear);
			
			System.out.println("oldGear: " + oldGearT);
			System.out.println("GEAR: " + gear);
			System.out.println("PLAY -> " + last_action);
			oldGearT = gear;

		} else if (tick >= Constantes.TICK_COMIENZO && contador_entrenamientos == Constantes.CARRERA_JUGADOR) {
			/**
			 * Cada 10 entrenamientos, probamos a jugar con el jugador para ver su progreso
			 * y poder sacar resultados consistentes.
			 */
			System.out.println("--JUGADOR--");
			int acc_gear = (int) play(sensors);
			gear = oldGearP;
			last_action = acc_gear;
			gear = aplicaAccionGear(acc_gear, gear);

			// Si el coche se sale de la pista, reiniciamos la partida.
			if (isStuck) {
				Action reset = new Action();
				reset.restartRace = true;
				isStuck = false;
				return reset;
			}

			oldGearP = gear;
			System.out.println("PLAY -> " + last_action);

		} else {
			gear = oldGearT;
		}

		aceleracion_anterior = aceleracion_actual;
		
		tick++;

		clutch = clutching(sensors, clutch);

		// build a CarControl variable and return it
		Action action = new Action();

		action.gear = gear;
		action.steering = steer;
		action.accelerate = accel;
		action.brake = brake;
		action.clutch = 0;

		oldGearT = gear;
		

		return action;
	}

	private int aplicaAccionGear(int acc_gear, int gear) {

		if (acc_gear == -1)
			gear--;
		else if (acc_gear == 1)
			gear++;

		if (gear < -1)
			gear = -1;
		else if (gear > 6)
			gear = 6;

		return gear;
	}

	private float play(SensorModel sensors) {

		Integer state = getGearState(sensors);

		if (Math.abs(sensors.getTrackPosition()) >= 1.3 || tick >= Constantes.NUMERO_MAXIMO_TICKS) {
			isStuck = true;
			float default_value = 0f;
			return default_value;
		}

		if (sensors.getLastLapTime() > 0.0) {
			System.out.println("VUELTA TERMINADA!: " + sensors.getLastLapTime());
			datos.setFinishedLap(true);
			isStuck = true;
			float default_value = 0f;
			return default_value;
		}

		int gear = qtable_marchas.getBestRewardPosition(state);

		last_distRaced = sensors.getDistanceRaced();
		last_lapTime = sensors.getCurrentLapTime();
		datos.addAccionValor(state, gear);
		datos.setLongitud_recorrida(last_distRaced);
		datos.setDistancia_punto_comienzo(sensors.getDistanceFromStartLine());

		return Constantes.GEAR_VALUES[gear][0];
	}

	private double getPorcentaje(SensorModel sensors) {

		if (iRestart >= Constantes.MAX_CARRERAS_INCREMENTO_PORCENTAJE) {
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

	private Integer getGearState(SensorModel sensors) {
		double actualSpeed = sensors.getSpeed();
		double rpm = sensors.getRPM();

		if ( (actualSpeed - oldSpeed) < 0) { // Est· frenando
			if (estaEntre(rpm, -1, 1000)) return 0;
			else if (estaEntre(rpm, 1001, 2000)) return 1;
			else if (estaEntre(rpm, 2001, 3000)) return 2;
			else if (estaEntre(rpm, 3001, 4000)) return 3;
			else if (estaEntre(rpm, 4001, 5000)) return 4;
			else if (estaEntre(rpm, 5001, 6000)) return 5;
			else if (estaEntre(rpm, 6001, 7000)) return 6;
			else if (estaEntre(rpm, 7001, 8000)) return 7;
			else if (estaEntre(rpm, 8001, 9000)) return 8;
			else return 9;
		} else if (actualSpeed - oldSpeed > 0) { // Est· acelerando
			if (estaEntre(rpm, 0, 1000)) return 10;
			else if (estaEntre(rpm, 1001, 2000)) return 11;
			else if (estaEntre(rpm, 2001, 3000)) return 12;
			else if (estaEntre(rpm, 3001, 4000)) return 13;
			else if (estaEntre(rpm, 4001, 5000)) return 14;
			else if (estaEntre(rpm, 5001, 6000)) return 15;
			else if (estaEntre(rpm, 6001, 7000)) return 16;
			else if (estaEntre(rpm, 7001, 8000)) return 17;
			else if (estaEntre(rpm, 8001, 9000)) return 18;
			else return 19;
		} else { // Se mantiene la velocidad.
			return 20;
		}
	}

	private Integer getSpeedState(SensorModel sensors) {

		double distVec9 = sensors.getTrackEdgeSensors()[9];

		if (estaEntre(distVec9, 0, 20))
			return 0;
		if (estaEntre(distVec9, 20, 40))
			return 1;
		if (estaEntre(distVec9, 40, 60))
			return 2;
		if (estaEntre(distVec9, 60, 80))
			return 3;
		if (estaEntre(distVec9, 80, 100))
			return 4;
		if (estaEntre(distVec9, 100, 120))
			return 5;
		if (estaEntre(distVec9, 120, 140))
			return 6;
		if (estaEntre(distVec9, 140, 160))
			return 7;
		if (estaEntre(distVec9, 160, 180))
			return 8;
		if (estaEntre(distVec9, 180, 200))
			return 9;
		if (distVec9 < 0)
			return 10;

		return null;
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
			else if (carAngle > Constantes.STEER_IZQUIERDA)
				return 3;
			else if (carAngle < Constantes.STEER_DERECHA)
				return 4;

		} else if (trackPosition < Constantes.CENTRO_MIN) { // derecha
			if (estaEntre(carAngle, Constantes.STEER_RECTO_MIN, Constantes.STEER_RECTO_MAX))
				return 5; // centro - coche mira recto
			else if (estaEntre(carAngle, Constantes.STEER_RECTO_MAX, Constantes.STEER_IZQUIERDA))
				return 6; // centro - coche mira der
			else if (estaEntre(carAngle, Constantes.STEER_DERECHA, Constantes.STEER_RECTO_MIN))
				return 7; // centro - coche mira izq
			else if (carAngle > Constantes.STEER_IZQUIERDA)
				return 8;
			else if (carAngle < Constantes.STEER_DERECHA)
				return 9;

		} else if (trackPosition > Constantes.CENTRO_MAX) { // Izq
			if (estaEntre(carAngle, Constantes.STEER_RECTO_MIN, Constantes.STEER_RECTO_MAX))
				return 10; // centro - coche mira recto
			else if (estaEntre(carAngle, Constantes.STEER_RECTO_MAX, Constantes.STEER_IZQUIERDA))
				return 11; // centro - coche mira der
			else if (estaEntre(carAngle, Constantes.STEER_DERECHA, Constantes.STEER_RECTO_MIN))
				return 12; // centro - coche mira izq
			else if (carAngle > Constantes.STEER_IZQUIERDA)
				return 14;
			else if (carAngle < Constantes.STEER_DERECHA)
				return 13;
		}

		return null;
	}

	public float train(Integer newState, Double porcentaje, SensorModel sensors) {

		// Elige la mejor opciÛn de la qtable. (Explotar).
		Integer accion = qtable_marchas.getBestRewardPosition(newState);

		// Elige una opciÛn aleatoria para evaluarla (Explorar).
		if (this.randomGenerator.nextDouble() > porcentaje) {
			System.out.println("EXPLORA");
			accion = this.randomGenerator.nextInt(Constantes.NUM_GEAR);
		}

		// Si el estado anterior es nulo (es la primera evaluacion) entonces lo hace
		// con el mismo estado actual.
		if (oldState == null)
			oldState = newState;

		if (oldAction == null)
			oldAction = accion;

		// Si termina la carrera se actualiza la mejor vuelta y se reinicia.
		if (sensors.getLastLapTime() > 0.0) {
			System.out.println("VUELTA TERMINADA!: " + sensors.getLastLapTime());
			// Si ha tardado menos que en la mejor vuelta:
			if (tick < bestLapTick) {
				bestLapTick = tick;
			}
			reiniciar();
		}

		// Si el coche tarda mas del numero maximo de ticks, se recompensa negativamente
		// y se reinicia.
		if (tick > Constantes.NUMERO_MAXIMO_TICKS
				|| Math.abs(sensors.getTrackPosition()) >= Constantes.LIMITE_CARRETERA) {

			if (tick > Constantes.NUMERO_MAXIMO_TICKS)
				System.out.println("TIEMPO AGOTADO!");
			else if (Math.abs(sensors.getTrackPosition()) >= Constantes.LIMITE_CARRETERA)
				System.out.println("SE HA SALIDO DE LA CARRETERA!");

			Double targetReward = Constantes.RECOMPENSA_NEGATIVA;
			qtable_marchas.setReward(oldState, newState, accion, oldAction, targetReward,
					getBestMoveFromTarget(newState));

			mostrar(sensors);
			qTableFrame_velocidad.setQTable(qtable_marchas);
			reiniciar();

		} else {

			Double targetReward = sensors.getDistanceFromStartLine() / sensors.getCurrentLapTime();
			Double rewardFuel = sensors.getFuelLevel() * 0.6;
			Double rewardSpeed = sensors.getSpeed() * 0.4;
			Double rewardAccel = sensors.getRPM()*(aceleracion_actual-aceleracion_anterior);
			
			targetReward = rewardAccel;
			// La recompensa podr√≠a ser en funci√≥n de las RPM, es decir, si se encuentra
			// en buenas revoluciones recompensar que no cambie de marcha
			// Si revoluciones bajas y disminuye --> recompensar
			// Si revoluciones bajas y aumenta o mantiene --> castigar
			// Si revoluciones altas y aumenta --> recompensar
			// Si revoulciones altas y disminuye o mantiene --> castigar

//			switch(getGearState(sensors)) {
//			case 0: //Revoluciones bajas y velocidad disminuye
//				if(oldGearT < actualGear)
//					targetReward = 1.0;
//				else
//					targetReward = -5.0;
//				break;
//			case 2: //Revoluciones altas y velocidad aumenta
//				if(oldGearT > actualGear)
//					targetReward = 1.0;
//				else
//					targetReward = -5.0;
//				break;
//			default: //1 o 3
//				if(oldGearT == actualGear)
//					targetReward = 1.0;
//				else
//					targetReward = -5.0;
//				break;
//			}

			Double reward = qtable_marchas.setReward(oldState, newState, accion, oldAction, targetReward,
					getBestMoveFromTarget(newState));

			mostrar(sensors);

		}

		// Actualiza la ventana de la Q-Tabla

		qTableFrame_velocidad.setQTable(qtable_marchas);

		// Actualiza el estado previo.

		//////////////////////////////////////

		oldAction = accion;
		oldState = newState;
		oldSpeed = sensors.getSpeed();

		return Constantes.GEAR_VALUES[accion][0];

	}

	private Integer getBestMoveFromTarget(Integer nextState) {
		Integer best_angle = null;
		best_angle = qtable_marchas.getBestRewardPosition(nextState);
		return best_angle;
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