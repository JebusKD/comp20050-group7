package types;

public abstract class QuaxTile {

	private QuaxTileColour tileColour;
	private QuaxTileGroup group;
	private int strategyValue;


	public QuaxTile() {
		this.tileColour = QuaxTileColour.NONE;
		this.group = null;
		this.strategyValue = 0;
	}

	public QuaxTile(QuaxTile t) {
		this.tileColour = t.tileColour;
		this.strategyValue = t.strategyValue;
		this.group = null; // Don't copy tile group, added in the board after object is constructed
	}


	public QuaxTileColour getTileColour() {
		return this.tileColour;
	}
	
	public QuaxTileGroup getGroup() {
		return this.group;
	}
	
	public int getStrategyValue() {
		return this.strategyValue;
	}
	
	public void setTileColour(QuaxTileColour tileColour) {
		this.tileColour = tileColour;
	}
	
	public void setGroup(QuaxTileGroup group) {
		this.group = group;
	}
	
	public void setStrategyValue(int value) {
		this.strategyValue = value;
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
