package player;

import java.util.concurrent.Executor;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import controller.QuaxController;
import model.QuaxBoard;
import types.*;

public abstract class QuaxPlayer {

	private QuaxTileColour colour;
	private QuaxController controller;


	public QuaxPlayer() {
		this.colour = null;
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
	
	public QuaxTileColour getColour() {
		assertNotNull(colour);
		return colour;
	}
	
	public void setColour(QuaxTileColour colour) {
		assert colour == QuaxTileColour.BLACK || colour == QuaxTileColour.WHITE;
		//assertNotNull(colour);
        this.colour = colour;
	}
	
	public abstract void movePrompt(QuaxBoard b);
	
	protected void submitMove(QuaxCoordinate move) {
		controller.makeMove(move);
	}
}
