package player;

import model.QuaxBoard;
import types.QuaxTileColour;

public class HumanPlayer extends QuaxPlayer {

	public HumanPlayer(QuaxTileColour colour) {
		super(colour);
	}
	
	@Override
	public void movePrompt(QuaxBoard b) {
	}
}
