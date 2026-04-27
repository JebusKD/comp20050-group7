package types;


public abstract class QuaxTile {

	private QuaxTileColour tileColour;
	private QuaxTileGroup tileGroup;
	private QuaxTileStrategyGroup tileStrategyGroup;

	private int strategyValue;


	public QuaxTile() {
		this.tileColour = QuaxTileColour.NONE;
		this.tileGroup = null;
		this.tileStrategyGroup = null;
		this.strategyValue = 0;
	}

	public QuaxTile(QuaxTile t) {
		this.tileColour = t.tileColour;
		this.tileGroup = null; // Don't copy tile group, added in the board after object is constructed
		//this.tileStrategyGroup = t.tileStrategyGroup;
		this.strategyValue = t.strategyValue;
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

	public void setTileColour(QuaxTileColour tileColour) {
		this.tileColour = tileColour;
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
	protected boolean isBlack() {
		return getTileColour() == QuaxTileColour.BLACK;
	}
	protected boolean isWhite() {
		return getTileColour() == QuaxTileColour.WHITE;
	}

	public abstract boolean onLow();
	public abstract boolean onHigh();
}