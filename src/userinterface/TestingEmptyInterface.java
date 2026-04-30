package userinterface;

import model.QuaxBoard;
import player.BotPlayer;
import types.*;


/* User Interface used in cases where JavaFX is not known to be running.
 * Disables all JavaFX-specific methods. */
public class TestingEmptyInterface implements UserInterface {

	@Override
	public void showWinLabel(QuaxTileColour c) {
	}

	@Override
	public void hideTurnTracker() {
	}


	@Override
	public void updateFromPreviousMove(QuaxBoard board) {
	}

	@Override
	public void setTile(QuaxCoordinate q, QuaxTileColour c) {
	}

	@Override
	public void setQuaxUIBoard(QuaxBoard b) {
	}

	@Override
	public void setPieRuleVisibility(boolean visibility) {
	}


	@Override
	public void showStrategy() {
    }

    @Override
    public void hideStrategy(QuaxBoard board) {
    }
    
    @Override
    public void setLinkedBot(BotPlayer bot) {
    }
    
    @Override
    public void setBotChosenMove(QuaxCoordinate c) {
    }
}
