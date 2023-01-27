package QLearning;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		QTable qtable_steer = new QTable("Test",Constantes.NUM_STATES_VEL, Constantes.NUM_VEL, Constantes.VEL_VALUES);
		qtable_steer.saveQTable("qtable_velocidad");
		qtable_steer.loadQTable("qtable_velocidad");
		QTableFrame qTableFrame_steer = new QTableFrame(qtable_steer, Constantes.VEL_VALUES, Constantes.NUM_VEL);
	}

}
