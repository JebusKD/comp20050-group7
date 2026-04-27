package userinterface;

import javafx.collections.ObservableList;
import javafx.geometry.VPos;
import javafx.scene.layout.*;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.css.Styleable;

import model.QuaxBoard;
import player.BotPlayer;
import types.*;

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

    private Stage quaxUIStage;
    private Scene interfaceScene;
    private Label boardWinLabel;
    private Button pieRuleButton;
    private VBox strategyColourIndicator;

    private UserInterfaceBoard quaxUIBoard;
    private PlayerTurnIndicator turnIndicator;


    public QuaxUserInterface(Stage stage) {
        this.quaxUIStage = stage;
        this.quaxUIBoard = new UserInterfaceBoard();

        initialiseWindow();
        initialiseStylesheets();

        setupStage();
    }


    private void setupStage() {
        this.quaxUIStage.setScene(this.interfaceScene);
        this.quaxUIStage.setMaximized(true);
        this.quaxUIStage.show();
    }

    private void initialiseStylesheets() {
        ObservableList<String> sheets = this.interfaceScene.getStylesheets();
        for (String stylesheet : STYLESHEETS) {
            sheets.add(getClass().getResource(stylesheet).toExternalForm());
        }
    }


    // TODO - Move all window methods into new class?
    private void initialiseWindow() {

        VBox sideBar = initialiseButtons();

        this.turnIndicator = new PlayerTurnIndicator();

        initialiseWinLabel();
        initialiseStrategyColourCoding();

        sideBar.getChildren().addAll(this.turnIndicator.getTurnTracker(), this.boardWinLabel,this.strategyColourIndicator);
        sideBar.getStyleClass().add("vbox");

        GridPane outer = new GridPane();

        outer.add(createTitle(),0,0);
        outer.add(quaxUIBoard.getStackUIBoard(),0,1);
        outer.add(sideBar,1,1);

        outer.setAlignment(Pos.CENTER);

        this.interfaceScene = new Scene(outer);
    }

    private VBox initialiseButtons() {
        VBox sideBar = new VBox(10);

        Button strat = new Button("Show Strategy");

        strat.setOnMouseClicked(event -> {
            strat.fireEvent(new ButtonClickEvent(ButtonClickEvent.SHOW_STRATEGY_CLICKED_EVENT));
        });

        strat.setId("showStrat");

        Button hideStrat = new Button("Hide Strategy");
        hideStrat.setId("hideStrat");

        hideStrat.setOnMouseClicked(event -> {
            hideStrat.fireEvent(new ButtonClickEvent(ButtonClickEvent.HIDE_STRATEGY_CLICKED_EVENT));
        });

        pieRuleButton = new Button("PieRule");

        pieRuleButton.setOnMouseClicked(event -> {
            pieRuleButton.fireEvent(new ButtonClickEvent(ButtonClickEvent.PIE_RULE_CLICKED_EVENT));
        });

        pieRuleButton.setId("PieRule");
        setPieRuleVisibility(false);

        strat.getStyleClass().add("button3");
        hideStrat.getStyleClass().add("button3");
        pieRuleButton.getStyleClass().add("button3");

        sideBar.getChildren().addAll(strat,hideStrat,pieRuleButton);

        return sideBar;
    }

    private void initialiseWinLabel() {
        this.boardWinLabel = new Label("_ wins");
        this.boardWinLabel.setVisible(false);
        this.boardWinLabel.getStyleClass().add("win-label");
    }

    private void initialiseStrategyColourCoding() {
        Label stratLabel = new Label("Strategy Value - SV");
        stratLabel.getStyleClass().add("stratLabel");

        Label stratTwo = new Label("SV2 - Low priority surrounding tile ");
        stratTwo.getStyleClass().add("stratTwo");

        Label stratThree = new Label("SV3 - Block opponent ");
        stratThree.getStyleClass().add("stratThree");

        Label stratFour = new Label("SV4 - Progress self");
        stratFour.getStyleClass().add("stratFour");

        Label stratFive = new Label("SV5 - Opponent has winning move");
        stratFive.getStyleClass().add("stratFive");

        Label stratSix = new Label("SV6 - Winning move for self");
        stratSix.getStyleClass().add("stratSix");

        //VBox stratColourIndicator = new VBox(10);
        this.strategyColourIndicator = new VBox(10);
        this.strategyColourIndicator.getChildren().addAll(stratLabel,stratTwo,stratThree,stratFour,stratFive,stratSix);
        this.strategyColourIndicator.getStyleClass().add("vbox");
        this.strategyColourIndicator.setVisible(false);
        this.strategyColourIndicator.setId("ColourIndicator");
        //return stratColourIndicator;
    }

    private Label createTitle() {
        Label title = new Label("Quax (Human V Bot)");
        title.getStyleClass().add("custom-title");
        title.setId("Title");
        return title;
    }


    public void showWinLabel(QuaxTileColour c) {
        boardWinLabel.setText(c + " wins");
        boardWinLabel.setVisible(true);
    }

    public void hideTurnTracker() {
        turnIndicator.getTurnTracker().setVisible(false);
    }


    public void updateFromPreviousMove(QuaxBoard board) {
        QuaxCoordinate previousMove = board.previousMove();
        if (previousMove == null) {
            this.turnIndicator.setIndicatorColour(QuaxTileColour.BLACK);
        }

        else {
            // TODO - LoD violation
            QuaxTileColour colour = board.getTile(previousMove).getTileColour();
            this.setTile(previousMove, colour);
            this.turnIndicator.setIndicatorColour(colour.flip());

            setPieRuleVisibility(board.isPieRuleValid());
        }
    }

    public void setTile(QuaxCoordinate q, QuaxTileColour c) {
        quaxUIBoard.setTile(q, c);
    }

    public void setQuaxUIBoard(QuaxBoard b) {
        quaxUIBoard.setStackUIBoard(b);
        setPieRuleVisibility(b.isPieRuleValid());
    }

    public void setPieRuleVisibility(boolean value) {
        pieRuleButton.setDisable(!value);
        pieRuleButton.setVisible(value);
    }

    // TODO - just fix
    public void showStrategy(BotPlayer bot) {
        for (QuaxTile t : bot.getStrategyValueGroup(1)) {
            quaxUIBoard.setTileBorder(t.getCoordinates(), QuaxTileBorder.NONE);
        }
        for (QuaxTile t : bot.getStrategyValueGroup(2)) {
            quaxUIBoard.setTileBorder(t.getCoordinates(), QuaxTileBorder.BLUE);
        }
        for(QuaxTile t : bot.getStrategyValueGroup(3)){
            quaxUIBoard.setTileBorder(t.getCoordinates(),QuaxTileBorder.GREEN);
        }
        for(QuaxTile t: bot.getStrategyValueGroup(4)){
            quaxUIBoard.setTileBorder(t.getCoordinates(),QuaxTileBorder.RED);
        }
        for(QuaxTile t : bot.getStrategyValueGroup(5)){
            quaxUIBoard.setTileBorder(t.getCoordinates(),QuaxTileBorder.PURPLE);
        }
        for(QuaxTile t: bot.getStrategyValueGroup(6)){
            quaxUIBoard.setTileBorder(t.getCoordinates(),QuaxTileBorder.PINK);
        }
        this.strategyColourIndicator.setVisible(true);
    }

    public void hideStrategy(QuaxBoard board) {
        for (QuaxTile t : board) {
            this.quaxUIBoard.setTileBorder(t.getCoordinates(), QuaxTileBorder.NONE);
        }
        this.strategyColourIndicator.setVisible(false);
    }


    // TODO - WAY to big for a nested class, move to another
    private static class UserInterfaceBoard {

        private static final double FRONT_HOURGLASS_GAP = 5.7;
        private static final double BACK_HOURGLASS_GAP = 11.4;
        private static final double GRIDPANE_PADDING = OCTAGON_WIDTH / (MAX_OCTAGONS + 1);

        private OctagonTile[][] octagonGridCells;
        private RhombusTile[][] rhombusGridCells;

        private StackPane stackUIBoard;


        public UserInterfaceBoard() {
            this.stackUIBoard = new StackPane(
                    createGradientBackground(),
                    createBehindHourglass(),
                    createHourglass(),
                    createGridBackground(),
                    createBoardCoordinates(),
                    createGrid()
            );
        }

        public StackPane getStackUIBoard() {
            return this.stackUIBoard;
        }

        public void setStackUIBoard(QuaxBoard stackUIBoard) {
            for (QuaxTile tile : stackUIBoard) {
                setTile(tile.getCoordinates(), tile.getTileColour());
            }
        }

        public void setTile(QuaxCoordinate q, QuaxTileColour c) {
            if (q.isOctagon()) {
                octagonGridCells[q.x()][q.y()].setColour(c);
            }
            else {
                rhombusGridCells[q.x()][q.y()].setColour(c);
            }
        }

        public void setTileBorder(QuaxCoordinate q, QuaxTileBorder b) {
            if (q.isOctagon()) {
                this.octagonGridCells[q.x()][q.y()].setBorder(b);
            }
            else {
                this.rhombusGridCells[q.x()][q.y()].setBorder(b);
            }
        }


        private static Rectangle createGradientBackground() {
            double size = OCTAGON_WIDTH*(MAX_OCTAGONS + 1)+(MAX_RHOMBUSES*OCTAGON_GRID_GAP);
            Stop[] stops = new Stop[]{
                    new Stop(0, Color.NAVY),
                    new Stop(1, Color.BLUEVIOLET),
            };

            LinearGradient lgl = new LinearGradient(1,0,1,1,true, CycleMethod.NO_CYCLE,stops);
            Rectangle background = new Rectangle(size, size); //the multicoloured border around the board
            background.setFill(lgl);

            return background;
        }

        private static Rectangle createBehindHourglass(){
            double size = (OCTAGON_WIDTH * BACK_HOURGLASS_GAP) + ((BACK_HOURGLASS_GAP - (MAX_OCTAGONS - MAX_RHOMBUSES)*2) * OCTAGON_GRID_GAP) + (OCTAGON_WIDTH/2);
            Rectangle background = new Rectangle(size, size);
            background.setFill(Color.WHITE);

            return background;
        }

        private static Polygon createHourglass(){
            double distance = (FRONT_HOURGLASS_GAP * OCTAGON_WIDTH) + ((FRONT_HOURGLASS_GAP - (MAX_OCTAGONS - MAX_RHOMBUSES)) * OCTAGON_GRID_GAP) + OCTAGON_WIDTH/4;

            Polygon hourglass = new Polygon(-distance,distance,
                    distance,distance,
                    -distance,-distance,
                    distance,-distance);
            hourglass.setFill(Color.BLACK);

            return hourglass;
        }

        private static Rectangle createGridBackground(){
            double size = ((MAX_OCTAGONS - 1)*OCTAGON_WIDTH) + OctagonBase.calculateSideLength(OCTAGON_WIDTH) + (MAX_RHOMBUSES * OCTAGON_GRID_GAP);

            Rectangle background = new Rectangle(size, size);
            background.setFill(Color.OLDLACE);

            return background;
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

        // TODO - No output arguments allowed
        private static GridPane createBoardCoordinates() {
            GridPane coordGrid = new GridPane();
            setCoordinateGridRowsColumns(coordGrid);

            GridPane topCoords = new GridPane();
            GridPane bottomCoords = new GridPane();
            setTopBottomCoordinateGrid(topCoords, bottomCoords);

            coordGrid.add(topCoords, 1, 0);
            coordGrid.add(bottomCoords, 1, 2);

            GridPane leftCoords = new GridPane();
            GridPane rightCoords = new GridPane();
            setLeftRightCoordinateGrid(leftCoords, rightCoords);

            coordGrid.add(leftCoords, 0, 1);
            coordGrid.add(rightCoords, 2, 1);

            return coordGrid;
        }

        private static void setCoordinateGridRowsColumns(GridPane coordinateGrid) {
            coordinateGrid.setAlignment(Pos.CENTER);

            coordinateGrid.getColumnConstraints().add(new ColumnConstraints());
            coordinateGrid.getColumnConstraints().add(new ColumnConstraints((MAX_OCTAGONS*OCTAGON_WIDTH)+(MAX_RHOMBUSES*OCTAGON_GRID_GAP)));
            coordinateGrid.getColumnConstraints().add(new ColumnConstraints());

            coordinateGrid.getRowConstraints().add(new RowConstraints());
            coordinateGrid.getRowConstraints().add(new RowConstraints((MAX_OCTAGONS*OCTAGON_WIDTH)+(MAX_RHOMBUSES*OCTAGON_GRID_GAP)));
            coordinateGrid.getRowConstraints().add(new RowConstraints());
        }


        private static void setTopBottomCoordinateGrid(GridPane top, GridPane bottom) {
            positionColumns(top, bottom);

            for (int i = 0; i < MAX_OCTAGONS; i++){
                top.getColumnConstraints().add(new ColumnConstraints(OCTAGON_WIDTH));
                bottom.getColumnConstraints().add(new ColumnConstraints(OCTAGON_WIDTH));

                addLetterCoordinateLabel(top, i);
                addLetterCoordinateLabel(bottom, i);
            }
        }

        private static void positionColumns(GridPane top, GridPane bottom) {
            top.setPadding(new Insets(0, 0, GRIDPANE_PADDING, 0));
            bottom.setPadding(new Insets(GRIDPANE_PADDING, 0, 0, 0));

            top.setHgap(OCTAGON_GRID_GAP);
            top.setAlignment(Pos.CENTER);
            bottom.setHgap(OCTAGON_GRID_GAP);
            bottom.setAlignment(Pos.CENTER);
        }

        private static void addLetterCoordinateLabel(GridPane gp, int i) {
            Label letterCLabel = new Label(String.valueOf((char) ('A' + i)));
            styleColumnLabel(letterCLabel);

            StackPane letterCoordPane = new StackPane(letterCLabel);
            gp.add(letterCoordPane, i,0);
        }

        private static void styleColumnLabel(Label letterLabel) {
            letterLabel.setPrefWidth(Region.USE_COMPUTED_SIZE);
            letterLabel.setPrefHeight(Region.USE_COMPUTED_SIZE);
            letterLabel.getStyleClass().add("coordinate-letter-style");
            letterLabel.setAlignment(Pos.CENTER);
        }


        private static void setLeftRightCoordinateGrid(GridPane left, GridPane right) {
            positionRows(left, right);

            for (int j = 0 ; j < MAX_OCTAGONS; j++){
                left.getRowConstraints().add(new RowConstraints(OCTAGON_WIDTH));
                right.getRowConstraints().add(new RowConstraints(OCTAGON_WIDTH));

                addNumberCoordinateLabel(left, j);
                addNumberCoordinateLabel(right, j);
            }
        }

        private static void positionRows(GridPane left, GridPane right) {
            left.setPadding(new Insets(0, GRIDPANE_PADDING, 0, 0));
            right.setPadding(new Insets(0, 0, 0, GRIDPANE_PADDING));

            left.setVgap(OCTAGON_GRID_GAP);
            left.setAlignment(Pos.CENTER);
            right.setVgap(OCTAGON_GRID_GAP);
            right.setAlignment(Pos.CENTER);
        }

        private static void addNumberCoordinateLabel(GridPane gp, int j) {
            Label numCLabel = new Label(String.valueOf(11 - j));
            styleRowLabel(numCLabel);

            StackPane leftCoordPane = new StackPane(numCLabel);
            gp.add(leftCoordPane,0,j);
        }

        private static void styleRowLabel(Label numberLabel) {
            numberLabel.getStyleClass().add("coordinate-number-style");
            numberLabel.setPrefHeight(Region.USE_COMPUTED_SIZE);
            numberLabel.setPrefWidth(Region.USE_COMPUTED_SIZE);
        }


        // TODO - What's this for?
        private StackPane createShowStratGrid() {
            StackPane gridStack = new StackPane(
                    createOctagonGrid(),
                    createRhombusGrid()
            );
            gridStack.setMaxHeight(Region.USE_PREF_SIZE);
            gridStack.setMaxWidth(Region.USE_PREF_SIZE);
            gridStack.setVisible(false);
            gridStack.setMouseTransparent(true);

            return gridStack;
        }


        private GridPane createOctagonGrid() {
            octagonGridCells = new OctagonTile[MAX_OCTAGONS][MAX_OCTAGONS];
            GridPane octagonGrid = new GridPane();
            positionBoardTileGrid(octagonGrid);

            initialiseOctagonGridRowColumns(octagonGrid);
            initialiseOctagonGridCells(octagonGrid);

            return octagonGrid;
        }

        private void initialiseOctagonGridRowColumns(GridPane oGrid) {
            for (int i = 0; i < MAX_OCTAGONS ; i++) {
                ColumnConstraints column = new ColumnConstraints(OCTAGON_WIDTH);
                oGrid.getColumnConstraints().add(column);

                RowConstraints row = new RowConstraints(OCTAGON_WIDTH);
                oGrid.getRowConstraints().add(row);
            }
        }

        private void initialiseOctagonGridCells(GridPane oGrid) {
            for (int i = 0; i < MAX_OCTAGONS ; i++) {
                for (int j = 0; j < MAX_OCTAGONS ; j++) {
                    OctagonTile newTile = new OctagonTile(new QuaxCoordinate(i, j, true));
                    newTile.setId("octagon" + i + "-" + j);
                    octagonGridCells[i][j] = newTile;
                    oGrid.add(newTile, i, j);
                }
            }
        }


        private static void positionBoardTileGrid(GridPane boardTiles) {
            boardTiles.setAlignment(Pos.TOP_LEFT);
            boardTiles.setVgap(OCTAGON_GRID_GAP);
            boardTiles.setHgap(OCTAGON_GRID_GAP);
            boardTiles.setPickOnBounds(false);
        }

        private GridPane createRhombusGrid() {
            rhombusGridCells = new RhombusTile[MAX_RHOMBUSES][MAX_RHOMBUSES];
            GridPane rhombusGrid = new GridPane();
            positionBoardTileGrid(rhombusGrid);

            rhombusGrid.setPadding(new Insets(calculateRhombusGridGap(), 0, 0, calculateRhombusGridGap()));

            initialiseRhombusGridRowColumns(rhombusGrid);
            initialiseRhombusGridCells(rhombusGrid);

            return rhombusGrid;
        }

        private double calculateRhombusGridGap() {
            double rhombusSideLength = OctagonBase.calculateSideLength(OCTAGON_WIDTH);
            double rhombusDiagonalHeight = (OCTAGON_WIDTH - rhombusSideLength) / 2;
            return rhombusSideLength + rhombusDiagonalHeight + (OCTAGON_GRID_GAP/2);
        }

        private void initialiseRhombusGridRowColumns(GridPane rGrid) {
            for (int i = 0; i < MAX_RHOMBUSES; i++) {
                ColumnConstraints column = new ColumnConstraints(OCTAGON_WIDTH);
                rGrid.getColumnConstraints().add(column);

                RowConstraints row = new RowConstraints(OCTAGON_WIDTH);
                row.setValignment(VPos.TOP);
                rGrid.getRowConstraints().add(row);
            }
        }

        private void initialiseRhombusGridCells(GridPane rGrid) {
            for (int i = 0; i < MAX_RHOMBUSES; i++) {
                for (int j = 0; j < MAX_RHOMBUSES; j++) {
                    RhombusTile newTile = new RhombusTile(new QuaxCoordinate(i, j, false));
                    newTile.setId("rhombus" + i + "-" + j);
                    rhombusGridCells[i][j] = newTile;
                    rGrid.add(newTile, i, j);
                }
            }
        }


        private interface Tile extends Styleable {
            default void setColour(QuaxTileColour colour) {
                this.getStyleClass().removeAll(QuaxTileColour.BLACK.tilecolourStyle(),
                        QuaxTileColour.WHITE.tilecolourStyle(),
                        QuaxTileColour.NONE.tilecolourStyle());
                this.getStyleClass().add(colour.tilecolourStyle());
            }
            default void setBorder(QuaxTileBorder border) {
                this.getStyleClass().removeAll("tileoutline-0", "tileoutline-1","tileoutline-2","tileoutline-3","tileoutline-4");
                this.getStyleClass().add("tileoutline-base");
                this.getStyleClass().add(border.tileBorderStyle());
            }

            QuaxCoordinate getCoordinate();
        }

        private static class OctagonTile extends OctagonBase implements Tile {

            private QuaxCoordinate octagonGridCoordinate;

            public OctagonTile(QuaxCoordinate coordinate) {
                super();
                this.getStyleClass().add("tile");
                this.getStyleClass().add("tiletype-octagon");
                this.setColour(QuaxTileColour.NONE);
                this.setBorder(QuaxTileBorder.NONE);
                this.octagonGridCoordinate = coordinate;

                this.setOnMouseClicked(new EventHandler<>() {
                    @Override
                    public void handle(MouseEvent arg0) {
                        fireEvent(new QuaxCoordinateEvent(QuaxCoordinateEvent.TILE_CLICKED_EVENT, getCoordinate()));
                    }
                });
            }

            @Override
            public QuaxCoordinate getCoordinate() {
                return this.octagonGridCoordinate;
            }
        }

        private static class RhombusTile extends RhombusBase implements Tile {
            private QuaxCoordinate rhombusGridCoordinate;

            public RhombusTile(QuaxCoordinate coordinate) {
                super();
                this.getStyleClass().add("tile");
                this.getStyleClass().add("tiletype-rhombus");
                this.setColour(QuaxTileColour.NONE);
                this.setBorder(QuaxTileBorder.NONE);
                this.rhombusGridCoordinate = coordinate;

                this.setOnMouseClicked(new EventHandler<>() {
                    @Override
                    public void handle(MouseEvent arg0) {
                        fireEvent(new QuaxCoordinateEvent(QuaxCoordinateEvent.TILE_CLICKED_EVENT, getCoordinate()));
                    }
                });
            }

            @Override
            public QuaxCoordinate getCoordinate() {
                return this.rhombusGridCoordinate;
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

    // TODO - Definitely too complicated for a nested class, move to another
    private static class PlayerTurnIndicator {

        private static final double HBOX_SPACING = 5;
        private static final double OCTAGON_OBJECT_WIDTH = 40;

        private OctagonTurnIndicator octagonIndicator;
        private RhombusTurnIndicator rhombusIndicator;
        private TurnText turnText;
        private HBox turnTracker;

        public PlayerTurnIndicator() {
            this.turnTracker = createTurnTracker();
            this.setIndicatorColour(QuaxTileColour.BLACK);
        }

        public HBox getTurnTracker() {
            return this.turnTracker;
        }

        public void setIndicatorColour(QuaxTileColour colour) {
            this.octagonIndicator.setTurnTileColour(colour);
            this.rhombusIndicator.setTurnTileColour(colour);
            this.turnText.setTurnColour(colour);
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
            default void setTurnTileColour(QuaxTileColour colour) {
                assert colour == QuaxTileColour.BLACK || colour == QuaxTileColour.WHITE;
                this.getStyleClass().removeAll(QuaxTileColour.BLACK.tilecolourStyle(),
                            QuaxTileColour.WHITE.tilecolourStyle());
                this.getStyleClass().add(colour.tilecolourStyle());
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
                this.setTurnTileColour(QuaxTileColour.BLACK);
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
                this.setId("Turn-text");
            }

            public void setTurnColour(QuaxTileColour colour) {
                assert colour == QuaxTileColour.BLACK || colour == QuaxTileColour.WHITE;
                this.setText(colour + " to play");
                /*
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
                */
            }
        }
    }
}