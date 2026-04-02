package player;

import java.util.concurrent.Executor;

import controller.QuaxController;
import model.QuaxBoard;
import types.QuaxCoordinate;
import types.QuaxTileColour;

public abstract class QuaxPlayer {

	private QuaxTileColour colour;
	private QuaxController controller;
	
	public QuaxPlayer(QuaxTileColour colour) {
		this.setColour(colour);
	}
	
	public void setController(QuaxController controller) {
		this.controller = controller;
	}
	
	protected Executor getExecutor() {
		return controller.getExecutor();
	}
	
	public QuaxTileColour getColour() {
		return this.colour;
	}
	
	public void setColour(QuaxTileColour colour) {
		if (colour == QuaxTileColour.NONE) {
            throw new IllegalArgumentException("Player cannot be assigned to no colour.");
        }
		else {
            this.colour = colour;
        }
	}
	
	public abstract void movePrompt(QuaxBoard b);
	
	protected void submitMove(QuaxCoordinate move) {
		controller.makeMove(move);
	}
}
