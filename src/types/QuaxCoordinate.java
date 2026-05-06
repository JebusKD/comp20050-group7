package types;

import static model.QuaxBoard.*;


public class QuaxCoordinate {

	private final int x;
	private final int y;
	private final boolean isOctagon;

	private QuaxCoordinate(int x, int y, boolean isOctagon) {
		assert x < NUM_OCTAGONS && x > -1;
		assert y < NUM_OCTAGONS && y > -1;
		this.x = x;
		this.y = y;
		this.isOctagon = isOctagon;
	}

	public static QuaxCoordinate newOctagonCoordinate(int x, int y) {
		if (x < 0 || x >= NUM_OCTAGONS) {
			throw new IllegalArgumentException("Octagon coordinate must be constructed for x value in range [0," + (NUM_OCTAGONS-1) +"]. Was " + x + ".");
		}
		if (y < 0 || y >= NUM_OCTAGONS) {
			throw new IllegalArgumentException("Octagon coordinate must be constructed for y value in range [0," + (NUM_OCTAGONS-1) +"]. Was " + y + ".");
		}
		
		return new QuaxCoordinate(x, y, true);
	}
	
	public static QuaxCoordinate newRhombusCoordinate(int x, int y) {
		if (x < 0 || x >= NUM_RHOMBUSES) {
			throw new IllegalArgumentException("Rhombus coordinate must be constructed for x value in range [0," + (NUM_RHOMBUSES-1) +"]. Was " + x + ".");
		}
		if (y < 0 || y >= NUM_RHOMBUSES) {
			throw new IllegalArgumentException("Rhombus coordinate must be constructed for y value in range [0," + (NUM_RHOMBUSES-1) +"]. Was " + y + ".");
		}
		
		return new QuaxCoordinate(x, y, false);
	}


	public int x() {
		return this.x;
	}
	
	public int y() {
		return this.y;
	}
	
	public boolean isOctagon() {
		return isOctagon;
	}
	
	public boolean isRhombus() {
		return !isOctagon();
	}
}
