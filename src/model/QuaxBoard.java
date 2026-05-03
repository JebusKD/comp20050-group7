package model;

import java.util.*;

import types.*;


/* Manage the game board state during the program */
public class QuaxBoard implements Iterable<QuaxTile> {

    public static final int NUM_OCTAGONS = 11;
    public static final int NUM_RHOMBUSES = 10;
	private static final int MAX_ADJACENT_TILE_GROUPS = 4;

	private final Octagon[][] octagonGrid;
	private final Rhombus[][] rhombusGrid;
	private final LinkedList<QuaxTileGroup> trackedGroups;
	private QuaxCoordinate previousMove;

	private int moveNumber;
	private boolean pieRuleDone;


	public QuaxBoard() {
		this.octagonGrid = new Octagon[NUM_OCTAGONS][NUM_OCTAGONS];
		this.rhombusGrid = new Rhombus[NUM_RHOMBUSES][NUM_RHOMBUSES];
		
		this.trackedGroups = new LinkedList<>();

		this.previousMove = null;
		this.moveNumber = 0;
		this.pieRuleDone = false;

		initialiseGrids();
	}

	// Copy constructor
	public QuaxBoard(QuaxBoard copyBoard) {
		this.octagonGrid = new Octagon[NUM_OCTAGONS][NUM_OCTAGONS];
		this.rhombusGrid = new Rhombus[NUM_RHOMBUSES][NUM_RHOMBUSES];
		
		this.trackedGroups = new LinkedList<>();

		this.previousMove = copyBoard.previousMove;
		this.moveNumber = copyBoard.moveNumber;
		this.pieRuleDone = copyBoard.pieRuleDone;

		initialiseGrids(copyBoard);
		initialiseGroups(copyBoard);
	}

	private void initialiseGrids() {
		for (int i = 0; i < NUM_OCTAGONS; i++) {
			for (int j = 0; j < NUM_OCTAGONS; j++) {
				this.octagonGrid[i][j] = new Octagon(i, j);
			}
		}

		for (int i = 0; i < NUM_RHOMBUSES; i++) {
			for (int j = 0; j < NUM_RHOMBUSES; j++) {
				this.rhombusGrid[i][j] = new Rhombus(i, j);
			}
		}
	}

	private void initialiseGrids(QuaxBoard copyBoard) {
		for (int i = 0; i < NUM_OCTAGONS; i++) {
			for (int j = 0; j < NUM_OCTAGONS; j++) {
				this.octagonGrid[i][j] = new Octagon(copyBoard.getOctagon(i, j));
			}
		}

		for (int i = 0; i < NUM_RHOMBUSES; i++) {
			for (int j = 0; j < NUM_RHOMBUSES; j++) {
				this.rhombusGrid[i][j] = new Rhombus(copyBoard.getRhombus(i, j));
			}
		}
	}

	private void initialiseGroups(QuaxBoard copyBoard) {
		for (QuaxTileGroup g : copyBoard.trackedGroups) {
			QuaxTileGroup newGroup = new QuaxTileGroup();
			GroupManager gm = new GroupManager();
			gm.trackGroup(newGroup);

			for (QuaxTile t : g) {
				newGroup.addTile(getTile(t.getCoordinates()));
			}
		}
	}



	public Octagon getOctagon(int x, int y) {
		return this.octagonGrid[x][y];
	}
	
	public Rhombus getRhombus(int x, int y) {
		return this.rhombusGrid[x][y];
	}
	
	public QuaxTile getTile(QuaxCoordinate coord) {
		if (coord.isOctagon()) {
			return this.getOctagon(coord.x(),coord.y());
		}
		else {
			return this.getRhombus(coord.x(),coord.y());
		}
	}
	
	public QuaxTileColour getTileColour(QuaxCoordinate coord) {
		return getTile(coord).getTileColour();
	}

	public int getMoveNumber() {
		return this.moveNumber;
	}

	public QuaxCoordinate previousMove() {
		return previousMove;
	}

	public boolean isStartingMove() {
		return previousMove == null;
	}



	/* Move validation checks */
	public boolean checkForWinningMove() {
		// Cannot win on first move
		if (isStartingMove()) {
			return false;
		}

		// TODO - violates LoD?
		// I'm thinking we do similar to botHaste, a private static boolean gameWon that we check
		return previousGroup().isWinningGroup();
	}

	private QuaxTileGroup previousGroup() {
		return getTile(previousMove).getTileGroup();
	}

	public boolean validMove(QuaxCoordinate coord, QuaxTileColour colour) {
		if (!checkForWinningMove() && (coord.isOctagon() || isValidRhombusPlacement(coord, colour))) {
			return getTile(coord).isFree();
		}

		return false;
	}

	public boolean validMove(QuaxTile t) {
		return validMove(t.getCoordinates(), currentColourTurn());
	}

	private QuaxTileColour currentColourTurn() {
		QuaxTileColour result;

		if (isStartingMove()) {
			result = QuaxTileColour.BLACK;
		}
		else {
			result = getTileColour(previousMove()).flip();
		}

		return result;
	}

	private boolean isValidRhombusPlacement(QuaxCoordinate coord, QuaxTileColour colour) {
		assert coord.isRhombus();
        QuaxTile[][] n = getNeighbours(coord);

        return (n[0][0].isSameColour(colour) && n[1][1].isSameColour(colour))
				|| (n[1][0].isSameColour(colour) && n[0][1].isSameColour(colour));
    }
	
	public boolean isValidRhombusForBoth(QuaxCoordinate coord) {
		return isValidRhombusPlacement(coord, QuaxTileColour.BLACK)
				&& isValidRhombusPlacement(coord, QuaxTileColour.WHITE);
	}



	public boolean attemptPieRule() {
		if (isPieRuleValid()) {
			this.pieRuleDone = true;
			this.moveNumber++;
			return true;
		}
		return false;
	}

	public boolean isPieRuleValid() {
		return this.moveNumber == 1 && !this.pieRuleDone;
	}


	public void makeMove(QuaxCoordinate coordinate, QuaxTileColour c) {
		QuaxTile tile;
		GroupManager moveManager = new GroupManager();

		if (coordinate.isOctagon()) {
			tile = this.octagonGrid[coordinate.x()][coordinate.y()];
		}
		else {
			tile = this.rhombusGrid[coordinate.x()][coordinate.y()];
		}

		tile.setTileColour(c);
		moveManager.assignGroup(tile);
		this.previousMove = coordinate;
		this.moveNumber++;
	}

	public void makeMove(QuaxTile t) {
		makeMove(t.getCoordinates(), currentColourTurn());
	}
	
	public void skipTurn() {
		this.moveNumber++;
	}


	/*
	 * Manage adding a tile to a group
	 */
	private class GroupManager {

		private void trackGroup(QuaxTileGroup g) {
			trackedGroups.addFirst(g);
		}

		private void untrackGroup(QuaxTileGroup g) {
			trackedGroups.remove(g);
		}


		private void assignGroup(QuaxTile newTile) {
			QuaxTileColour c = newTile.getTileColour();
			assert c != QuaxTileColour.NONE;
			
			QuaxTile[][] neighbours = getNeighbours(newTile.getCoordinates());

			ArrayList<QuaxTileGroup> nearGroups = getAdjacentGroups(neighbours, c);
			expandLargestGroup(newTile, nearGroups);
		}


		private ArrayList<QuaxTileGroup> getAdjacentGroups(QuaxTile[][] neighbours, QuaxTileColour c) {
			ArrayList<QuaxTileGroup> nearbyGroups = new ArrayList<>(MAX_ADJACENT_TILE_GROUPS);

			for (QuaxTile[] tileArray : neighbours) {
				for (QuaxTile tile : tileArray) {
					if (tile.tileExists() && tile.isSameColour(c) &&
							tileNotMemberOfGroup(nearbyGroups, tile)) {
						nearbyGroups.add(tile.getTileGroup());
					}
				}
			}

			return nearbyGroups;
		}

		private boolean tileNotMemberOfGroup(ArrayList<QuaxTileGroup> groups, QuaxTile t) {
			return !(groups.contains(t.getTileGroup()));
		}


		private void expandLargestGroup(QuaxTile tile, ArrayList<QuaxTileGroup> adjacentGroups) {
			if (adjacentGroups.isEmpty()) {
				trackGroup(new QuaxTileGroup(tile));
			}
			else {
				QuaxTileGroup largestGroup = getBiggestGroup(adjacentGroups);
				largestGroup.addTile(tile);
				mergeNearbyGroups(largestGroup, adjacentGroups);
			}
		}

		private QuaxTileGroup getBiggestGroup(ArrayList<QuaxTileGroup> adjGroups) {
			int maxSize = -1;
			QuaxTileGroup biggestGroup = null;

			for (QuaxTileGroup g : adjGroups) {
				if (g.size() > maxSize) {
					biggestGroup = g;
					maxSize = g.size();
				}
			}

			return biggestGroup;
		}

		private void mergeNearbyGroups(QuaxTileGroup largest, ArrayList<QuaxTileGroup> adjacentGroups) {
			for (QuaxTileGroup g : adjacentGroups) {
				if (g != largest) {
					largest.merge(g);
					untrackGroup(g);
				}
			}
		}

	}



	public QuaxTile[][] getNeighbours(QuaxCoordinate coord) {
		return NeighbourFinder.getCoordinateNeighbours(coord, this);
	}

	public List<QuaxTile> getNeighboursList(QuaxTile t) {
		LinkedList<QuaxTile> result = new LinkedList<>();
		for (QuaxTile[] arr : getNeighbours(t.getCoordinates())) {
			for (QuaxTile n : arr) {
				if (n.tileExists()) {
					result.add(n);
				}
			}
		}
		return result;
	}
	
	public QuaxTile[][] getSquareOctagonNeighbours(Octagon o) {
		return NeighbourFinder.getSquareOctagonNeighbours(o.getCoordinates(), this);
	}

	/*
	 * Handle searching for neighbours
	 * //TODO - make proper nested class - instance of Board, etc.?
	 */
	private static class NeighbourFinder {

		private static QuaxTile[][] getCoordinateNeighbours(QuaxCoordinate coord, QuaxBoard b) {
			QuaxTile[][] neighbours;

			if (coord.isOctagon()) {
				neighbours = getOctagonNeighbours(coord, b);
			}
			else {
				neighbours = getRhombusNeighbours(coord, b);
			}

			return neighbours;
		}


		private static QuaxTile[][] getRhombusNeighbours(QuaxCoordinate coord, QuaxBoard b) {
			QuaxTile[][] neighbours = new QuaxTile[2][2];

			neighbours[0][0] = b.getOctagon(coord.x(), coord.y());
			neighbours[0][1] = b.getOctagon(coord.x(), coord.y() + 1);
			neighbours[1][0] = b.getOctagon(coord.x() + 1, coord.y());
			neighbours[1][1] = b.getOctagon(coord.x() + 1, coord.y() + 1);

			return neighbours;
		}


		private static QuaxTile[][] getOctagonNeighbours(QuaxCoordinate coordinate, QuaxBoard b) {
			QuaxTile[][] neighbours = new QuaxTile[3][3];

			neighbours[0] = getLeftNeighbours(coordinate, b);
			neighbours[1] = getVerticalNeighbours(coordinate, b);
			neighbours[2] = getRightNeighbours(coordinate, b);

			return neighbours;
		}

		private static QuaxTile[] getLeftNeighbours(QuaxCoordinate coord, QuaxBoard b) {
			int minusX = coord.x() - 1, minusY = coord.y() - 1, plusY = coord.y() + 1;
			QuaxTile[] adjTiles = createOutOfBoundsRow();

			if (minusX >= 0) {
				if (minusY >= 0) {
					adjTiles[0] = b.getRhombus(minusX, minusY);
				}

				adjTiles[1] = b.getOctagon(minusX, coord.y());

				if (plusY <= NUM_RHOMBUSES) {
					adjTiles[2] = b.getRhombus(minusX, coord.y());
				}
			}

			return adjTiles;
		}

		private static QuaxTile[] getRightNeighbours(QuaxCoordinate coord, QuaxBoard b) {
			int plusX = coord.x() + 1, minusY = coord.y() - 1, plusY = coord.y() + 1;
			QuaxTile[] adjTiles = createOutOfBoundsRow();

			if (plusX <= NUM_RHOMBUSES) {
				if (minusY >= 0) {
					adjTiles[0] = b.getRhombus(coord.x(), minusY);
				}

				adjTiles[1] = b.octagonGrid[plusX][coord.y()];

				if (plusY <= NUM_RHOMBUSES) {
					adjTiles[2] = b.getRhombus(coord.x(), coord.y());
				}
			}

			return adjTiles;
		}

		private static QuaxTile[] getVerticalNeighbours(QuaxCoordinate coord, QuaxBoard b) {
			int minusY = coord.y() - 1, plusY = coord.y() + 1;
			QuaxTile[] adjTiles = createOutOfBoundsRowWithHiddenCenter();

			if (minusY >= 0) {
				adjTiles[0] = b.getOctagon(coord.x(), minusY);
			}

			if (plusY <= NUM_RHOMBUSES) {
				adjTiles[2] = b.getOctagon(coord.x(), plusY);
			}

			return adjTiles;
		}



		private static QuaxTile[] createOutOfBoundsRow() {
			return new QuaxTile[] { QuaxTile.OUT_OF_BOUNDS_TILE,
									QuaxTile.OUT_OF_BOUNDS_TILE,
									QuaxTile.OUT_OF_BOUNDS_TILE };
		}
		
		private static QuaxTile[] createOutOfBoundsRowWithHiddenCenter() {
			return new QuaxTile[] { QuaxTile.OUT_OF_BOUNDS_TILE,
									QuaxTile.HIDDEN_TILE,
									QuaxTile.OUT_OF_BOUNDS_TILE };
		}


		private static QuaxTile[][] getSquareOctagonNeighbours(QuaxCoordinate coordinate, QuaxBoard board) {
			assert board != null && coordinate != null && coordinate.isOctagon();

			QuaxTile[][] neighbours = new QuaxTile[3][3];
			for (int i = -1; i <= 1; i++) {
				neighbours[i + 1] = createOctagonSquareNeighboursArray(coordinate, board, i);
			}
			return neighbours;
		}
		
		private static QuaxTile[] createOctagonSquareNeighboursArray
									(QuaxCoordinate center, QuaxBoard board, int verticalOffset) {
			QuaxTile[] array;
			if (verticalOffset == 0) {
				array = createOutOfBoundsRowWithHiddenCenter();
			} else {
				array = createOutOfBoundsRow();
			}
			
			int y = center.y() + verticalOffset;
			if (y >= 0 && y < NUM_OCTAGONS) {
				for (int i = -1; i <= 1; i++) {
					int x = center.x() + i;
					if (x >= 0 && x < NUM_OCTAGONS) {
						array[i+1] = board.getOctagon(x, y);
					}
					
				}
			}
			
			return array;
		}
	}


	/* Create an Iterable for all tiles in the board */
	public Iterator<QuaxTile> iterator() {
		return new QuaxBoardIterator(this);
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
	
	public Iterator<Octagon> octagonIterator() {
		return new QuaxBoardOctagonIterator(this);
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
