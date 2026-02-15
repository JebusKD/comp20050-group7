package userinterface;

import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import types.QuaxTileColour;

public class QuaxUserInterface {

	private static final double OCTAGON_WIDTH = 50;

	private Stage stage;

	private OctagonTile[][] octagonGridCells;
	private RhombusTile[][] rhombusGridCells;

	private GridPane octagonGrid;
	private GridPane rhombusGrid;
	private StackPane board;

	// TODO these are temporary assignments for bottom and sidebar - change to appropriate types
	private StackPane topBar;
	private StackPane bottomBar;
	private StackPane sideBar;

	private GridPane window;
	private Scene scene;

	public QuaxUserInterface(Stage stage) {
		this.stage = stage;

		initialiseOctagonGrid();
		initialiseRhombusGrid();

		initialiseWindow();

		stage.setScene(scene);
	}

	private void initialiseWindow() {
		this.window = new GridPane();

		this.board = new StackPane(octagonGrid, rhombusGrid);
		this.window.add(this.board, 0, 1);

		this.topBar = new StackPane();
		this.bottomBar = new StackPane();
		this.sideBar = new StackPane();

		this.window.add(this.topBar, 0, 0);
		this.window.add(this.bottomBar, 0, 2);
		this.window.add(this.sideBar, 1, 0, 3, 1);

		this.scene = new Scene(this.window, 720, 480);
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

	private abstract static class OctagonBase extends Polygon {

		private static final double[] POINTS = generatePolygonPoints(OCTAGON_WIDTH);

		public static final double SIDELEN = sideLength(OCTAGON_WIDTH);

		public static double sideLength(double width) {
			return width / (1 + (2 / Math.sqrt(2)));
		}

		private static double[] generatePolygonPoints(double width) {
			double sideLength = width / (1 + (2 / Math.sqrt(2)));
			double halfSide = sideLength / 2;
			double radius = width / 2;

			return new double[] { -halfSide, radius,
								  halfSide, radius,
								  radius, halfSide,
								  radius, -halfSide,
								  halfSide, -radius,
								  -halfSide, -radius,
								  -radius, -halfSide,
								  -radius, halfSide };
		}

		public OctagonBase() {
			super(POINTS);
		}

		public OctagonBase(double width) {
			super(generatePolygonPoints(width));
		}
	}

	private static class OctagonTile extends OctagonBase implements Tile {

		private QuaxTileColour colour;

		public OctagonTile() {
			super();
			this.setColour(QuaxTileColour.NONE);
		}

		@Override
		public void setColour(QuaxTileColour colour) {
			this.colour = colour;
			switch (colour) {
			case QuaxTileColour.NONE :
				this.setFill(Color.GRAY);
				break;
			case QuaxTileColour.BLACK :
				this.setFill(Color.BLACK);
				break;
			case QuaxTileColour.WHITE :
				this.setFill(Color.WHITE);
				break;
			}
		}


	}

	private abstract static class RhombusBase extends Rectangle {

		public RhombusBase() {
			super(OctagonBase.SIDELEN, OctagonBase.SIDELEN);
			this.setRotate(45.0);
		}
	}

	private static class RhombusTile extends RhombusBase implements Tile {

		private QuaxTileColour colour;

		public RhombusTile() {
			super();
			this.setColour(QuaxTileColour.NONE);
		}

		@Override
		public void setColour(QuaxTileColour colour) {
			this.colour = colour;
			switch (colour) {
			case QuaxTileColour.NONE :
				this.setFill(Color.GRAY);
				break;
			case QuaxTileColour.BLACK :
				this.setFill(Color.BLACK);
				break;
			case QuaxTileColour.WHITE :
				this.setFill(Color.WHITE);
				break;
			}
		}
	}
}
