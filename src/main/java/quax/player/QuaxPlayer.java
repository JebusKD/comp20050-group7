package quax.player;

import quax.controller.QuaxController;
import javafx.event.Event;
import javafx.scene.Scene;
import javafx.stage.Stage;
import quax.types.QuaxCoordinate;
import quax.types.QuaxCoordinateEvent;
import quax.types.QuaxTileColour;

public abstract class QuaxPlayer {

    private QuaxTileColour colour;

    private Scene scene;

    public QuaxPlayer(QuaxTileColour colour, Scene scene) {
        this.setColour(colour);
        this.scene = scene;
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

        Event.fireEvent(scene, submission);
    }
}
