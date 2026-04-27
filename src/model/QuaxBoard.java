package model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import types.*;


public class QuaxBoard implements Iterable<QuaxTile> {

    public static final int MAX_OCTAGONS = 11;
    public static final int MAX_RHOMBUSES = 10;
	private static final int ADJACENT_TILES = 4;

	private Octagon[][] octagonGrid;
	private Rhombus[][] rhombusGrid;
	private LinkedList<QuaxTileGroup> trackedGroups;
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

	private void initialiseGroups(QuaxBoard b) {
		for (QuaxTileGroup g : b.trackedGroups) {
			QuaxTileGroup newGroup = new QuaxTileGroup();
			this.trackGroup(newGroup);

			for (QuaxTile t : g) {
				newGroup.addTile(getTile(t.getCoordinates()));
			}
		}
	}

	private void trackGroup(QuaxTileGroup g) {
		this.trackedGroups.addFirst(g);
	}

	private void untrackGroup(QuaxTileGroup g) {
		this.trackedGroups.remove(g);
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

	public int getMoveNumber() {
		return this.moveNumber;
	}

	public QuaxCoordinate previousMove() {
		return previousMove;
	}

	public boolean isStartingMove() {
		return previousMove == null;
	}


	public boolean checkForWinningMove() {
		// Cannot win on first move
		if (isStartingMove()) {
			return false;
		}

		// TODO - violates LoD?
		QuaxTileGroup moveGroup = getTile(previousMove).getTileGroup();
		return moveGroup.isWinningGroup();
	}

	public boolean validMove(QuaxCoordinate q, QuaxTileColour t) {
		if (!checkForWinningMove() && (q.isOctagon() || isValidRhombusPlacement(q, t))) {
			QuaxTile tile = getTile(q);
			return tile.getTileColour() == QuaxTileColour.NONE;
		}

		return false;
	}

	public boolean isValidRhombusPlacement(QuaxCoordinate q, QuaxTileColour c) {
        QuaxTile[][] n = getNeighbours(q);

        return (n[0][0].getTileColour() == c && n[1][1].getTileColour() == c)
                || n[1][0].getTileColour() == c && n[0][1].getTileColour() == c;
    }

	public void makeMove(QuaxCoordinate q, QuaxTileColour c) {
		QuaxTile tile;
		if (q.isOctagon()) {
			tile = this.octagonGrid[q.x()][q.y()];
		}
		else {
			tile = this.rhombusGrid[q.x()][q.y()];
		}
		tile.setTileColour(c);
		assignGroup(tile);
		this.previousMove = q;
		this.moveNumber++;
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

	// TODO - Move to new class?
	private void assignGroup(QuaxTile newTile) {
		QuaxTile[][] neighbours = getNeighbours(newTile.getCoordinates());
		QuaxTileColour c = newTile.getTileColour();
		assert c != QuaxTileColour.NONE;

		ArrayList<QuaxTileGroup> nearGroups = getAdjacentGroups(neighbours, c);
		expandGroup(newTile, nearGroups);
	}

	private ArrayList<QuaxTileGroup> getAdjacentGroups(QuaxTile[][] neighbours, QuaxTileColour c) {
		ArrayList<QuaxTileGroup> nearGroups = new ArrayList<>(ADJACENT_TILES);

		for (QuaxTile[] tileArray : neighbours) {
			for (QuaxTile tile : tileArray) {
				if (tile != null && tile.getTileColour().equals(c) && !(nearGroups.contains(tile.getTileGroup()))) {
					nearGroups.add(tile.getTileGroup());
				}
			}
		}

		return nearGroups;
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

	// TODO - Move to new class?
	public QuaxTile[][] getNeighbours(QuaxCoordinate q) {
		QuaxTile[][] neighbours;

		if (q.isOctagon()) {
			neighbours = getOctagonNeighbours(q);
		}
		else {
			neighbours = getRhombusNeighbours(q);
		}

		return neighbours;
	}

	private QuaxTile[][] getRhombusNeighbours(QuaxCoordinate qc) {
		Octagon[][] adjTiles = new Octagon[2][2];
		adjTiles[0][0] = octagonGrid[qc.x()][qc.y()];
		adjTiles[0][1] = octagonGrid[qc.x()][qc.y() + 1];
		adjTiles[1][0] = octagonGrid[qc.x() + 1][qc.y()];
		adjTiles[1][1] = octagonGrid[qc.x() + 1][qc.y() + 1];
		return adjTiles;
	}

	private QuaxTile[][] getOctagonNeighbours(QuaxCoordinate qc) {
		QuaxTile[][] adjTiles = new QuaxTile[3][3];

		getLeftNeighbours(qc, adjTiles);
		getRightNeighbours(qc, adjTiles);
		getVerticalNeighbours(qc, adjTiles);

		return adjTiles;
	}

	private void getLeftNeighbours(QuaxCoordinate coordinate, QuaxTile[][] adjTiles) {
		int minusX = coordinate.x() - 1, minusY = coordinate.y() - 1, plusY = coordinate.y() + 1;

		if (minusX >= 0) {
			if (minusY >= 0) {
				adjTiles[0][0] = rhombusGrid[minusX][minusY];
			}
			adjTiles[0][1] = octagonGrid[minusX][coordinate.y()];
			if (plusY <= MAX_RHOMBUSES) {
				adjTiles[0][2] = rhombusGrid[minusX][coordinate.y()];
			}
		}
	}

	private void getRightNeighbours(QuaxCoordinate coordinate, QuaxTile[][] adjTiles) {
		int plusX = coordinate.x() + 1, minusY = coordinate.y() - 1, plusY = coordinate.y() + 1;

		if (plusX <= MAX_RHOMBUSES) {
			if (minusY >= 0) {
				adjTiles[2][0] = rhombusGrid[coordinate.x()][minusY];
			}
			adjTiles[2][1] = octagonGrid[plusX][coordinate.y()];
			if (plusY <= MAX_RHOMBUSES) {
				adjTiles[2][2] = rhombusGrid[coordinate.x()][coordinate.y()];
			}
		}
	}

	private void getVerticalNeighbours(QuaxCoordinate coordinate, QuaxTile[][] adjTiles) {
		int minusY = coordinate.y() - 1, plusY = coordinate.y() + 1;
		if (minusY >= 0) {
			adjTiles[1][0] = octagonGrid[coordinate.x()][minusY];
		}

		if (plusY <= MAX_RHOMBUSES) {
			adjTiles[1][2] = octagonGrid[coordinate.x()][plusY];
		}
	}


	public Iterator<QuaxTile> iterator() {
		return new QuaxBoardIterator(this);
	}
	
	public static class QuaxBoardIterator implements Iterator<QuaxTile> {
		private static final int MAX_ELEMENTS = (MAX_OCTAGONS*MAX_OCTAGONS) + (MAX_RHOMBUSES*MAX_RHOMBUSES);
		
		private int cursor;
		private ArrayList<QuaxTile> elements;
		
		public QuaxBoardIterator(QuaxBoard source) {
			this.cursor = 0;
			this.elements = new ArrayList<>(MAX_ELEMENTS);
			
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
			return cursor < MAX_ELEMENTS;
		}
		
		@Override
		public QuaxTile next() {
			assert hasNext();
			return elements.get(cursor++);
		}
	}
}
