import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

@SuppressWarnings("serial")
public class Main extends JFrame {

	public static final int CANVAS_WIDTH = 400; // ширина поля
	public static final int CANVAS_HEIGHT = 300; // высота поля
	public static final int[] LINES = { 80, 200, 320 }; // расположение стержней
	public static final int FLOOR_WIDTH = 100; // ширина основания стержня
	public static final int LINE_HEIGHT = 200; // высота стержня
	public static final int[] DISC_SIZE = { 10, 10 }; // ширина, высота

	public static final Color LINE_COLOR = Color.BLACK; // цвет линий
	public static final Color SELECT_COLOR = Color.RED; // цвет выбранного
														// кольца
	public static final Color SELECTOR_COLOR = new Color(255, 255, 0, 192); // цвет
																			// селектора
																			// стержня
	public static final Color CANVAS_BACKGROUND = Color.WHITE; // задний фон

	public static final int COUNT_MIN = 1; // минимальное количество колец
	public static final int COUNT_MAX = 10; // максимальное количество колец

	private int ppos = -1; // выбранный стержень
	private int pos = 0; // текущая позиция указателя

	private DrawCanvas canvas;

	public static Game game;

	public Main() {
		this.setResizable(false);

		// интерфейс

		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(2, 3));

		JButton btnLeft = new JButton("Move left (A)");
		panel.add(btnLeft);
		btnLeft.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				leftPressed();
				requestFocus();
			}
		});

		JButton btnRight = new JButton("Move right (D)");
		panel.add(btnRight);
		btnRight.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rightPressed();
				requestFocus();
			}
		});

		JButton btnAction = new JButton("Select/Move (Space)");
		panel.add(btnAction);
		btnAction.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionPressed();
				requestFocus();
			}
		});

		final JSpinner spinner = new JSpinner();
		spinner.setValue(3);

		JButton btnNew = new JButton("New game");
		panel.add(btnNew);
		btnNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int count = COUNT_MIN;
				try {
					count = (Integer) spinner.getValue();
				} catch (Exception ex) {
					System.out.print(ex.toString());
				}

				count = Math.min(count, COUNT_MAX);
				count = Math.max(count, COUNT_MIN);

				game.newGame(count);
				ppos = -1;
				pos = 0;

				spinner.setValue(count);

				repaint();
				requestFocus();
			}
		});

		JTextField label = new JTextField("Disc count: ");
		label.setEditable(false);
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		panel.add(label);

		panel.add(spinner);

		canvas = new DrawCanvas();
		canvas.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));

		Container cp = getContentPane();
		cp.setLayout(new BorderLayout());
		cp.add(canvas, BorderLayout.CENTER);
		cp.add(panel, BorderLayout.SOUTH);

		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent evt) {
				switch (evt.getKeyCode()) {
				case KeyEvent.VK_LEFT:
				case KeyEvent.VK_A:
					leftPressed();
					break;
				case KeyEvent.VK_RIGHT:
				case KeyEvent.VK_D:
					rightPressed();
					break;
				case KeyEvent.VK_SPACE:
					actionPressed();
					break;
				case KeyEvent.VK_ESCAPE:
					ppos = -1;
					repaint();
					break;
				}
			}
		});

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setTitle("Hanoi Tower");

		pack();
		setVisible(true);
		requestFocus();

		// центрирование окна
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((int) ((dimension.getWidth() - getWidth()) / 2), (int) ((dimension.getHeight() - getHeight()) / 2));
	}

	public void leftPressed() {
		if (game.solved)
			return;
		if (--pos < 0) {
			pos = 2;
		}
		repaint();
	}

	public void rightPressed() {
		if (game.solved)
			return;
		pos = ++pos % 3;
		repaint();
	}

	public void actionPressed() {
		if (game.solved) {
			game.newGame();
			ppos = -1;
			pos = 0;
		} else if (ppos == pos) {
			ppos = -1;
		} else if (ppos >= 0) {
			if (!game.moveDisc(ppos, pos)) {
				ppos = pos;
			} else {
				ppos = -1;
			}
			if (game.isSolved()) {
				repaint();
				JOptionPane.showMessageDialog(null, "You won!");
			}
		} else {
			if (!game.isTowerEmpty(pos)) {
				ppos = pos;
			}
		}
		repaint();
	}

	class DrawCanvas extends JPanel {
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);

			setBackground(CANVAS_BACKGROUND);

			int ya = CANVAS_HEIGHT / 2 + LINE_HEIGHT / 2; // координата Y пола

			g.setColor(SELECTOR_COLOR);

			g.fillRect(LINES[pos] - FLOOR_WIDTH / 2, CANVAS_HEIGHT / 2 - LINE_HEIGHT / 2, FLOOR_WIDTH, LINE_HEIGHT); // рисуем
																														// селектор

			for (int i = 0; i < 3; i++) {

				g.setColor(LINE_COLOR);

				// рисуем вертикальные линии
				g.drawLine(LINES[i], CANVAS_HEIGHT / 2 + LINE_HEIGHT / 2, LINES[i], CANVAS_HEIGHT / 2 - LINE_HEIGHT / 2);
				// рисуем горизонтальные линии
				g.drawLine(LINES[i] - FLOOR_WIDTH / 2, CANVAS_HEIGHT / 2 + LINE_HEIGHT / 2, LINES[i] + FLOOR_WIDTH / 2,
						CANVAS_HEIGHT / 2 + LINE_HEIGHT / 2);
				// рисуем диски
				for (int j = 0; j < game.towers.get(i).size(); j++) {
					Game.Disc d = game.towers.get(i).get(j);
					int e = d.value;
					if (i == ppos && d == game.towers.get(i).lastElement()) {
						g.setColor(SELECT_COLOR);
					} else {
						g.setColor(LINE_COLOR);
					}
					g.fillRect(LINES[i] - DISC_SIZE[0] / 2 * e, ya - DISC_SIZE[1] * j - DISC_SIZE[1], DISC_SIZE[0] * e,
							DISC_SIZE[1]);
					g.setColor(Color.WHITE);
					g.drawString(Integer.toString(d.value - 1), LINES[i] - 3, ya - DISC_SIZE[1] * j);

				}
			}

			g.setColor(LINE_COLOR);
			g.setFont(Font.getFont("Arial"));
			g.drawString("Moves: " + (game.moves), 10, 15);

			if (game.solved) {
				g.drawString(
						"Moves: " + (game.moves) + " (Minimum required moves: "
								+ Math.round((Math.pow(2, game.size) - 1)) + ")", 10, 15);
				g.drawString("Press \"New game\" or Space to start a new game!", 10, 30);
			} else {
				g.drawString("Moves: " + (game.moves), 10, 15);
			}
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				game = new Game(3);
				new Main();
			}
		});
	}
}