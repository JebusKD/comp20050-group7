package types;

public class Octagon extends QuaxTile {

	private final int xPos;
	private final int yPos;
	
	public Octagon(int x, int y) {
		this.xPos = x;
		this.yPos = y;
	}
	
	public QuaxCoordinate getCoordinates() {
		return new QuaxCoordinate(xPos, yPos, true);
	}
	
	@Override
	public boolean onLow() {
		if (isFree()) return false;
		
		if (getColour() == QuaxTileColour.BLACK)
			return yPos == 10;
		else return xPos == 0;
	}
	
	@Override
	public boolean onHigh() {
		if (isFree()) return false;
		
		if (getColour() == QuaxTileColour.BLACK)
			return yPos == 0;
		else return xPos == 10;
	}
		
}
