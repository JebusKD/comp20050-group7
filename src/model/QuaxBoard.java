package model;

import java.util.*;

import types.*;


/** Manage the game board state during the game */
public class QuaxBoard implements Iterable<QuaxTile> {

    public static final int MAX_OCTAGONS = 11;
    public static final int MAX_RHOMBUSES = 10;
	private static final int MAX_ADJACENT_TILE_GROUPS = 4;

	private final Octagon[][] octagonGrid;
	private final Rhombus[][] rhombusGrid;
	private final LinkedList<QuaxTileGroup> trackedGroups;
	private QuaxCoordinate previousMove;

	private int moveNumber;
	private boolean pieRuleDone;


	public QuaxBoard() {
		this.octagonGrid = new Octagon[MAX_OCTAGONS][MAX_OCTAGONS];
		this.rhombusGrid = new Rhombus[MAX_RHOMBUSES][MAX_RHOMBUSES];
		
		this.trackedGroups = new LinkedList<>();

		this.previousMove = null;
		this.moveNumber = 0;
		this.pieRuleDone = false;

		initialiseGrids();
	}

	// Copy constructor
	public QuaxBoard(QuaxBoard b) {
		this.octagonGrid = new Octagon[MAX_OCTAGONS][MAX_OCTAGONS];
		this.rhombusGrid = new Rhombus[MAX_RHOMBUSES][MAX_RHOMBUSES];
		
		this.trackedGroups = new LinkedList<>();

		this.previousMove = b.previousMove;
		this.moveNumber = b.moveNumber;
		this.pieRuleDone = b.pieRuleDone;

		initialiseGrids(b);
		initialiseGroups(b);
	}

	private void initialiseGrids() {
		for (int i = 0; i < MAX_OCTAGONS; i++) {
			for (int j = 0; j < MAX_OCTAGONS; j++) {
				this.octagonGrid[i][j] = new Octagon(i, j);
			}
		}

		for (int i = 0; i < MAX_RHOMBUSES; i++) {
			for (int j = 0; j < MAX_RHOMBUSES; j++) {
				this.rhombusGrid[i][j] = new Rhombus(i, j);
			}
		}
	}

	private void initialiseGrids(QuaxBoard b) {
		for (int i = 0; i < MAX_OCTAGONS; i++) {
			for (int j = 0; j < MAX_OCTAGONS; j++) {
				this.octagonGrid[i][j] = new Octagon(b.getOctagon(i, j));
			}
		}

		for (int i = 0; i < MAX_RHOMBUSES; i++) {
			for (int j = 0; j < MAX_RHOMBUSES; j++) {
				this.rhombusGrid[i][j] = new Rhombus(b.getRhombus(i, j));
			}
		}
	}

	private void initialiseGroups(QuaxBoard b) {
		for (QuaxTileGroup g : b.trackedGroups) {
			QuaxTileGroup newGroup = new QuaxTileGroup();
			GroupManager gm = new GroupManager();
			gm.trackGroup(newGroup);

			for (QuaxTile t : g) {
				newGroup.addTile(getTile(t.getCoordinates()));
			}
		}
	}


	public Octagon getOctagon(int x, int y) {
		return octagonGrid[x][y];
	}
	
	public Rhombus getRhombus(int x, int y) {
		return rhombusGrid[x][y];
	}
	
	public QuaxTile getTile(QuaxCoordinate c) {
		if (c.isOctagon()) {
			return octagonGrid[c.x()][c.y()];
		}
		else {
			return rhombusGrid[c.x()][c.y()];
		}
	}
	
	public QuaxTileColour getTileColour(QuaxCoordinate c) {
		return getTile(c).getTileColour();
	}

	public int getMoveNumber() {
		return this.moveNumber;
	}

	public QuaxCoordinate previousMove() {
		return previousMove;
	}

	private boolean isStartingMove() {
		return previousMove == null;
	}


	/* Move validation checks */
	public boolean checkForWinningMove() {
		// Cannot win on first move
		if (isStartingMove()) {
			return false;
		}

		// TODO - violates LoD?
		return previousGroup().isWinningGroup();
	}
	
	public QuaxTileColour currentColourTurn() {
		QuaxTileColour result;
		if (isStartingMove()) {
			result = QuaxTileColour.BLACK;
		} else {
			result = getTileColour( previousMove() ).flip();
		}
		return result;
	}

	private QuaxTileGroup previousGroup() {
		return getTile(previousMove).getTileGroup();
	}


	public boolean validMove(QuaxCoordinate q, QuaxTileColour colour) {
		if (!checkForWinningMove() && (q.isOctagon() || isValidRhombusPlacement(q, colour))) {
			return getTile(q).isFree();
		}

		return false;
	}
	
	public boolean validMove(QuaxCoordinate q) {
		return validMove(q, currentColourTurn());
	}

	public boolean validMove(QuaxTile t) {
		return validMove(t.getCoordinates());
	}
	
	private boolean isValidRhombusPlacement(QuaxCoordinate q, QuaxTileColour colour) {
        QuaxTile[][] n = getNeighbours(q);

        return (n[0][0].isSameColour(colour) && n[1][1].isSameColour(colour))
				|| (n[1][0].isSameColour(colour) && n[0][1].isSameColour(colour));
    }
	
	public boolean isValidRhombusForBoth(QuaxCoordinate c) {
		return isValidRhombusPlacement(c, QuaxTileColour.BLACK)
				&& isValidRhombusPlacement(c, QuaxTileColour.WHITE);
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


	public void makeMove(QuaxCoordinate q, QuaxTileColour c) {
		QuaxTile tile;
		GroupManager moveManager = new GroupManager();

		if (q.isOctagon()) {
			tile = this.octagonGrid[q.x()][q.y()];
		}
		else {
			tile = this.rhombusGrid[q.x()][q.y()];
		}

		tile.setTileColour(c);
		moveManager.assignGroup(tile);
		this.previousMove = q;
		this.moveNumber++;
	}
	
	public void makeMove(QuaxCoordinate q) {
		makeMove(q, currentColourTurn());
	}

	public void makeMove(QuaxTile t) {
		makeMove(t.getCoordinates());
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
			QuaxTile[][] neighbours = getNeighbours(newTile.getCoordinates());
			QuaxTileColour c = newTile.getTileColour();
			assert c != QuaxTileColour.NONE;

			ArrayList<QuaxTileGroup> nearGroups = getAdjacentGroups(neighbours, c);
			expandGroup(newTile, nearGroups);
		}

		private ArrayList<QuaxTileGroup> getAdjacentGroups(QuaxTile[][] neighbours, QuaxTileColour c) {
			ArrayList<QuaxTileGroup> nearbyGroups = new ArrayList<>(MAX_ADJACENT_TILE_GROUPS);

			for (QuaxTile[] tileArray : neighbours) {
				for (QuaxTile tile : tileArray) {
					if (isOwnedTile(tile, c) &&	tileNotMemberOfGroup(nearbyGroups, tile)) {
						nearbyGroups.add(tile.getTileGroup());
					}
				}
			}

			return nearbyGroups;
		}

		private boolean isOwnedTile(QuaxTile t, QuaxTileColour c) {
			return (t != null && t.isSameColour(c));
		}

		private boolean tileNotMemberOfGroup(ArrayList<QuaxTileGroup> groups, QuaxTile t) {
			return !(groups.contains(t.getTileGroup()));
		}


		private void expandGroup(QuaxTile tile, ArrayList<QuaxTileGroup> adjacentGroups) {
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



	public QuaxTile[][] getNeighbours(QuaxCoordinate q) {
		QuaxTile[][] neighbours;
		NeighbourFinder nf = new NeighbourFinder();

		if (q.isOctagon()) {
			neighbours = nf.getOctagonNeighbours(q);
		}
		else {
			neighbours = nf.getRhombusNeighbours(q);
		}

		return neighbours;
	}
	
	public QuaxTile[][] getNeighbours(QuaxTile t) {
		return getNeighbours(t.getCoordinates());
	}

	/*
	 * Handle searching for neighbours
	 */
	private class NeighbourFinder {

		private QuaxTile[][] getRhombusNeighbours(QuaxCoordinate qc) {
			Octagon[][] neighbours = new Octagon[2][2];

			neighbours[0][0] = getOctagon(qc.x(), qc.y());
			neighbours[0][1] = getOctagon(qc.x(), qc.y() + 1);
			neighbours[1][0] = getOctagon(qc.x() + 1, qc.y());
			neighbours[1][1] = getOctagon(qc.x() + 1, qc.y() + 1);

			return neighbours;
		}


		private QuaxTile[][] getOctagonNeighbours(QuaxCoordinate qc) {
			QuaxTile[][] neighbours = new QuaxTile[3][3];

			neighbours[0] = getLeftNeighbours(qc);
			neighbours[1] = getVerticalNeighbours(qc);
			neighbours[2] = getRightNeighbours(qc);

			return neighbours;
		}

		private QuaxTile[] getLeftNeighbours(QuaxCoordinate coordinate) {
			int minusX = coordinate.x() - 1, minusY = coordinate.y() - 1, plusY = coordinate.y() + 1;
			QuaxTile[] adjTiles = new QuaxTile[3];

			if (minusX >= 0) {
				if (minusY >= 0) {
					adjTiles[0] = getRhombus(minusX, minusY);
				}

				adjTiles[1] = getOctagon(minusX, coordinate.y());

				if (plusY <= MAX_RHOMBUSES) {
					adjTiles[2] = getRhombus(minusX, coordinate.y());
				}
			}

			return adjTiles;
		}

		private QuaxTile[] getRightNeighbours(QuaxCoordinate coordinate) {
			int plusX = coordinate.x() + 1, minusY = coordinate.y() - 1, plusY = coordinate.y() + 1;
			QuaxTile[] adjTiles = new QuaxTile[3];

			if (plusX <= MAX_RHOMBUSES) {
				if (minusY >= 0) {
					adjTiles[0] = rhombusGrid[coordinate.x()][minusY];
				}

				adjTiles[1] = octagonGrid[plusX][coordinate.y()];

				if (plusY <= MAX_RHOMBUSES) {
					adjTiles[2] = rhombusGrid[coordinate.x()][coordinate.y()];
				}
			}

			return adjTiles;
		}

		private QuaxTile[] getVerticalNeighbours(QuaxCoordinate coordinate) {
			int minusY = coordinate.y() - 1, plusY = coordinate.y() + 1;
			QuaxTile[] adjTiles = new QuaxTile[3];

			if (minusY >= 0) {
				adjTiles[0] = octagonGrid[coordinate.x()][minusY];
			}

			if (plusY <= MAX_RHOMBUSES) {
				adjTiles[2] = octagonGrid[coordinate.x()][plusY];
			}

			return adjTiles;
		}
	}


	/* Create an Iterable for all tiles in the board */
	public Iterator<QuaxTile> iterator() {
		return new QuaxBoardIterator(this);
	}

	private static class QuaxBoardIterator implements Iterator<QuaxTile> {
		private static final int NUM_ELEMENTS = (MAX_OCTAGONS*MAX_OCTAGONS) + (MAX_RHOMBUSES*MAX_RHOMBUSES);

		private int cursor;
		private final ArrayList<QuaxTile> elements;

		public QuaxBoardIterator(QuaxBoard source) {
			this.cursor = 0;
			this.elements = new ArrayList<>(NUM_ELEMENTS);

			for (int i = 0; i < MAX_OCTAGONS - 1 ; i++) {
				for (int j = 0; j < MAX_OCTAGONS; j++) {
					this.elements.add(source.getOctagon(i, j));
				}
				for (int j = 0; j < MAX_RHOMBUSES; j++) {
					this.elements.add(source.getRhombus(i, j));
				}
			}
			for (int j = 0; j < MAX_OCTAGONS; j++) {
				this.elements.add(source.getOctagon(MAX_OCTAGONS - 1, j));
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
	
	public static Iterator<QuaxCoordinate> rhombusCoordinateIterator() {
		return new QuaxBoardRhombusCoordinateIterator();
	}

	private static class QuaxBoardRhombusIterator implements Iterator<QuaxTile> {
		private static final int NUM_ELEMENTS = MAX_RHOMBUSES * MAX_RHOMBUSES;
		
		private int cursor;
		private final ArrayList<QuaxTile> elements;
		
		public QuaxBoardRhombusIterator(QuaxBoard source) {
			this.cursor = 0;
			this.elements = new ArrayList<>(NUM_ELEMENTS);
			
			for (int i = 0; i < MAX_RHOMBUSES; i++) {
				for (int j = 0; j < MAX_RHOMBUSES; j++) {
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
	
	public static class QuaxBoardPermutationIterator implements Iterator<QuaxBoard> {
		private final ArrayList<QuaxBoard> elements;
		private int cursor;
		
		private QuaxBoardPermutationIterator(QuaxBoard b) {
			this.elements = new ArrayList<>();
			this.cursor = 0;
			populatePermutations(b);
		}
		
		private void populatePermutations(QuaxBoard b) {
			for (QuaxTile t : b) {
				QuaxBoard copy = new QuaxBoard(b);
				QuaxCoordinate coordinate = t.getCoordinates();
				if (copy.validMove(coordinate)) {
					copy.makeMove(coordinate);
				}
				elements.addLast(copy);
			}
		}
		
		@Override
		public boolean hasNext() {
			return cursor < elements.size();
		}
		
		@Override
		public QuaxBoard next() {
			return elements.get(cursor);
		}
	}
}
