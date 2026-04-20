package model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import types.*;

public class QuaxBoard implements Iterable<QuaxTile> {

    public static final int MAX_OCTAGONS = 11;
    public static final int MAX_RHOMBUSES = 10;
	private static final int ADJACENT_TILES = 4;

	private Octagon[][] octagonGrid;
	private Rhombus[][] rhombusGrid;
	
	private QuaxCoordinate previousMove;
	private int moveNumber;
	private boolean pieRuleDone;
	
	private LinkedList<QuaxTileGroup> trackedGroups;


	public QuaxBoard() {
		this.octagonGrid = new Octagon[MAX_OCTAGONS][MAX_OCTAGONS];
		this.rhombusGrid = new Rhombus[MAX_RHOMBUSES][MAX_RHOMBUSES];
		
		this.trackedGroups = new LinkedList<>();
		
		this.moveNumber = 0;
		this.pieRuleDone = false;
		this.previousMove = null;

		initialiseGrids();
	}

	private void initialiseGrids() {
		for (int i = 0; i < MAX_OCTAGONS; i++) {
			for (int j = 0; j < MAX_OCTAGONS; j++) {
				octagonGrid[i][j] = new Octagon(i, j);
			}
		}

		for (int i = 0; i < MAX_RHOMBUSES; i++) {
			for (int j = 0; j < MAX_RHOMBUSES; j++) {
				rhombusGrid[i][j] = new Rhombus(i, j);
			}
		}
	}
	
	// Copy constructor
	public QuaxBoard(QuaxBoard b) {
		this.octagonGrid = new Octagon[MAX_OCTAGONS][MAX_OCTAGONS];
		this.rhombusGrid = new Rhombus[MAX_RHOMBUSES][MAX_RHOMBUSES];
		
		this.trackedGroups = new LinkedList<>();
		
		this.moveNumber = b.moveNumber;
		this.pieRuleDone = b.pieRuleDone;
		this.previousMove = b.previousMove;

		for (int i = 0; i < MAX_OCTAGONS; i++) {
			for (int j = 0; j < MAX_OCTAGONS; j++) {
				octagonGrid[i][j] = new Octagon(b.octagonGrid[i][j]);
			}
		}

		for (int i = 0; i < MAX_RHOMBUSES; i++) {
			for (int j = 0; j < MAX_RHOMBUSES; j++) {
				rhombusGrid[i][j] = new Rhombus(b.rhombusGrid[i][j]);
			}
		}

		initialiseGroup(b);
	}

	private void initialiseGroup(QuaxBoard b) {
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
		if (c.isOctagonMove()) {
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
		return getTile(this.previousMove).getGroup().isWinningGroup();
	}

	public boolean validMove(QuaxCoordinate q, QuaxTileColour t) {
		if (!checkForWinningMove() && (q.isOctagonMove() || isValidRhombusPlacement(q, t))) {
			QuaxTile tile = getTile(q);
			return tile.getColour() == QuaxTileColour.NONE;
		}

		return false;
	}

	// TODO - Too many returns?
	public boolean isValidRhombusPlacement(QuaxCoordinate q, QuaxTileColour c) {
        QuaxTile[][] n = neighbours(q);

        if ((n[0][0].getColour() == c && n[1][1].getColour() == c)
				|| n[1][0].getColour() == c &&  n[0][1].getColour() == c ) {
            return true;
        }

        return false;
    }

	public void makeMove(QuaxCoordinate q, QuaxTileColour c) {
		QuaxTile tile;
		if (q.isOctagonMove()) {
			tile = octagonGrid[q.x()][q.y()];
		}
		else {
			tile = rhombusGrid[q.x()][q.y()];
		}
		tile.setColour(c);
		assignGroup(tile);
		this.previousMove = q;
		this.moveNumber++;
	}


	private void assignGroup(QuaxTile newTile) {
		QuaxTile[][] neighbours = neighbours(newTile.getCoordinates());

		QuaxTileColour c = newTile.getColour();
		if (c == QuaxTileColour.NONE) {
			throw new IllegalArgumentException("Tile with no colour cannot be a member of a group.");
		}

		ArrayList<QuaxTileGroup> nearGroups = getAdjacentGroups(neighbours, c);

		if (nearGroups.isEmpty()) {
			trackGroup(new QuaxTileGroup(newTile));
		}
		else {
			QuaxTileGroup largestGroup = getBiggestGroup(nearGroups);

			largestGroup.addTile(newTile);
			for (QuaxTileGroup g : nearGroups) {
				if (g != largestGroup) {
					largestGroup.merge(g);
					untrackGroup(g);
				}
			}
		}
	}

	private ArrayList<QuaxTileGroup> getAdjacentGroups(QuaxTile[][] neighbours, QuaxTileColour c) {
		ArrayList<QuaxTileGroup> nearGroups = new ArrayList<>(ADJACENT_TILES);

		for (QuaxTile[] tileArray : neighbours) {
			for (QuaxTile tile : tileArray) {
				if (tile != null && tile.getColour().equals(c) && !(nearGroups.contains(tile.getGroup()))) {
					nearGroups.add(tile.getGroup());
				}
			}
		}

		return nearGroups;
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


	public QuaxTile[][] neighbours(QuaxCoordinate q) {
		QuaxTile[][] neighbours;

		if (q.isOctagonMove()) {
			neighbours = getOctagonNeighbours(q);
		}
		else {
			neighbours = getRhombusNeighbours(q);
		}

		return neighbours;
	}

	private QuaxTile[][] getOctagonNeighbours(QuaxCoordinate qc) {
		QuaxTile[][] adjTiles = new QuaxTile[3][3];
		int minusX = qc.x() - 1, plusX = qc.x() + 1,
				minusY = qc.y() - 1, plusY = qc.y() + 1;

		if (minusX >= 0) {
			if (minusY >= 0) {
				adjTiles[0][0] = rhombusGrid[minusX][minusY];
			}
			adjTiles[0][1] = octagonGrid[minusX][qc.y()];
			if (plusY <= MAX_RHOMBUSES) {
				adjTiles[0][2] = rhombusGrid[minusX][qc.y()];
			}
		}

		if (plusX <= MAX_RHOMBUSES) {
			if (minusY >= 0) {
				adjTiles[2][0] = rhombusGrid[qc.x()][minusY];
			}
			adjTiles[2][1] = octagonGrid[plusX][qc.y()];
			if (plusY <= MAX_RHOMBUSES) {
				adjTiles[2][2] = rhombusGrid[qc.x()][qc.y()];
			}
		}

		if (minusY >= 0) {
			adjTiles[1][0] = octagonGrid[qc.x()][minusY];
		}

		if (plusY <= MAX_RHOMBUSES) {
			adjTiles[1][2] = octagonGrid[qc.x()][plusY];
		}

		return adjTiles;
	}

	private QuaxTile[][] getRhombusNeighbours(QuaxCoordinate qc) {
		Octagon[][] adjTiles = new Octagon[2][2];
		adjTiles[0][0] = octagonGrid[qc.x()][qc.y()];
		adjTiles[0][1] = octagonGrid[qc.x()][qc.y() + 1];
		adjTiles[1][0] = octagonGrid[qc.x() + 1][qc.y()];
		adjTiles[1][1] = octagonGrid[qc.x() + 1][qc.y() + 1];
		return adjTiles;
	}


	public boolean attemptPieRule() {
		if (isPieRuleValid()) {
			pieRuleDone = true;
			moveNumber++;
			return true;
		}
		return false;
	}
	
	public boolean isPieRuleValid() {
		return moveNumber == 1 && !pieRuleDone;
	}

	public Iterator<QuaxTile> iterator() {
		return new QuaxBoardIterator(this);
	}
	
	public static class QuaxBoardIterator implements Iterator<QuaxTile> {
		private static final int MAX_ELEMENTS = 221;
		
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
			if (!hasNext()) {
				throw new NoSuchElementException("No more elements in iteration.");
			}
			return elements.get(cursor++);
		}
	}
}
