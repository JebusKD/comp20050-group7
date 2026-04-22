package player;

import java.util.concurrent.Executor;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import controller.QuaxController;
import model.QuaxBoard;
import types.*;

public abstract class QuaxPlayer {

	private QuaxTileColour playerColour;
	private QuaxController controller;


	public QuaxPlayer() {
		this.playerColour = null;
		this.controller = null;
	}
	
	public void setController(QuaxController controller) {
		assertNotNull(controller);
		this.controller = controller;
	}
	
	protected Executor getExecutor() {
		assertNotNull(controller);
		return controller.getExecutor();
	}
	
	public QuaxTileColour getPlayerColour() {
		assertNotNull(playerColour);
		return playerColour;
	}
	
	public void setPlayerColour(QuaxTileColour colour) {
		assert colour == QuaxTileColour.BLACK || colour == QuaxTileColour.WHITE;
        this.playerColour = colour;
	}
	
	public abstract void movePrompt(QuaxBoard b);
	
	protected void submitMove(QuaxCoordinate move) {
		controller.makeMove(move);
	}
}
