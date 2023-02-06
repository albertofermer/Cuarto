package zaVolante;

import java.util.Random;
import Datos.Dato;
import QLearning.*;
import champ2011client.Action;
import champ2011client.Controller;
import champ2011client.SensorModel;
import champ2011client.SocketHandler;

public class TrainVolante extends Controller {

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
	private int contador_vueltas = 0;

	Integer lastLap = 0;
	Integer tick = 0;
	float oldSteer;
	double porcentaje = Constantes.PORCENTAJE_INICIAL;
	boolean isStuck = false;

	// Datos
	Dato datos;

	// current clutch
	private float clutch = 0;

	/* Q-Table - Volante */
	/////////////////////////////////////////////////////////////////////////
	private static QTable qtable_steer = new QTable("Volante",Constantes.NUM_STATES_STEER, Constantes.NUM_ANGLES, Constantes.STEER_VALUES); // Numero de estados de giro de volante (3).
	private static QTableFrame qTableFrame_steer = new QTableFrame(qtable_steer, Constantes.STEER_VALUES, Constantes.NUM_ANGLES);
	private Random randomGenerator = new Random();
	/////////////////////////////////////////////////////////////////////////
	
	private float last_steer;
	private double last_trackPosition;
	private double last_distRaced;
	private double last_distFromStartLine;
	
	private boolean carrera_terminada = false;
	
	private String name_qtable = "qtable_volante";
	private String name_politica = "volante";
	String name_datos = Constantes.FILE_NAME + "_volante";
	
	SocketHandler mySocket;

	public TrainVolante() {
		datos = new Dato(Constantes.NUM_STATES_STEER, Constantes.NUM_ANGLES);
		qtable_steer.loadQTable(name_qtable);
		qTableFrame_steer.setQTable(qtable_steer);
		datos.writeHeader(name_datos); // escribe el header.
	}

	public void reset() {

		if (contador_entrenamientos == Constantes.CARRERA_JUGADOR) {
			/* Escribimos los datos que vamos a sacar para hacer gráficas */
			datos.setIndice_carrera(indice_carreras);
			datos.setTicks_duracion(tick);
			datos.setLongitud_recorrida(last_distRaced);
			datos.setEpsilon(1-porcentaje);
			datos.writeDistRaced(name_datos);
		}
		
		iRestart++;
		contador_entrenamientos++;
		indice_carreras++;
		tick = 0;
		recompensa_acumulada = 0.0;
		contador_vueltas = 0;

		qtable_steer.saveQTable(name_qtable);


		if (contador_entrenamientos == Constantes.CARRERA_JUGADOR + 1)
			contador_entrenamientos = 0;

	}

	public void shutdown() {
		qtable_steer.saveQTable(name_qtable);
		Politica.savePolitica(name_politica, qtable_steer, Constantes.STEER_VALUES);
		
		if (contador_entrenamientos == Constantes.CARRERA_JUGADOR) {
			/* Escribimos los datos que vamos a sacar para hacer gráficas */
			datos.setIndice_carrera(indice_carreras);
			datos.setTicks_duracion(tick);
			datos.setLongitud_recorrida(last_distRaced);
			datos.setEpsilon(1-porcentaje);
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

	private float getAccel(SensorModel sensors) {
		// checks if car is out of track
		if (sensors.getTrackPosition() < 1 && sensors.getTrackPosition() > -1) {
			// reading of sensor at +5 degree w.r.t. car axis
			float rxSensor = (float) sensors.getTrackEdgeSensors()[10];
			// reading of sensor parallel to car axis
			float sensorsensor = (float) sensors.getTrackEdgeSensors()[9];
			// reading of sensor at -5 degree w.r.t. car axis
			float sxSensor = (float) sensors.getTrackEdgeSensors()[8];

			float targetSpeed;

			// track is straight and enough far from a turn so goes to max speed
			if (sensorsensor > maxSpeedDist || (sensorsensor >= rxSensor && sensorsensor >= sxSensor))
				targetSpeed = maxSpeed;
			else {
				// approaching a turn on right
				if (rxSensor > sxSensor) {
					// computing approximately the "angle" of turn
					float h = sensorsensor * sin5;
					float b = rxSensor - sensorsensor * cos5;
					float sinAngle = b * b / (h * h + b * b);
					// estimate the target speed depending on turn and on how close it is
					targetSpeed = maxSpeed * (sensorsensor * sinAngle / maxSpeedDist);
				}
				// approaching a turn on left
				else {
					// computing approximately the "angle" of turn
					float h = sensorsensor * sin5;
					float b = sxSensor - sensorsensor * cos5;
					float sinAngle = b * b / (h * h + b * b);
					// estimate the target speed depending on turn and on how close it is
					targetSpeed = maxSpeed * (sensorsensor * sinAngle / maxSpeedDist);
				}

			}

			// accel/brake command is exponentially scaled w.r.t. the difference between
			// target speed and current one
			return (float) (2 / (1 + Math.exp(sensors.getSpeed() - targetSpeed)) - 1);
			// return 1;
		} else
			return (float) 0.3; // when out of track returns a moderate acceleration command

	}

	public Action control(SensorModel sensors, SocketHandler mySocket) {
		
		if (sensors.getLastLapTime() > 0.0) {
			System.out.println("VUELTA TERMINADA!");
			Action restart = new Action();
			restart.restartRace = true;
			return restart;
		}
		
		this.mySocket = mySocket;

		// compute accel/brake command
		float accel_and_brake = getAccel(sensors);
		// compute gear
		int gear = getGear(sensors);

		float steer;
		// compute steering
		
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
			steer = train(getSteerState(sensors), getPorcentaje(sensors), sensors);

		} else if (contador_entrenamientos == Constantes.CARRERA_JUGADOR) {
			/**
			 * Cada 10 entrenamientos, probamos a jugar con el jugador para ver su progreso
			 * y poder sacar resultados consistentes.
			 */
			System.out.println("--JUGADOR--");
			steer = play(sensors);

			// Si el coche se sale de la pista, reiniciamos la partida.
			if (isStuck) {
				Action reset = new Action();
				reset.restartRace = true;
				isStuck = false;
				return reset;
			}

		} else {
			steer = oldSteer;
		}

		tick++;

		if (steer == -1) {
			System.out.println("########################################");
		}

		// set accel and brake from the joint accel/brake command
		float accel, brake;
		if (accel_and_brake > 0) {
			accel = accel_and_brake;
			brake = 0;
		} else {
			accel = 0;
			// apply ABS to brake
			brake = filterABS(sensors, -accel_and_brake);
		}
//	        
		clutch = clutching(sensors, clutch);

//	        
		// build a CarControl variable and return it
		Action action = new Action();

		action.gear = gear;
		action.steering = steer;
		action.accelerate = accel;
		action.brake = 0;
		action.clutch = 0;

		oldSteer = steer;


		return action;
	}

	private float play(SensorModel sensors) {
		
		if (Math.abs(sensors.getTrackPosition()) > 1) {

			isStuck = true;
			return 0.0f;
		}

		Integer state = getSteerState(sensors);
		int steer = qtable_steer.getBestRewardPosition(state);
		
		last_distRaced = sensors.getDistanceRaced();
		
		return Constantes.STEER_VALUES[steer][0];
	}

	private double getPorcentaje(SensorModel sensors) {

		if (iRestart == Constantes.MAX_CARRERAS_INCREMENTO_PORCENTAJE) {
			porcentaje += Constantes.INCREMENTO_PORCENTAJE;
			iRestart = 0;
		}

		if (porcentaje > Constantes.MAX_PORCENTAJE)
			porcentaje = Constantes.MAX_PORCENTAJE;

		return porcentaje;
		// return 0;
	}

	private boolean estaEntre(double valor, double minimo, double maximo) {
		return (minimo <= valor && valor <= maximo);

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
				return 13;
			else if (carAngle < Constantes.STEER_DERECHA)
				return 14;
		}

		return null;
	}

	public float train(Integer newState, Double porcentaje, SensorModel sensors) {

		// Paso 1. Escoger un movimiento.

		// Elige la posici�n que obtenga una mayor recompensa a partir del estado
		// actual. //EXPLOTA
		Integer accion = qtable_steer.getBestRewardPosition(newState);


		if (porcentaje > 1.0)
			porcentaje = 1.0;
		
		// Explora nuevos estados
		if (this.randomGenerator.nextDouble() > porcentaje) {
			// Elige un movimiento aleatorio
			System.out.println("EXPLORA");
			accion = this.randomGenerator.nextInt(Constantes.NUM_ANGLES);
		}

		// Si el estado anterior es nulo (es la primera evaluaci�n) entonces lo hace con
		// el mismo estado actual.
		if (oldState == null)
			oldState = newState;

		if (oldAction == null)
			oldAction = accion;


		// Double targetReward = 0.0;

		if (Math.abs(sensors.getTrackPosition()) > 1) {
			/**
			 * Si el coche se sale de la carretera, entonces se recompensa negativamente.
			 */
			Double targetReward = -1000.0 * Math.abs(sensors.getAngleToTrackAxis());

			/**
			 * Antes de reiniciar deber�amos actualizar la tabla con una recompensa
			 * negativa.
			 */

			Double reward = qtable_steer.setReward(oldState, newState, accion, oldAction, targetReward,
					getBestMoveFromTarget(newState));

			System.out.println("Porcentaje: " + porcentaje);
			System.out.println("Estado: " + getSteerState(sensors));
			System.out.println("Posicion: " + sensors.getTrackPosition());
			System.out.println("Angulo: " + sensors.getAngleToTrackAxis());
			System.out.println("Steer: " + Constantes.STEER_VALUES[accion]);
			System.out.println("Old Steer: " + Constantes.STEER_VALUES[oldAction]);
			System.out.println("Recompensa Actual: " + targetReward);
			System.out.println("Recompensa Previa: " + reward);
			System.out.println("Distancia Recorrida: " + sensors.getDistanceRaced());
			System.out.println("Distancia desde el inicio: " + sensors.getDistanceFromStartLine());
			System.out.println("-----------------------------");
			// Actualiza la ventana de la Q-Tabla
			qtable_steer.setReward(oldState, newState, accion, oldAction, targetReward,
					getBestMoveFromTarget(newState));
			qTableFrame_steer.setQTable(qtable_steer);
			
			recompensa_acumulada += reward;
			
			
			Action action = new Action();
			action.restartRace = true;
			mySocket.send(action.toString());

		} else {
			/**
			 * La recompensa ser� proporcional a la distancia recorrida (cuanto mayor
			 * distancia, mayor recompensa) e inversamente proporcional a la distancia al
			 * centro de la carretera (cuanto m�s cercano a 0, m�s recompensa).
			 */
			double rewardTrackPosition = Math.pow(1 / ((Math.abs(sensors.getTrackPosition())) + 1), 4) * 0.7;
			double rewardAngle = Math.pow(1 / ((Math.abs(sensors.getAngleToTrackAxis())) + 1), 4) * 0.3;

			Double targetReward = rewardTrackPosition + rewardAngle;

			// Se establece la recompensa para el estado anterior en funci�n del estado
			// actual.

			Double reward = qtable_steer.setReward(oldState, newState, accion, oldAction, targetReward,
					getBestMoveFromTarget(newState));
			
			System.out.println("Porcentaje: " + porcentaje);
			System.out.println("Estado: " + getSteerState(sensors));
			System.out.println("Estado Antiguo: " + oldState);
			System.out.println("Accion_Actual : " + Constantes.STEER_VALUES[accion]);
			System.out.println("Accion_Anterior : " + Constantes.STEER_VALUES[oldAction]);
			System.out.println("Posicion: " + sensors.getTrackPosition());
			System.out.println("Angulo: " + sensors.getAngleToTrackAxis());
			System.out.println("Recompensa Actual: " + targetReward);
			System.out.println("Recompensa Previa: " + reward);
			System.out.println("Recompensa Acumulada " + recompensa_acumulada);
			System.out.println("Distancia Recorrida: " + sensors.getDistanceRaced());
			System.out.println("Distancia desde el inicio: " + sensors.getDistanceFromStartLine());
			System.out.println("-----------------------------");

			recompensa_acumulada += reward;

			oldState = newState;
		}

		// Actualiza la ventana de la Q-Tabla

		qTableFrame_steer.setQTable(qtable_steer);

		// Actualiza el estado previo.

		//////////////////////////////////////

		oldAction = accion;

		return Constantes.STEER_VALUES[accion][0];

	}

	// Para calcular la maxFutureQ en la QTable
	private Integer getBestMoveFromTarget(Integer nextState) {
		Integer best_angle = null;
		best_angle = qtable_steer.getBestRewardPosition(nextState);
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