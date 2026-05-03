package userinterface;

import model.QuaxBoard;
import player.BotPlayer;
import types.*;


public interface UserInterface {

	void showWinLabel(QuaxTileColour winnerColour);

	void hideTurnTracker();


	void updateFromPreviousMove(QuaxBoard board);

	void setTile(QuaxCoordinate tileCoord, QuaxTileColour colour);
	
	void setQuaxUIBoard(QuaxBoard board);
	
	void setPieRuleVisibility(boolean visibility);


	void showStrategy();

	void hideStrategy(QuaxBoard board);
	
	void setLinkedBot(BotPlayer bot);
	
	void setBotChosenMove(QuaxCoordinate botCoord);
}
