package userinterface;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import types.QuaxTileColour;

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
	private StackPane sideBar;

	private GridPane window;
	private Scene scene;
	
	private double sceneWidth;
	private double sceneHeight;

	public QuaxUserInterface(Stage stage) {
		this.stage = stage;

		initialiseOctagonGrid();
		initialiseRhombusGrid();
		
		initialiseWindow();
		
		initialiseStylesheets();
		
		stage.setScene(scene);
	}
	
	private void initialiseStylesheets() {
		scene.getStylesheets().add(getClass().getResource("/userinterface/stylesheets/tile-styling.css").toExternalForm());
	}

	private void initialiseWindow() {
		this.window = new GridPane();

		// TODO debug, remove
		window.setGridLinesVisible(true);
		
		this.board = new StackPane(octagonGrid, rhombusGrid);
		this.window.add(this.board, 0, 1);

		this.topBar = new StackPane();
		topBar.getChildren().add(new Rectangle(50, 50));
		this.bottomBar = new StackPane();
		bottomBar.getChildren().add(new Rectangle(50, 50));
		this.sideBar = new StackPane();
		sideBar.getChildren().add(new Rectangle(50, 680));
	
		this.window.add(this.topBar, 0, 0);
		this.window.add(this.bottomBar, 0, 2);
		this.window.add(this.sideBar, 1, 0, 1, 3);

		this.sceneWidth = 720;
		this.sceneHeight = 480;
		
		this.scene = new Scene(this.window, sceneWidth, sceneHeight);
		
		this.scene.widthProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number oldVal, Number newVal) {
				setSceneWidth((double)newVal);
			}
			
		});
		
		this.scene.heightProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number oldVal, Number newVal) {
				setSceneHeight((double)newVal);
			}
			
		});
		
		recalculateUIScale();
	}

	private void initialiseOctagonGrid() {
		octagonGridCells = new OctagonTile[11][11];
		octagonGrid = new GridPane();
		octagonGrid.setAlignment(Pos.CENTER);
		octagonGrid.setVgap(OCTAGON_GRID_GAP);
		octagonGrid.setHgap(OCTAGON_GRID_GAP);
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
		rhombusGrid.setAlignment(Pos.CENTER);
		rhombusGrid.setVgap(RHOMBUS_GRID_GAP);
		rhombusGrid.setHgap(RHOMBUS_GRID_GAP);
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				RhombusTile newTile = new RhombusTile();
				rhombusGridCells[i][j] = newTile;
				rhombusGrid.add(newTile, i, j);
			}
		}
	}
	
	private void recalculateUIScale() {/*
		double min = Math.min(sceneWidth, sceneHeight);
		
		double scaleRatio = min / (11 * OCTAGON_WIDTH);
		
		board.setScaleX(scaleRatio);
		board.setScaleY(scaleRatio);
*/
	}
	
	private void setSceneWidth(double value) {
		this.sceneWidth = value;
		recalculateUIScale();
	}
	
	private void setSceneHeight(double value) {
		this.sceneHeight = value;
		recalculateUIScale();
	}

	private static double calculateRhombusGridGap(double oct_gap, double oct_width) {
		return (oct_gap) + (oct_width - OctagonBase.sideLength(oct_width));
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
			this.getStyleClass().add("tiletype-octagon");
			this.setColour(QuaxTileColour.NONE);
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

		public RhombusTile() {
			super();
			this.getStyleClass().add("tiletype-rhombus");
			this.setColour(QuaxTileColour.NONE);
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
