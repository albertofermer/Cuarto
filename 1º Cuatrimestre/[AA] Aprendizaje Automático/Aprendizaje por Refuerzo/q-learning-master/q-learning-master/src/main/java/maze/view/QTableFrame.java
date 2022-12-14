package maze.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import maze.model.MovePosition;
import maze.model.QCell;
import maze.model.QTable;

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
		this.setSize(new Dimension(800,600));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocation(801, 0);
		
		// Creates QTable
		this.setQTable(qTable);
		//this.columnsNames = new String[]{ "s", "m", "r" };
		this.columnsNames = new String[] {"ESTADO","UP", "DOWN", "LEFT", "RIGHT"};
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
			this.data = new String[size][5];
		}
		
		while (i < size) {
			Integer qCellSource = i;
			//for (MovePosition movePosition: MovePosition.values()) {
				QCell qCell = this.qTable.getQCell(qCellSource);
				
				if (this.data[i]== null) {
					
					String[] values = new String[MovePosition.values().length]; // 4 posiciones
					values[0] = Integer.toString(i + 1); // estado
					//values[1] = movePosition.toString(); // movimiento
					values[1] = qCell.getReward(MovePosition.UP).toString();
					values[2] = qCell.getReward(MovePosition.DOWN).toString();
					values[3] = qCell.getReward(MovePosition.LEFT).toString();
					values[4] = qCell.getReward(MovePosition.RIGHT).toString();
					//System.out.println(qCell.getReward(movePosition));
					//values[2] = qCell.getReward(movePosition).toString(); // recompensa
					this.data[i] = values;
					
				} else {
					this.data[i][0] = Integer.toString(i); // estado
					this.data[i][1] = qCell.getReward(MovePosition.UP).toString();
					this.data[i][2] = qCell.getReward(MovePosition.DOWN).toString();
					this.data[i][3] = qCell.getReward(MovePosition.LEFT).toString();
					this.data[i][4] = qCell.getReward(MovePosition.RIGHT).toString();
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
		this.setQTableData();
	}
	
	
	
	
	
}
