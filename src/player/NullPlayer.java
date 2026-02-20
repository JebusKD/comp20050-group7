package player;

import javafx.stage.Stage;
import types.QuaxTileColour;

/* The NullPlayer exists to do nothing - simply
 	fill in as a placeholder for an actual player
 	and should never be used in production code-
 	exclusively within testing.
 */
public class NullPlayer extends QuaxPlayer {

	public NullPlayer(QuaxTileColour colour, Stage stage) {
		super(colour, stage);
	}
	
	@Override
	public void movePrompt() {
	}

}
