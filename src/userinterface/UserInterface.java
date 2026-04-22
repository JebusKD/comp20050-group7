package userinterface;

import model.QuaxBoard;
import player.BotPlayer;
import types.*;

public interface UserInterface {

	void showWinLabel(QuaxTileColour c);

	void hideTurnTracker();

	void updateFromPreviousMove(QuaxBoard board);

	void setTile(QuaxCoordinate q, QuaxTileColour c);
	
	void setBoard(QuaxBoard b);
	
	void setPieRuleVisibility(boolean visibility);

	void showStrategy(BotPlayer bot);

	void hideStrategy(QuaxBoard board);
}
