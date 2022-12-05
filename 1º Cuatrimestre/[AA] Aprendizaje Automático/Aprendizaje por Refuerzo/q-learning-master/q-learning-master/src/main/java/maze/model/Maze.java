package maze.model;

public class Maze {

	Integer[][] map;

	public Integer[][] getMap() {
		return map;
	}

	public void setMap(Integer[][] map) {
		this.map = map;
	}

	public Maze(Integer[][] map) {
		this.map = map;
	}

	public void printMap() {
		for (int i = 0; i < this.map.length; i++) {
			for (int j = 0; j < this.map[i].length; j++) {
				System.out.print(this.map[i][j] + "\t");
			}
			System.out.print("\n");
		}
	}

	public Integer[] getCoordinates(Integer position) {
		Integer[] coordinates = { 0, 0 };
		Integer columnSize = this.map[0].length;
		coordinates[0] = position / columnSize;
		coordinates[1] = position % columnSize;
		return coordinates;
	}

	public Integer getPosition(Integer x, Integer y) {
		
		if (this.validateMovement(x, y).equals(Boolean.FALSE)) {
			return -1;
		}
		
		Integer columnSize = this.map[0].length;
		return x * columnSize + y;
	}

	public Integer move(Integer currentState, MovePosition movePosition) {
		Integer[] currentCoordinates = this.getCoordinates(currentState);
		switch (movePosition) {
		case UP: {
			currentCoordinates[0]--;
			break;
		}
		case DOWN: {
			currentCoordinates[0]++;
			break;
		}
		case LEFT: {
			currentCoordinates[1]--;
			break;
		}
		case RIGHT: {
			currentCoordinates[1]++;
			break;
		}
		}
		return this.getPosition(currentCoordinates[0], currentCoordinates[1]);

	}

	public Boolean validateMovement(Integer x, Integer y) {
		Boolean result = true;
		if (x < 0 || y < 0 || x >= map.length || y >= map[0].length || this.map[x][y] == 0) {
			result = false;
		}
		return result;
	}

}
