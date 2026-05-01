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
import userinterface.QuaxUserInterface;

import static model.QuaxBoard.MAX_OCTAGONS;
import static model.QuaxBoard.MAX_RHOMBUSES;
import static userinterface.QuaxUserInterface.OCTAGON_GRID_GAP;
import static userinterface.QuaxUserInterface.OCTAGON_WIDTH;

import java.util.Iterator;


public class UserInterfaceBoard {

    private static final double FRONT_HOURGLASS_GAP = 5.7;
    private static final double BACK_HOURGLASS_GAP = 11.4;
    
    private static final String PREVIOUS_MOVE_STYLE = "tilecolour-green";

    private OctagonTile[][] octagonGridCells;
    private RhombusTile[][] rhombusGridCells;

    private final StackPane stackUIBoard;


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
        GridBuilder gb = new GridBuilder();
        StackPane gridStack = new StackPane(
                gb.createOctagonGrid(),
                gb.createRhombusGrid()
        );

        gridStack.setMaxHeight(Region.USE_PREF_SIZE);
        gridStack.setMaxWidth(Region.USE_PREF_SIZE);

        return gridStack;
    }

    private class GridBuilder {
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
                    OctagonTile newTile = new OctagonTile(QuaxCoordinate.newOctagonCoordinate(i, j));
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
                    RhombusTile newTile = new RhombusTile(QuaxCoordinate.newRhombusCoordinate(i, j));
                    newTile.setId("rhombus" + i + "-" + j);
                    rhombusGridCells[i][j] = newTile;
                    rGrid.add(newTile, i, j);
                }
            }
        }
    }


    private GridPane createBoardCoordinates() {
        CoordinateBuilder coordBuild = new CoordinateBuilder();
        return coordBuild.getCoordinateGrid();
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
        getTileFromCoordinate(q).setColour(c);
    }

    public void setTileBorder(QuaxCoordinate q, QuaxTileBorder b) {
        getTileFromCoordinate(q).setBorder(b);
    }

    private Tile getTileFromCoordinate(QuaxCoordinate q) {
        Tile tile;
        if (q.isOctagon()) {
            tile = this.octagonGridCells[q.x()][q.y()];
        }
        else {
            tile = this.rhombusGridCells[q.x()][q.y()];
        }
        return tile;
    }


    public void setBotChosenCell(QuaxCoordinate q) {
        clearBotChosenMove();
        getTileFromCoordinate(q).setPreviousMove();
    }

    private void clearBotChosenCell(QuaxCoordinate q) {
        getTileFromCoordinate(q).clearPreviousMove();
    }


    public void clearTileBorders() {
        Iterator<QuaxCoordinate> iterator = QuaxBoard.coordinateIterator();

        while (iterator.hasNext()) {
            setTileBorder(iterator.next(), QuaxTileBorder.NONE);
        }
    }

    public void clearBotChosenMove() {
        Iterator<QuaxCoordinate> iterator = QuaxBoard.coordinateIterator();

        while (iterator.hasNext()) {
            clearBotChosenCell(iterator.next());
        }
    }


    private interface Tile extends Styleable {
    	default void removeTileColour() {
    		this.getStyleClass().removeAll(
    				QuaxTileColour.BLACK.tilecolourStyle(),
                    QuaxTileColour.WHITE.tilecolourStyle(),
                    QuaxTileColour.NONE.tilecolourStyle());
    	}
    	
        default void setColour(QuaxTileColour colour) {
        	this.removeTileColour();
            this.getStyleClass().add(colour.tilecolourStyle());
        }
        
        default void setBorder(QuaxTileBorder border) {
            this.clearBorder();
            this.getStyleClass().addAll("tileoutline-base", border.tileBorderStyle());
        }
        
        default void clearBorder() {
        	for (QuaxTileBorder b : QuaxUserInterface.STRATEGY_GROUP_BORDERS) {
        		this.getStyleClass().remove(b.tileBorderStyle());
        	}
        }
        
        void setPreviousMove();
        void clearPreviousMove();

        QuaxCoordinate getCoordinate();
    }

    private static class OctagonTile extends OctagonBase implements Tile {

        private QuaxCoordinate octagonGridCoordinate;

        public OctagonTile(QuaxCoordinate coordinate) {
            super();
            this.getStyleClass().addAll("tile", "tiletype-octagon");
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
        public void setPreviousMove() {
        	this.getStyleClass().add(PREVIOUS_MOVE_STYLE);
        }
        
        @Override
        public void clearPreviousMove() {
        	this.getStyleClass().remove(PREVIOUS_MOVE_STYLE);
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
            this.getStyleClass().addAll("tile", "tiletype-rhombus");
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
        public void setPreviousMove() {
        	this.getStyleClass().add(PREVIOUS_MOVE_STYLE);
        }
        
        @Override
        public void clearPreviousMove() {
        	this.getStyleClass().remove(PREVIOUS_MOVE_STYLE);
        }
        
        @Override
        public QuaxCoordinate getCoordinate() {
            return this.rhombusGridCoordinate;
        }
    }
}

