package types;

import java.util.ArrayList;

public class QuaxCoordinate {
	private final int x;
	private final int y;
	private final boolean octagonMove;
	
	// TODO Flag argument, see clean code "Error Handling"
	// Change this constructor to private and construct via static methods
	public QuaxCoordinate(int x, int y, boolean octagonMove) {
		this.x = x;
		this.y = y;
		this.octagonMove = octagonMove;
	}
	
	public int x() {
		return this.x;
	}
	
	public int y() {
		return this.y;
	}
	
	public boolean isOctagonMove() {
		return octagonMove;
	}
	
	public boolean isRhombusMove() {
		return !octagonMove;
	}
	
	public static boolean validOctagonCoordinates(int x, int y) {
		return 0 <= x && x <= 10 && 0 <= y && y <= 10;
	}
	
	public static boolean validRhombusCoordinates(int x, int y) {
		return 0 <= x && x <= 9 && 0 <= y && y <= 9;
	}
	
	public ArrayList<QuaxCoordinate> getNeighbouringCoordinates() {
		ArrayList<QuaxCoordinate> neighbours;
		if (isOctagonMove()) {
			neighbours = new ArrayList<QuaxCoordinate>(8);
			for (int i = -1; i <= 1; i += 2) {
				if (validOctagonCoordinates(x + i, y))
					neighbours.add(new QuaxCoordinate(x + i, y, true));
				if (validOctagonCoordinates(x, y + i)) 
					neighbours.add(new QuaxCoordinate(x, y + i, true));
			}
			for (int i = 0; i <= 3; i++) {
				if (validRhombusCoordinates(x - (i % 2), y - (i / 2)))
					neighbours.add(new QuaxCoordinate(x - (i % 2), y - (i / 2), true));
			}
		}
		else {
			neighbours = new ArrayList<QuaxCoordinate>(4);
			for (int i = 0; i <= 3; i++) {
				neighbours.add(new QuaxCoordinate(x + (i % 2), y + (i / 2), true));
			}
		}
		return neighbours;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		else if (o instanceof QuaxCoordinate c)
			return this.x == c.x &&
				   this.y == c.y &&
				   this.octagonMove == c.octagonMove;
		else return false;
	}
}
