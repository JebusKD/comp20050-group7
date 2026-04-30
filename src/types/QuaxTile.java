package types;


public abstract class QuaxTile {

	private QuaxTileColour tileColour;
	private QuaxTileGroup tileGroup;
	private QuaxTileStrategyGroup tileStrategyGroup; //TODO - Remove?

	private int strategyValue;

	protected final int xPosition;
	protected final int yPosition;

	public QuaxTile(int x, int y) {
		this.tileColour = QuaxTileColour.NONE;
		this.tileGroup = null;
		this.tileStrategyGroup = null;
		this.strategyValue = 0;
		this.xPosition = x;
		this.yPosition = y;
	}

	public QuaxTile(QuaxTile t) {
		this.tileColour = t.tileColour;
		this.tileGroup = null; // Don't copy tile group, added in the board after object is constructed
		//TODO - Initialise StratGroup too
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
	// TODO - Add getStrategyGroup?

	// TODO - Add assertions?
	public void setTileColour(QuaxTileColour colour) {
		this.tileColour = colour;
	}

	public void setTileGroup(QuaxTileGroup tileGroup) {
		this.tileGroup = tileGroup;
	}

	public void setStrategyValue(int value) {
		this.strategyValue = value;
	}

	public void setTileStrategyGroup(QuaxTileStrategyGroup stratGroup) {
		this.tileStrategyGroup = stratGroup;
	}


	public abstract QuaxCoordinate getCoordinates();

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
		return getTileColour() == c && c != QuaxTileColour.NONE;
	}

	public abstract boolean onLow();
	public abstract boolean onHigh();
}