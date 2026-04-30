package quax.player;

import quax.controller.QuaxController;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.scene.Scene;
import javafx.stage.Stage;
import quax.model.QuaxBoard;
import quax.types.QuaxCoordinate;
import quax.types.QuaxCoordinateEvent;
import quax.types.QuaxTileColour;
import java.util.concurrent.Executor;

public abstract class QuaxPlayer {

    private QuaxTileColour playerColour;
    private QuaxController playerController;


    public QuaxPlayer() {
        this.playerColour = null;
        this.playerController = null;
    }


    public void setPlayerController(QuaxController controller) {
        assert controller != null;
        this.playerController = controller;
    }

    protected Executor getExecutor() {
        assert playerController != null;
        return playerController.getQuaxExecutor();
    }

    private Executor getSubmitter() {
        assert playerController != null;
        return playerController.getQuaxMoveSubmitter();
    }


    public QuaxTileColour getPlayerColour() {
        assert this.playerColour == QuaxTileColour.BLACK || this.playerColour == QuaxTileColour.WHITE;
        return this.playerColour;
    }

    public void setPlayerColour(QuaxTileColour colour) {
        assert colour == QuaxTileColour.BLACK || colour == QuaxTileColour.WHITE;
        this.playerColour = colour;
    }


    public abstract void movePrompt(QuaxBoard b);

    protected void submitMove(QuaxCoordinate move) {
        getSubmitter().execute(new Runnable() {
            @Override
            public void run() {
                playerController.tryMove(move);
            }
        });
    }
}