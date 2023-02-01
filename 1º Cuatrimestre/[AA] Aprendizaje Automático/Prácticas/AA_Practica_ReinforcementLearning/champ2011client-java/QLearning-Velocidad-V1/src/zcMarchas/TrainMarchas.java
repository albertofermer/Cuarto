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
	private static QTable qtable_marchas = new QTable("Marchas", Constantes.NUM_STATES_GEAR, Constantes.NUM_GEAR, Constantes.GEAR_VALUES);
	private static QTableFrame qTableFrame_velocidad = new QTableFrame(qtable_marchas, Constantes.GEAR_VALUES, Constantes.NUM_GEAR);
	private Random randomGenerator = new Random();
	/////////////////////////////////////////////////////////////////////////

	private float last_steer;
	private double last_trackPosition;
	private double last_distRaced;
	private double last_distFromStartLine;

	private boolean carrera_terminada = false;

	private String name_qtable = "qtable_marchas";
	private String name_politica = "marchas";
	String name_datos = Constantes.FILE_NAME + "_marchas";

	ArrayList<float[]> recompensa = new ArrayList<>();

	SocketHandler mySocket;
	Politica politica_volante;
	Politica politica_velocidad;

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

	public void reset() {

		if (contador_entrenamientos == Constantes.CARRERA_JUGADOR) {
			/* Escribimos los datos que vamos a sacar para hacer gráficas */
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
		// contador_vueltas = 0;
		oldTrackPosition = 0.0;
		
		//Marcha inicial es N(0) ¿Punto Muerto?
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
			/* Escribimos los datos que vamos a sacar para hacer gráficas */
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
			train(getGearState(sensors), getPorcentaje(sensors), sensors);

			recompensa = new ArrayList<>();
			Action restart = new Action();
			restart.restartRace = true;
			return restart;
		}

		this.mySocket = mySocket;

		// compute steering, accel and brake
		float steer = politica_volante.getAccion(getSteerState(sensors))[0];
		float accel = politica_velocidad.getAccion(getSpeedState(sensors))[0];
		float brake = politica_velocidad.getAccion(getSpeedState(sensors))[1];

		System.out.println("Tick: " + tick);
		System.out.println("Entrenamiento: " + contador_entrenamientos);
		System.out.println("Carrera #" + indice_carreras);
		
		int gear;
		
		if (tick >= Constantes.TICK_COMIENZO && (tick % Constantes.TICK_ENTRENAMIENTO == 0)
				&& contador_entrenamientos < Constantes.CARRERA_JUGADOR) {

			/**
			 * A partir del tick de comienzo, comenzamos a entrenar el coche. Lo haremos
			 * cada TICK_ENTRENAMIENTO ticks para no aprender cada tick.
			 */
			System.out.println("TRAIN");
			// compute gear
			int acc_gear = (int) train(getGearState(sensors), getPorcentaje(sensors), sensors);
			gear = oldGearT;
			
			System.out.println("Estado: ");
			System.out.println("Accion GEAR: " + acc_gear);
			gear = aplicaAccionGear(acc_gear, gear);
			System.out.println("oldGear: " + oldGearT);
			System.out.println("GEAR: " + gear);
			oldGearT = gear;
			
		} else if (tick >= Constantes.TICK_COMIENZO && contador_entrenamientos == Constantes.CARRERA_JUGADOR) {
			/**
			 * Cada 10 entrenamientos, probamos a jugar con el jugador para ver su progreso
			 * y poder sacar resultados consistentes.
			 */
			System.out.println("--JUGADOR--");
			int acc_gear = (int) play(sensors);
			gear = oldGearP;
			
			gear = aplicaAccionGear(acc_gear, gear);
			
			// Si el coche se sale de la pista, reiniciamos la partida.
			if (isStuck) {
				Action reset = new Action();
				reset.restartRace = true;
				isStuck = false;
				return reset;
			}
			
			oldGearP = gear;

		} else {
			gear = oldGearT;
		}

		tick++;

		clutch = clutching(sensors, clutch);

		/**
		 * Si el coche no se mueve, al menos, una diferencia de 5 metros en 10 ticks, se
		 * reinicia el juego y se punt�a negativamente.
		 */
		System.out.println(Math.abs(sensors.getTrackPosition() - oldTrackPosition));
		if (Math.abs(sensors.getTrackPosition() - oldTrackPosition) <= 0.001 && sensors.getSpeed() < 1) {
			// Si hay una diferencia minima aumenta en uno el contador.
			count_tick++;
		} else {
			// Si aumenta dicha diferencia, se resetea el contador.
			count_tick = 0;
		}

		// Actualiza la posici�n de referencia cada X ticks.
		if (tick > Constantes.TICK_COMIENZO && tick % Constantes.TICKS_ESPERA == 0) {
			oldTrackPosition = sensors.getTrackPosition();
		}

		// build a CarControl variable and return it
		Action action = new Action();

		action.gear = gear;
		action.steering = steer;
		action.accelerate = accel;
		action.brake = 0;
		action.clutch = 0;

		oldGearT = gear;
		System.out.println("PLAY -> " + gear);

		return action;
	}

	private int aplicaAccionGear(int acc_gear, int gear) {
		
		if(acc_gear == -1)
			gear--;
		else if(acc_gear == 1)
			gear++;
			
		if(gear < -1)
			gear = -1;
		else if(gear >6)
			gear = 6;
		
		return gear;
	}

	private float play(SensorModel sensors) {

		Integer state = getGearState(sensors);

		if (/*state == 10*/ Math.abs(sensors.getTrackPosition()) == 1.3 || count_tick > Constantes.TICKS_ESPERA) {
			isStuck = true;
			float default_value = 0f;
			return default_value;
		}

		int gear = qtable_marchas.getBestRewardPosition(state);

		last_distRaced = sensors.getDistanceRaced();
		return Constantes.GEAR_VALUES[gear][0];
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
	
	private Integer getGearState(SensorModel sensors) {
		double actualSpeed = sensors.getSpeed();
		double rpm = sensors.getRPM();
		
		
		if(actualSpeed < oldSpeed) {
			if(estaEntre(rpm, 0, 2000))
				return 0;
			else
				return 1;
		}else if(Math.abs(actualSpeed - oldSpeed) > 1) {
			if(estaEntre(rpm, 7000, 10000))
				return 2;
			else
				return 3;
		}else {
			return 4;
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
	
	private Integer getStateGear(SensorModel sensors) {
		return -1;
	}

	public float train(Integer newState, Double porcentaje, SensorModel sensors) {

		// Elige la posici�n que obtenga una mayor recompensa a partir del estado
		// actual. //EXPLOTA
		Integer accion = qtable_marchas.getBestRewardPosition(newState);

		if (porcentaje > 1.0)
			porcentaje = 1.0;

		// Explora nuevos estados
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

		if (/*newState == 10 || */ Math.abs(sensors.getTrackPosition()) >= 1.1 || count_tick > Constantes.TICKS_ESPERA) {
			/**
			 * Si el coche se sale de la carretera, entonces se recompensa negativamente.
			 */

			/**
			 * Antes de reiniciar deberiamos actualizar la tabla con una recompensa
			 * negativa.
			 */
			// if (newState == 10 || count_tick > Constantes.TICKS_ESPERA) {

			System.out.println("SE HA SALIDO DE LA CARRETERA.");
			Double targetReward = -1000.0;
			Double reward = qtable_marchas.setReward(oldState, newState, accion, oldAction, targetReward,
					getBestMoveFromTarget(newState));

			// }
			System.out.println("Porcentaje: " + porcentaje);
			System.out.println("Estado: " + getSpeedState(sensors));
			System.out.println("Posicion: " + sensors.getTrackPosition());
			System.out.println("Angulo: " + sensors.getAngleToTrackAxis());
			System.out.println("Distancia Vector#9: " + sensors.getTrackEdgeSensors()[9]);
			System.out.println("Velocidad: " + sensors.getSpeed());
			System.out.println("Distancia Recorrida: " + sensors.getDistanceRaced());
			System.out.println("Distancia desde el inicio: " + sensors.getDistanceFromStartLine());
			System.out.println("-----------------------------");
			// Actualiza la ventana de la Q-Tabla
			qTableFrame_velocidad.setQTable(qtable_marchas);

			// recompensa_acumulada += reward;

			Action action = new Action();
			action.restartRace = true;
			mySocket.send(action.toString());

		} else {

			/**
			 * La recompensa sera proporcional a la distancia recorrida (cuanto mayor
			 * distancia, mayor recompensa) e inversamente proporcional a la distancia al
			 * centro de la carretera (cuanto mas cercano a 0, mas recompensa).
			 */
			
			Double targetReward = sensors.getDistanceFromStartLine()/sensors.getCurrentLapTime();
			
			// La recompensa podría ser en función de las RPM, es decir, si se encuentra en buenas revoluciones recompensar que no cambie de marcha
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

			// Se establece la recompensa para el estado anterior en funci�n del estado
			// actual.

			Double reward = qtable_marchas.setReward(oldState, newState, accion, oldAction, targetReward,
					getBestMoveFromTarget(newState));

			System.out.println("Porcentaje: " + porcentaje);
			System.out.println("Estado: " + getGearState(sensors));
			System.out.println("Estado Antiguo: " + oldState);
			System.out.println("Accion_Actual : " + Constantes.GEAR_VALUES[accion]);
			System.out.println("RPM: " + sensors.getRPM());
			System.out.println("Velocidad: " + sensors.getSpeed());
			System.out.println("Recompensa Actual: " + targetReward);
			System.out.println("Recompensa Acumulada " + recompensa_acumulada);
			System.out.println("Distancia a la meta: " + sensors.getDistanceFromStartLine());
			System.out.println("-----------------------------");

			// recompensa_acumulada += reward;


			oldState = newState;
			oldSpeed = sensors.getSpeed();
		}

		// Actualiza la ventana de la Q-Tabla

		qTableFrame_velocidad.setQTable(qtable_marchas);

		// Actualiza el estado previo.

		//////////////////////////////////////

		oldAction = accion;

		return Constantes.GEAR_VALUES[accion][0];

	}

	// Para calcular la maxFutureQ en la QTable
	private Integer getBestMoveFromTarget(Integer nextState) {
		Integer best_angle = null;
		best_angle = qtable_marchas.getBestRewardPosition(nextState);
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