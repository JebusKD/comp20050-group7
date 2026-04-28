package types;


/*
 * Represent the Octagon tiles on the board
 */
public class Octagon extends QuaxTile {

	// TODO - Move to parent class?
	private final int xPosition;
	private final int yPosition;


	public Octagon(int x, int y) {
		super();
		this.xPosition = x;
		this.yPosition = y;
	}
	
	public Octagon(Octagon o) {
		super(o);
		this.xPosition = o.xPosition;
		this.yPosition = o.yPosition;
	}


	@Override
	public QuaxCoordinate getCoordinates() {
		return new QuaxCoordinate(xPosition, yPosition, true);
	}

	@Override
	public boolean onLow() {
		if (isFree()) {
            return false;
        }

		return (xPosition == 0 && isWhite()) || (yPosition == 10 && isBlack());
	}

	@Override
	public boolean onHigh() {
		if (isFree()) {
            return false;
        }

		return (xPosition == 10 && isWhite()) || (yPosition == 0 && isBlack());
	}
}
