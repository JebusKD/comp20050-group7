package player;

import model.QuaxBoard;
import types.*;


public class BogoBot extends BotPlayer {

	public BogoBot() {
		super();
	}

	//@Override
	protected QuaxCoordinate computeMove(QuaxBoard b) {

        setUpStrategy(b);
		return decideMove(b);
	}
}
