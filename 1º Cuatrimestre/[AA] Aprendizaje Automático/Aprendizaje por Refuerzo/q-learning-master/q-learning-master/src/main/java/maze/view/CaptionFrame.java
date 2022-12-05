package maze.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class CaptionFrame extends JFrame{
	
	private static final long serialVersionUID = 1L;
	private static Integer CAPTIONS = 7;
	
	public CaptionFrame() {
		this.setTitle("Captions");
		this.setSize(new Dimension(300,400));
		this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		this.setLocation(250, 610);
		this.setLayout(new GridLayout(8,1));
		this.setBackground(Color.DARK_GRAY);

		JLabel titleLabel = new JLabel("Caption");
		titleLabel.setFont(new Font("ARIAL", Font.BOLD, 27));
		this.add(titleLabel);
		
		Color[] colors = {
			Color.WHITE, Color.GREEN, Color.BLUE, Color.DARK_GRAY, Color.LIGHT_GRAY, Color.BLACK, Color.RED
		};
		String[] captions = {
				"Start Position",
				"End Position",
				"Current Position",
				"Obstacle",
				"Available Position",
				"Out of Borders",
				"Best Path"
		};
		
		for (Integer i =0; i < CAPTIONS; i++) {
			JPanel captionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			JPanel squarePanel = new JPanel();
			JLabel captionLabel = new JLabel(captions[i]);
			captionLabel.setFont(new Font("ARIAL", Font.BOLD, 14));
			squarePanel.setBackground(colors[i]);
			squarePanel.setPreferredSize(new Dimension(45,45));
			captionPanel.setPreferredSize(new Dimension(100,45));			
			captionPanel.add(squarePanel);
			captionPanel.add(captionLabel);
			this.add(captionPanel);
		}
		
		this.setVisible(true);
	}

}
