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

	//TODO - Too many returns?
	@Override
	public boolean onLow() {
		if (isFree()) {
            return false;
        }
		
		if (getColour() == QuaxTileColour.BLACK) {
            return yPosition == 10;
        }

		return xPosition == 0;
	}

	//TODO - Too many returns?
	@Override
	public boolean onHigh() {
		if (isFree()) {
            return false;
        }
		
		if (getColour() == QuaxTileColour.BLACK) {
            return yPosition == 0;
        }

        return xPosition == 10;
	}
}
