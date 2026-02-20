package userinterface;

import java.util.List;

import controller.QuaxController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import model.QuaxBoard;
import types.QuaxTileColour;
import types.QuaxTileGroup;
import types.QuaxCoordinate;
import types.QuaxCoordinateEvent;
import types.QuaxTile;

public class QuaxUserInterface {

	private static final double OCTAGON_WIDTH = 60;
	
	private static final double OCTAGON_GRID_GAP = 1;
	private static final double RHOMBUS_GRID_GAP = calculateRhombusGridGap(OCTAGON_GRID_GAP, OCTAGON_WIDTH);

	private Stage stage;
	
	private OctagonTile[][] octagonGridCells;
	private RhombusTile[][] rhombusGridCells;

	private GridPane octagonGrid;
	private GridPane rhombusGrid;
	private StackPane board;

	// TODO these are temporary assignments for bottom and sidebar - change to appropriate types
	private StackPane topBar;
	private StackPane bottomBar;
	private VBox sideBar;

	private StackPane window;
	private GridPane regions;
	private Scene scene;
	
	private double sceneWidth;
	private double sceneHeight;


	public QuaxUserInterface(Stage stage) {
		this.stage = stage;

		initialiseOctagonGrid();
		initialiseRhombusGrid();
		
		initialiseWindow();
		
		initialiseEventHandlers();
		initialiseStylesheets();
		
		stage.setScene(scene);
		
		stage.show();
	}
	
	private void initialiseStylesheets() {
		scene.getStylesheets().add(getClass().getResource("/userinterface/stylesheets/tile-styling.css").toExternalForm());
		scene.getStylesheets().add(getClass().getResource("/userinterface/stylesheets/board-styling.css").toExternalForm());
	}

	private void initialiseWindow() {
		this.regions = new GridPane();
		this.window = new StackPane(regions);
		window.setAlignment(Pos.CENTER);
		regions.setAlignment(Pos.CENTER);

		// TODO debug, remove
		regions.setGridLinesVisible(true);
		
		Rectangle boardBackground = new Rectangle(13 * OCTAGON_WIDTH, 13 * OCTAGON_WIDTH);
		boardBackground.getStyleClass().add("board-background");
		
		this.board = new StackPane(boardBackground, octagonGrid, rhombusGrid);
		this.regions.add(this.board, 0, 1);

		this.topBar = new StackPane();
		topBar.getChildren().add(new Rectangle(50, 50));
		this.bottomBar = new StackPane();
		bottomBar.getChildren().add(new Rectangle(50, 50));
		
		this.sideBar = new VBox(10);
		sideBar.getChildren().add(new Button("New 2-Player Game"));
		sideBar.getChildren().add(new Button("New Game vs. Bot"));
		sideBar.getChildren().add(new Button("Show Strategy"));
		sideBar.getChildren().add(new Button("Hide Strategy"));
		
	
		this.regions.add(this.topBar, 0, 0);
		this.regions.add(this.bottomBar, 0, 2);
		this.regions.add(this.sideBar, 1, 0, 1, 3);

		this.sceneWidth = 720;
		this.sceneHeight = 480;
		
		this.scene = new Scene(this.window, sceneWidth, sceneHeight);

	}

	private void initialiseEventHandlers() {
		
	}
	
	private void initialiseOctagonGrid() {
		octagonGridCells = new OctagonTile[11][11];
		octagonGrid = new GridPane();
		octagonGrid.setAlignment(Pos.CENTER);
		octagonGrid.setVgap(OCTAGON_GRID_GAP);
		octagonGrid.setHgap(OCTAGON_GRID_GAP);
		octagonGrid.setPickOnBounds(false);
		for (int i = 0; i < 11; i++) {
			for (int j = 0; j < 11; j++) {
				OctagonTile newTile = new OctagonTile(new QuaxCoordinate(i, j, true));
				octagonGridCells[i][j] = newTile;
				octagonGrid.add(newTile, i, j);
			}
		}
	}

	private void initialiseRhombusGrid() {
		rhombusGridCells = new RhombusTile[11][11];
		rhombusGrid = new GridPane();
		rhombusGrid.setAlignment(Pos.CENTER);
		rhombusGrid.setVgap(RHOMBUS_GRID_GAP);
		rhombusGrid.setHgap(RHOMBUS_GRID_GAP);
		rhombusGrid.setPickOnBounds(false);
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				RhombusTile newTile = new RhombusTile(new QuaxCoordinate(i, j, false));
				rhombusGridCells[i][j] = newTile;
				rhombusGrid.add(newTile, i, j);
			}
		}
	}
	
	public void setBoard(QuaxBoard b) {
		
		for (int i = 0; i < 11; i++) {
			for (int j = 0; j < 11; j++) {
				OctagonTile o = octagonGridCells[i][j];
				o.setColour(b.getOctagon(i, j).getColour());
			}
		}
		
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				RhombusTile r = rhombusGridCells[i][j];
				r.setColour(b.getRhombus(i, j).getColour());
			}
		}
	}

	
	public void fetchPreviousMove(QuaxBoard b) {
		QuaxCoordinate previousMove = b.previousMove();
		if (previousMove != null) this.setTile(previousMove, b.getTile(previousMove).getColour());
	}
	
	public void setTile(QuaxCoordinate q, QuaxTileColour c) {
		if (q.isOctagonMove())
			octagonGridCells[q.x()][q.y()].setColour(c);
		else rhombusGridCells[q.x()][q.y()].setColour(c);
	}

	private static double calculateRhombusGridGap(double oct_gap, double oct_width) {
		return (oct_gap) + (oct_width - OctagonBase.sideLength(oct_width));
	}
	
	public Scene getScene() {
		return this.scene;
	}
	
	private static interface Tile {
		public void setColour(QuaxTileColour colour);
		public QuaxCoordinate getCoordinate();
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
		private QuaxCoordinate coordinate;

		public OctagonTile(QuaxCoordinate coordinate) {
			super();
			this.getStyleClass().add("tile");
			this.getStyleClass().add("tiletype-octagon");
			this.setColour(QuaxTileColour.NONE);
			this.coordinate = coordinate;
			
			this.setOnMouseClicked(new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent arg0) {
					fireEvent(new QuaxCoordinateEvent(QuaxController.TILE_CLICKED_EVENT, getCoordinate()));
				}
				
			});
		}
		
		@Override
		public QuaxCoordinate getCoordinate() {
			return this.coordinate;
		}

		@Override
		public void setColour(QuaxTileColour colour) {
			this.colour = colour;
			this.getStyleClass().remove("tilecolour-none");
			this.getStyleClass().remove("tilecolour-black");
			this.getStyleClass().remove("tilecolour-white");
			switch (colour) {
			case QuaxTileColour.NONE :
				this.getStyleClass().add("tilecolour-none");
				break;
			case QuaxTileColour.BLACK :
				this.getStyleClass().add("tilecolour-black");
				break;
			case QuaxTileColour.WHITE :
				this.getStyleClass().add("tilecolour-white");
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
		private QuaxCoordinate coordinate;

		public RhombusTile(QuaxCoordinate coordinate) {
			super();
			this.getStyleClass().add("tile");
			this.getStyleClass().add("tiletype-rhombus");
			this.setColour(QuaxTileColour.NONE);
			this.coordinate = coordinate;
			
			this.setOnMouseClicked(new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent arg0) {
					fireEvent(new QuaxCoordinateEvent(QuaxController.TILE_CLICKED_EVENT, getCoordinate()));
				}
				
			});
		}
		
		@Override
		public QuaxCoordinate getCoordinate() {
			return this.coordinate;
		}

		@Override
		public void setColour(QuaxTileColour colour) {
			this.getStyleClass().remove("tilecolour-none");
			this.getStyleClass().remove("tilecolour-black");
			this.getStyleClass().remove("tilecolour-white");
			switch (colour) {
			case QuaxTileColour.NONE :
				this.getStyleClass().add("tilecolour-none");
				break;
			case QuaxTileColour.BLACK :
				this.getStyleClass().add("tilecolour-black");
				break;
			case QuaxTileColour.WHITE :
				this.getStyleClass().add("tilecolour-white");
				break;
			}
		}
	}
}
