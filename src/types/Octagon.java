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
		return QuaxCoordinate.newOctagonCoordinate(xPosition, yPosition);
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
