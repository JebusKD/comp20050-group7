package quax.player;

import javafx.stage.Stage;
import quax.model.QuaxBoard;
import quax.types.QuaxCoordinate;
import quax.types.QuaxTileColour;

public class BogoBot extends BotPlayer {

	public BogoBot() {
		super();
	}

	@Override
	protected QuaxCoordinate computeMove(QuaxBoard b) {
		//setAll(b, 1);
        setUpStrategy(b);
		return decideMove(b);
	}
	
}
