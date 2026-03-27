package userinterface;

import java.util.List;

import controller.QuaxController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import model.QuaxBoard;
import types.QuaxTileColour;
import types.QuaxTileGroup;
import types.ButtonClickEvent;
import types.QuaxCoordinate;
import types.QuaxCoordinateEvent;
import types.QuaxTile;

public class QuaxUserInterface {

	private static final double OCTAGON_WIDTH = 40;
	
	private static final double OCTAGON_GRID_GAP = 1;
	private static final double RHOMBUS_GRID_GAP = calculateRhombusGridGap(OCTAGON_GRID_GAP, OCTAGON_WIDTH);

	private Stage stage;
	
	private OctagonTile[][] octagonGridCells;
	private RhombusTile[][] rhombusGridCells;

	private GridPane octagonGrid;
	private GridPane rhombusGrid;
	private StackPane board;
	
	private StackPane topBar;
	private StackPane bottomBar;
	private VBox sideBar;
	private HBox turns;
	
	private Label title;
	private StackPane stack;

	private StackPane window;
	private GridPane regions;
	private Scene scene;
	
	private double sceneWidth;
	private double sceneHeight;
	
	private Button pieRuleButton;
	
	private OctagonObject octagonObject;
    private RhombusObject rhombusObject;
    private Label labelTurn;

	public QuaxUserInterface(Stage stage) {
		this.stage = stage;

		initialiseOctagonGrid();
		initialiseRhombusGrid();
		
		initialiseWindow();
		
		initialiseStylesheets();
		
		stage.setScene(scene);
		
		stage.setMaximized(true);
		stage.show();
	}
	
	private void initialiseStylesheets() {
		scene.getStylesheets().add(getClass().getResource("/userinterface/stylesheets/tile-styling.css").toExternalForm());
		scene.getStylesheets().add(getClass().getResource("/userinterface/stylesheets/board-styling.css").toExternalForm());
		scene.getStylesheets().add(getClass().getResource("/userinterface/stylesheets/ui-styling.css").toExternalForm());
		scene.getStylesheets().add(getClass().getResource("/userinterface/stylesheets/button-styling.css").toExternalForm());
	
	}

	private void initialiseWindow() {
		StackPane gridHolder = new StackPane(octagonGrid,rhombusGrid);

        gridHolder.setMaxHeight(Region.USE_PREF_SIZE);
        gridHolder.setMaxWidth(Region.USE_PREF_SIZE);

        Rectangle gridBackground = createGridBackground(OCTAGON_WIDTH, OCTAGON_GRID_GAP);
       // gridBackground.getStyleClass().add("background-rectangle");
        gridBackground.setFill(Color.OLDLACE);

        double hourglassGap = OCTAGON_WIDTH/4;
        Polygon hourglassBorder = new Polygon(calculateHourglassPoints(OCTAGON_WIDTH,OCTAGON_GRID_GAP,hourglassGap));
        hourglassBorder.setFill(Color.BLACK);

        Rectangle behindHourglassBorder = createBehindHourglass(OCTAGON_WIDTH,OCTAGON_GRID_GAP,hourglassGap);
        behindHourglassBorder.setFill(Color.WHITE);
		BorderPane boardBackground = new BorderPane();
		
		Stop[] stops = new Stop[]{
                new Stop(0, Color.NAVY),
                new Stop(1, Color.BLUEVIOLET),
        };
		
		LinearGradient lgl = new LinearGradient(1,0,1,1,true, CycleMethod.NO_CYCLE,stops);
		Rectangle rectangle = new Rectangle(OCTAGON_WIDTH*12+(10*OCTAGON_GRID_GAP),OCTAGON_WIDTH*12+(10*OCTAGON_GRID_GAP)); //the multicoloured border around the board
        rectangle.setFill(lgl);
		
        GridPane boardWithCoords = initialiseCoordsImage();

        this.board = new StackPane(rectangle,behindHourglassBorder,hourglassBorder,gridBackground,boardWithCoords,gridHolder);
		
        this.sideBar = initialiseButtons();
        this.turns = initialisePlayerTurn();
        turns.getStyleClass().add("hbox-custom");
        sideBar.getChildren().add(turns);
        sideBar.getStyleClass().add("vbox");
        
        GridPane outer = new GridPane();
        
        this.title = new Label("Quax");
        title.getStyleClass().add("custom-title");
		
        outer.add(title,0,0);
        outer.add(board,0,1);
        outer.add(this.sideBar,1,1);

        outer.setAlignment(Pos.CENTER);
        
		this.sceneWidth = 720;
		this.sceneHeight = 480;
		
		this.scene = new Scene(outer, sceneWidth, sceneHeight);

	}
	
	private GridPane initialiseCoordsImage(){
		double paddingValue = OCTAGON_WIDTH / 12;
		
        GridPane coordGrid = new GridPane();
        
        coordGrid.setAlignment(Pos.CENTER);
        
        coordGrid.getColumnConstraints().add(new ColumnConstraints());
        coordGrid.getColumnConstraints().add(new ColumnConstraints((11*OCTAGON_WIDTH)+(10*OCTAGON_GRID_GAP)));
        coordGrid.getColumnConstraints().add(new ColumnConstraints());
        
        coordGrid.getRowConstraints().add(new RowConstraints());
        coordGrid.getRowConstraints().add(new RowConstraints((11*OCTAGON_WIDTH)+(10*OCTAGON_GRID_GAP)));
        coordGrid.getRowConstraints().add(new RowConstraints());
        
        GridPane topCoords = new GridPane();
        GridPane bottomCoords = new GridPane();
        
        topCoords.setPadding(new Insets(0, 0, paddingValue, 0));
    	bottomCoords.setPadding(new Insets(paddingValue, 0, 0, 0));
        
        topCoords.setHgap(OCTAGON_GRID_GAP);
        topCoords.setAlignment(Pos.CENTER);
        bottomCoords.setHgap(OCTAGON_GRID_GAP);
        bottomCoords.setAlignment(Pos.CENTER);
        
        for(int i = 0; i < 11; i++){
        	
        	topCoords.getColumnConstraints().add(new ColumnConstraints(OCTAGON_WIDTH));
        	bottomCoords.getColumnConstraints().add(new ColumnConstraints(OCTAGON_WIDTH));
        	
            Label letterCoordTop = new Label(String.valueOf((char) ('A' + i)));
            Label letterCoordBottom = new Label(String.valueOf((char) ('A' + i)));

        	StackPane topCoordPane = new StackPane(letterCoordTop);
        	StackPane bottomCoordPane = new StackPane(letterCoordBottom);
            
            letterCoordTop.setPrefWidth(Region.USE_COMPUTED_SIZE);
            letterCoordTop.setPrefHeight(Region.USE_COMPUTED_SIZE);
            letterCoordTop.getStyleClass().add("coordinate-letter-style");
            letterCoordTop.setAlignment(Pos.CENTER);

            letterCoordBottom.setPrefWidth(Region.USE_COMPUTED_SIZE);
            letterCoordBottom.setPrefHeight(Region.USE_COMPUTED_SIZE);
            letterCoordBottom.getStyleClass().add("coordinate-letter-style");
            letterCoordBottom.setAlignment(Pos.CENTER);
/*
            letterCoordTop.setPadding(new Insets(20,0,0,20));
            letterCoordBottom.setPadding(new Insets(5,5,0,5));
  */          
            topCoords.add(topCoordPane,i,0);
            bottomCoords.add(bottomCoordPane,i,0);
        }
        
        coordGrid.add(topCoords, 1, 0);
        coordGrid.add(bottomCoords, 1, 2);

        GridPane leftCoords = new GridPane();
        GridPane rightCoords = new GridPane();
        
        leftCoords.setPadding(new Insets(0, paddingValue, 0, 0));
    	rightCoords.setPadding(new Insets(0, 0, 0, paddingValue));
        
        leftCoords.setVgap(OCTAGON_GRID_GAP);
        leftCoords.setAlignment(Pos.CENTER);
        rightCoords.setVgap(OCTAGON_GRID_GAP);
        rightCoords.setAlignment(Pos.CENTER);

        for(int j =0;j < 11; j++){
        	leftCoords.getRowConstraints().add(new RowConstraints(OCTAGON_WIDTH));
        	rightCoords.getRowConstraints().add(new RowConstraints(OCTAGON_WIDTH));
        	
            Label numCoordLeft = new Label(String.valueOf(11 -j));
            Label numCoordRight = new Label(String.valueOf(11-j));
            numCoordLeft.getStyleClass().add("coordinate-number-style");
            numCoordLeft.setPrefHeight(Region.USE_COMPUTED_SIZE);
            numCoordLeft.setPrefWidth(Region.USE_COMPUTED_SIZE);
            //numCoordLeft.setPadding(new Insets(20,0,0,20));

            numCoordRight.getStyleClass().add("coordinate-number-style");
            numCoordLeft.setPrefHeight(Region.USE_COMPUTED_SIZE);
            numCoordLeft.setPrefWidth(Region.USE_COMPUTED_SIZE);
            //numCoordRight.setPadding(new Insets(5,0,0,5));
            
            StackPane leftCoordPane = new StackPane(numCoordLeft);
        	StackPane rightCoordPane = new StackPane(numCoordRight);

            leftCoords.add(leftCoordPane,0,j);
            rightCoords.add(rightCoordPane,0,j);
        }
        
        coordGrid.add(leftCoords, 0, 1);
        coordGrid.add(rightCoords, 2, 1);
        
        return coordGrid;
    }
	
	private void initialiseOctagonGrid() {
		octagonGridCells = new OctagonTile[11][11];
		octagonGrid = new GridPane();
		octagonGrid.setAlignment(Pos.TOP_LEFT);
		octagonGrid.setVgap(OCTAGON_GRID_GAP);
		octagonGrid.setHgap(OCTAGON_GRID_GAP);
		octagonGrid.setPickOnBounds(false);

		for (int i = 0; i < 11; i++) {
	         ColumnConstraints column = new ColumnConstraints(OCTAGON_WIDTH);
	         octagonGrid.getColumnConstraints().add(column);
	     }
		
		for (int i = 0; i < 11; i++) {
	         RowConstraints row = new RowConstraints(OCTAGON_WIDTH);
	         octagonGrid.getRowConstraints().add(row);
	     }
		
		for (int i = 0; i < 11; i++) {
			for (int j = 0; j < 11; j++) {
				OctagonTile newTile = new OctagonTile(new QuaxCoordinate(i, j, true));
				newTile.setId("octagon" + i + "-" + j);
				octagonGridCells[i][j] = newTile;
				octagonGrid.add(newTile, i, j);
			}
		}
	}

	private void initialiseRhombusGrid() {
		rhombusGridCells = new RhombusTile[11][11];
		rhombusGrid = new GridPane();
		rhombusGrid.setAlignment(Pos.TOP_LEFT);
		rhombusGrid.setVgap(OCTAGON_GRID_GAP);
		rhombusGrid.setHgap(OCTAGON_GRID_GAP);
		rhombusGrid.setPickOnBounds(false);
		
		rhombusGrid.setPadding(new Insets(RHOMBUS_GRID_GAP, 0, 0, RHOMBUS_GRID_GAP));	
		
		for (int i = 0; i < 10; i++) {
	         ColumnConstraints column = new ColumnConstraints(OCTAGON_WIDTH);
	         rhombusGrid.getColumnConstraints().add(column);
	     }
		
		for (int i = 0; i < 10; i++) {
	         RowConstraints row = new RowConstraints(OCTAGON_WIDTH);
	         row.setValignment(VPos.TOP);
	         rhombusGrid.getRowConstraints().add(row);
	     }
		
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				RhombusTile newTile = new RhombusTile(new QuaxCoordinate(i, j, false));
				newTile.setId("rhombus" + i + "-" + j);
				rhombusGridCells[i][j] = newTile;
				rhombusGrid.add(newTile, i, j);
			}
		}
	}
	
	private VBox initialiseButtons(){
        VBox sideBar = new VBox(10);

        Button strat = new Button("Show Strategy");
        Button hideStrat = new Button("Hide Strategy");
        pieRuleButton = new Button("PieRule");

        pieRuleButton.setOnMouseClicked(event -> {
        	pieRuleButton.fireEvent(new ButtonClickEvent(QuaxController.PIE_RULE_CLICKED_EVENT));
        });
        
        strat.getStyleClass().add("button3");
        hideStrat.getStyleClass().add("button3");
        pieRuleButton.getStyleClass().add("button3");

        sideBar.getChildren().addAll(strat,hideStrat,pieRuleButton);

        return sideBar;
    }

    private HBox initialisePlayerTurn(){
       HBox playerTurn = new HBox(5);
       octagonObject = new OctagonObject(40);
       octagonObject.setId("Octagon-object");
       rhombusObject = new RhombusObject();
       rhombusObject.setId("Rhombus-object");
       if(octagonObject.getFill() == null){
           octagonObject.setFill(Color.BLACK);
       }
       if(rhombusObject.getFill() == null){
           rhombusObject.setFill(Color.BLACK);
       }

        labelTurn = new Label("BLACK to play");
        labelTurn.getStyleClass().add("turn-label");

        playerTurn.getChildren().addAll(labelTurn,octagonObject,rhombusObject);
        return playerTurn;
    }

    private void initialisePlayerTurnHelper(QuaxTileColour c,OctagonObject o,RhombusObject r,Label labelTurn){
        if(c == QuaxTileColour.WHITE){
           o.setColour(QuaxTileColour.BLACK);
           r.setColour(QuaxTileColour.BLACK);
           labelTurn.setText("BLACK to play");
       }
        else{
            o.setColour(QuaxTileColour.WHITE);
            r.setColour(QuaxTileColour.WHITE);
            labelTurn.setText("WHITE to play");
        }
        labelTurn.getStyleClass().add("turn-label");
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
	
	public static Rectangle createGridBackground(double octagonWidth,double octagonGridGap){
        double gridBackgroundSize = (10*octagonWidth) + OctagonBase.sideLength(octagonWidth) + (10 * octagonGridGap);

        return new Rectangle(gridBackgroundSize,gridBackgroundSize,gridBackgroundSize,gridBackgroundSize);
    }

    public static double[] calculateHourglassPoints(double oct_width,double oct_grid_gap,double gap){
        double distance = (5.7 * oct_width) + (4.7 * oct_grid_gap) + gap;
        return new double[]{-distance,distance,distance,distance,-distance,-distance,distance,-distance};
    }

    public static Rectangle createBehindHourglass(double octagonWidth,double octagonGridGap,double hourglassGap){
        double size = (octagonWidth * 11.4) + (9.4 * octagonGridGap) + (2*hourglassGap);
        return new Rectangle(size,size);
    }
	
	public void fetchPreviousMove(QuaxBoard b) {
		QuaxCoordinate previousMove = b.previousMove();
		if(previousMove == null){
            initialisePlayerTurnHelper(QuaxTileColour.BLACK,octagonObject,rhombusObject,labelTurn);
        }
        if (previousMove != null) {
            this.setTile(previousMove, b.getTile(previousMove).getColour());
            initialisePlayerTurnHelper(b.getTile(previousMove).getColour(),octagonObject,rhombusObject,labelTurn);

        }
	}
	
	public void setTile(QuaxCoordinate q, QuaxTileColour c) {
		if (q.isOctagonMove())
			octagonGridCells[q.x()][q.y()].setColour(c);
		else rhombusGridCells[q.x()][q.y()].setColour(c);
	}

	private static double calculateRhombusGridGap(double oct_gap, double oct_width) {
		double sidelen = OctagonBase.sideLength(oct_width);
		double diagonalHeight = (oct_width - sidelen) / 2;
		return sidelen + diagonalHeight + (oct_gap/2);
	}
	
	public Scene getScene() {
		return this.scene;
	}
	
	public void setPieRuleVisibility(boolean value) {
		pieRuleButton.setDisable(!value);
        pieRuleButton.setVisible(value);
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

	private abstract static class RhombusBase extends Polygon {

		public RhombusBase() {
			this((OCTAGON_WIDTH - OctagonBase.SIDELEN) / 2);
		}
		
		public RhombusBase(double radius) {
			super(-radius, 0,
				   0, radius,
				   radius, 0,
				   0, -radius);
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
	
	private static class OctagonObject extends OctagonBase {
        private QuaxTileColour colour;

        public OctagonObject(double width) {
            super(width);
            this.getStyleClass().add("tilecolour-black");
            this.setColour(QuaxTileColour.BLACK);
        }

        public void setColour(QuaxTileColour colour) {
            this.colour = colour;
            this.getStyleClass().remove("tilecolour-none");
            this.getStyleClass().remove("tilecolour-black");
            this.getStyleClass().remove("tilecolour-white");
            switch (colour) {
                case QuaxTileColour.NONE :
                    this.getStyleClass().addAll("tilecolour-none","object");
                    break;
                case QuaxTileColour.BLACK :
                    this.getStyleClass().addAll("tilecolour-black","object");
                    break;
                case QuaxTileColour.WHITE :
                    this.getStyleClass().addAll("tilecolour-white","object");
                    break;
            }
        }

    }

    private static class RhombusObject extends RhombusBase{
        private QuaxTileColour colour;

        public RhombusObject() {
            super();
            this.getStyleClass().add("tilecolour-black");
            this.setColour(QuaxTileColour.BLACK);
        }

        public void setColour(QuaxTileColour colour) {
            this.colour = colour;
            this.getStyleClass().remove("tilecolour-none");
            this.getStyleClass().remove("tilecolour-black");
            this.getStyleClass().remove("tilecolour-white");
            switch (colour) {
                case QuaxTileColour.NONE :
                    this.getStyleClass().addAll("tilecolour-none","object");
                    break;
                case QuaxTileColour.BLACK :
                    this.getStyleClass().addAll("tilecolour-black","object");
                    break;
                case QuaxTileColour.WHITE :
                    this.getStyleClass().addAll("tilecolour-white","object");
                    break;
            }
        }
    }
}
