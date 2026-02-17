package types;

public class QuaxCoordinate {
	private int x;
	private int y;
	private boolean octagonMove;
	
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
	
	public int[] coordinates() {
		return new int[] {x, y};
	}
	
	public boolean isOctagonMove() {
		return octagonMove;
	}
	
	public boolean isRhombusMove() {
		return !octagonMove;
	}
}
