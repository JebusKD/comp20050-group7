package model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import types.Octagon;
import types.QuaxCoordinate;
import types.QuaxTile;
import types.QuaxTileColour;
import types.QuaxTileGroup;
import types.Rhombus;

public class QuaxBoard implements Iterable<QuaxTile> {

	private Octagon[][] octagonGrid;
	private Rhombus[][] rhombusGrid;
	
	private QuaxCoordinate previousMove;
	
	private LinkedList<QuaxTileGroup> trackedGroups;
	
	public QuaxBoard() {
		this.octagonGrid = new Octagon[11][11];
		this.rhombusGrid = new Rhombus[10][10];
		
		this.trackedGroups = new LinkedList<QuaxTileGroup>();
		
		for (int i = 0; i < 11; i++) {
			for (int j = 0; j < 11; j++) {
				octagonGrid[i][j] = new Octagon(i, j);
			}
		}
		
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				rhombusGrid[i][j] = new Rhombus(i, j);
			}
		}
		
		this.previousMove = null;
	}
	
	// Copy constructor
	public QuaxBoard(QuaxBoard b) {
		this.octagonGrid = new Octagon[11][11];
		this.rhombusGrid = new Rhombus[10][10];
		
		this.trackedGroups = new LinkedList<QuaxTileGroup>();
		
		for (int i = 0; i < 11; i++) {
			for (int j = 0; j < 11; j++) {
				octagonGrid[i][j] = new Octagon(b.octagonGrid[i][j]);
			}
		}
		
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				rhombusGrid[i][j] = new Rhombus(b.rhombusGrid[i][j]);
			}
		}
		
		this.previousMove = b.previousMove;
		
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
		if (c.isOctagonMove()) return octagonGrid[c.x()][c.y()];
		else return rhombusGrid[c.x()][c.y()];
	}
	
	public boolean validMove(QuaxCoordinate q, QuaxTileColour t) {
		if (checkForWinningMove()) return false;

		if (q.isOctagonMove()) {
			Octagon tile = getOctagon(q.x(), q.y());
			if (tile.getColour() != QuaxTileColour.NONE) return false;
		} else {
			if(!isValidRhombusPlacement(q, t)) return false;
			Rhombus tile = getRhombus(q.x(), q.y());
			if (tile.getColour() != QuaxTileColour.NONE) return false;
		}
		return true;
	}
	
	public boolean isValidRhombusPlacement(QuaxCoordinate q, QuaxTileColour c){
        QuaxTile[][] n = neighbours(q);

        if( n[0][0].getColour() == c && n[1][1].getColour() == c){
            return true;
        }
        if( n[1][0].getColour() == c &&  n[0][1].getColour() == c){
            return true;
        }
        return false;
    }
	
	public QuaxTile[][] neighbours(QuaxCoordinate q) {
		
		QuaxTile[][] neighbours;
		if (q.isOctagonMove()) {
			neighbours = new QuaxTile[3][3];
			int minusX = q.x() - 1,
				plusX = q.x() + 1,
				minusY = q.y() - 1,
				plusY = q.y() + 1;
			if (minusX >= 0) {
				if (minusY >= 0) neighbours[0][0] = rhombusGrid[minusX][minusY];
				neighbours[0][1] = octagonGrid[minusX][q.y()];
				if (plusY <= 10) neighbours[0][2] = rhombusGrid[minusX][q.y()];
			}
			if (plusX <= 10) {
				if (minusY >= 0) neighbours[2][0] = rhombusGrid[q.x()][minusY];
				neighbours[2][1] = octagonGrid[plusX][q.y()];
				if (plusY <= 10) neighbours[2][2] = rhombusGrid[q.x()][q.y()];
			}
			if (minusY >= 0) neighbours[1][0] = octagonGrid[q.x()][minusY];
			if (plusY <= 10) neighbours[1][2] = octagonGrid[q.x()][plusY];
		}
		else {
			neighbours = new Octagon[2][2];
			int plusX = q.x() + 1,
				plusY = q.y() + 1;
			neighbours[0][0] = octagonGrid[q.x()][q.y()];
			neighbours[0][1] = octagonGrid[q.x()][plusY];
			neighbours[1][0] = octagonGrid[plusX][q.y()];
			neighbours[1][1] = octagonGrid[plusX][plusY];
		};
		return neighbours;
	}
	
	private void assignGroup(QuaxTile newTile) {
		QuaxTile[][] neighbours = neighbours(newTile.getCoordinates());
		
		QuaxTileColour c = newTile.getColour();
		if (c == QuaxTileColour.NONE) throw new IllegalArgumentException("Tile with no colour cannot be a member of a group.");
		
		ArrayList<QuaxTileGroup> nearGroups = new ArrayList<QuaxTileGroup>(4);
		
		for (QuaxTile[] t_a : neighbours) {
			for (QuaxTile t : t_a) {
				if (t != null && t.getColour().equals(c) && !(nearGroups.contains(t.getGroup())))
					nearGroups.add(t.getGroup());
			}
		}
		
		if (nearGroups.size() == 0)
			trackGroup(new QuaxTileGroup(newTile));
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
		QuaxTile t;
		if (q.isOctagonMove()) {
			t = octagonGrid[q.x()][q.y()];
		}
		else {
			t = rhombusGrid[q.x()][q.y()];
			
		}
		t.setColour(c);
		assignGroup(t);
		this.previousMove = q;
		if (checkForWinningMove())
			System.out.println("Game won!");
	}
	
	public List<QuaxTileGroup> getGroups() {
		return this.trackedGroups;
	}
	
	public boolean checkForWinningMove() {
		if (this.previousMove == null) return false;
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
			this.elements = new ArrayList<QuaxTile>(MAX_ELEMENTS);
			
			for (int i = 0; i < 10; i++) {
				for (int j = 0; j < 11; j++) {
					this.elements.add(source.getOctagon(i, j));
				}
				for (int j = 0; j < 10; j++) {
					this.elements.add(source.getRhombus(i, j));
				}
			}
			for (int j = 0; j < 11; j++) {
				this.elements.add(source.getOctagon(10, j));
			}
		}
		
		@Override
		public boolean hasNext() {
			return cursor < MAX_ELEMENTS;
		}
		
		@Override
		public QuaxTile next() {
			if (!hasNext()) throw new NoSuchElementException("No more elements in iteration.");
			return elements.get(cursor++);
		}
	}
}
