package model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import types.*;

public class QuaxBoard implements Iterable<QuaxTile> {

    public static final int MAX_OCTAGONS = 11;
    public static final int MAX_RHOMBUSES = 10;

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
		
		this.previousMove = null;
	}
	
	// Copy constructor
	public QuaxBoard(QuaxBoard b) {
		this.octagonGrid = new Octagon[MAX_OCTAGONS][MAX_OCTAGONS];
		this.rhombusGrid = new Rhombus[MAX_RHOMBUSES][MAX_RHOMBUSES];
		
		this.trackedGroups = new LinkedList<>();
		
		this.moveNumber = b.moveNumber;
		this.pieRuleDone = b.pieRuleDone;
		
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
		
		this.previousMove = b.previousMove;

		// TODO - Have initialiseGroup() method?
		for (QuaxTileGroup g : b.trackedGroups) {
			QuaxTileGroup newGroup = new QuaxTileGroup();
			this.trackGroup(newGroup);
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
		if (c.isOctagonMove()) {
			return octagonGrid[c.x()][c.y()];
		}
		else {
			return rhombusGrid[c.x()][c.y()];
		}
	}

	// TODO - Too many returns?
	public boolean validMove(QuaxCoordinate q, QuaxTileColour t) {
		if (checkForWinningMove()) {
            return false;
        }

		if (q.isOctagonMove()) {
			Octagon tile = getOctagon(q.x(), q.y());
			if (tile.getColour() != QuaxTileColour.NONE) {
                return false;
            }
		}
        else {
			if (!isValidRhombusPlacement(q, t)) {
                return false;
            }
			Rhombus tile = getRhombus(q.x(), q.y());
			if (tile.getColour() != QuaxTileColour.NONE){
                return false;
            }
		}

		return true;
	}
	
	public boolean isValidRhombusPlacement(QuaxCoordinate q, QuaxTileColour c){
        QuaxTile[][] n = neighbours(q);

        if (n[0][0].getColour() == c && n[1][1].getColour() == c) {
            return true;
        }
        if (n[1][0].getColour() == c &&  n[0][1].getColour() == c) {
            return true;
        }
        return false;
    }

    // TODO - Too long
	public QuaxTile[][] neighbours(QuaxCoordinate q) {
		
		QuaxTile[][] neighbours;
		if (q.isOctagonMove()) {
			neighbours = new QuaxTile[3][3];
			int minusX = q.x() - 1, plusX = q.x() + 1, minusY = q.y() - 1, plusY = q.y() + 1;

			if (minusX >= 0) {
				if (minusY >= 0) {
                    neighbours[0][0] = rhombusGrid[minusX][minusY];
                }
				neighbours[0][1] = octagonGrid[minusX][q.y()];
				if (plusY <= MAX_RHOMBUSES) {
                    neighbours[0][2] = rhombusGrid[minusX][q.y()];
                }
			}

			if (plusX <= MAX_RHOMBUSES) {
				if (minusY >= 0) {
                    neighbours[2][0] = rhombusGrid[q.x()][minusY];
                }
				neighbours[2][1] = octagonGrid[plusX][q.y()];
				if (plusY <= MAX_RHOMBUSES) {
                    neighbours[2][2] = rhombusGrid[q.x()][q.y()];
                }
			}

			if (minusY >= 0) {
                neighbours[1][0] = octagonGrid[q.x()][minusY];
            }

			if (plusY <= MAX_RHOMBUSES) {
                neighbours[1][2] = octagonGrid[q.x()][plusY];
            }
		}
		else {
			neighbours = new Octagon[2][2];
			neighbours[0][0] = octagonGrid[q.x()][q.y()];
			neighbours[0][1] = octagonGrid[q.x()][q.y() + 1];
			neighbours[1][0] = octagonGrid[q.x() + 1][q.y()];
			neighbours[1][1] = octagonGrid[q.x() + 1][q.y() + 1];
		}

		return neighbours;
	}

	public int getMoveNumber() {
		return this.moveNumber;
	}

    // TODO - Change neighbours variable name, also too long
	private void assignGroup(QuaxTile newTile) {
		QuaxTile[][] neighbours = neighbours(newTile.getCoordinates());
		
		QuaxTileColour c = newTile.getColour();
		if (c == QuaxTileColour.NONE) {
            throw new IllegalArgumentException("Tile with no colour cannot be a member of a group.");
        }
		
		ArrayList<QuaxTileGroup> nearGroups = new ArrayList<>(4);
		
		for (QuaxTile[] tileArray : neighbours) {
			for (QuaxTile tile : tileArray) {
				if (tile != null && tile.getColour().equals(c) && !(nearGroups.contains(tile.getGroup()))) {
					nearGroups.add(tile.getGroup());
				}
			}
		}
		
		if (nearGroups.isEmpty()) {
            trackGroup(new QuaxTileGroup(newTile));
        }
		else {
			int maxSize = -1;
			QuaxTileGroup biggestGroup = null;
			for (QuaxTileGroup g : nearGroups) {
				if (g.size() > maxSize) {
					biggestGroup = g;
					maxSize = g.size();
				}
			}

			biggestGroup.addTile(newTile);
			for (QuaxTileGroup g : nearGroups) {
				if (g != biggestGroup) {
					biggestGroup.merge(g);
					untrackGroup(g);
				}
			}
		}
	}
	
	private void trackGroup(QuaxTileGroup g) {
		this.trackedGroups.addFirst(g);
	}
	
	private void untrackGroup(QuaxTileGroup g) {
		this.trackedGroups.remove(g);
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
	
	public List<QuaxTileGroup> getGroups() {
		return this.trackedGroups;
	}
	
	public boolean checkForWinningMove() {
		if (this.previousMove == null) {
            return false;
        }
		return getTile(this.previousMove).getGroup().isWinningGroup();
	}
	
	public QuaxCoordinate previousMove() {
		return previousMove;
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
				this.elements.add(source.getOctagon(10, j));
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
