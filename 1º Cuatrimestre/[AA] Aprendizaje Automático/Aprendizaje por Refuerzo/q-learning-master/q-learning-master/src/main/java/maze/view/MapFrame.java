package maze.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

public class MapFrame extends JPanel {

	private static final long serialVersionUID = 1L;
	private Integer xSize, ySize;
	private Color[][] mapColors;
	private Integer[][] map;
	private Integer[] currentPosition, startPosition, endPosition;

	public MapFrame(Integer[][] map, Integer[] currentPosition,Integer[] startPosition, Integer[] endPosition) {
		this.xSize = map.length;
		this.ySize = map[0].length;
		this.map = map;
		this.currentPosition = currentPosition;
		this.startPosition = startPosition;
		this.endPosition = endPosition;
		this.colorMap();
		new CaptionFrame();
	}

	private void colorMap() {
		this.mapColors = new Color[xSize][ySize];
		for (Integer i = 0; i < xSize; i++) {
			for (Integer j = 0; j < ySize; j++) {
				mapColors[i][j] = Color.BLACK;
			}
		}
	}
	
	public void setMap(Integer[][] map) {
		this.map = map;
	}
	
	public void setCurrentPosition(Integer[] currentPosition) {
		this.currentPosition = currentPosition;
	}
	

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		// Clear the board
		g.clearRect(0, 0, getWidth(), getHeight());
		// Draw the grid
		int rectWidth = getHeight() / xSize;
		int rectHeight = getWidth() / ySize;

		for (int i = 0; i < xSize; i++) {
			for (int j = 0; j < ySize; j++) {
				int y = i * rectWidth;
				int x = j * rectHeight;
				Color mapColor;
				switch (this.map[i][j]) {
					case -1: {
						mapColor = Color.RED;
						break;
					}
					case -100: {
						mapColor = Color.DARK_GRAY;
						break;
					}
					case 100: 
					case 1: {
						mapColor = Color.LIGHT_GRAY;
						break;
					}
					default: {
						mapColor = Color.BLACK;
					}
				}
				g.setColor(mapColor);
				g.fillRect(x, y, rectHeight, rectWidth);
			}
		}
		
		// Current Position
		g.setColor(Color.BLUE);
		g.fillRect(this.currentPosition[1] * rectHeight, this.currentPosition[0] * rectWidth, rectHeight, rectWidth);
		
		// Start Position
		g.setColor(Color.WHITE);
		g.fillRect(this.startPosition[1] * rectHeight, this.startPosition[0] * rectWidth, rectHeight, rectWidth);
		
		// End Position
		g.setColor(Color.GREEN);
		g.fillRect(this.endPosition[1] * rectHeight, this.endPosition[0] * rectWidth, rectHeight, rectWidth);
		
	}

}
