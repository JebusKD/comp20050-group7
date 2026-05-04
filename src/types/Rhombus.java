package types;

import static model.QuaxBoard.NUM_RHOMBUSES;

public class Rhombus extends QuaxTile {

	public Rhombus(int x, int y) {
		if (x < 0 || x >= NUM_RHOMBUSES) {
			throw new IllegalArgumentException("Rhombus must be constructed with x coordinate in range [0," + (NUM_RHOMBUSES-1) + "].");
		}
		if (y < 0 || y >= NUM_RHOMBUSES) {
			throw new IllegalArgumentException("Rhombus must be constructed with y coordinate in range [0," + (NUM_RHOMBUSES-1) + "].");
		}
		
		super(x, y);
	}
	
	public Rhombus(Rhombus r) {
		if (r == null) {
			throw new IllegalArgumentException("Rhombus cannot be constructed using null Rhombus as copy.");
		}
		
		super(r);
	}


	@Override
	public QuaxCoordinate getCoordinates() {
		assert getXPosition() >= 0 && getXPosition() < NUM_RHOMBUSES
				&& getYPosition() >= 0 && getYPosition() < NUM_RHOMBUSES;
				
		return QuaxCoordinate.newRhombusCoordinate(getXPosition(), getYPosition());
	}
}
