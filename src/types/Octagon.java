package types;


/*
 * Represent the Octagon tiles on the board
 */
public class Octagon extends QuaxTile {

	public Octagon(int x, int y) {
		super(x, y);
	}
	
	public Octagon(Octagon o) {
		super(o);
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
