package player;

import types.QuaxTileColour;

public class HumanPlayer extends QuaxPlayer {
	
	private String name;
	
	public HumanPlayer(String name, QuaxTileColour colour) {
		super(colour);
		this.name = name;
	}
}
