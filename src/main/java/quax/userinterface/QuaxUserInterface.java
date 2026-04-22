package quax.userinterface;

import java.awt.*;
import java.util.List;

import javafx.collections.ObservableList;
import javafx.geometry.VPos;
import javafx.scene.layout.*;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Text;
import quax.controller.QuaxController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import quax.model.QuaxBoard;
import quax.player.BotPlayer;
import quax.types.*;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.css.Styleable;

import static quax.model.QuaxBoard.MAX_OCTAGONS;
import static quax.model.QuaxBoard.MAX_RHOMBUSES;

public class QuaxUserInterface implements UserInterface {

    private static final double OCTAGON_WIDTH = 40;
    private static final double OCTAGON_GRID_GAP = 1;

    private static final String[] STYLESHEETS = new String[] {
            "/tile-styling.css",
            "/board-styling.css",
            "/ui-styling.css",
            "/button-styling.css"
    };

    private Stage stage;

    private UserInterfaceBoard board;
    private PlayerTurnIndicator turnIndicator;
    private VBox stratColourIndicator;

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

        this.stratColourIndicator = initialiseStrategyColourCoding();

        sideBar.getChildren().addAll(this.turnIndicator.getTurnTracker(), this.winLabel,this.stratColourIndicator);
        sideBar.getStyleClass().add("vbox");

        GridPane outer = new GridPane();

        Label title = new Label("Quax: Human V Bot");
        title.setId("Title");
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

        strat.getStyleClass().add("button3");
        hideStrat.getStyleClass().add("button3");
        pieRuleButton.getStyleClass().add("button3");

        sideBar.getChildren().addAll(strat,hideStrat,pieRuleButton);

        return sideBar;
    }

    private VBox initialiseStrategyColourCoding(){
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

        VBox stratColourIndicator = new VBox(10);
        stratColourIndicator.getChildren().addAll(stratLabel,stratTwo,stratThree,stratFour,stratFive,stratSix);
        stratColourIndicator.getStyleClass().add("vbox");
        stratColourIndicator.setVisible(false);
        stratColourIndicator.setId("ColourIndicator");
        return stratColourIndicator;
    }

    public void showWinLabel(QuaxTileColour c){
        winLabel.setText(c + " wins");
        winLabel.setVisible(true);
    }

    public void hideTurnTracker() {
        turnIndicator.getTurnTracker().setVisible(false);
    }

    public void updateFromPreviousMove(QuaxBoard board) {
        QuaxCoordinate previousMove = board.previousMove();
        if (previousMove == null) {
            this.turnIndicator.setColour(QuaxTileColour.BLACK);
        }
        if (previousMove != null) {
            QuaxTileColour colour = board.getTile(previousMove).getColour();
            this.setTile(previousMove, colour);
            this.turnIndicator.setColour(colour.flip());
        }
    }

    public void setTile(QuaxCoordinate q, QuaxTileColour c) {
        board.setTile(q, c);
    }

    public void setBoard(QuaxBoard b) {
        board.setBoard(b);
    }

    public Scene getScene() {
        return this.scene;
    }

    public void setPieRuleVisibility(boolean value) {
        pieRuleButton.setDisable(!value);
        pieRuleButton.setVisible(value);
    }

    public void showStrategy(BotPlayer bot){
        for (QuaxTile t : bot.getStratGroup(1)) {
            board.setTileBorder(t.getCoordinates(), QuaxTileBorder.NONE);
        }
        for (QuaxTile t : bot.getStratGroup(2)) {
            board.setTileBorder(t.getCoordinates(), QuaxTileBorder.BLUE);
        }
        for(QuaxTile t : bot.getStratGroup(3)){
            board.setTileBorder(t.getCoordinates(),QuaxTileBorder.GREEN);
        }
        for(QuaxTile t: bot.getStratGroup(4)){
            board.setTileBorder(t.getCoordinates(),QuaxTileBorder.RED);
        }
        for(QuaxTile t : bot.getStratGroup(5)){
            board.setTileBorder(t.getCoordinates(),QuaxTileBorder.PURPLE);
        }
        for(QuaxTile t: bot.getStratGroup(6)){
            board.setTileBorder(t.getCoordinates(),QuaxTileBorder.PINK);
        }
        this.stratColourIndicator.setVisible(true);
    }

    public void hideStrategy(QuaxBoard board){
        for(QuaxTile t : board){
            this.board.setTileBorder(t.getCoordinates(), QuaxTileBorder.NONE);
        }
        this.stratColourIndicator.setVisible(false);
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

        public void setTileBorder(QuaxCoordinate q, QuaxTileBorder b) {
            if (q.isOctagonMove()) {
                octagonGridCells[q.x()][q.y()].setBorder(b);
            } else {
                rhombusGridCells[q.x()][q.y()].setBorder(b);
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

        private StackPane createShowStratGrid(){
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
            default void setBorder(QuaxTileBorder border) {
                this.getStyleClass().removeAll("tileoutline-0", "tileoutline-1","tileoutline-2","tileoutline-3","tileoutline-4");
                this.getStyleClass().add("tileoutline-base");
                this.getStyleClass().add(border.tileborderStyle());
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
                this.setBorder(QuaxTileBorder.NONE);
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
                this.setBorder(QuaxTileBorder.NONE);
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
                this.setId("Turn-text");
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