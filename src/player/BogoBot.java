package player;

import model.QuaxBoard;
import types.QuaxCoordinate;

public class BogoBot extends BotPlayer {

	public BogoBot() {
		super();
	}

	@Override
	protected void computeMove() {
        setAll(getSubmissionBoard(), 1);
        while (!isInterrupted());
        decideMove();
    }
}
