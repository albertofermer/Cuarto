package QLearning;

import java.util.ArrayList;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		QTable qtable_velocidad = new QTable("Velocidad", Constantes.NUM_STATES_VEL, Constantes.NUM_VEL,
				Constantes.VEL_VALUES);
		qtable_velocidad.loadQTable("qtable_velocidad");
		QTable qtable_steer = new QTable("Volante",Constantes.NUM_STATES_STEER, Constantes.NUM_ANGLES, Constantes.STEER_VALUES);
		qtable_steer.loadQTable("qtable_volante");
		
		Politica.savePolitica("volante", qtable_steer, Constantes.STEER_VALUES);
		Politica.savePolitica("velocidad", qtable_velocidad, Constantes.VEL_VALUES);

	}

}
