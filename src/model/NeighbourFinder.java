package model;

import static model.QuaxBoard.*;
import types.*;

/*
 * Handle searching for neighbours
 */
class NeighbourFinder {

    private final QuaxBoard searchBoard;
    private final QuaxCoordinate searchCoordinate;

    NeighbourFinder(QuaxBoard board, QuaxCoordinate coordinate) {
        this.searchBoard = board;
        this.searchCoordinate = coordinate;
    }

    public QuaxTile[][] getCoordinateNeighbours() {
        QuaxTile[][] neighbours;

        if (searchCoordinate.isOctagon()) {
            neighbours = getOctagonNeighbours();
        } else {
            neighbours = getRhombusNeighbours();
        }

        return neighbours;
    }


    private QuaxTile[][] getRhombusNeighbours() {
        QuaxTile[][] neighbours = new QuaxTile[2][2];

        neighbours[0][0] = searchBoard.getOctagon(searchCoordinate.x(), searchCoordinate.y());
        neighbours[0][1] = searchBoard.getOctagon(searchCoordinate.x(), searchCoordinate.y() + 1);
        neighbours[1][0] = searchBoard.getOctagon(searchCoordinate.x() + 1, searchCoordinate.y());
        neighbours[1][1] = searchBoard.getOctagon(searchCoordinate.x() + 1, searchCoordinate.y() + 1);

        return neighbours;
    }


    private QuaxTile[][] getOctagonNeighbours() {
        QuaxTile[][] neighbours = new QuaxTile[3][3];

        neighbours[0] = getLeftNeighbours();
        neighbours[1] = getVerticalNeighbours();
        neighbours[2] = getRightNeighbours();

        return neighbours;
    }

    private QuaxTile[] getLeftNeighbours() {
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


    private QuaxTile[] createOutOfBoundsRow() {
        return new QuaxTile[]{QuaxTile.OUT_OF_BOUNDS_TILE,
                QuaxTile.OUT_OF_BOUNDS_TILE,
                QuaxTile.OUT_OF_BOUNDS_TILE};
    }

    private QuaxTile[] createOutOfBoundsRowWithHiddenCentre() {
        return new QuaxTile[]{QuaxTile.OUT_OF_BOUNDS_TILE,
                QuaxTile.HIDDEN_TILE,
                QuaxTile.OUT_OF_BOUNDS_TILE};
    }


    // TODO - Definitely need a comment
    public QuaxTile[][] getSquareOctagonNeighbours() {
        assert searchBoard != null && searchCoordinate != null && searchCoordinate.isOctagon();

        QuaxTile[][] neighbours = new QuaxTile[3][3];
        for (int i = -1; i <= 1; i++) {
            neighbours[i + 1] = createOctagonSquareNeighboursArray(i);
        }
        return neighbours;
    }

    private QuaxTile[] createOctagonSquareNeighboursArray(int verticalOffset) {
        QuaxTile[] array;
        if (verticalOffset == 0) {
            array = createOutOfBoundsRowWithHiddenCentre();
        } else {
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
