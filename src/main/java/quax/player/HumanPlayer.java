package quax.player;

import quax.controller.QuaxController;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.stage.Stage;
import quax.model.QuaxBoard;
import quax.types.Octagon;
import quax.types.QuaxCoordinateEvent;
import quax.types.QuaxTileColour;

public class HumanPlayer extends QuaxPlayer {

    public HumanPlayer() {
        super();
    }

    @Override
    public void movePrompt(QuaxBoard b) {
    }


}
