package userinterface.interfacebuilders;

import javafx.css.Styleable;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;

import model.QuaxBoard;
import types.*;
import static model.QuaxBoard.MAX_OCTAGONS;
import static model.QuaxBoard.MAX_RHOMBUSES;
import static userinterface.QuaxUserInterface.OCTAGON_GRID_GAP;
import static userinterface.QuaxUserInterface.OCTAGON_WIDTH;


public class UserInterfaceBoard {

    private static final double FRONT_HOURGLASS_GAP = 5.7;
    private static final double BACK_HOURGLASS_GAP = 11.4;

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
            this.octagonGridCells[q.x()][q.y()].setColour(c);
        }
        else {
            this.rhombusGridCells[q.x()][q.y()].setColour(c);
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


    private Rectangle createGradientBackground() {
        double size = OCTAGON_WIDTH * (MAX_OCTAGONS + 1)
                        + (MAX_RHOMBUSES * OCTAGON_GRID_GAP);

        Stop[] stops = new Stop[] {
                new Stop(0, Color.NAVY),
                new Stop(1, Color.BLUEVIOLET),
        };

        LinearGradient lgl = new LinearGradient(1,0,1,1,true, CycleMethod.NO_CYCLE,stops);
        Rectangle background = new Rectangle(size, size); //the multicoloured border around the board
        background.setFill(lgl);

        return background;
    }

    private Rectangle createBehindHourglass() {
        double size = (OCTAGON_WIDTH * BACK_HOURGLASS_GAP)
                        + ((BACK_HOURGLASS_GAP - (MAX_OCTAGONS - MAX_RHOMBUSES)*2) * OCTAGON_GRID_GAP)
                        + (OCTAGON_WIDTH / 2);

        Rectangle background = new Rectangle(size, size);
        background.setFill(Color.WHITE);

        return background;
    }

    private Polygon createHourglass() {
        double distance = (FRONT_HOURGLASS_GAP * OCTAGON_WIDTH)
                        + ((FRONT_HOURGLASS_GAP - (MAX_OCTAGONS - MAX_RHOMBUSES)) * OCTAGON_GRID_GAP)
                        + (OCTAGON_WIDTH / 4);

        Polygon hourglass = new Polygon(-distance,distance,
                distance,distance,
                -distance,-distance,
                distance,-distance);
        hourglass.setFill(Color.BLACK);

        return hourglass;
    }

    private Rectangle createGridBackground() {
        double size = ((MAX_OCTAGONS - 1) * OCTAGON_WIDTH)
                + OctagonBase.calculateSideLength(OCTAGON_WIDTH)
                + (MAX_RHOMBUSES * OCTAGON_GRID_GAP);

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

    private GridPane createBoardCoordinates() {
        CoordinateBuilder coordBuild = new CoordinateBuilder();
        return coordBuild.getCoordinateGrid();
    }


    // TODO - What's this for?
    private StackPane createShowStrategyGrid() {
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

