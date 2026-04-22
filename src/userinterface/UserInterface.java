package userinterface;

import model.QuaxBoard;
import player.BotPlayer;
import types.*;

public interface UserInterface {

	public void showWinLabel(QuaxTileColour c);

	public void hideTurnTracker();

	public void updateFromPreviousMove(QuaxBoard board);

	public void setTile(QuaxCoordinate q, QuaxTileColour c);
	
	public void setBoard(QuaxBoard b);
	
	public void setPieRuleVisibility(boolean visibility);

	public void showStrategy(BotPlayer bot);
	
	public void hideStrategy(QuaxBoard board);
}
