package model;

import java.util.*;

import types.*;


/* Manage the game board state during the program */
public class QuaxBoard implements Iterable<QuaxTile> {

    public static final int NUM_OCTAGONS = 11;
    public static final int NUM_RHOMBUSES = 10;
	private static final int MAX_ADJACENT_TILE_GROUPS = 4;

	private final Octagon[][] octagonGrid;
	private final Rhombus[][] rhombusGrid;
	private final LinkedList<QuaxTileGroup> trackedGroups;
	private QuaxCoordinate previousMove;

	private int moveNumber;
	private boolean pieRuleDone;


	public QuaxBoard() {
		this.octagonGrid = new Octagon[NUM_OCTAGONS][NUM_OCTAGONS];
		this.rhombusGrid = new Rhombus[NUM_RHOMBUSES][NUM_RHOMBUSES];
		
		this.trackedGroups = new LinkedList<>();

		this.previousMove = null;
		this.moveNumber = 0;
		this.pieRuleDone = false;

		initialiseGrids();
	}

	public QuaxBoard(QuaxBoard copyBoard) {
		if (copyBoard == null) {
			throw new IllegalArgumentException("Cannot create board copy from null QuaxBoard.");
		}
		
		this.octagonGrid = new Octagon[NUM_OCTAGONS][NUM_OCTAGONS];
		this.rhombusGrid = new Rhombus[NUM_RHOMBUSES][NUM_RHOMBUSES];
		
		this.trackedGroups = new LinkedList<>();

		this.previousMove = copyBoard.previousMove;
		this.moveNumber = copyBoard.moveNumber;
		this.pieRuleDone = copyBoard.pieRuleDone;

		initialiseGrids(copyBoard);
		initialiseGroups(copyBoard);
	}

	private void initialiseGrids() {
		assert octagonGrid != null && rhombusGrid != null;
		
		for (int i = 0; i < NUM_OCTAGONS; i++) {
			for (int j = 0; j < NUM_OCTAGONS; j++) {
				this.octagonGrid[i][j] = new Octagon(i, j);
			}
		}

		for (int i = 0; i < NUM_RHOMBUSES; i++) {
			for (int j = 0; j < NUM_RHOMBUSES; j++) {
				this.rhombusGrid[i][j] = new Rhombus(i, j);
			}
		}
	}

	private void initialiseGrids(QuaxBoard copyBoard) {
		assert copyBoard != null &&
				octagonGrid != null && rhombusGrid != null;
		
		for (int i = 0; i < NUM_OCTAGONS; i++) {
			for (int j = 0; j < NUM_OCTAGONS; j++) {
				this.octagonGrid[i][j] = new Octagon(copyBoard.getOctagon(i, j));
			}
		}

		for (int i = 0; i < NUM_RHOMBUSES; i++) {
			for (int j = 0; j < NUM_RHOMBUSES; j++) {
				this.rhombusGrid[i][j] = new Rhombus(copyBoard.getRhombus(i, j));
			}
		}
	}

	private void initialiseGroups(QuaxBoard copyBoard) {
		assert copyBoard != null;
		
		for (QuaxTileGroup g : copyBoard.trackedGroups) {
			QuaxTileGroup newGroup = new QuaxTileGroup();
			GroupManager gm = new GroupManager();
			gm.trackGroup(newGroup);

			for (QuaxTile t : g) {
				newGroup.addTile(getTile(t.getCoordinates()));
			}
		}
	}


	public Octagon getOctagon(int x, int y) {
		assert octagonGrid != null;
		if (x < 0 || x >= NUM_OCTAGONS) {
			throw new IllegalArgumentException("getOctagon must be called for x coordinate in range [0," + NUM_OCTAGONS + "]. Was " + x + ".");
		}
		if (y < 0 || y >= NUM_OCTAGONS) {
			throw new IllegalArgumentException("getOctagon must be called for y coordinate in range [0," + NUM_OCTAGONS + "]. Was " + y + ".");
		}
		
		return this.octagonGrid[x][y];
	}
	
	public Rhombus getRhombus(int x, int y) {
		assert rhombusGrid != null;
		if (x < 0 || x >= NUM_RHOMBUSES) {
			throw new IllegalArgumentException("getRhombus must be called for x coordinate in range [0," + NUM_RHOMBUSES + "]. Was " + x + ".");
		}
		if (y < 0 || y >= NUM_RHOMBUSES) {
			throw new IllegalArgumentException("getRhombus must be called for y coordinate in range [0," + NUM_RHOMBUSES + "]. Was " + y + ".");
		}
		
		return this.rhombusGrid[x][y];
	}
	
	public QuaxTile getTile(QuaxCoordinate coord) {
		if (coord == null) {
			throw new IllegalArgumentException("getTile cannot be called for null coordiante.");
		}
		
		if (coord.isOctagon()) {
			return getOctagon(coord.x(), coord.y());
		}
		else {
			return getRhombus(coord.x(), coord.y());
		}
	}
	
	public QuaxTileColour getTileColour(QuaxCoordinate coord) {
		if (coord == null) {
			throw new IllegalArgumentException("getTileColour cannot be called for null coordiante.");
		}
		
		return getTile(coord).getTileColour();
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



	/* Following are all move validation checks */
	public boolean checkForWinningMove() {
		if (isStartingMove()) {
			return false;
		}

		return previousGroup().isWinningGroup();
	}

	private QuaxTileGroup previousGroup() {
		assert previousMove != null;
		return getTile(previousMove).getTileGroup();
	}


	public boolean validMove(QuaxCoordinate coord, QuaxTileColour colour) {
		if (coord == null) {
			throw new IllegalArgumentException("Cannot check validity of move for null coordinate.");
		}
		if (colour == null || colour == QuaxTileColour.NONE) {
			throw new IllegalArgumentException("Cannot check validity of move for " + colour + " colour.");
		}
		
		if (!checkForWinningMove() && (coord.isOctagon() || isValidRhombusPlacement(coord, colour))) {
			return getTile(coord).isFree();
		}

		return false;
	}

	public boolean validMove(QuaxTile tile) {
		if (tile == null) {
			throw new IllegalArgumentException("Cannot check validity of move for null tile.");
		}
		if (tile.tileDoesNotExist()) {
			throw new IllegalArgumentException("Cannot check validity of move for non-existing tile.");
		}
		
		return validMove(tile.getCoordinates(), currentColourTurn());
	}


	private QuaxTileColour currentColourTurn() {
		QuaxTileColour result;

		if (isStartingMove()) {
			result = QuaxTileColour.BLACK;
		}
		else {
			assert previousMove() != null;
			result = getTileColour(previousMove()).flip();
		}

		return result;
	}

	private boolean isValidRhombusPlacement(QuaxCoordinate coord, QuaxTileColour colour) {
		assert coord.isRhombus();
        QuaxTile[][] n = getNeighbours(coord);

        return (n[0][0].isSameColour(colour) && n[1][1].isSameColour(colour))
				|| (n[1][0].isSameColour(colour) && n[0][1].isSameColour(colour));
    }
	
	public boolean isValidRhombusPlacementForBothPlayers(QuaxCoordinate coord) {
		if (coord == null) {
			throw new IllegalArgumentException("Cannot check validity of rhombus placement for null coordinates.");
		}
		if (coord.isOctagon()) {
			throw new IllegalArgumentException("Cannot check validity of rhombus placement for Octagon coordinates.");
		}
		
		return isValidRhombusPlacement(coord, QuaxTileColour.BLACK)
				&& isValidRhombusPlacement(coord, QuaxTileColour.WHITE);
	}


	/* Following are all board state changing methods */
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


	public void makeMove(QuaxCoordinate coordinate, QuaxTileColour colour) {
		if (coordinate == null) {
			throw new IllegalArgumentException("Cannot make move onto null coordinate.");
		}
		if (colour != QuaxTileColour.BLACK && colour != QuaxTileColour.WHITE) {
			throw new IllegalArgumentException("Cannot make move to " + colour + " colour.");
		}
		
		QuaxTile tile;
		GroupManager moveManager = new GroupManager();

		if (coordinate.isOctagon()) {
			tile = this.octagonGrid[coordinate.x()][coordinate.y()];
		}
		else {
			tile = this.rhombusGrid[coordinate.x()][coordinate.y()];
		}

		tile.setTileColour(colour);
		moveManager.assignGroup(tile);
		this.previousMove = coordinate;
		this.moveNumber++;
	}

	public void makeMove(QuaxTile t) {
		if (t == null) {
			throw new IllegalArgumentException("Cannot make move with null tile.");
		}
		if (t.tileDoesNotExist()) {
			throw new IllegalArgumentException("Cannot make move with non-existing tile.");
		}
		
		makeMove(t.getCoordinates(), currentColourTurn());
	}
	
	public void skipTurn() {
		this.moveNumber++;
	}


	private class GroupManager {

		private void trackGroup(QuaxTileGroup g) {
			assert g != null && g.groupExists();
			
			trackedGroups.addFirst(g);
		}

		private void untrackGroup(QuaxTileGroup g) {
			assert g != null && g.groupExists();
			
			trackedGroups.remove(g);
		}


		private void assignGroup(QuaxTile newTile) {
			assert newTile != null && newTile.tileExists();
			
			QuaxTileColour c = newTile.getTileColour();
			assert c != QuaxTileColour.NONE;
			
			QuaxTile[][] neighbours = getNeighbours(newTile.getCoordinates());

			ArrayList<QuaxTileGroup> nearGroups = getAdjacentGroups(neighbours, c);
			expandLargestGroup(newTile, nearGroups);
		}


		private ArrayList<QuaxTileGroup> getAdjacentGroups(QuaxTile[][] neighbours, QuaxTileColour colour) {
			assert neighbours != null && (colour == QuaxTileColour.BLACK || colour == QuaxTileColour.WHITE);
			
			ArrayList<QuaxTileGroup> nearbyGroups = new ArrayList<>(MAX_ADJACENT_TILE_GROUPS);

			for (QuaxTile[] tileArray : neighbours) {
				for (QuaxTile tile : tileArray) {
					if (tile.tileExists() && tile.isSameColour(colour)
							&& tileNotMemberOfGroup(nearbyGroups, tile)) {

						nearbyGroups.add(tile.getTileGroup());
					}
				}
			}

			return nearbyGroups;
		}

		private boolean tileNotMemberOfGroup(ArrayList<QuaxTileGroup> groups, QuaxTile t) {
			assert groups != null && t != null && t.tileExists();
			
			return !(groups.contains(t.getTileGroup()));
		}


		private void expandLargestGroup(QuaxTile tile, ArrayList<QuaxTileGroup> adjacentGroups) {
			assert tile != null && tile.tileExists() && adjacentGroups != null;
			
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
			assert adjGroups != null;
			
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
			assert largest != null && largest.groupExists()
					&& adjacentGroups != null;
			
			for (QuaxTileGroup g : adjacentGroups) {
				if (g != largest) {
					largest.merge(g);
					untrackGroup(g);
				}
			}
		}

	}



	/* Use NeighbourFinder class for strategy and group handling */
	public QuaxTile[][] getNeighbours(QuaxCoordinate centreCoord) {
		if (centreCoord == null) {
			throw new IllegalArgumentException("Centre Coordinate cannot be null.");
		}
		
		NeighbourFinder nf = new NeighbourFinder(this, centreCoord);
		return nf.getCoordinateNeighbours();
	}

	public List<QuaxTile> getNeighboursList(QuaxTile centreTile) {
		if (centreTile == null) {
			throw new IllegalArgumentException("getNeighboursList cannot be run for null centre tile.");
		}
		if (centreTile.tileDoesNotExist()) {
			throw new IllegalArgumentException("getNeighboursList cannot be run for non-existing centre tile.");
		}
		
		LinkedList<QuaxTile> result = new LinkedList<>();

		for (QuaxTile[] arr : getNeighbours(centreTile.getCoordinates())) {
			for (QuaxTile n : arr) {
				if (n.tileExists()) {
					result.add(n);
				}
			}
		}

		return result;
	}
	
	public QuaxTile[][] getSquareOfOctagonNeighbours(Octagon o) {
		if (o == null) {
			throw new IllegalArgumentException("getSquareOfOctagonNeighbours cannot be run for null Octagon.");
		}
		
		NeighbourFinder nf = new NeighbourFinder(this, o.getCoordinates());
		return nf.getSquareOfAdjacentOctagonNeighbours();
	}


	/* Create Iterables of different types for the board,
	 * Enables easier for loops, primarily in the bot strategy
	 */
	public Iterator<QuaxTile> iterator() {
		BoardIterators bi = new BoardIterators(this);
		return bi.iterator();
	}

	public static Iterator<QuaxCoordinate> coordinateIterator() {
		return BoardIterators.coordinateIterator();
	}

	public Iterator<Octagon> octagonIterator() {
		BoardIterators bi = new BoardIterators(this);
		return bi.octagonIterator();
	}

	public static Iterator<QuaxCoordinate> rhombusCoordinateIterator() {
		return BoardIterators.rhombusCoordinateIterator();
	}
}
