package types;


public abstract class QuaxTile {

	private QuaxTileColour tileColour;
	private QuaxTileGroup tileGroup;

	private int strategyValue;

	protected final int xPosition;
	protected final int yPosition;


	public QuaxTile(int x, int y) {
		this.tileColour = QuaxTileColour.NONE;
		this.tileGroup = null;
		this.strategyValue = 0;
		this.xPosition = x;
		this.yPosition = y;
	}

	public QuaxTile(QuaxTile t) {
		this.tileColour = t.tileColour;
		this.tileGroup = null; // Don't copy tile group, added in the board after object is constructed

		this.strategyValue = t.strategyValue;
		this.xPosition = t.xPosition;
		this.yPosition = t.yPosition;
	}


	public QuaxTileColour getTileColour() {
		return this.tileColour;
	}

	public QuaxTileGroup getTileGroup() {
		return this.tileGroup;
	}

	public int getStrategyValue() {
		return this.strategyValue;
	}


	public void setTileColour(QuaxTileColour colour) {
		assert colour != null;
		this.tileColour = colour;
	}

	public void setTileGroup(QuaxTileGroup tileGroup) {
		this.tileGroup = tileGroup;
	}

	public void setStrategyValue(int value) {
		this.strategyValue = value;
	}


	public abstract QuaxCoordinate getCoordinates();

	/*
	 * Shorthand boolean checks for brevity
	 */
	public boolean isFree() {
		return getTileColour() == QuaxTileColour.NONE;
	}
	public boolean isBlack() {
		return getTileColour() == QuaxTileColour.BLACK;
	}
	public boolean isWhite() {
		return getTileColour() == QuaxTileColour.WHITE;
	}
	public boolean isSameColour(QuaxTileColour c) {
		// TODO Any instance where NONE is used will return false, --> Added assertion
		// I'd suggest an assertion
		assert c == QuaxTileColour.BLACK || c == QuaxTileColour.WHITE;
		return getTileColour() == c;
	}
	
	public boolean isOpponentColour(QuaxTileColour c) {
		assert c != QuaxTileColour.NONE;
		return getTileColour() == c.flip();
	}

	/*
	 * Check if the tile is on
	 * 	 the bottom or left (if BLACK or WHITE)
	 *  and
	 *   the top or the right (if BLACK or WHITE)
	 */
	public abstract boolean onLow();
	public abstract boolean onHigh();
}