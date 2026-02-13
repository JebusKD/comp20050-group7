package userinterface;

import javafx.scene.layout.GridPane;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import types.QuaxTileColour;

public class QuaxUserInterface {

	private Stage stage;
	
	private OctagonTile[][] octagonGridCells;
	private RhombusTile[][] rhombusGridCells;
	
	private GridPane octagonGrid;
	private GridPane rhombusGrid;
	
	public QuaxUserInterface(Stage stage) {
		this.stage = stage;
		
		initialiseOctagonGrid();
		initialiseRhombusGrid();
	}
	
	private void initialiseOctagonGrid() {
		octagonGridCells = new OctagonTile[11][11];
		octagonGrid = new GridPane();
		for (int i = 0; i < 11; i++) {
			for (int j = 0; j < 11; j++) {
				OctagonTile newTile = new OctagonTile();
				octagonGridCells[i][j] = newTile;
				octagonGrid.add(newTile, i, j);
			}
		}
	}
	
	private void initialiseRhombusGrid() {
		rhombusGridCells = new RhombusTile[11][11];
		rhombusGrid = new GridPane();
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				RhombusTile newTile = new RhombusTile();
				rhombusGridCells[i][j] = newTile;
				rhombusGrid.add(newTile, i, j);
			}
		}
	}
	
	private static interface Tile {
		public void setColour(QuaxTileColour colour);
	}
	
	private static class OctagonTile extends Polygon implements Tile {
		
	}
	
	private static class RhombusTile extends Rectangle implements Tile {
		
	}
	
}
