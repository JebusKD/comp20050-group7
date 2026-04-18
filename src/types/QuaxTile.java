package types;

public abstract class QuaxTile {

	private QuaxTileColour colour;
	private QuaxTileGroup group;
	private int strategyValue;


	public QuaxTile() {
		this.colour = QuaxTileColour.NONE;
		this.group = null;
		this.strategyValue = 0;
	}

    // TODO - Keep/Remove Comment?
	public QuaxTile(QuaxTile t) {
		this.colour = t.colour;
		this.strategyValue = t.strategyValue;
		this.group = null; // Groups aren't copied, add in the board after the fact.
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
	
	public boolean isFree() {
		return this.colour == QuaxTileColour.NONE;
	}
	
	public void setStrategyValue(int value) {
		this.strategyValue = value;
	}

	public abstract QuaxCoordinate getCoordinates();
	// TODO - Rename methods?
	public abstract boolean onLow();
	public abstract boolean onHigh();
}
