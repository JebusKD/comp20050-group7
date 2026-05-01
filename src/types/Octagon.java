package types;

import model.QuaxBoard;

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

		return distanceToLowWall() == 0;
	}

	@Override
	public boolean onHigh() {
		if (isFree()) {
            return false;
        }

		return distanceToHighWall() == 0;
	}
	
	public int distanceToLowWall() {
		assert isOccupied();
		if (getTileColour() == QuaxTileColour.BLACK) {
			return yPosition;
		}
		else {
			return xPosition;
		}
	}

	public int distanceToHighWall() {
		assert isOccupied();
		if (getTileColour() == QuaxTileColour.BLACK) {
			return QuaxBoard.MAX_OCTAGONS - (yPosition + 1);
		}
		else {
			return QuaxBoard.MAX_OCTAGONS - (xPosition + 1);
		}
	}
}
