package types;

public class Rhombus extends QuaxTile {

	private final int xPos;
	private final int yPos;
	
	public Rhombus(int x, int y) {
		super();
		this.xPos = x;
		this.yPos = y;
	}
	
	public Rhombus(Rhombus r) {
		super(r);
		this.xPos = r.xPos;
		this.yPos = r.yPos;
	}
	
	@Override
	public QuaxCoordinate getCoordinates() {
		return new QuaxCoordinate(xPos, yPos, false);
	}
	
	@Override
	public boolean onLow() {
		return false;
	}

	@Override
	public boolean onHigh() {
		return false;
	}
}
