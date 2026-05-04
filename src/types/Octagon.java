package types;

import static model.QuaxBoard.NUM_OCTAGONS;
import model.QuaxBoard;


public class Octagon extends QuaxTile {

	public Octagon(int x, int y) {
		if (x < 0 || x >= NUM_OCTAGONS) {
			throw new IllegalArgumentException("Octagon must be constructed with x coordinate in range [0," + (NUM_OCTAGONS-1) + "].");
		}
		if (y < 0 || y >= NUM_OCTAGONS) {
			throw new IllegalArgumentException("Octagon must be constructed with y coordinate in range [0," + (NUM_OCTAGONS-1) + "].");
		}
		
		super(x, y);
	}
	
	public Octagon(Octagon o) {
		if (o == null) {
			throw new IllegalArgumentException("Octagon cannot be constructed using null Octagon as copy.");
		}
		
		super(o);
	}


	@Override
	public QuaxCoordinate getCoordinates() {
		assert getXPosition() >= 0 && getXPosition() < NUM_OCTAGONS
				&& getYPosition() >= 0 && getYPosition() < NUM_OCTAGONS;
		
		return QuaxCoordinate.newOctagonCoordinate(getXPosition(), getYPosition());
	}

	/* Return the distance from
	 *  The bottom border if BLACK
	 *  The left border if WHITE
	 */
	public int distanceToLowWall() {
		assert getXPosition() >= 0 && getXPosition() < NUM_OCTAGONS
				&& getYPosition() >= 0 && getYPosition() < NUM_OCTAGONS;
		
		if (isFree()) {
			throw new IllegalStateException("Distance to Low Wall cannot be evaluated for free Octagons.");
		}
		
		if (getTileColour() == QuaxTileColour.BLACK) {
			return getYPosition();
		}
		else {
			return getXPosition();
		}
	}

	/* Return the distance from
	 *  The top border if BLACK
	 *  The right border if WHITE
	 */
	public int distanceToHighWall() {
		assert getXPosition() >= 0 && getXPosition() < NUM_OCTAGONS
				&& getYPosition() >= 0 && getYPosition() < NUM_OCTAGONS;
		
		if (isFree()) {
			throw new IllegalStateException("Distance to High Wall cannot be evaluated for free Octagons.");
		}
		
		if (getTileColour() == QuaxTileColour.BLACK) {
			return QuaxBoard.NUM_OCTAGONS - (getYPosition() + 1);
		}
		else {
			return QuaxBoard.NUM_OCTAGONS - (getXPosition() + 1);
		}
	}
}
