package player;

import model.QuaxBoard;
import types.QuaxCoordinate;

public class BogoBot extends BotPlayer {

	public BogoBot() {
		super();
	}

	@Override
	protected QuaxCoordinate computeMove(QuaxBoard board) {
        setAll(board, 1);
        return decideMove(board);
    }
}
