package zcMarchas;

import java.util.ArrayList;
import java.util.Random;

import QLearning.Constantes;
import QLearning.Politica;
import QLearning.QTable;
import QLearning.QTableFrame;
import champ2011client.Action;
import champ2011client.Controller;
import champ2011client.SensorModel;
import champ2011client.SocketHandler;

public class JugadorMarchas extends Controller {

	/* Gear Changing Constants */
	final int[] gearUp = { 3500, 4500, 4500, 5000, 6500, 0 };; // 2000-7000 es el m�ximo
	final int[] gearDown = { 0, 2000, 2000, 2000, 3000, 4000 };

	/* Stuck constants */
	final int stuckTime = 25;
	final float stuckAngle = (float) 0.523598775; // PI/6

	/* Accel and Brake Constants */
	final float maxSpeedDist = 1;
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

	private int stuck = 0;

	// current clutch
	private float clutch = 0;

	
	/**
	 * Q-TABLE
	 */

	Politica politica_velocidad = new Politica();
	Politica politica_volante = new Politica();
	Politica politica_marchas = new Politica();

	public JugadorMarchas() {
		politica_velocidad.loadPolitica("velocidad");
		politica_volante.loadPolitica("volante");
		politica_marchas.loadPolitica("marchas");
	}

	public void reset() {
		System.out.println("Restarting the race!");
	}

	public void shutdown() {
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

		/**
		 * QTable
		 */

		// check if car is currently stuck
		if (Math.abs(sensors.getAngleToTrackAxis()) > stuckAngle) {
			// update stuck counter
			stuck++;
		} else {
			// if not stuck reset stuck counter
			stuck = 0;
		}

		// after car is stuck for a while apply recovering policy
		if (stuck > stuckTime) {
			/*
			 * set gear and sterring command assuming car is pointing in a direction out of
			 * track
			 */

			// to bring car parallel to track axis
			float steer = (float) (-sensors.getAngleToTrackAxis() / steerLock);
			int gear = -1; // gear R

			// if car is pointing in the correct direction revert gear and steer
			if (sensors.getAngleToTrackAxis() * sensors.getTrackPosition() > 0) {
				gear = 1;
				steer = -steer;
			}
			clutch = clutching(sensors, clutch);
			// build a CarControl variable and return it
			Action action = new Action();
			action.gear = gear;
			action.steering = steer;
			action.accelerate = 1.0;
			action.brake = 0;
			action.clutch = clutch;
			return action;
		}else // car is not stuck
		{
			// compute gear
			int gear = getGearState(sensors);

			// compute steering
			float steer = play(sensors)[0];
			
	        // set accel and brake from the joint accel/brake command 
			float accel = play(sensors)[1];
			float brake = play(sensors)[2];
	        
			clutch = clutching(sensors, clutch);
	        
			// build a CarControl variable and return it
			Action action = new Action();
			action.gear = gear;
			action.steering = steer;
			action.accelerate = accel;
			action.brake = 0;
			action.clutch = 0;
			return action;
		}
	}

	private float[] play(SensorModel sensors) {
		float[] play = new float[4];
		Integer steerState = getSteerState(sensors);
		play[0] = politica_volante.getAccion(steerState)[0];
		
		Integer velocidadState = getSpeedState(sensors);
		play[1] = politica_velocidad.getAccion(velocidadState)[0];
		play[2] = politica_velocidad.getAccion(velocidadState)[0];
		
		Integer gearState = getGearState(sensors);
		play[3] = politica_marchas.getAccion(gearState)[0];
		return play;
	}

	private Integer getGearState(SensorModel sensors) {
		// TODO Auto-generated method stub
		return null;
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
				return 14;
			else if (carAngle < Constantes.STEER_DERECHA)
				return 13;
		}

		return null;
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