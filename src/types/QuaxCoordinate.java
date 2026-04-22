package types;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

import model.QuaxBoard;

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
		
		if (octagonMove && !validOctagonCoordinates(x, y))
			throw new IllegalArgumentException("QuaxCoordinate constructed with out-of-bounds points. (Octagon x: " + x + ", y: " + y + ")");
		else if (!octagonMove && !validRhombusCoordinates(x, y))
			throw new IllegalArgumentException("QuaxCoordinate constructed with out-of-bounds points. (Rhombus x: " + x + ", y: " + y + ")");
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
					neighbours.add(new QuaxCoordinate(x - (i % 2), y - (i / 2), false));
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
	
	public boolean againstWall() {
		return isOctagonMove() && (x == 0 || y == 0 || x == 10 || y == 10);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		QuaxCoordinate other = (QuaxCoordinate) obj;
		return octagonMove == other.octagonMove && x == other.x && y == other.y;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(octagonMove, x, y);
	}
	
	public static void main(String[] args) {
		System.out.println(new QuaxCoordinate(3, 3, true).equals(new QuaxCoordinate(3, 3, true)));
		System.out.println(new QuaxCoordinate(3, 4, true).equals(new QuaxCoordinate(3, 3, true)));
	}
	
	public static Iterator<QuaxCoordinate> boardCoordinateIterator() {
		return new QuaxCoordinateBoardIterator();
	}
	// TODO Repeated code, fix
	private static class QuaxCoordinateBoardIterator implements Iterator<QuaxCoordinate> {
		private static final int MAX_ELEMENTS = 221;
		
		private int cursor;
		private ArrayList<QuaxCoordinate> elements;
		
		public QuaxCoordinateBoardIterator() {
			this.cursor = 0;
			this.elements = new ArrayList<>(MAX_ELEMENTS);
			
			for (int i = 0; i < 10 ; i++) {
				for (int j = 0; j < 11; j++) {
					this.elements.add(new QuaxCoordinate(i, j, true));
				}
				for (int j = 0; j < 10; j++) {
					this.elements.add(new QuaxCoordinate(i, j, false));
				}
			}
			for (int j = 0; j < 11; j++) {
				this.elements.add(new QuaxCoordinate(10, j, true));
			}
		}
		
		@Override
		public boolean hasNext() {
			return cursor < MAX_ELEMENTS;
		}
		
		@Override
		public QuaxCoordinate next() {
			if (!hasNext()) throw new NoSuchElementException("No more elements in iteration.");
			return elements.get(cursor++);
		}
	}
}
