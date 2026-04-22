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

	private void initialiseGrids(QuaxBoard b) {
		for (int i = 0; i < MAX_OCTAGONS; i++) {
			for (int j = 0; j < MAX_OCTAGONS; j++) {
				octagonGrid[i][j] = new Octagon(b.getOctagon(i, j));
			}
		}

		for (int i = 0; i < MAX_RHOMBUSES; i++) {
			for (int j = 0; j < MAX_RHOMBUSES; j++) {
				rhombusGrid[i][j] = new Rhombus(b.getRhombus(i, j));
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
		QuaxTileGroup moveGroup = getTile(previousMove).getGroup();
		return moveGroup.isWinningGroup();
	}

	public boolean validMove(QuaxCoordinate q, QuaxTileColour t) {
		if (!checkForWinningMove() && (q.isOctagon() || isValidRhombusPlacement(q, t))) {
			QuaxTile tile = getTile(q);
			return tile.getColour() == QuaxTileColour.NONE;
		}

		return false;
	}

	public boolean isValidRhombusPlacement(QuaxCoordinate q, QuaxTileColour c) {
        QuaxTile[][] n = getNeighbours(q);

        return (n[0][0].getColour() == c && n[1][1].getColour() == c)
                || n[1][0].getColour() == c && n[0][1].getColour() == c;
    }

	public QuaxTile[][] getNeighbours(QuaxCoordinate q) {
		QuaxTile[][] neighbours;
		NeighbourFinder nFinder = new NeighbourFinder();

		if (q.isOctagon()) {
			neighbours = nFinder.getOctagonNeighbours(q);
		}
		else {
			neighbours = nFinder.getRhombusNeighbours(q);
		}

		return neighbours;
	}

	public void makeMove(QuaxCoordinate q, QuaxTileColour c) {
		QuaxTile tile;
		GroupManager gm = new GroupManager();

		if (q.isOctagon()) {
			tile = octagonGrid[q.x()][q.y()];
		}
		else {
			tile = rhombusGrid[q.x()][q.y()];
		}

		tile.setColour(c);
		gm.assignGroup(tile);
		this.previousMove = q;
		this.moveNumber++;
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

	/*
	 * Handle all group tracking related methods
	 */
	private class GroupManager {
		private void assignGroup(QuaxTile newTile) {
			QuaxTile[][] neighbours = getNeighbours(newTile.getCoordinates());
			QuaxTileColour c = newTile.getColour();
			assert c != QuaxTileColour.NONE;

			ArrayList<QuaxTileGroup> nearGroups = getAdjacentGroups(neighbours, c);
			expandGroup(newTile, nearGroups);
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

	/* Find all neighbours when given a co-ordinate
	 */
	private class NeighbourFinder {
		private QuaxTile[][] getRhombusNeighbours(QuaxCoordinate qc) {
			Octagon[][] adjTiles = new Octagon[2][2];
			adjTiles[0][0] = getOctagon(qc.x(), qc.y());
			adjTiles[0][1] = getOctagon(qc.x(), qc.y() + 1);
			adjTiles[1][0] = getOctagon(qc.x() + 1, qc.y());
			adjTiles[1][1] = getOctagon(qc.x() + 1, qc.y() + 1);
			return adjTiles;
		}


		private QuaxTile[][] getOctagonNeighbours(QuaxCoordinate qc) {
			QuaxTile[][] adjTiles = new QuaxTile[3][3];

			adjTiles[0] = getLeftNeighbours(qc);
			adjTiles[1] = getVerticalNeighbours(qc);
			adjTiles[2] = getRightNeighbours(qc);

			return adjTiles;
		}

		private QuaxTile[] getLeftNeighbours(QuaxCoordinate qc) {
			QuaxTile[] leftNeighbours = new QuaxTile[3];
			int minusX = qc.x() - 1, minusY = qc.y() - 1, plusY = qc.y() + 1;

			if (minusX >= 0) {
				if (minusY >= 0) {
					leftNeighbours[0] = getRhombus(minusX, minusY);
				}
				leftNeighbours[1] = getOctagon(minusX, qc.y());
				if (plusY <= MAX_RHOMBUSES) {
					leftNeighbours[2] = getRhombus(minusX, qc.y());
				}
			}

			return leftNeighbours;
		}

		private QuaxTile[] getRightNeighbours(QuaxCoordinate qc) {
			QuaxTile[] rightNeighbours = new QuaxTile[3];
			int plusX = qc.x() + 1, minusY = qc.y() - 1, plusY = qc.y() + 1;

			if (plusX <= MAX_RHOMBUSES) {
				if (minusY >= 0) {
					rightNeighbours[0] = getRhombus(qc.x(), minusY);
				}
				rightNeighbours[1] = getOctagon(plusX, qc.y());
				if (plusY <= MAX_RHOMBUSES) {
					rightNeighbours[2] = getRhombus(qc.x(), qc.y());
				}
			}

			return rightNeighbours;
		}

		private QuaxTile[] getVerticalNeighbours(QuaxCoordinate qc) {
			QuaxTile[] vertNeighbours = new QuaxTile[3];
			int minusY = qc.y() - 1, plusY = qc.y() + 1;

			if (minusY >= 0) {
				vertNeighbours[0] = getOctagon(qc.x(), minusY);
			}

			if (plusY <= MAX_RHOMBUSES) {
				vertNeighbours[2] = getOctagon(qc.x(), plusY);
			}

			return vertNeighbours;
		}
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
			assert hasNext();
			return elements.get(cursor++);
		}
	}
}
