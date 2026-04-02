package player;

import model.QuaxBoard;
import types.QuaxTileColour;

public class HumanPlayer extends QuaxPlayer {

	private String name;
	
	public HumanPlayer(String name, QuaxTileColour colour) {
		super(colour);
		this.name = name;
	}
	
	@Override
	public void movePrompt(QuaxBoard b) {
		System.out.println(name + "'s move.");
	}
}
