package maze.model;

public class QCell {

	private Double belowReward, upReward, leftReward, rightReward;
	
	public QCell() {
		this.belowReward = Double.MIN_VALUE;
		this.upReward = Double.MIN_VALUE;
		this.leftReward = Double.MIN_VALUE;
		this.rightReward = Double.MIN_VALUE;
	}

	public Double getBelowReward() {
		return belowReward;
	}

	public void setBelowReward(Double belowReward) {
		this.belowReward = belowReward;
	}

	public Double getUpReward() {
		return upReward;
	}

	public void setUpReward(Double upReward) {
		this.upReward = upReward;
	}

	public Double getLeftReward() {
		return leftReward;
	}

	public void setLeftReward(Double leftReward) {
		this.leftReward = leftReward;
	}

	public Double getRightReward() {
		return rightReward;
	}

	public void setRightReward(Double rightReward) {
		this.rightReward = rightReward;
	}
	
	public Double getReward(MovePosition movement) {
		switch (movement) {
		case UP:
			return this.getUpReward();
		case DOWN:
			return this.getBelowReward();
		case LEFT:
			return this.getLeftReward();
		case RIGHT:
			return this.getRightReward();
		}
		return null;
	}
	
}
