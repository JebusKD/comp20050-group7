package model;

import types.Octagon;
import types.QuaxCoordinate;
import types.QuaxTile;
import types.QuaxTileColour;
import types.Rhombus;

public class QuaxBoard {

	private Octagon[][] octagonGrid;
	private Rhombus[][] rhombusGrid;
	
	private QuaxCoordinate previousMove;
	
	public QuaxBoard() {
		this.octagonGrid = new Octagon[11][11];
		this.rhombusGrid = new Rhombus[10][10];
		
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
		/* TODO this fulfils sprint 2, feature 1. */
		/* It's been temporarily disabled. Simply uncomment when needed. */
		/* if (q.isOctagonMove()) {
			Octagon tile = getOctagon(q.x(), q.y());
			if (tile.getColour() != QuaxTileColour.NONE) return false;
		} else {
			Rhombus tile = getRhombus(q.x(), q.y());
			if (tile.getColour() != QuaxTileColour.NONE) return false;
		} */
		return true;
	}
	
	public void makeMove(QuaxCoordinate q, QuaxTileColour c) {
		if (q.isOctagonMove()) {
			octagonGrid[q.x()][q.y()].setColour(c);
			
		}
		else {
			rhombusGrid[q.x()][q.y()].setColour(c);
		}
		this.previousMove = q;
	}
	
	public QuaxCoordinate previousMove() {
		return previousMove;
	}
}
