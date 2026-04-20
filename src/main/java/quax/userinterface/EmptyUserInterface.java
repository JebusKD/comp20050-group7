package quax.userinterface;

import quax.model.QuaxBoard;
import quax.player.BotPlayer;
import quax.types.QuaxCoordinate;
import quax.types.QuaxTileColour;

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

    public void showStrategy(BotPlayer bot) {

    }

    @Override
    public void hideStrategy(QuaxBoard board) {

    }
}
