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
    private QuaxTileColour colour;
    private QuaxTileBorder border;
    private QuaxTileGroup group;
    private QuaxTileStrategyGroup strategyGroup;
    private int strategyValue;

    public QuaxTile() {
        this.colour = QuaxTileColour.NONE;
        this.border = QuaxTileBorder.NONE;
        this.group = null;
        this.strategyValue = 0;
    }
    public QuaxTile(QuaxTile t) {
        this.colour = t.colour;
        this.border = t.border;
        this.strategyValue = t.strategyValue;
        this.group = null; // Groups aren't copied, add in the board after the fact.
    }

    public QuaxTileColour getColour() {
        return this.colour;
    }

    public QuaxTileBorder getBorder() {
        return this.border;
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

    public void setBorder(QuaxTileBorder border) {this.border = border;}

    public void setGroup(QuaxTileGroup group) {
        this.group = group;
    }

    public void setStrategyGroup(QuaxTileStrategyGroup strategyGroup) {this.strategyGroup = strategyGroup;}

    public boolean isFree() {
        return this.colour == QuaxTileColour.NONE;
    }

    public void setStrategyValue(int value) {
        this.strategyValue = value;
    }

    public abstract QuaxCoordinate getCoordinates();
    public abstract boolean onLow();
    public abstract boolean onHigh();

}