package userinterface;

import model.QuaxBoard;
import types.QuaxCoordinate;
import types.QuaxTileColour;

public interface UserInterface {

	void showWinLabel(QuaxTileColour c);

	void hideTurnTracker();

	void updateFromPreviousMove(QuaxBoard board);

	void setTile(QuaxCoordinate q, QuaxTileColour c);
	
	void setBoard(QuaxBoard b);
	
	void setPieRuleVisibility(boolean visibility);
}
