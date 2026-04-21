package types;

public abstract class QuaxTile {

	private QuaxTileColour colour;
	private QuaxTileGroup group;
	private int strategyValue;


	public QuaxTile() {
		this.colour = QuaxTileColour.NONE;
		this.strategyValue = 0;
		this.group = null;
	}

	public QuaxTile(QuaxTile t) {
		this.colour = t.colour;
		this.strategyValue = t.strategyValue;
		this.group = null; // Don't copy tile group, added in the board after object is constructed
	}
	
	public QuaxTileColour getColour() {
		return this.colour;
	}
	
	public QuaxTileGroup getGroup() {
		return this.group;
	}
	
	public int getStrategyValue() {
		return this.strategyValue;
	}
	
	public void setColour(QuaxTileColour colour) {
		this.colour = colour;
	}
	
	public void setGroup(QuaxTileGroup group) {
		this.group = group;
	}
	
	public void setStrategyValue(int value) {
		this.strategyValue = value;
	}

	public abstract QuaxCoordinate getCoordinates();

	public boolean isFree() {
		return getColour() == QuaxTileColour.NONE;
	}
	protected boolean isBlack() {
		return getColour() == QuaxTileColour.BLACK;
	}
	protected boolean isWhite() {
		return getColour() == QuaxTileColour.WHITE;
	}

	public abstract boolean onLow();
	public abstract boolean onHigh();
	public abstract boolean onLeft();
	public abstract boolean onRight();
}
