package types;

public class Octagon extends QuaxTile {

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
		return yPosition == 10;
	}

	@Override
	public boolean onHigh() {
		return yPosition == 0;
	}

	@Override
	public boolean onLeft() {
		return xPosition == 0;
	}

	@Override
	public boolean onRight() {
		return xPosition == 10;
	}
}
