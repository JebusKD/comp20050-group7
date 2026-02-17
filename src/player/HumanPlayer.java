package player;

import java.util.Scanner;

import javafx.scene.Scene;
import javafx.stage.Stage;
import model.QuaxBoard;
import types.Octagon;
import types.QuaxTileColour;

public class HumanPlayer extends QuaxPlayer {
	
	private String name;
	
	public HumanPlayer(String name, QuaxTileColour colour, Scene scene) {
		super(colour, scene);
		this.name = name;
	}
	
	@Override
	public void movePrompt() {
		System.out.println(name + "'s move.");
	}
}
