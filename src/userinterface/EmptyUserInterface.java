package userinterface;

import model.QuaxBoard;
import player.BotPlayer;
import types.QuaxCoordinate;
import types.QuaxTileColour;

/* User Interface used in cases where JavaFX is not known to be running.
 * Disables all JavaFX-specific methods. */
public class EmptyUserInterface implements UserInterface {

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
	public void setBoard(QuaxBoard b) {
	}

	@Override
	public void setPieRuleVisibility(boolean visibility) {
	}
	
	@Override
	public void showStrategy(BotPlayer bot) {
    }

    @Override
    public void hideStrategy(QuaxBoard board) {
    }

}
