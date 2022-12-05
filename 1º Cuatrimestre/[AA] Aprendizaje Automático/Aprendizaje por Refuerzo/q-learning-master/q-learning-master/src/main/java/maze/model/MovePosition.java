package maze.model;

public enum MovePosition {

	UP (0), DOWN (1), LEFT (2), RIGHT (3);
	
	private int value;

    private MovePosition(Integer value) {
		this.value = value;
	}

    public int getValue() {
        return value;
    }
    
}
