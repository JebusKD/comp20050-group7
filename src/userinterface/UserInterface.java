package userinterface;

import bot.BotPlayer;
import model.QuaxBoard;
import types.QuaxCoordinate;
import types.QuaxTileColour;

public interface UserInterface {

	public void showWinLabel(QuaxTileColour c);

	public void hideTurnTracker();

	public void updateFromPreviousMove(QuaxBoard board);

	public void setTile(QuaxCoordinate q, QuaxTileColour c);
	
	public void setBoard(QuaxBoard b);
	
	public void setPieRuleVisibility(boolean visibility);
	
	public void setBotReference(BotPlayer b);
}
