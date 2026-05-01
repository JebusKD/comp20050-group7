package types;

import static model.QuaxBoard.*;


public class QuaxCoordinate {

	private final int x;
	private final int y;
	private final boolean isOctagon;

	private QuaxCoordinate(int x, int y, boolean isOctagon) {
		assert x < MAX_OCTAGONS && x > -1;
		assert y < MAX_OCTAGONS && y > -1;
		this.x = x;
		this.y = y;
		this.isOctagon = isOctagon;
	}

	public static QuaxCoordinate newOctagonCoordinate(int x, int y) {
		return new QuaxCoordinate(x, y, true);
	}
	
	public static QuaxCoordinate newRhombusCoordinate(int x, int y) {
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
