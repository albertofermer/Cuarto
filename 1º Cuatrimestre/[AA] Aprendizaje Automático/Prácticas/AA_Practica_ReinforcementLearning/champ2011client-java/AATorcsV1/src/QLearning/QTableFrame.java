package QLearning;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class QTableFrame extends JFrame{
	
	private static final long serialVersionUID = 1L;
	private QTable qTable;
	private JTable jTable;
	private JScrollPane jScrollPane;
	private String[][] data;
	private String[] columnsNames;
	
	public QTableFrame(QTable qTable) {
		// Creates Window
		this.setTitle("Q Table");
		this.setSize(new Dimension(800,110)); // 27.5 * num filas
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocation(801, 0);
		
		// Creates QTable
		this.setQTable(qTable);

		
		//this.columnsNames = new String[]{ "s", "m", "r" };
		this.columnsNames = new String[Constantes.NUM_ANGLES + 1] ;
		
		columnsNames[0] = "Estado";
		for (int column_name = 1; column_name < this.columnsNames.length; column_name++) {
			columnsNames[column_name] = Double.toString(-1+(0.25)*(column_name-1));
			//System.out.println(columnsNames[column_name]);
		}
		
		
		
		this.jTable = new JTable(this.data, columnsNames);
		this.jTable.disable();
		
		// Display Data
		this.jScrollPane = new JScrollPane(jTable);
		this.add(jScrollPane);
		this.setVisible(true);
	}

	private void setQTableData() {
		Integer size = this.qTable.size();
		Integer i = 0;
		
		if(this.data == null) {
			//this.data = new String[size * 4][3];
			this.data = new String[size][Constantes.NUM_ANGLES+1];
		}
		
		while (i < size) {
			Integer qCellSource = i;
			//for (MovePosition movePosition: MovePosition.values()) {
				QCell qCell = this.qTable.getQCell(qCellSource);
				
				if (this.data[i]== null) {
					
					String[] values = new String[Constantes.NUM_ANGLES+1]; 
					values[0] = Integer.toString(i + 1); // estado
					//values[1] = movePosition.toString(); // movimiento
					
					for (int index = 1 ; index < Constantes.NUM_ANGLES+1; index++) {
						System.out.println(index);
						values[index] = qCell.getReward(index-1).toString();
					}

					this.data[i] = values;
					
				} else {
					this.data[i][0] = Integer.toString(i); // estado
					for (int index = 1 ; index < Constantes.NUM_ANGLES+1; index++) {
						
						data[i][index] = qCell.getReward(index-1).toString();
					}

//					this.data[i][1] = movePosition.toString(); // movimiento
//					this.data[i][2] = qCell.getReward(movePosition).toString(); // recompensa
				}
				
				i++;
			//}
		}
		
		
		if (this.jTable != null) {
			this.jTable.repaint();
		}
		
	}
	
	public void setQTable(QTable qTable) {
		this.qTable = qTable;
		//this.initQTableData();
		this.setQTableData();
	}
	
	
	
	
	
}
