package player;

import javafx.stage.Stage;
import model.QuaxBoard;
import types.QuaxCoordinate;
import types.QuaxTileColour;

public class BogoBot extends BotPlayer {

	public BogoBot(QuaxTileColour colour, Stage stage) {
		super(colour, stage);
	}

	@Override
	protected QuaxCoordinate computeMove(QuaxBoard b) {
        setAll(b, 1);
        return decideMove(b);
    }
}
