package model;

import types.Octagon;
import types.Rhombus;

public class QuaxBoard {

	private Octagon[][] octagonGrid;
	private Rhombus[][] rhombusGrid;
	
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
	}
}
