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
	
	public QuaxTile[][] neighbours(QuaxCoordinate q) {
		System.out.println("Neighbours called.");
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
		else return null;
		return neighbours;
	}
	
	public void makeMove(QuaxCoordinate q, QuaxTileColour c) {
		if (q.isOctagonMove()) {
			Octagon o = octagonGrid[q.x()][q.y()];
			o.setColour(c);
			for (QuaxTile[] t_a : neighbours(q)) {
				for (QuaxTile t : t_a) {
					System.out.println("tile check");
					if (t != null) t.setColour(c);
				}
			}
			
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
