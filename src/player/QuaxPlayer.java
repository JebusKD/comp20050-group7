package player;

import types.QuaxTileColour;

public abstract class QuaxPlayer {

	private QuaxTileColour colour;
	
	public QuaxPlayer(QuaxTileColour colour) {
		this.setColour(colour);
	}
	
	public QuaxTileColour getColour() {
		return this.colour;
	}
	
	public void setColour(QuaxTileColour colour) {
		if (colour == QuaxTileColour.NONE)
			throw new IllegalArgumentException("Player cannot be assigned to no colour.");
		else this.colour = colour;
	}
}
