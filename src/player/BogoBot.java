package player;

import javafx.stage.Stage;
import model.QuaxBoard;
import types.QuaxCoordinate;
import types.QuaxTileColour;

public class BogoBot extends BotPlayer {

	public BogoBot() {
		super();
	}

	@Override
	protected QuaxCoordinate computeMove(QuaxBoard b) {

        setUpStrategy(b);
		return decideMove(b);
	}
	
}
