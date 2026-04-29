package types;


/*
 * Represent the Rhombus tiles on the board
 */
public class Rhombus extends QuaxTile {

	// TODO - Move to parent class?
	private final int xPosition;
	private final int yPosition;


	public Rhombus(int x, int y) {
		super();
		this.xPosition = x;
		this.yPosition = y;
	}
	
	public Rhombus(Rhombus r) {
		super(r);
		this.xPosition = r.xPosition;
		this.yPosition = r.yPosition;
	}


	@Override
	public QuaxCoordinate getCoordinates() {
		return new QuaxCoordinate(xPosition, yPosition, false);
	}


	@Override
	public boolean onLow() {
		return false;
	}

	@Override
	public boolean onHigh() {
		return false;
	}
}
