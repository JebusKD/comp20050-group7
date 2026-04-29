package bot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Vector;
import java.util.function.IntUnaryOperator;

import model.QuaxBoard;
import types.QuaxCoordinate;
import types.QuaxTile;
import types.QuaxTileColour;


/*
 * Rudimentary bot that will keep in mind a path and will focus
 * on building that path. It will keep distance with the other player
 * where possible.
 * 
 * Tries to expand close to existing path.
 * If opponent is near the path, it will reinforce it.
 * If opponent blocks the path, it redirects itself.
 * 
 *
 */
public class PathBot extends BotPlayer {
	private HashSet<Path> paths;
	
	public PathBot() {
		super();
		paths = new HashSet<Path>();
	}

	protected boolean opening() {
		int moveNumber = getSubmissionBoard().getMoveNumber();
		/*if (moveNumber == 0) {
			// Starting as black and need to consider starting location.
			setAll(getSubmissionBoard(), 0);
			// Place centrally.
			centerSprawl(2, (_) -> 1).execute(getSubmissionBoard());
		}
		else if (moveNumber == 1) {
			// Starting as white and need to consider the pie rule.
			
			// If opponent has placed centrally, use the pie rule.
			if (centerSprawl(2, null).blackContents(getSubmissionBoard()) == 1) {
				doPieRule();
			}
			else {
				setAll(getSubmissionBoard(), 1);
				// Otherwise, place centrally but away from opponent.
				new AuraStrategyOperation(
						new ColourStrategyOperation(getSubmissionBoard(), QuaxTileColour.BLACK, null),
						centerSprawl(3, null),
						(_) -> -1)
				.execute(getSubmissionBoard());
				centerSprawl(2, (prevValue) -> prevValue + 1)
				.execute(getSubmissionBoard());
			}
		
		} else if (moveNumber == 2 && getColour() == QuaxTileColour.WHITE) {
			// We started as black, opponent used pie rule, starting location.
			
		}
		else return false;
		*/
		
		if (moveNumber <= 1) {
			getSubmissionBoard().setCoordinateStrategyValue(new QuaxCoordinate(3, 4, true), 100);
		}
		if (moveNumber <= 3) {
			getSubmissionBoard().setCoordinateStrategyValue(new QuaxCoordinate(1, 4, true), 100);
		}
		else {
			return false;
		}
		
		return true;
	}

	@Override
	protected void computeMove() {

		if (!attemptImmediateWin(getSubmissionBoard()) && !opening()) {
				standardTurn();
				System.out.println(paths);
		}
	}
	
	private void standardTurn() {
		setAll(getSubmissionBoard(), 5);
		avoidUselessRhombuses(getSubmissionBoard());
		
		modifyVulnerableRhombuses((prevValue) -> prevValue + 10);
		modifyWeakRhombuses((prevValue) -> prevValue + 40);
		modifyDefendableRhombuses((prevValue) -> prevValue - 20);
		modifyUnnecesaryRhombuses((prevValue) -> prevValue - 2);
		modifyReinforceWeakness((prevValue) -> prevValue + 100);
		
		recalculatePaths();
		
		extendOurPaths((prevValue, distance, curSize) -> prevValue + (prevValue * (curSize + distance)));
		shrinkEnemyPaths((prevValue, distance, curSize) -> prevValue + (prevValue * (curSize + distance)));
	}
	
	public HashSet<Path> getPaths() {
		return this.paths;
	}
	
	private void recalculatePaths() {
		this.paths.clear();
		this.paths.addAll(Path.calculatePaths(new QuaxBoard(getSubmissionBoard())));
	}
	
	private void extendOurPaths(IntTernaryOperator op) {
		HashMap<QuaxCoordinate, Integer> extendingMoves = new HashMap<QuaxCoordinate, Integer>();
		
		Iterator<QuaxCoordinate> iterator = new QuaxBoard.QuaxBoardValidCoordinateIterator(getSubmissionBoard());
		
		while (iterator.hasNext()) {
			QuaxCoordinate c = iterator.next();
			int newValue = getStrategyValueOfExtendingMove(c, op);
			if (newValue != IGNORE_VALUE)
				getSubmissionBoard().setCoordinateStrategyValue(c, newValue);
		}
		
	}
	
	private void shrinkEnemyPaths(IntTernaryOperator op) {
		//op.applyAsInt(3, 2, 1);
	}
	
	private void modifyVulnerableRhombuses(IntUnaryOperator op) {
		List<QuaxCoordinate> endangeredRhombuses = new LinkedList<QuaxCoordinate>();
		
		Iterator<QuaxTile> iterator = getSubmissionBoard().rhombusIterator();
		
		while (iterator.hasNext()) {
			QuaxCoordinate coord = iterator.next().getCoordinates();
			
			if (getSubmissionBoard().isValidForBoth(coord)) {
				endangeredRhombuses.add(coord);
			}
		}
		
		SimpleStrategyOperation.simpleExecution(
				getSubmissionBoard(),
				endangeredRhombuses,
				op);
	}
	
	private void modifyDefendableRhombuses(IntUnaryOperator op) {
		List<QuaxCoordinate> strongTiles = new LinkedList<QuaxCoordinate>();
		int currentWeaknesses = weakRhombusCount( getSubmissionBoard() );
		
		for ( QuaxBoard b : new BoardPermutations(getSubmissionBoard()) ) {
			if (weakRhombusCount( b ) == currentWeaknesses + 1) {
				strongTiles.add(b.previousMove());
			}
		}
		
		SimpleStrategyOperation.simpleExecution(
				getSubmissionBoard(),
				strongTiles,
				op);
	}
	
	private void modifyWeakRhombuses(IntUnaryOperator op) {
		List<QuaxCoordinate> weakTiles = new LinkedList<QuaxCoordinate>();
		int currentWeaknesses = weakRhombusCount( getSubmissionBoard() );
		
		for ( QuaxBoard b : new BoardPermutations(getSubmissionBoard()) ) {
			if (weakRhombusCount( b ) > currentWeaknesses + 1) {
				weakTiles.add(b.previousMove());
			}
		}
		
		SimpleStrategyOperation.simpleExecution(
				getSubmissionBoard(),
				weakTiles,
				op);
	}
	
	// "Unnecessary rhombus" is one that isn't being threatened.
	// It'd be better to place an adjacent octagonal tile instead.
	private void modifyUnnecesaryRhombuses(IntUnaryOperator op) {
		List<QuaxCoordinate> unnecessaryRhombuses = new LinkedList<QuaxCoordinate>();
		
		Iterator<QuaxTile> iterator = getSubmissionBoard().rhombusIterator();
		
		while (iterator.hasNext()) {
			QuaxCoordinate coord = iterator.next().getCoordinates();
			
			if (!getSubmissionBoard().isValidForBoth(coord)) {
				unnecessaryRhombuses.add(coord);
			}
		}
		
		SimpleStrategyOperation.simpleExecution(
				getSubmissionBoard(),
				unnecessaryRhombuses,
				op);
	}
	
	// Assuming we skip our turn, could our opponent make a move that
	// creates 2+ weaknesses? If so, try prevent that.
	private void modifyReinforceWeakness(IntUnaryOperator op) {
		List<QuaxCoordinate> reinforceTiles = new LinkedList<QuaxCoordinate>();
		
		QuaxBoard skippedBoard = new QuaxBoard(getSubmissionBoard());
		skippedBoard.skipTurn();
		int currentWeaknesses = weakRhombusCount( skippedBoard );
		
		for ( QuaxBoard b : new BoardPermutations(skippedBoard) ) {
			if (weakRhombusCount( b ) > currentWeaknesses + 1) {
				reinforceTiles.add(b.previousMove());
			}
		}
		
		SimpleStrategyOperation.simpleExecution(
				getSubmissionBoard(),
				reinforceTiles,
				op);
	}
	
	private boolean mergeAllPaths(Path p) {
		boolean mergesDone = false;
		for (Path m : paths) {
			if (!p.equals(m) && p.isSameColour(m) && p.tryMergePath(m)) {
				paths.remove(m);
				mergesDone = true;
			}
		}
		return mergesDone;
	}
	
	public int getStrategyValueOfExtendingMove(QuaxCoordinate c, IntTernaryOperator op) {
		int prevValue = getSubmissionBoard().getCoordinateStrategyValue(c);
		int curMax = IGNORE_VALUE;
		
		for (Path p : paths) {
			if (isPathMyColour(p)) {
				Path newPath = p.tryConnect(c, getSubmissionBoard());
				if (newPath != null) {
					int attempt = op.applyAsInt(prevValue, newPath.length, p.countTotalTiles());
					if (attempt > curMax) curMax = attempt;
				}
			}
		}
		
		return curMax;
	}
	
	public boolean isPathMyColour(Path p) {
		return p.getColour() == this.getColour();
	}
	
	public boolean isPathOpponentColour(Path p) {
		return p.getColour() == this.getOpponentColour();
	}
	
	@FunctionalInterface
	private static interface IntTernaryOperator {
		public int applyAsInt(int x, int y, int z);
	}
	
	private static class Path {
		
		private final QuaxTileColour colour;
		private QuaxBoard board;
		private final HashSet<QuaxCoordinate> tangibleTiles;
		private final HashSet<QuaxCoordinate> ghostTiles;
		private int length;
		private boolean couldConnect;
		
		private Path(QuaxTileColour colour, QuaxBoard board) {
			// TODO change to assert
			if ((this.colour = colour) == QuaxTileColour.NONE) throw new IllegalArgumentException("Cannot be constructed with no colour.");
			this.board = board;
			this.tangibleTiles = new HashSet<QuaxCoordinate>();
			this.ghostTiles = new HashSet<QuaxCoordinate>();
			this.couldConnect = true;
			this.length = 0;
		}
		
		private Path(Path original) {
			this.colour = original.colour;
			this.board = original.board;
			this.tangibleTiles = new HashSet<QuaxCoordinate>(original.tangibleTiles);
			this.ghostTiles = new HashSet<QuaxCoordinate>(original.ghostTiles);
			this.length = original.length;
			this.couldConnect = original.couldConnect;
		}
		
		public boolean couldConnect() {
			return this.couldConnect;
		}
		
		public void findConnection() {
			this.couldConnect = false;
			
			for (QuaxTile t : board) {
				if (notTangible(t) && isTileMyColour(t)) {
					Path connectionResult = tryConnect(t.getCoordinates(), board);
					if (connectionResult != null) {
						absorbPath(connectionResult);
						couldConnect = true;
					}
				}
			}
		}
		
		private static Collection<Path> calculatePaths(QuaxBoard b) {
			HashSet<Path> paths = new HashSet<Path>();
			
			for (QuaxTile t : b) {
				if (t.isOccupied()) {
					Path newPath = Path.newPath(b, t);
					while (newPath.couldConnect()) {
						newPath.findConnection();
						newPath.recalculateGhosts();
					}
					paths.add(newPath);
				}
			}
			
			return paths;
		}
		
		private void absorbPath(Path p) {
			int tangibleBefore = countTangibleTiles(),
				ghostsBefore = countGhostTiles();
			for (QuaxCoordinate c : p.tangibleTiles) {
				this.addTangibleTile(c);
			}
			for (QuaxCoordinate c : p.ghostTiles) {
				this.addGhostTile(c);
			}
			if (tangibleBefore != countTangibleTiles()
					|| ghostsBefore != countGhostTiles()) {
				this.recalculateGhosts();
			}
			this.recalculateLength();
		}
		
		private boolean tryMergePath(Path mergee) {
			boolean result = false;
			if (this.equals(mergee)) result = true;
			else if (isJoined(mergee)) {
				absorbPath(mergee);
				result = true;
			}
			//TODO trymergewithghost
			//else result = tryMergeWithGhost(mergee);
			
			return result;
		}
		
		private Path tryConnect(QuaxCoordinate c, QuaxBoard b) {
			Path path = null;
			
			QuaxBoard copyBoard = new QuaxBoard(b);
	
			if (isGhost(c) || isAdjacent(c) || canConnectWithGhost(c, b)) {
				path = copyPathAndAddTangible(c);
			} /*else {  //TODO uncomment
				path = tryFormGhost(c, );
			}*/
			return path;
		}
		
		public int getLength() {
			return this.length;
		}
		
		private void recalculateLength() {
			QuaxCoordinate[] furthestCoordinates = new QuaxCoordinate[2];
			for (int i = 0; i <= 1; i++) {
				furthestCoordinates[i] = favouredDirections().get(i).wallCoordinate();
			}
			for (QuaxCoordinate c : tangibleTiles) {
				for (int i = 0; i <= 1; i++) {
					if (favouredDirections().get(i)
						.compareDistanceFromWall(
								furthestCoordinates[i],
								c
								) < 0) {
						furthestCoordinates[i] = c;
					}
				}
			}
			
			this.length = Math.abs( favouredDirections().get(0).compareCoordinateDistance(furthestCoordinates[0], furthestCoordinates[1]) );
		}
		
		public boolean canConnectWithGhost(QuaxCoordinate c, QuaxBoard b) {
			
			if (!board.validMove(c)) return false;
			
			QuaxBoard copyBoard = new QuaxBoard(b);
			copyBoard.makeMove(c);
			
			Path copyPath = new Path(this);
			
			copyPath.board = copyBoard;
			copyPath.addTangibleTile(c);
			copyPath.recalculateGhosts();
			
			return !copyPath.isDisjoint();
		}
		
		public Path tryFormGhost(QuaxCoordinate c, QuaxBoard b) {
			
			if (/*isAdjacent(c) || */isGhostRhombus(c, board) || isHop(c, board)) {
				System.out.println("Ghost added.");
				return copyPathAndAddGhost(c);
			}
			
			return null;
		}
		
		public static boolean isGhostRhombus(QuaxCoordinate c, QuaxBoard b) {
			return c.isRhombusMove() && !b.isValidForBoth(c) && b.validMove(c);
		}
		
		public boolean isHop(QuaxCoordinate c, QuaxBoard b) {
			if (c.isRhombusMove() || b.isOccupied(c)) return false;
			
			return isStraightHop(c, b, colour);// || isDiagonalHop(c, b);
		}
		
		private static boolean isStraightHop(QuaxCoordinate c, QuaxBoard b, QuaxTileColour colour) {
			QuaxTile[][] neighbours = b.neighbours(c);
			for (int i = 0; i <= 1; i++) {
				if (colour.matchesOrIsNull(neighbours[i][1-i])
						&& colour.matchesOrIsNull(neighbours[2-i][1+i])) {
					int leftCount = 0,
						rightCount = 0;
					for (int j = 0; j <= 2; j++) {
						if (colour.matchesFlipped(neighbours[j*(1-i)][(j*i)]))
								leftCount++;
						if (colour.matchesFlipped(neighbours[(j*(1-i))+(2*i)][(j*(1-i))+(2*i)]))
								rightCount++;
					}
					return leftCount == 0 || rightCount == 0;
				}
			}
			return false;
		}
		
		public HashSet<QuaxCoordinate> unoccupiedNeighbours(QuaxCoordinate c) {
			HashSet<QuaxCoordinate> neighbours = new HashSet<QuaxCoordinate>();
			
			for (QuaxCoordinate n : c.getNeighbouringCoordinates()) {
				if (board.isFree(c)) {
					neighbours.add(c);
				}
			}
			return neighbours;
		}
		
		public void recalculateGhosts() {
			this.ghostTiles.clear();
			for (QuaxCoordinate c : tangibleTiles) {
				for (QuaxCoordinate n : unoccupiedNeighbours(c)) {
					Path newPath = tryFormGhost(n, board);
					if (newPath != null) {
						absorbPath(newPath);
					}
				}
			}
		}
		
		public boolean isAdjacent(QuaxCoordinate c) {
			for (QuaxCoordinate n : c.getNeighbouringCoordinates()) {
				if (tangibleTiles.contains(n)) return true;
			}
			return false;
		}
		
		public boolean isJoined(Path other) {
			for (QuaxCoordinate c : tangibleTiles) {
				if (other.isTangible(c) || other.isAdjacent(c)) return true;
			}
			
			return false;
		}
		
		public boolean isDisjoint() {
			boolean isConnected;
			for (QuaxCoordinate c : tangibleTiles) {
				isConnected = false;
				for (QuaxCoordinate n : c.getNeighbouringCoordinates()) {
					if (isIn(n)) {
						isConnected = true;
						break; // TODO not sure if this is allowed by his clean principles.
					}
				}
				if (!isConnected) return true;
			}
			return false;
		}
		
		public int countTangibleTiles() {
			return tangibleTiles.size();
		}
		
		public int countGhostTiles() {
			return ghostTiles.size();
		}
		
		public int countTotalTiles() {
			return countTangibleTiles() + countGhostTiles();
		}
		
		public void addTangibleTile(QuaxCoordinate c) {
			ghostTiles.remove(c);
			tangibleTiles.add(c);
			recalculateLength();
		}
		
		public QuaxTileColour getColour() {
			return this.colour;
		}
		
		public boolean isSameColour(Path other) {
			return this.getColour() == other.getColour();
		}
		
		public boolean isTileMyColourOrNull(QuaxTile t) {
			return t == null || isTileMyColour(t);
		}
		
		public boolean isTileMyColour(QuaxTile t) {
			return this.colour == t.getColour();
		}
		
		public boolean isTileOtherColour(QuaxTile t) {
			if (t == null) return false;
			else return this.colour.flip() == t.getColour();
		}
		
		public boolean sharesTangible(Path other) {
			for (QuaxCoordinate c : tangibleTiles) {
				if (other.isTangible(c))
					return true;
			}
			return false;
		}
		
		public boolean isTangible(QuaxCoordinate c) {
			return tangibleTiles.contains(c);
		}
		
		public boolean notTangible(QuaxTile t) {
			return notTangible(t.getCoordinates());
		}
		
		public boolean notTangible(QuaxCoordinate c) {
			return !isTangible(c);
		}
		
		public boolean isGhost(QuaxCoordinate c) {
			return ghostTiles.contains(c);
		}
		
		public boolean notGhost(QuaxCoordinate c) {
			return !isGhost(c);
		}
		
		public boolean isIn(QuaxCoordinate c) {
			return isTangible(c) || isGhost(c);
		}
		
		public boolean notIn(QuaxCoordinate c) {
			return !isIn(c);
		}
		
		public void addGhostTile(QuaxCoordinate c) {
			if (!tangibleTiles.contains(c)) {
				ghostTiles.add(c);
				recalculateLength();
			}
		}
		
		public Vector<Direction> favouredDirections() {
			return Direction.favouredDirections(colour);
		}
		
		public Path copyPathAndAddTangible(QuaxCoordinate c) {
			Path newPath = new Path(this);
			newPath.addTangibleTile(c);
			newPath.recalculateGhosts();
			return newPath;
		}
		
		public Path copyPathAndAddGhost(QuaxCoordinate c) {
			Path newPath = new Path(this);
			newPath.addGhostTile(c);
			return newPath;
		}
		
		public boolean contains(Path subPath) {
			return tangibleTiles.containsAll(subPath.tangibleTiles);
		}
		
		public static Path newPath(QuaxBoard b, QuaxTile t) {
			return newPath(b, t.getCoordinates());
		}
		
		public static Path newPath(QuaxBoard b, QuaxCoordinate c) {
			Path newPath = new Path(b.getTileColour(c), b);
			newPath.addTangibleTile(c);
			return newPath;
		}

		@Override
		public int hashCode() {
			return Objects.hash(colour, ghostTiles, length, tangibleTiles);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Path other = (Path) obj;
			return colour == other.colour && Objects.equals(ghostTiles, other.ghostTiles) && length == other.length
					&& Objects.equals(tangibleTiles, other.tangibleTiles);
		}

		@Override
		public String toString() {
			return "Path|Colour:"+this.colour+"|Members:"+countTotalTiles()+"|Length:"+this.length;
		}

		private static enum Direction {
			UP {
				@Override
				public Direction flip() {
					return DOWN;
				}
				
				@Override
				public Direction rotateClockwise() {
					return RIGHT;
				}
				
				@Override
				public Direction rotateCounterClockwise() {
					return LEFT;
				}

				@Override
				public int compareCoordinateDistance(QuaxCoordinate origin, QuaxCoordinate point) {
					return ((2 * point.y()) - (point.isRhombusMove() ? 1 : 0)) - ((2 * origin.y()) - (origin.isRhombusMove() ? 1 : 0));
				}
				
				@Override
				public QuaxCoordinate wallCoordinate() {
					return new QuaxCoordinate(5, 0, true);
				}
			},
			DOWN {
				@Override
				public Direction flip() {
					return UP;
				}
				
				@Override
				public Direction rotateClockwise() {
					return LEFT;
				}
				
				@Override
				public Direction rotateCounterClockwise() {
					return RIGHT;
				}
				
				@Override
				public int compareCoordinateDistance(QuaxCoordinate origin, QuaxCoordinate point) {
					return ((2 * point.y()) + (point.isRhombusMove() ? 1 : 0)) - ((2 * origin.y()) + (origin.isRhombusMove() ? 1 : 0));
				}
				
				@Override
				public QuaxCoordinate wallCoordinate() {
					return new QuaxCoordinate(5, 10, true);
				}
			},
			LEFT {
				@Override
				public Direction flip() {
					return RIGHT;
				}
				
				@Override
				public Direction rotateClockwise() {
					return UP;
				}
				
				@Override
				public Direction rotateCounterClockwise() {
					return DOWN;
				}

				@Override
				public int compareCoordinateDistance(QuaxCoordinate origin, QuaxCoordinate point) {
					return ((2 * point.x()) - (point.isRhombusMove() ? 1 : 0)) - ((2 * origin.x()) - (origin.isRhombusMove() ? 1 : 0));
				}
				
				@Override
				public QuaxCoordinate wallCoordinate() {
					return new QuaxCoordinate(0, 5, true);
				}
			},
			RIGHT {
				@Override
				public Direction flip() {
					return LEFT;
				}
				
				@Override
				public Direction rotateClockwise() {
					return DOWN;
				}
				
				@Override
				public Direction rotateCounterClockwise() {
					return UP;
				}
				
				@Override
				public int compareCoordinateDistance(QuaxCoordinate origin, QuaxCoordinate point) {
					return ((2 * point.x()) + (point.isRhombusMove() ? 1 : 0)) - ((2 * origin.x()) + (origin.isRhombusMove() ? 1 : 0));
				}
				
				@Override
				public QuaxCoordinate wallCoordinate() {
					return new QuaxCoordinate(10, 5, true);
				}
				
				
			};
			public abstract Direction flip();
			public abstract Direction rotateClockwise();
			public abstract Direction rotateCounterClockwise();
			public abstract int compareCoordinateDistance(QuaxCoordinate origin, QuaxCoordinate point);
			public abstract QuaxCoordinate wallCoordinate();
			
			public int distanceFromWall(QuaxCoordinate origin) {
				return Math.abs(compareCoordinateDistance(origin, wallCoordinate()));
			}
			
			public int compareDistanceFromWall(QuaxCoordinate c1, QuaxCoordinate c2) {
				return distanceFromWall(c1) - distanceFromWall(c2);
			}
			
			public static Vector<Direction> favouredDirections(QuaxTileColour c) {
				Vector<Direction> v = new Vector<>(2);
				switch (c) {
				case BLACK:
					v.add(UP);
					v.add(DOWN);
					break;
				case WHITE:
					v.add(LEFT);
					v.add(RIGHT);
					break;
				case NONE:
					throw new IllegalArgumentException("Colour cannot be NONE.");
				}
				return v;
			}
		}
	}
	/*
	public static void main(String[] args) {
		QuaxBoard b = new QuaxBoard();
		
		b.makeMove(new QuaxCoordinate(1, 1, true));
		b.makeMove(new QuaxCoordinate(8, 8, true));
		b.makeMove(new QuaxCoordinate(3, 1, true));
		b.makeMove(new QuaxCoordinate(8, 6, true));
		
		System.out.println("black: " + Path.isStraightHop(new QuaxCoordinate(2,1,true), b, QuaxTileColour.BLACK));
		System.out.println("white: " + Path.isStraightHop(new QuaxCoordinate(8,7,true), b, QuaxTileColour.WHITE));
	}*/
	
}
