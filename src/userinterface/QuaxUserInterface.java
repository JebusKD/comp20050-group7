package userinterface;

import javafx.collections.ObservableList;
import javafx.css.Styleable;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
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
import javafx.stage.Stage;
import controller.QuaxController;
import model.QuaxBoard;
import types.QuaxTileColour;
import types.ButtonClickEvent;
import types.QuaxCoordinate;
import types.QuaxCoordinateEvent;
import types.QuaxTile;
import static model.QuaxBoard.MAX_OCTAGONS;
import static model.QuaxBoard.MAX_RHOMBUSES;

public class QuaxUserInterface implements UserInterface {

	private static final double OCTAGON_WIDTH = 40;
	private static final double OCTAGON_GRID_GAP = 1;

	private static final String[] STYLESHEETS = new String[] {
		"/userinterface/stylesheets/tile-styling.css",
		"/userinterface/stylesheets/board-styling.css",
		"/userinterface/stylesheets/ui-styling.css",
		"/userinterface/stylesheets/button-styling.css"
	};
	
	private Stage stage;
	
	private UserInterfaceBoard board;
	private PlayerTurnIndicator turnIndicator;
	
	private Label winLabel;

	private Scene scene;
	
	private Button pieRuleButton;

    public QuaxUserInterface(Stage stage) {
		this.stage = stage;
		this.board = new UserInterfaceBoard();
		
		initialiseWindow();
		initialiseStylesheets();
		
		setupStage();
	}
	
	private void setupStage() {
		stage.setScene(scene);
		stage.setMaximized(true);
		stage.show();
	}
	
	private void initialiseStylesheets() {
		ObservableList<String> sheets = scene.getStylesheets();
		for (String stylesheet : STYLESHEETS) {
			sheets.add(getClass().getResource(stylesheet).toExternalForm());
		}
	}

	private void initialiseWindow() {
        VBox sideBar;
        sideBar = initialiseButtons();
        this.turnIndicator = new PlayerTurnIndicator();
        
        this.winLabel = new Label("_ wins");
        winLabel.setVisible(false);
        winLabel.getStyleClass().add("win-label");
        
        sideBar.getChildren().addAll(this.turnIndicator.getTurnTracker(), this.winLabel);
        sideBar.getStyleClass().add("vbox");
        
        GridPane outer = new GridPane();
        
        Label title = new Label("Quax");
        title.getStyleClass().add("custom-title");
		
        outer.add(title,0,0);
        outer.add(board.getBoard(),0,1);
        outer.add(sideBar,1,1);

        outer.setAlignment(Pos.CENTER);
		
		this.scene = new Scene(outer);
	}
	
	private VBox initialiseButtons(){
        VBox sideBar = new VBox(10);

        Button strat = new Button("Show Strategy");
        Button hideStrat = new Button("Hide Strategy");
        pieRuleButton = new Button("PieRule");

        pieRuleButton.setOnMouseClicked(event -> {
        	pieRuleButton.fireEvent(new ButtonClickEvent(ButtonClickEvent.PIE_RULE_CLICKED_EVENT));
        });
        
        setPieRuleVisibility(false);
        
        strat.getStyleClass().add("button3");
        hideStrat.getStyleClass().add("button3");
        pieRuleButton.getStyleClass().add("button3");

        sideBar.getChildren().addAll(strat,hideStrat,pieRuleButton);

        return sideBar;
    }
	
	@Override
	public void showWinLabel(QuaxTileColour c){
        winLabel.setText(c + " wins");
        winLabel.setVisible(true);       
    }
	
	@Override
	public void hideTurnTracker() {
		turnIndicator.getTurnTracker().setVisible(false);
	}
	
	@Override
	public void updateFromPreviousMove(QuaxBoard board) {
		QuaxCoordinate previousMove = board.previousMove();
		if (previousMove == null) {
            this.turnIndicator.setColour(QuaxTileColour.BLACK);
        }
		else {
        	QuaxTileColour colour = board.getTile(previousMove).getColour();
            this.setTile(previousMove, colour);
            this.turnIndicator.setColour(colour.flip());
            if (board.getMoveNumber() == 1) {
            	this.setPieRuleVisibility(true);
            } else {
            	this.setPieRuleVisibility(false);
            }
		}
	}
	
	@Override
	public void setTile(QuaxCoordinate q, QuaxTileColour c) {
		board.setTile(q, c);
	}
	
	@Override
	public void setBoard(QuaxBoard b) {
		board.setBoard(b);
		if (b.getMoveNumber() == 1) {
			setPieRuleVisibility(true);
		}
		else {
			setPieRuleVisibility(false);
		}
	}
	
	@Override
	public void setPieRuleVisibility(boolean value) {
		pieRuleButton.setDisable(!value);
        pieRuleButton.setVisible(value);
	}

	public Scene getScene() {
		return this.scene;
	}
	
	private static class UserInterfaceBoard {
		private OctagonTile[][] octagonGridCells;
		private RhombusTile[][] rhombusGridCells;

		private StackPane board;
		
		public UserInterfaceBoard() {
			this.board = new StackPane(
				createGradientBackground(),
				createBehindHourglass(),
				createHourglass(),
				createGridBackground(),
				createBoardCoordinates(),
				createGrid()
			);
		}
		
		public StackPane getBoard() {
			return this.board;
		}
		
		public void setBoard(QuaxBoard board) {
			for (QuaxTile tile : board) {
				setTile(tile.getCoordinates(), tile.getColour());
			}
		}
		
		public void setTile(QuaxCoordinate q, QuaxTileColour c) {
			if (q.isOctagonMove()) {
                octagonGridCells[q.x()][q.y()].setColour(c);
            }
			else {
                rhombusGridCells[q.x()][q.y()].setColour(c);
            }
		}

		private static Rectangle createGridBackground(){
	        double size = ((MAX_OCTAGONS - 1)*OCTAGON_WIDTH) + OctagonBase.calculateSideLength(OCTAGON_WIDTH) + (MAX_RHOMBUSES * OCTAGON_GRID_GAP);
	        
	        Rectangle background = new Rectangle(size, size);
	        background.setFill(Color.OLDLACE);
	        
	        return background;
	    }
		
		// TODO Remove "Magic number", add hourglass gap as a constant.
	    private static Polygon createHourglass(){
	        double distance = (5.7 * OCTAGON_WIDTH) + (4.7 * OCTAGON_GRID_GAP) + OCTAGON_WIDTH/4;
	        Polygon hourglass = new Polygon(-distance,distance,
	        						         distance,distance,
	        						        -distance,-distance,
	        						         distance,-distance);
	        hourglass.setFill(Color.BLACK);
	        return hourglass;
	    }
	    
	    // TODO Remove "Magic number", add hourglass gap as a constant.
	    private static Rectangle createBehindHourglass(){
	        double size = (OCTAGON_WIDTH * 11.4) + (9.4 * OCTAGON_GRID_GAP) + (2*OCTAGON_WIDTH/4);
	        Rectangle background = new Rectangle(size, size);
	        background.setFill(Color.WHITE);
	        
	        return background;
	    }

        // TODO - Keep Constant Gradient Background??
	    private static Rectangle createGradientBackground() {
	    	return createGradientBackground(OCTAGON_WIDTH*(MAX_OCTAGONS + 1)+(MAX_RHOMBUSES*OCTAGON_GRID_GAP));
	    }
		
	    private static Rectangle createGradientBackground(double size) {
	    	Stop[] stops = new Stop[]{
	                new Stop(0, Color.NAVY),
	                new Stop(1, Color.BLUEVIOLET),
	        };
			
			LinearGradient lgl = new LinearGradient(1,0,1,1,true, CycleMethod.NO_CYCLE,stops);
			Rectangle background = new Rectangle(size, size); //the multicoloured border around the board
	        background.setFill(lgl);
	        
	        return background;
	    }
	    
	    // TODO magic numbers galore, also loaded function, break into pieces?
	    private static GridPane createBoardCoordinates(){
			double paddingValue = OCTAGON_WIDTH / (MAX_OCTAGONS + 1);
			
	        GridPane coordGrid = new GridPane();
	        
	        coordGrid.setAlignment(Pos.CENTER);
	        
	        coordGrid.getColumnConstraints().add(new ColumnConstraints());
	        coordGrid.getColumnConstraints().add(new ColumnConstraints((MAX_OCTAGONS*OCTAGON_WIDTH)+(MAX_RHOMBUSES*OCTAGON_GRID_GAP)));
	        coordGrid.getColumnConstraints().add(new ColumnConstraints());
	        
	        coordGrid.getRowConstraints().add(new RowConstraints());
	        coordGrid.getRowConstraints().add(new RowConstraints((MAX_OCTAGONS*OCTAGON_WIDTH)+(MAX_RHOMBUSES*OCTAGON_GRID_GAP)));
	        coordGrid.getRowConstraints().add(new RowConstraints());
	        
	        GridPane topCoords = new GridPane();
	        GridPane bottomCoords = new GridPane();
	        
	        topCoords.setPadding(new Insets(0, 0, paddingValue, 0));
	    	bottomCoords.setPadding(new Insets(paddingValue, 0, 0, 0));
	        
	        topCoords.setHgap(OCTAGON_GRID_GAP);
	        topCoords.setAlignment(Pos.CENTER);
	        bottomCoords.setHgap(OCTAGON_GRID_GAP);
	        bottomCoords.setAlignment(Pos.CENTER);
	        
	        for(int i = 0; i < MAX_OCTAGONS; i++){
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

	        for(int j = 0 ; j < MAX_OCTAGONS; j++){
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
	    
		private StackPane createGrid() {
			StackPane gridStack = new StackPane(
					createOctagonGrid(),
					createRhombusGrid()
			);

	        gridStack.setMaxHeight(Region.USE_PREF_SIZE);
	        gridStack.setMaxWidth(Region.USE_PREF_SIZE);

	        return gridStack;
		}
		
		private GridPane createOctagonGrid() {
			octagonGridCells = new OctagonTile[MAX_OCTAGONS][MAX_OCTAGONS];
			GridPane octagonGrid = new GridPane();
			octagonGrid.setAlignment(Pos.TOP_LEFT);
			octagonGrid.setVgap(OCTAGON_GRID_GAP);
			octagonGrid.setHgap(OCTAGON_GRID_GAP);
			octagonGrid.setPickOnBounds(false);

			for (int i = 0; i < MAX_OCTAGONS ; i++) {
		         ColumnConstraints column = new ColumnConstraints(OCTAGON_WIDTH);
		         octagonGrid.getColumnConstraints().add(column);
		     }
			
			for (int i = 0; i < MAX_OCTAGONS ; i++) {
		         RowConstraints row = new RowConstraints(OCTAGON_WIDTH);
		         octagonGrid.getRowConstraints().add(row);
		     }
			
			for (int i = 0; i < MAX_OCTAGONS ; i++) {
				for (int j = 0; j < MAX_OCTAGONS ; j++) {
					OctagonTile newTile = new OctagonTile(new QuaxCoordinate(i, j, true));
					newTile.setId("octagon" + i + "-" + j);
					octagonGridCells[i][j] = newTile;
					octagonGrid.add(newTile, i, j);
				}
			}
			return octagonGrid;
		}

		private GridPane createRhombusGrid() {
			rhombusGridCells = new RhombusTile[MAX_RHOMBUSES][MAX_RHOMBUSES];
			GridPane rhombusGrid = new GridPane();
			rhombusGrid.setAlignment(Pos.TOP_LEFT);
			rhombusGrid.setVgap(OCTAGON_GRID_GAP);
			rhombusGrid.setHgap(OCTAGON_GRID_GAP);
			rhombusGrid.setPickOnBounds(false);
			
			double rhombusGridGap = calculateRhombusGridGap();
			
			rhombusGrid.setPadding(new Insets(rhombusGridGap, 0, 0, rhombusGridGap));	
			
			for (int i = 0; i < MAX_RHOMBUSES; i++) {
		         ColumnConstraints column = new ColumnConstraints(OCTAGON_WIDTH);
		         rhombusGrid.getColumnConstraints().add(column);
		     }
			
			for (int i = 0; i < MAX_RHOMBUSES; i++) {
		         RowConstraints row = new RowConstraints(OCTAGON_WIDTH);
		         row.setValignment(VPos.TOP);
		         rhombusGrid.getRowConstraints().add(row);
		     }
			
			for (int i = 0; i < MAX_RHOMBUSES; i++) {
				for (int j = 0; j < MAX_RHOMBUSES; j++) {
					RhombusTile newTile = new RhombusTile(new QuaxCoordinate(i, j, false));
					newTile.setId("rhombus" + i + "-" + j);
					rhombusGridCells[i][j] = newTile;
					rhombusGrid.add(newTile, i, j);
				}
			}
			return rhombusGrid;
		}
		
		private static double calculateRhombusGridGap() {
			double rhombusSideLength = OctagonBase.calculateSideLength(OCTAGON_WIDTH);
			double rhombusDiagonalHeight = (OCTAGON_WIDTH - rhombusSideLength) / 2;
			return rhombusSideLength + rhombusDiagonalHeight + (OCTAGON_GRID_GAP/2);
		}

		private interface Tile extends Styleable {
			default void setColour(QuaxTileColour colour) {
				this.getStyleClass().removeAll(QuaxTileColour.BLACK.tilecolourStyle(),
											   QuaxTileColour.WHITE.tilecolourStyle(),
											   QuaxTileColour.NONE.tilecolourStyle());
				this.getStyleClass().add(colour.tilecolourStyle());
	        }

            QuaxCoordinate getCoordinate();
		}
		
		private static class OctagonTile extends OctagonBase implements Tile {

			private QuaxCoordinate coordinate;

			public OctagonTile(QuaxCoordinate coordinate) {
				super();
				this.getStyleClass().add("tile");
				this.getStyleClass().add("tiletype-octagon");
				this.setColour(QuaxTileColour.NONE);
				this.coordinate = coordinate;
				
				this.setOnMouseClicked(new EventHandler<>() {
					@Override
					public void handle(MouseEvent arg0) {
						fireEvent(new QuaxCoordinateEvent(QuaxCoordinateEvent.TILE_CLICKED_EVENT, getCoordinate()));
					}
				});
			}
			
			@Override
			public QuaxCoordinate getCoordinate() {
				return this.coordinate;
			}
		}
		
		private static class RhombusTile extends RhombusBase implements Tile {
			private QuaxCoordinate coordinate;

			public RhombusTile(QuaxCoordinate coordinate) {
				super();
				this.getStyleClass().add("tile");
				this.getStyleClass().add("tiletype-rhombus");
				this.setColour(QuaxTileColour.NONE);
				this.coordinate = coordinate;
				
				this.setOnMouseClicked(new EventHandler<>() {
					@Override
					public void handle(MouseEvent arg0) {
						fireEvent(new QuaxCoordinateEvent(QuaxCoordinateEvent.TILE_CLICKED_EVENT, getCoordinate()));
					}
				});
			}
			
			@Override
			public QuaxCoordinate getCoordinate() {
				return this.coordinate;
			}
		}
	}

	private abstract static class OctagonBase extends Polygon {

		private static final double[] POINTS = generatePolygonPoints(OCTAGON_WIDTH);
		public static final double SIDELENGTH = calculateSideLength(OCTAGON_WIDTH);
		
		public static double calculateSideLength(double width) {
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


	private abstract static class RhombusBase extends Polygon {

		public RhombusBase() {
			this((OCTAGON_WIDTH - OctagonBase.SIDELENGTH) / 2);
		}
		
		public RhombusBase(double radius) {
			super(-radius, 0,
				   0, radius,
				   radius, 0,
				   0, -radius);
		}
	}
    
    private static class PlayerTurnIndicator {
    	
    	private static final double HBOX_SPACING = 5;
    	private static final double OCTAGON_OBJECT_WIDTH = 40;
    	
    	private OctagonTurnIndicator octagonIndicator;
    	private RhombusTurnIndicator rhombusIndicator;
    	private TurnText turnText;
    	private HBox turnTracker;
    	
    	public PlayerTurnIndicator() {
    		this.turnTracker = createTurnTracker();
    		this.setColour(QuaxTileColour.BLACK);
    	}
    	
    	public HBox getTurnTracker() {
    		return this.turnTracker;
    	}
    	
    	public void setColour(QuaxTileColour colour) {
    		this.octagonIndicator.setColour(colour);
    		this.rhombusIndicator.setColour(colour);
    		this.turnText.setColour(colour);
    	}
    	
    	private HBox createTurnTracker() {
    		HBox box = new HBox(HBOX_SPACING);
    		createComponents();
    		box.getStyleClass().add("hbox-custom");
    		box.getChildren().addAll(turnText, octagonIndicator, rhombusIndicator);
    		return box;
    	}
    	
    	private void createComponents() {
    		this.octagonIndicator = new OctagonTurnIndicator();
    		this.rhombusIndicator = new RhombusTurnIndicator();
    		this.turnText = new TurnText();
    	}

    	private interface TurnIndicatorShape extends Styleable {
    		default void setColour(QuaxTileColour colour) {
    			if (colour == QuaxTileColour.NONE) {
                    throw new IllegalArgumentException();
                }
    			else {
    				this.getStyleClass().removeAll(QuaxTileColour.BLACK.tilecolourStyle(),
							   					   QuaxTileColour.WHITE.tilecolourStyle());
    				this.getStyleClass().add(colour.tilecolourStyle());
    			}
            }
    	}
    	
    	private static class OctagonTurnIndicator extends OctagonBase implements TurnIndicatorShape {
            public OctagonTurnIndicator() {
            	this(OCTAGON_OBJECT_WIDTH);
            }
            
            public OctagonTurnIndicator(double width) {
                super(width);
                this.setId("Octagon-object"); // TODO Change this ID - also needs to be done in UI test
                this.getStyleClass().add("turn-indicator-shape");
                this.setColour(QuaxTileColour.BLACK);
            }
        }

        private static class RhombusTurnIndicator extends RhombusBase implements TurnIndicatorShape {
            public RhombusTurnIndicator() {
                super();
                this.setId("Rhombus-object"); // TODO Change this ID - also needs to be done in UI test
                this.getStyleClass().add("turn-indicator-shape");
            }
        }
        
        private static class TurnText extends Label {
        	public TurnText() {
        		super();
        		this.getStyleClass().add("turn-label");
        	}
        	
        	public void setColour(QuaxTileColour colour) {
        		switch (colour) {
        			case BLACK :
        				this.setText("BLACK to play");
        				break;
        			case WHITE :
        				this.setText("WHITE to play");
        				break;
        			default :
        				throw new IllegalArgumentException("Cannot be set to none.");
        		}
        	}
        }
    }
}
