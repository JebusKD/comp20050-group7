package types;


/*
 * Represent the Rhombus tiles on the board
 */
public class Rhombus extends QuaxTile {

	public Rhombus(int x, int y) {
		super(x, y);
	}
	
	public Rhombus(Rhombus r) {
		super(r);
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
