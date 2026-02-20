package player;

import controller.QuaxController;
import javafx.event.Event;
import javafx.scene.Scene;
import javafx.stage.Stage;
import types.QuaxCoordinate;
import types.QuaxCoordinateEvent;
import types.QuaxTileColour;

public abstract class QuaxPlayer {

	private QuaxTileColour colour;
	
	private Stage stage;
	
	public QuaxPlayer(QuaxTileColour colour, Stage stage) {
		this.setColour(colour);
		this.stage = stage;
	}
	
	public QuaxTileColour getColour() {
		return this.colour;
	}
	
	public void setColour(QuaxTileColour colour) {
		if (colour == QuaxTileColour.NONE)
			throw new IllegalArgumentException("Player cannot be assigned to no colour.");
		else this.colour = colour;
	}
	
	public abstract void movePrompt();
	
	protected void submitMove(QuaxCoordinate move) {
		QuaxCoordinateEvent submission = new QuaxCoordinateEvent(QuaxController.MOVE_SUBMITTED_EVENT, move);
		
		Event.fireEvent(stage, submission);
	}
}
