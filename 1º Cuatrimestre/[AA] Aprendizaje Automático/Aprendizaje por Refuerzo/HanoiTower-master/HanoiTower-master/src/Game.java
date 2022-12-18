import java.util.ArrayList;
import java.util.Stack;

public class Game {

	public int size;

	public int moves = 0;
	public boolean solved = false;

	public ArrayList<Tower> towers = new ArrayList<Tower>();

	public Game(int size) {
		this.size = size;
		newGame();
	}

	public void newGame() {
		if (!towers.isEmpty()) {
			for (Tower s : towers) {
				s.clear();
			}
			towers.clear();
		}
		for (int i = 0; i < 3; i++) {
			towers.add(new Tower());
		}
		for (int i = 0; i < size; i++) {
			Disc d = new Disc(size - i);
			towers.get(0).push(d);
		}
		moves = 0;
		solved = false;
	}

	public void newGame(int newSize) {
		this.size = newSize;
		newGame();
	}

	public boolean isSolved() {
		return (solved = towers.get(0).empty() && towers.get(1).empty());
	}

	public boolean isTowerEmpty(int towerId) {
		return towers.get(towerId).isEmpty();
	}

	public boolean moveDisc(int x1, int x2) {
		if (x1 == x2) {
			return false;
		}

		Tower from = towers.get(x1);
		Tower to = towers.get(x2);

		boolean isMovePossible = true;

		int fromLast = 0;
		int toLast = size;

		if (from.isEmpty()) {
			return false;
		} else {
			fromLast = from.lastElement().value;
		}
		if (!to.isEmpty()) {
			toLast = to.lastElement().value;
		}
		if (fromLast > toLast) {
			isMovePossible = false;
		}
		if (isMovePossible) {
			Disc d = from.pop();
			to.add(d);
			moves++;
		}
		return isMovePossible;
	}

	@SuppressWarnings("serial")
	public class Tower extends Stack<Disc> {

	}

	public class Disc {
		public int value;

		public Disc(int v) {
			value = v;
		}

	}

}
