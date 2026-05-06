package types;

import static model.QuaxBoard.NUM_RHOMBUSES;

public class Rhombus extends QuaxTile {

	public Rhombus(int x, int y) {
		super(x, y);
		
		if (x < 0 || x >= NUM_RHOMBUSES) {
			throw new IllegalArgumentException("Rhombus must be constructed with x coordinate in range [0," + (NUM_RHOMBUSES-1) + "].");
		}
		if (y < 0 || y >= NUM_RHOMBUSES) {
			throw new IllegalArgumentException("Rhombus must be constructed with y coordinate in range [0," + (NUM_RHOMBUSES-1) + "].");
		}
	}
	
	public Rhombus(Rhombus r) {
		super(r);
		
		if (r == null) {
			throw new IllegalArgumentException("Rhombus cannot be constructed using null Rhombus as copy.");
		}
	}


	@Override
	public QuaxCoordinate getCoordinates() {
		assert getXPosition() >= 0 && getXPosition() < NUM_RHOMBUSES
				&& getYPosition() >= 0 && getYPosition() < NUM_RHOMBUSES;
				
		return QuaxCoordinate.newRhombusCoordinate(getXPosition(), getYPosition());
	}
}
