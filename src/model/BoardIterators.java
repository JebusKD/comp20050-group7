package model;

import java.util.*;

import types.*;
import static model.QuaxBoard.*;


class BoardIterators implements Iterable<QuaxTile> {

    private final QuaxBoard iteratorBoard;

    BoardIterators(QuaxBoard board) {
        iteratorBoard = board;
    }


    /* Iterate through all tiles in the board */
    public Iterator<QuaxTile> iterator() {
        return new QuaxBoardIterator(iteratorBoard);
    }

    private static class QuaxBoardIterator implements Iterator<QuaxTile> {
        private static final int NUM_ELEMENTS = (NUM_OCTAGONS*NUM_OCTAGONS) + (NUM_RHOMBUSES*NUM_RHOMBUSES);

        private int cursor;
        private final ArrayList<QuaxTile> elements;

        public QuaxBoardIterator(QuaxBoard source) {
            this.cursor = 0;
            this.elements = new ArrayList<>(NUM_ELEMENTS);

            for (int i = 0; i < NUM_OCTAGONS - 1 ; i++) {
                for (int j = 0; j < NUM_OCTAGONS; j++) {
                    this.elements.add(source.getOctagon(i, j));
                }
                for (int j = 0; j < NUM_RHOMBUSES; j++) {
                    this.elements.add(source.getRhombus(i, j));
                }
            }
            for (int j = 0; j < NUM_OCTAGONS; j++) {
                this.elements.add(source.getOctagon(NUM_OCTAGONS - 1, j));
            }
        }

        @Override
        public boolean hasNext() {
            return cursor < NUM_ELEMENTS;
        }

        @Override
        public QuaxTile next() {
            assert hasNext();
            return elements.get(cursor++);
        }

        private QuaxCoordinate nextCoordinate() {
            assert hasNext();
            return next().getCoordinates();
        }
    }


    /* Iterate through all tile *coordinates* in the board.
     *  This is not dependent on the board state, and is used exclusively when
     *   showing the bot's strategy
     */
    public static Iterator<QuaxCoordinate> coordinateIterator() {
        return new QuaxBoardCoordinateIterator();
    }

    private static class QuaxBoardCoordinateIterator implements Iterator<QuaxCoordinate> {
        private final QuaxBoardIterator boardIterator;

        public QuaxBoardCoordinateIterator() {
            this.boardIterator = new QuaxBoardIterator(new QuaxBoard());
        }

        @Override
        public boolean hasNext() {
            return boardIterator.hasNext();
        }

        @Override
        public QuaxCoordinate next() {
            return boardIterator.nextCoordinate();
        }
    }


    /* Iterate through all octagon tiles in the board.
     *  Dependent on the board state
     *  Used when refining the bot's strategy
     */
    public Iterator<Octagon> octagonIterator() {
        return new QuaxBoardOctagonIterator(iteratorBoard);
    }

    private static class QuaxBoardOctagonIterator implements Iterator<Octagon> {
        private static final int NUM_ELEMENTS = NUM_OCTAGONS * NUM_OCTAGONS;

        private int cursor;
        private final ArrayList<Octagon> elements;

        public QuaxBoardOctagonIterator(QuaxBoard source) {
            this.cursor = 0;
            this.elements = new ArrayList<>(NUM_ELEMENTS);

            for (int i = 0; i < NUM_OCTAGONS; i++) {
                for (int j = 0; j < NUM_OCTAGONS; j++) {
                    this.elements.add(source.getOctagon(i, j));
                }
            }
        }

        @Override
        public boolean hasNext() {
            return cursor < NUM_ELEMENTS;
        }

        @Override
        public Octagon next() {
            assert hasNext();
            return elements.get(cursor++);
        }

        private QuaxCoordinate nextCoordinate() {
            return next().getCoordinates();
        }
    }


    /* Iterate through all octagon tiles in the board.
     *  Independent of the board state
     *  Used when refining the bot's strategy
     */
    public static Iterator<QuaxCoordinate> rhombusCoordinateIterator() {
        return new QuaxBoardRhombusCoordinateIterator();
    }

    private static class QuaxBoardRhombusIterator implements Iterator<QuaxTile> {
        private static final int NUM_ELEMENTS = NUM_RHOMBUSES * NUM_RHOMBUSES;

        private int cursor;
        private final ArrayList<QuaxTile> elements;

        public QuaxBoardRhombusIterator(QuaxBoard source) {
            this.cursor = 0;
            this.elements = new ArrayList<>(NUM_ELEMENTS);

            for (int i = 0; i < NUM_RHOMBUSES; i++) {
                for (int j = 0; j < NUM_RHOMBUSES; j++) {
                    this.elements.add(source.getRhombus(i, j));
                }
            }
        }

        @Override
        public boolean hasNext() {
            return cursor < NUM_ELEMENTS;
        }

        @Override
        public QuaxTile next() {
            assert hasNext();
            return elements.get(cursor++);
        }

        private QuaxCoordinate nextCoordinate() {
            return next().getCoordinates();
        }
    }

    private static class QuaxBoardRhombusCoordinateIterator implements Iterator<QuaxCoordinate> {
        private final QuaxBoardRhombusIterator boardIterator;

        public QuaxBoardRhombusCoordinateIterator() {
            this.boardIterator = new QuaxBoardRhombusIterator(new QuaxBoard());
        }

        @Override
        public boolean hasNext() {
            return boardIterator.hasNext();
        }

        @Override
        public QuaxCoordinate next() {
            return boardIterator.nextCoordinate();
        }
    }
}
