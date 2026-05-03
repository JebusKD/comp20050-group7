package player;

import java.util.concurrent.Executor;

import controller.QuaxController;
import model.QuaxBoard;
import types.*;


public abstract class QuaxPlayer {

    private QuaxTileColour playerColour;
    private QuaxController playerController;

    public QuaxPlayer() {
        this.playerColour = null;
        this.playerController = null;
    }


    public void setPlayerController(QuaxController controller) {
        if (controller == null) {
        	throw new IllegalArgumentException("Controller cannot be null.");
        }
        
        this.playerController = controller;
    }

    protected final Executor getExecutor() {
        if (playerController == null) {
        	throw new IllegalStateException("Player has yet to be assigned corresponding controller.");
        }
        
        return this.playerController.getQuaxExecutor();
    }
    
    private final Executor getSubmitter() {
    	if (playerController == null) {
        	throw new IllegalStateException("Player has yet to be assigned corresponding controller.");
        }
    	
        return this.playerController.getQuaxMoveSubmitter();
    }


    public QuaxTileColour getPlayerColour() {
        assert this.playerColour == QuaxTileColour.BLACK || this.playerColour == QuaxTileColour.WHITE;
        return this.playerColour;
    }

    public void setPlayerColour(QuaxTileColour colour) {
        if (colour == QuaxTileColour.NONE) {
        	throw new IllegalArgumentException("Player cannot be assigned to no colour.");
        }
        
        this.playerColour = colour;
    }

    public abstract void movePrompt(QuaxBoard board);

    protected final void submitMove(QuaxCoordinate move) {
    	if (move == null) {
    		throw new IllegalArgumentException("Move coordinate cannot be null.");
    	}
    	if (playerController.currentPlayer() != this) {
    		throw new IllegalStateException("Move cannot be submitted when not player's turn.");
    	}
    	
    	getSubmitter().execute(new Runnable() {
			@Override
			public void run() {
				playerController.attemptMove(move);
			}
		});
	}
}
