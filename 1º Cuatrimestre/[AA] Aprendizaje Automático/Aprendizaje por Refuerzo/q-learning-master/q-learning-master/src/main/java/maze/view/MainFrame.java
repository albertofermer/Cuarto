package maze.view;

import java.awt.Dimension;

import javax.swing.JFrame;

public class MainFrame {
	
	private JFrame frame;
	private MapFrame mp;
	
	public MainFrame() {
		this.frame = new JFrame("Main Map");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.frame.setSize(new Dimension(800,600));
		this.frame.setVisible(true);
	}

	public void updateMap(Integer[][] map, Integer[] currentPosition, Integer[] startPosition, Integer [] endPosition) {
		if (this.mp == null) {
			this.mp = new MapFrame(map, currentPosition, startPosition, endPosition);
			this.frame.add(mp);
		} else {
			this.mp.setMap(map);
			this.mp.setCurrentPosition(currentPosition);
			this.mp.repaint();
		}
		this.frame.validate();
	}
}
