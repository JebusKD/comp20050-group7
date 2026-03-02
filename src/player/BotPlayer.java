package player;

import javafx.stage.Stage;
import model.QuaxBoard;
import types.QuaxCoordinate;
import types.QuaxTileColour;

public abstract class BotPlayer extends QuaxPlayer {

	public BotPlayer(QuaxTileColour colour, Stage stage) {
		super(colour, stage);
	}
	
	protected abstract QuaxCoordinate computeMove(QuaxBoard b);
	
	@Override
	public void movePrompt(QuaxBoard b) {
		Thread t = new Thread(() -> {
			submitMove(computeMove(b));
		});
		t.start();
	}
	
}
