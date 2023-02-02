package QLearning;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class QTableFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private QTable qTable;
	private JTable jTable;
	private JScrollPane jScrollPane;
	private String[][] data;
	private String[] columnsNames;

	float[][] acciones;
	int num_acciones;

	public QTableFrame(QTable qTable, float[][] acciones, int num_acciones) {
		this.acciones = acciones;
		this.num_acciones = num_acciones;

		// Creates Window
		this.setTitle("Q Table - " + qTable.nombre);
		this.setSize(new Dimension(num_acciones*80, (int) (20*qTable.size())));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocation(801, 0);

		// Creates QTable
		this.setQTable(qTable);

		// this.columnsNames = new String[]{ "s", "m", "r" };
		
		
		
		this.columnsNames = new String[num_acciones + 1];

		columnsNames[0] = "Estado";
		for (int column_name = 1; column_name < this.columnsNames.length; column_name++) {
			String state = "";
			for(int i = 0; i < acciones[column_name-1].length; i++) {
				state += "/"+acciones[column_name-1][i] + "/";
			}
			columnsNames[column_name] = state;
		}

		System.out.println(columnsNames[1]);

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

		if (this.data == null) {
			this.data = new String[size][num_acciones + 1];
		}

		while (i < size) {
			Integer qCellSource = i;
			QCell qCell = this.qTable.getQCell(qCellSource);

			if (this.data[i] == null) {

				String[] values = new String[num_acciones + 1];
				values[0] = Integer.toString(i + 1); // estado
				// values[1] = movePosition.toString(); // movimiento

				for (int index = 1; index < num_acciones + 1; index++) {
					System.out.println(index);
					values[index] = qCell.getReward(index - 1).toString();
				}

				this.data[i] = values;

			} else {
				this.data[i][0] = Integer.toString(i); // estado
				for (int index = 1; index < num_acciones + 1; index++) {

					data[i][index] = qCell.getReward(index - 1).toString();
				}
			}
			i++;
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
