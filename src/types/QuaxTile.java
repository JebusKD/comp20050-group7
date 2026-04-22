package types;

public abstract class QuaxTile {

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