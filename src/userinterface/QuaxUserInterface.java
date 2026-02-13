package userinterface;

import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import types.QuaxTileColour;

public class QuaxUserInterface {

	private Stage stage;
	
	private OctagonTile[][] octagonGrid;
	private RhombusTile[][] rhombusGrid;
	
	public QuaxUserInterface(Stage stage) {
		this.stage = stage;
	}
	
	private static interface Tile {
		public void setColour(QuaxTileColour colour);
	}
	
	private static class OctagonTile extends Polygon implements Tile {
		
	}
	
	private static class RhombusTile extends Rectangle implements Tile {
		
	}
	
}
