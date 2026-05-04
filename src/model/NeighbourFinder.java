package model;

import static model.QuaxBoard.*;
import types.*;

/* Handle searching for neighbours */
class NeighbourFinder {

    private final QuaxBoard searchBoard;
    private final QuaxCoordinate searchCoordinate;

    NeighbourFinder(QuaxBoard board, QuaxCoordinate coordinate) {
        assert board != null && coordinate != null;

        this.searchBoard = board;
        this.searchCoordinate = coordinate;
    }

    public QuaxTile[][] getCoordinateNeighbours() {
    	assert searchCoordinate != null;
    	
        QuaxTile[][] neighbours;

        if (searchCoordinate.isOctagon()) {
            neighbours = getOctagonNeighbours();
        } else {
            neighbours = getRhombusNeighbours();
        }

        return neighbours;
    }


    /* Rhombus neighbours refer to the four octagon tiles connected diagonally */
    private QuaxTile[][] getRhombusNeighbours() {
    	assert searchBoard != null && searchCoordinate != null;
    	
        QuaxTile[][] neighbours = new QuaxTile[2][2];

        neighbours[0][0] = searchBoard.getOctagon(searchCoordinate.x(), searchCoordinate.y());
        neighbours[0][1] = searchBoard.getOctagon(searchCoordinate.x(), searchCoordinate.y() + 1);
        neighbours[1][0] = searchBoard.getOctagon(searchCoordinate.x() + 1, searchCoordinate.y());
        neighbours[1][1] = searchBoard.getOctagon(searchCoordinate.x() + 1, searchCoordinate.y() + 1);

        return neighbours;
    }

    /* Octagon neighbours refer to:
     * The four octagon tiles directly connected horizontally and vertically
     * The four rhombus tiles connected diagonally
     */
    private QuaxTile[][] getOctagonNeighbours() {
        QuaxTile[][] neighbours = new QuaxTile[3][3];

        neighbours[0] = getLeftNeighbours();
        neighbours[1] = getVerticalNeighbours();
        neighbours[2] = getRightNeighbours();

        return neighbours;
    }

    private QuaxTile[] getLeftNeighbours() {
    	assert searchCoordinate != null && searchBoard != null;
    	
        int minusX = searchCoordinate.x() - 1,
                minusY = searchCoordinate.y() - 1,
                plusY = searchCoordinate.y() + 1;
        QuaxTile[] adjTiles = createOutOfBoundsRow();

        if (minusX >= 0) {
            if (minusY >= 0) {
                adjTiles[0] = searchBoard.getRhombus(minusX, minusY);
            }

            adjTiles[1] = searchBoard.getOctagon(minusX, searchCoordinate.y());

            if (plusY <= NUM_RHOMBUSES) {
                adjTiles[2] = searchBoard.getRhombus(minusX, searchCoordinate.y());
            }
        }

        return adjTiles;
    }

    private QuaxTile[] getVerticalNeighbours() {
    	assert searchCoordinate != null && searchBoard != null;
    	
        int minusY = searchCoordinate.y() - 1,
                plusY = searchCoordinate.y() + 1;
        QuaxTile[] adjTiles = createOutOfBoundsRowWithHiddenCentre();

        if (minusY >= 0) {
            adjTiles[0] = searchBoard.getOctagon(searchCoordinate.x(), minusY);
        }

        if (plusY <= NUM_RHOMBUSES) {
            adjTiles[2] = searchBoard.getOctagon(searchCoordinate.x(), plusY);
        }

        return adjTiles;
    }

    private QuaxTile[] getRightNeighbours() {
    	assert searchCoordinate != null && searchBoard != null;
    	
        int plusX = searchCoordinate.x() + 1,
                minusY = searchCoordinate.y() - 1,
                plusY = searchCoordinate.y() + 1;
        QuaxTile[] adjTiles = createOutOfBoundsRow();

        if (plusX <= NUM_RHOMBUSES) {
            if (minusY >= 0) {
                adjTiles[0] = searchBoard.getRhombus(searchCoordinate.x(), minusY);
            }

            adjTiles[1] = searchBoard.getOctagon(plusX, searchCoordinate.y());

            if (plusY <= NUM_RHOMBUSES) {
                adjTiles[2] = searchBoard.getRhombus(searchCoordinate.x(), searchCoordinate.y());
            }
        }

        return adjTiles;
    }


    /* Create special objects for out of bounds tiles
     * Allows less null checks
     */
    private static QuaxTile[] createOutOfBoundsRow() {
        return new QuaxTile[]{QuaxTile.OUT_OF_BOUNDS_TILE,
                QuaxTile.OUT_OF_BOUNDS_TILE,
                QuaxTile.OUT_OF_BOUNDS_TILE};
    }

    private static QuaxTile[] createOutOfBoundsRowWithHiddenCentre() {
        return new QuaxTile[]{QuaxTile.OUT_OF_BOUNDS_TILE,
                QuaxTile.HIDDEN_TILE,
                QuaxTile.OUT_OF_BOUNDS_TILE};
    }


    /* Create the array representing the 3 x 3 square of octagons surrounding a tile
     * This is different from the regular neighbours and does not include rhombus tiles
     *
     *      ON[0][0]  |  ON[0][1]  | ON[0][2]
     *      ON[1][0]  |   Centre   | ON[1][2]
     *      ON[2][0]  |  ON[2][1]  | ON[2][2]
     */
    public QuaxTile[][] getSquareOfAdjacentOctagonNeighbours() {
        assert searchBoard != null && searchCoordinate != null && searchCoordinate.isOctagon();

        QuaxTile[][] neighbours = new QuaxTile[3][3];
        for (int i = -1; i <= 1; i++) {
            neighbours[i + 1] = createRowOfSquareOfAdjacentOctagonNeighbours(i);
        }
        return neighbours;
    }


    private QuaxTile[] createRowOfSquareOfAdjacentOctagonNeighbours(int verticalOffset) {
    	assert searchCoordinate != null && searchBoard != null;
    	
        QuaxTile[] array;
        if (verticalOffset == 0) {
            array = createOutOfBoundsRowWithHiddenCentre();
        }
        else {
            array = createOutOfBoundsRow();
        }

        int y = searchCoordinate.y() + verticalOffset;

        if (y >= 0 && y < NUM_OCTAGONS) {
            for (int i = -1; i <= 1; i++) {
                int x = searchCoordinate.x() + i;
                if (x >= 0 && x < NUM_OCTAGONS) {
                    array[i + 1] = searchBoard.getOctagon(x, y);
                }

            }
        }

        return array;
    }
}
