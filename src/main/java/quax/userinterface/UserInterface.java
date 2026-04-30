package quax.userinterface;

import quax.model.QuaxBoard;
import quax.player.BotPlayer;
import quax.types.*;


public interface UserInterface {

    void showWinLabel(QuaxTileColour c);

    void hideTurnTracker();


    void updateFromPreviousMove(QuaxBoard board);

    void setTile(QuaxCoordinate q, QuaxTileColour c);

    void setQuaxUIBoard(QuaxBoard b);

    void setPieRuleVisibility(boolean visibility);

    void showStrategy();

    void hideStrategy(QuaxBoard board);

    void setLinkedBot(BotPlayer bot);

    void setBotChosenMove(QuaxCoordinate c);
}
