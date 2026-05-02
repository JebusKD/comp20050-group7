package userinterface.interfacebuilders;

import java.util.Iterator;
import javafx.css.Styleable;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

import model.QuaxBoard;
import types.*;
import userinterface.QuaxUserInterface;


public class UserInterfaceBoard {

    private static final String PREVIOUS_MOVE_STYLE = "tilecolour-green";

    private final OctagonTile[][] octagonGridCells;
    private final RhombusTile[][] rhombusGridCells;

    private final StackPane stackUIBoard;


    public UserInterfaceBoard() {
        BackgroundBoardBuilder bgBuilder = new BackgroundBoardBuilder();

        this.stackUIBoard = new StackPane(bgBuilder.initialiseBoard());
        this.octagonGridCells = bgBuilder.getOctagonTileGrid();
        this.rhombusGridCells = bgBuilder.getRhombusTileGrid();
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

    static class OctagonTile extends OctagonBase implements Tile {

        private final QuaxCoordinate octagonGridCoordinate;

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

    static class RhombusTile extends RhombusBase implements Tile {

        private final QuaxCoordinate rhombusGridCoordinate;

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

