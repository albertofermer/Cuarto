package champ2011client;

import java.util.ArrayList;
import java.util.Random;

import QLearning.Constantes;
import QLearning.QTable;
import QLearning.QTableFrame;

public class SimpleDriver3 extends Controller{
	
	/* Gear Changing Constants*/
	final int[]  gearUp={5000,6000,6000,6500,7000,0};
	final int[]  gearDown={0,2500,3000,3000,3500,3500};

	/* Stuck constants*/
	final int  stuckTime = 25;
	final float  stuckAngle = (float) 0.523598775; //PI/6

	/* Accel and Brake Constants*/
	final float maxSpeedDist=7;
	final float maxSpeed=50;
	final float sin5 = (float) 0.08716;
	final float cos5 = (float) 0.99619;

	/* Steering constants*/
	final float steerLock=(float) 0.785398;
	final float steerSensitivityOffset=(float) 80.0;
	final float wheelSensitivityCoeff=1;	

	/* ABS Filter Constants */
	final float wheelRadius[]={(float) 0.3179,(float) 0.3179,(float) 0.3276,(float) 0.3276};
	final float absSlip=(float) 2.0;
	final float absRange=(float) 3.0;
	final float absMinSpeed=(float) 3.0;
	
	/* Clutching Constants */
	final float clutchMax=(float) 0.5;
	final float clutchDelta=(float) 0.05;
	final float clutchRange=(float) 0.82;
	final float	clutchDeltaTime=(float) 0.02;
	final float clutchDeltaRaced=10;
	final float clutchDec=(float) 0.01;
	final float clutchMaxModifier=(float) 1.3;
	final float clutchMaxTime=(float) 1.5;
	
	private int stuck=0;

	// current clutch
	private float clutch=0;
	
	
	/**
	 * 		Q-TABLE
	 */
		
	private static QTable qtable = new QTable(Constantes.NUM_STATES_STEER);  // Numero de estados de giro de volante (3).
	private static QTableFrame qTableFrame = new QTableFrame(qtable);
	private Random randomGenerator = new Random();

	/**
	 * socket
	 */
	
	SocketHandler mySocket;
	
	public SimpleDriver3() {
		qtable.loadQTable();
		qTableFrame.setQTable(qtable);
	}
	
	public void reset() {
		qtable.saveQTable();
		System.out.println("Restarting the race!");
	}

	public void shutdown() {
		qtable.saveQTable();
		System.out.println("Bye bye!");		
	}
	
	
	private int getGear(SensorModel sensors){
	    int gear = sensors.getGear();
	    double rpm  = sensors.getRPM();

	    // if gear is 0 (N) or -1 (R) just return 1 
	    if (gear<1)
	        return 1;
	    // check if the RPM value of car is greater than the one suggested 
	    // to shift up the gear from the current one     
	    if (gear <6 && rpm >= gearUp[gear-1])
	        return gear + 1;
	    else
	    	// check if the RPM value of car is lower than the one suggested 
	    	// to shift down the gear from the current one
	        if (gear > 1 && rpm <= gearDown[gear-1])
	            return gear - 1;
	        else // otherwhise keep current gear
	            return gear;
	}

//	private float getSteer(SensorModel sensors){
//		// steering angle is compute by correcting the actual car angle w.r.t. to track 
//		// axis [sensors.getAngle()] and to adjust car position w.r.t to middle of track [sensors.getTrackPos()*0.5]
//	    float targetAngle=(float) (sensors.getAngleToTrackAxis()-sensors.getTrackPosition()*0.5);
//	    // at high speed reduce the steering command to avoid loosing the control
//	    if (sensors.getSpeed() > steerSensitivityOffset)
//	        return (float) (targetAngle/(steerLock*(sensors.getSpeed()-steerSensitivityOffset)*wheelSensitivityCoeff));
//	    else
//	        return (targetAngle)/steerLock;
//
//	}
	
	private float getAccel(SensorModel sensors)
	{
	    // checks if car is out of track
	    if (sensors.getTrackPosition() < 1 && sensors.getTrackPosition() > -1)
	    {
	        // reading of sensor at +5 degree w.r.t. car axis
	        float rxSensor=(float) sensors.getTrackEdgeSensors()[10];
	        // reading of sensor parallel to car axis
	        float sensorsensor=(float) sensors.getTrackEdgeSensors()[9];
	        // reading of sensor at -5 degree w.r.t. car axis
	        float sxSensor=(float) sensors.getTrackEdgeSensors()[8];

	        float targetSpeed;

	        // track is straight and enough far from a turn so goes to max speed
	        if (sensorsensor>maxSpeedDist || (sensorsensor>=rxSensor && sensorsensor >= sxSensor))
	            targetSpeed = maxSpeed;
	        else
	        {
	            // approaching a turn on right
	            if(rxSensor>sxSensor)
	            {
	                // computing approximately the "angle" of turn
	                float h = sensorsensor*sin5;
	                float b = rxSensor - sensorsensor*cos5;
	                float sinAngle = b*b/(h*h+b*b);
	                // estimate the target speed depending on turn and on how close it is
	                targetSpeed = maxSpeed*(sensorsensor*sinAngle/maxSpeedDist);
	            }
	            // approaching a turn on left
	            else
	            {
	                // computing approximately the "angle" of turn
	                float h = sensorsensor*sin5;
	                float b = sxSensor - sensorsensor*cos5;
	                float sinAngle = b*b/(h*h+b*b);
	                // estimate the target speed depending on turn and on how close it is
	                targetSpeed = maxSpeed*(sensorsensor*sinAngle/maxSpeedDist);
	            }

	        }

	        // accel/brake command is exponentially scaled w.r.t. the difference between target speed and current one
	        return (float) (2/(1+Math.exp(sensors.getSpeed() - targetSpeed)) - 1);
	        //return 1;
	    }
	    else
	        return (float) 0.3; // when out of track returns a moderate acceleration command

	}

	public Action control(SensorModel sensors, SocketHandler mySocket){
		
		this.mySocket = mySocket;
		
		// check if car is currently stuck
		if ( Math.abs(sensors.getAngleToTrackAxis()) > stuckAngle )
	    {
			// update stuck counter
	        //stuck++;
	    }
	    else
	    {
	    	// if not stuck reset stuck counter
	        stuck = 0;
	    }

		// after car is stuck for a while apply recovering policy
	    if (stuck > stuckTime)
	    {
	    	/* set gear and sterring command assuming car is 
	    	 * pointing in a direction out of track */
	    	
	    	// to bring car parallel to track axis
	        float steer = (float) (- sensors.getAngleToTrackAxis() / steerLock); 
	        int gear=-1; // gear R
	        
	        // if car is pointing in the correct direction revert gear and steer  
	        if (sensors.getAngleToTrackAxis()*sensors.getTrackPosition()>0)
	        {
	            gear = 1;
	            steer = -steer;
	        }
	        clutch = clutching(sensors, clutch);
	        // build a CarControl variable and return it
	        Action action = new Action ();
	        action.gear = gear;
	        action.steering = steer;
	        action.accelerate = 1.0;
	        action.brake = 0;
	        action.clutch = clutch;
	        
	       // action.restartRace = true;
	        
	        return action;
	    }

	    else // car is not stuck
	    {
	    	// compute accel/brake command
	        float accel_and_brake = getAccel(sensors);
	        // compute gear 
	        int gear = getGear(sensors);
	        
	        
	        // compute steering
	        //float steer = getSteer(sensors);
	        float steer = train(getSteerState(sensors.getTrackPosition()),false, getPorcentaje(sensors),sensors);
	        // normalize steering
	        if (steer < -1)
	            steer = -1;
	        if (steer > 1)
	            steer = 1;
//	        
//	        System.out.println(sensors.getTrackPosition());
	        
//	        // set accel and brake from the joint accel/brake command 
	        float accel,brake;
	        if (accel_and_brake>0)
	        {
	            accel = accel_and_brake;
	            brake = 0;
	        }
	        else
	        {
	            accel = 0;
	            // apply ABS to brake
	            brake = filterABS(sensors,-accel_and_brake);
	        }
//	        
	        clutch = clutching(sensors, clutch);
//	        
	        // build a CarControl variable and return it
	        Action action = new Action ();
	        action.gear = gear;

//	        System.out.println("Estado: " +getSteerState(sensors.getTrackPosition()));
//	        System.out.println("Posicion: " +sensors.getTrackPosition());
//	        System.out.println("Angulo: " + sensors.getAngleToTrackAxis());
//	        System.out.println("Steer: " + steer);
//	        System.out.println("Distancia Recorrida: " + sensors.getDistanceRaced());
//	        System.out.println("Distancia desde el inicio: " + sensors.getDistanceFromStartLine());
	        //double porcentaje = getPorcentaje(sensors);
	        //sensors.getAngleToTrackAxis()
	        action.steering = steer;

			action.accelerate = accel;
	        action.brake = 0;
	        action.clutch = 0;
	        return action;
	    }
	}
	
	private double getPorcentaje(SensorModel sensors) {
		
		//System.out.println(sensors.getCurrentLapTime());
		if (sensors.getCurrentLapTime() > 10.0) return 0.99;
		else return 0.8;
	}

	private Integer getSteerState(double trackPosition) {
		//derecha negativo izquierda positivo
		if (trackPosition <= 0.2 && trackPosition >= -0.2) return 0; // centro
		else if (trackPosition < -0.2 && trackPosition >=-0.5) return 1; // centro-derecha
		else if (trackPosition > 0.2 && trackPosition <= 0.5) return 2; // centro-izquierda
		else if (trackPosition > 0.5) return 3; //izquierda
		else if(trackPosition < - 0.5) return 4; // derecha
		
		return null;
	}

	public float train(Integer startState, Boolean isStuck, Double porcentaje, SensorModel sensors){
				
		float steer = 0.0f;
		

			if (porcentaje > 1.0)
				porcentaje = 1.0;
			
			
			System.out.println(porcentaje);

			// El estado actual ser� el estado siguiente del estado anterior.
			Integer currentState = startState;

			// Mientras no se llegue al estado objetivo


				// Paso 1. Escoger un movimiento.

				// Elige la posici�n que obtenga una mayor recompensa a partir del estado
				// actual. //EXPLOTA
				Integer best_steer_angle = qtable.getBestRewardPosition(currentState);

				// Calculamos el estado anterior
				Integer previousState = null;
				// Explora nuevos estados
				if (this.randomGenerator.nextDouble() > porcentaje) { // EXPLORA
					// Elige un movimiento aleatorio
					Integer sorted = this.randomGenerator.nextInt(Constantes.NUM_ANGLES);
					best_steer_angle = sorted;
				}
				
				// Hay que calcular la recompensa para el estado anterior.
				
				// Estado Anterior
				previousState = currentState;

		
				// Obtiene la recompensa del estado actual
				Double targetReward = 0.0;
				
				

				if(Math.abs(sensors.getTrackPosition()) > 1) {
					/**
					 * Si el coche se sale de la carretera, entonces se recompensa negativamente.
					 */
					targetReward = -1.0;
					Action action = new Action();
					action.restartRace = true;
					
					/**
					 * Antes de reiniciar deber�amos actualizar la tabla con una recompensa negativa.
					 */
					
					Double reward = qtable.setReward(previousState, currentState, best_steer_angle, targetReward,
							getBestMoveFromTarget(previousState));


					
			        System.out.println("Estado: " +getSteerState(sensors.getTrackPosition()));
			        System.out.println("Posicion: " +sensors.getTrackPosition());
			        System.out.println("Angulo: " + sensors.getAngleToTrackAxis());
			        System.out.println("Steer: " + steer);
					System.out.println("Recompensa: " + targetReward);
					System.out.println("Distancia Recorrida: " + sensors.getDistanceRaced());
			        System.out.println("Distancia desde el inicio: " + sensors.getDistanceFromStartLine());
					System.out.println("-----------------------------");
					// Actualiza la ventana de la Q-Tabla
					qtable.setReward(previousState, currentState, best_steer_angle, targetReward,
							getBestMoveFromTarget(previousState));
					qTableFrame.setQTable(qtable);
					
					mySocket.send(action.toString());
					
				}else {
					/**
					 * La recompensa ser� proporcional a la distancia recorrida (cuanto mayor distancia, mayor recompensa)
					 * e inversamente proporcional a la distancia al centro de la carretera (cuanto m�s cercano a 0, m�s recompensa).
					 */
					targetReward = (sensors.getDistanceRaced())/(sensors.getAngleToTrackAxis() + 1);
					
					// sensors.getDistanceFromStartLine()
					//targetReward = 1/(Math.abs(sensors.getTrackPosition())+1);
				}
					
				
				
				
				switch (currentState) {
					case 0: // centro
						targetReward = 1000.0;
						break;
					case 1: //centro-derecha
						targetReward = 200.0;
						break;
					case 2: // centro-izquierda
						targetReward = 200.0;
						break;
					case 3: //izquierda
						targetReward = -1.0;
						break;
					case 4: // derecha
						targetReward = -1.0;
						break;
					default:
						System.out.println("ERROR");
				}
				
				// Se establece la recompensa para el estado anterior en funci�n del estado actual.
				Double reward = qtable.setReward(previousState, currentState, best_steer_angle, targetReward,
							getBestMoveFromTarget(previousState));

				// Actualiza la ventana de la Q-Tabla

				qTableFrame.setQTable(qtable);

				// Actualiza el estado previo.
				previousState = currentState;
			//////////////////////////////////////
		        System.out.println("Estado: " +getSteerState(sensors.getTrackPosition()));
		        System.out.println("Posicion: " +sensors.getTrackPosition());
		        System.out.println("Angulo: " + sensors.getAngleToTrackAxis());
		        System.out.println("Steer: " + steer);
				System.out.println("Recompensa: " + targetReward);
				System.out.println("Distancia Recorrida: " + sensors.getDistanceRaced());
		        System.out.println("Distancia desde el inicio: " + sensors.getDistanceFromStartLine());
			System.out.println("-----------------------------");
			
		return Constantes.STEER_VALUES[qtable.getBestRewardPosition(currentState)];
		
	}
	
	

	// Para calcular la maxFutureQ en la QTable
	private Integer getBestMoveFromTarget(Integer nextState) {
		Integer best_angle = null;
		best_angle = qtable.getBestRewardPosition(nextState);
		return best_angle;
	}


	private float filterABS(SensorModel sensors,float brake){
		// convert speed to m/s
		float speed = (float) (sensors.getSpeed() / 3.6);
		// when spedd lower than min speed for abs do nothing
	    if (speed < absMinSpeed)
	        return brake;
	    
	    // compute the speed of wheels in m/s
	    float slip = 0.0f;
	    for (int i = 0; i < 4; i++)
	    {
	        slip += sensors.getWheelSpinVelocity()[i] * wheelRadius[i];
	    }
	    // slip is the difference between actual speed of car and average speed of wheels
	    slip = speed - slip/4.0f;
	    // when slip too high applu ABS
	    if (slip > absSlip)
	    {
	        brake = brake - (slip - absSlip)/absRange;
	    }
	    
	    // check brake is not negative, otherwise set it to zero
	    if (brake<0)
	    	return 0;
	    else
	    	return brake;
	}
	
	float clutching(SensorModel sensors, float clutch)
	{
	  	 
	  float maxClutch = clutchMax;

	  // Check if the current situation is the race start
	  if (sensors.getCurrentLapTime()<clutchDeltaTime  && getStage()==Stage.RACE && sensors.getDistanceRaced()<clutchDeltaRaced)
	    clutch = maxClutch;

	  // Adjust the current value of the clutch
	  if(clutch > 0)
	  {
	    double delta = clutchDelta;
	    if (sensors.getGear() < 2)
		{
	      // Apply a stronger clutch output when the gear is one and the race is just started
		  delta /= 2;
	      maxClutch *= clutchMaxModifier;
	      if (sensors.getCurrentLapTime() < clutchMaxTime)
	        clutch = maxClutch;
		}

	    // check clutch is not bigger than maximum values
		clutch = Math.min(maxClutch,clutch);

		// if clutch is not at max value decrease it quite quickly
		if (clutch!=maxClutch)
		{
		  clutch -= delta;
		  clutch = Math.max((float) 0.0,clutch);
		}
		// if clutch is at max value decrease it very slowly
		else
			clutch -= clutchDec;
	  }
	  return clutch;
	}
	
	public float[] initAngles()	{
		
		float[] angles = new float[19];

		/* set angles as {-90,-75,-60,-45,-30,-20,-15,-10,-5,0,5,10,15,20,30,45,60,75,90} */
		for (int i=0; i<5; i++)
		{
			angles[i]=-90+i*15;
			angles[18-i]=90-i*15;
		}

		for (int i=5; i<9; i++)
		{
				angles[i]=-20+(i-5)*5;
				angles[18-i]=20-(i-5)*5;
		}
		angles[9]=0;
		return angles;
	}
}
