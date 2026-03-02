package player;

import controller.QuaxController;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.QuaxBoard;
import types.Octagon;
import types.QuaxCoordinateEvent;
import types.QuaxTileColour;

public class HumanPlayer extends QuaxPlayer {
	
	private String name;
	
	public HumanPlayer(String name, QuaxTileColour colour, Stage stage) {
		super(colour, stage);
		this.name = name;
	}
	
	@Override
	public void movePrompt(QuaxBoard b) {
		System.out.println(name + "'s move.");
		
		
	}
	
	
}
