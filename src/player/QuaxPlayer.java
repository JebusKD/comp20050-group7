package player;

import controller.QuaxController;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.QuaxBoard;
import types.*;
import java.util.concurrent.Executor;

public abstract class QuaxPlayer {

    private QuaxTileColour colour;
    private QuaxController controller;

    public QuaxPlayer() {
        this.colour = null;
        this.controller = null;
    }

    public void setController(QuaxController controller) {
        this.controller = controller;
    }

    protected Executor getExecutor() {
        if (controller == null) {
            throw new IllegalStateException("Player not initialised to a controller.");
        }
        else {
            return controller.getExecutor();
        }
    }

    public QuaxTileColour getColour() {
        if (this.colour == null) {
            throw new IllegalStateException("Player not initialised to a controller.");
        }
        else {
            return this.colour;
        }
    }

    public void setColour(QuaxTileColour colour) {
        if (colour == QuaxTileColour.NONE || colour == null) {
            throw new IllegalArgumentException("Invalid colour assigned to player " + colour);
        }
        else {
            this.colour = colour;
        }
    }

    public abstract void movePrompt(QuaxBoard b);

    protected void submitMove(QuaxCoordinate move) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				controller.makeMove(move);
			}
		});
	}
}
