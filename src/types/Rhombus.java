package types;


public class Rhombus extends QuaxTile {

	public Rhombus(int x, int y) {
		super(x, y);
	}
	
	public Rhombus(Rhombus r) {
		super(r);
	}


	@Override
	public QuaxCoordinate getCoordinates() {
		return QuaxCoordinate.newRhombusCoordinate(getXPosition(), getYPosition());
	}
}
