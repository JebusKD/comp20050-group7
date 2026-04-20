package types;

public class QuaxCoordinate {
	private final int x;
	private final int y;
	private final boolean octagonMove;
	
	// TODO Flag argument, see clean code "Error Handling"
	// Change this constructor to private and construct via static methods
	public QuaxCoordinate(int x, int y, boolean octagonMove) {
		this.x = x;
		this.y = y;
		this.octagonMove = octagonMove;
	}
	
	public int x() {
		return this.x;
	}
	
	public int y() {
		return this.y;
	}
	
	public boolean isOctagonMove() {
		return octagonMove;
	}
	
	public boolean isRhombusMove() {
		return !octagonMove;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		else if (o instanceof QuaxCoordinate c)
			return this.x == c.x &&
				   this.y == c.y &&
				   this.octagonMove == c.octagonMove;
		else return false;
	}
}
