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
			return getOctagon(coord.x(), coord.y());
		}
		else {
			return getRhombus(coord.x(), coord.y());
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

	public boolean validMove(QuaxTile tile) {
		return validMove(tile.getCoordinates(), currentColourTurn());
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
	
	public boolean isValidRhombusPlacementForBothPlayers(QuaxCoordinate coord) {
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


	public void makeMove(QuaxCoordinate coordinate, QuaxTileColour colour) {
		QuaxTile tile;
		GroupManager moveManager = new GroupManager();

		if (coordinate.isOctagon()) {
			tile = this.octagonGrid[coordinate.x()][coordinate.y()];
		}
		else {
			tile = this.rhombusGrid[coordinate.x()][coordinate.y()];
		}

		tile.setTileColour(colour);
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


		private ArrayList<QuaxTileGroup> getAdjacentGroups(QuaxTile[][] neighbours, QuaxTileColour colour) {
			ArrayList<QuaxTileGroup> nearbyGroups = new ArrayList<>(MAX_ADJACENT_TILE_GROUPS);

			for (QuaxTile[] tileArray : neighbours) {
				for (QuaxTile tile : tileArray) {
					if (tile.tileExists() && tile.isSameColour(colour)
							&& tileNotMemberOfGroup(nearbyGroups, tile)) {

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



	public QuaxTile[][] getNeighbours(QuaxCoordinate centreCoord) {
		NeighbourFinder nf = new NeighbourFinder(this, centreCoord);
		return nf.getCoordinateNeighbours();
	}

	public List<QuaxTile> getNeighboursList(QuaxTile centreTile) {
		LinkedList<QuaxTile> result = new LinkedList<>();

		for (QuaxTile[] arr : getNeighbours(centreTile.getCoordinates())) {
			for (QuaxTile n : arr) {
				if (n.tileExists()) {
					result.add(n);
				}
			}
		}

		return result;
	}
	
	public QuaxTile[][] getSquareOctagonNeighbours(Octagon o) {
		NeighbourFinder nf = new NeighbourFinder(this, o.getCoordinates());
		return nf.getSquareOctagonNeighbours();
	}

	/*
	 * Handle searching for neighbours
	 */
	private static class NeighbourFinder {

		private final QuaxBoard searchBoard;
		private final QuaxCoordinate searchCoordinate;

		private NeighbourFinder(QuaxBoard board, QuaxCoordinate coordinate) {
			this.searchBoard = board;
			this.searchCoordinate = coordinate;
		}

		private QuaxTile[][] getCoordinateNeighbours() {
			QuaxTile[][] neighbours;

			if (searchCoordinate.isOctagon()) {
				neighbours = getOctagonNeighbours();
			}
			else {
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

				adjTiles[1] = searchBoard.octagonGrid[plusX][searchCoordinate.y()];

				if (plusY <= NUM_RHOMBUSES) {
					adjTiles[2] = searchBoard.getRhombus(searchCoordinate.x(), searchCoordinate.y());
				}
			}

			return adjTiles;
		}



		private QuaxTile[] createOutOfBoundsRow() {
			return new QuaxTile[] { QuaxTile.OUT_OF_BOUNDS_TILE,
									QuaxTile.OUT_OF_BOUNDS_TILE,
									QuaxTile.OUT_OF_BOUNDS_TILE };
		}
		
		private QuaxTile[] createOutOfBoundsRowWithHiddenCentre() {
			return new QuaxTile[] { QuaxTile.OUT_OF_BOUNDS_TILE,
									QuaxTile.HIDDEN_TILE,
									QuaxTile.OUT_OF_BOUNDS_TILE };
		}


		// TODO - Definitely need a comment
		private QuaxTile[][] getSquareOctagonNeighbours() {
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
			}
			else {
				array = createOutOfBoundsRow();
			}
			
			int y = searchCoordinate.y() + verticalOffset;

			if (y >= 0 && y < NUM_OCTAGONS) {
				for (int i = -1; i <= 1; i++) {
					int x = searchCoordinate.x() + i;
					if (x >= 0 && x < NUM_OCTAGONS) {
						array[i+1] = searchBoard.getOctagon(x, y);
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
