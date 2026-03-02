package player;

import model.QuaxBoard;

public abstract class BotPlayer extends QuaxPlayer {

	protected abstract QuaxCoordinate computeMove(QuaxBoard b);
	
	@Override
	public abstract void movePrompt(QuaxBoard b) {
		submitMove(computeMove(b));
	}
	
}
