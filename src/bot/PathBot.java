package bot;

import java.util.ArrayList;
import java.util.Collection;
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
		if (moveNumber == 0) {
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
		
		return true;
	}
	
	@Override
	protected void computeMove() {

		if (!attemptImmediateWin(getSubmissionBoard()) && !opening()) {
				standardTurn();
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
	
	private void recalculatePaths() {
		this.paths.clear();
		this.paths.addAll(calculatePaths(new QuaxBoard(getSubmissionBoard())));
	}

	private static Collection<Path> calculatePaths(QuaxBoard b) {
		HashSet<Path> paths = new HashSet<Path>();
		
		for (QuaxTile t : b) {
			if (t.isOccupied()) {
				Path newPath = Path.newPath(b, t);
				while (newPath.couldConnect()) {
					newPath.findConnection();
				}
				paths.add(newPath);
			}
		}
	}
	
	private void extendOurPaths(IntTernaryOperator op) {
		op.applyAsInt(3, 2, 1);
	}
	
	private void shrinkEnemyPaths(IntTernaryOperator op) {
		op.applyAsInt(3, 2, 1);
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
				if (notTangible(t) && isMyColour(t)) {
					ArrayList<Path> connectionResults = tryConnect(t.getCoordinates());
					if (connectionResults.size() > 0) {
						absorbConnection(connectionResults.get(0));
						couldConnect = true;
					}
				}
			}
		}
		
		private void absorbConnection(Path p) {
			for (QuaxCoordinate c : p.tangibleTiles) {
				this.addTangibleTile(c);
			}
			for (QuaxCoordinate c : p.ghostTiles) {
				this.addGhostTile(c);
			}
		}
		
		private HashSet<Path> tryConnect(QuaxCoordinate c) {
			HashSet<Path> newPaths = new HashSet<Path>();
			if (ghostTiles.contains(c) || isAdjacent(c)) {
				newPaths.add( copyPathAndAdd(c) );
			} else {
				newPaths.addAll( tryFormGhosts(c) );
			}
			return newPaths;
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
						.compareCoordinateDistance(
								furthestCoordinates[i],
								c)
						> 0) {
						furthestCoordinates[i] = c;
					}
				}
			}
			
			this.length = Math.abs( favouredDirections().get(0).compareCoordinateDistance(furthestCoordinates[0], furthestCoordinates[0]) );
		}
		
		public ArrayList<Path> tryFormGhosts(QuaxCoordinate c) {
			
		}
		
		public boolean isAdjacent(QuaxCoordinate c) {
			for (QuaxCoordinate n : c.getNeighbouringCoordinates()) {
				if (tangibleTiles.contains(n)) return true;
			}
			return false;
		}
		
		public HashSet<QuaxCoordinate> adjacentGhosts(QuaxCoordinate c) {
			HashSet<QuaxCoordinate> ghosts = new HashSet<>();
			for (QuaxCoordinate n : c.getNeighbouringCoordinates()) {
				if (ghostTiles.contains(n)) ghosts.add(n);
			}
			return ghosts;
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
		
		public boolean isMyColour(QuaxTile t) {
			return t.getColour() == colour;
		}
		
		public boolean notTangible(QuaxTile t) {
			return notTangible(t.getCoordinates());
		}
		
		public boolean notTangible(QuaxCoordinate c) {
			return !tangibleTiles.contains(c);
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
		
		public Path copyPathAndAdd(QuaxCoordinate c) {
			Path newPath = new Path(this);
			newPath.addTangibleTile(c);
			return newPath;
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
				public int index() {
					return 0;
				}
				
				@Override
				public int compareCoordinateDistance(QuaxCoordinate origin, QuaxCoordinate point) {
					return ((2 * point.x()) - (point.isRhombusMove() ? 1 : 0)) - ((2 * origin.x()) - (origin.isRhombusMove() ? 1 : 0));
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
				public int index() {
					return 2;
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
				public int index() {
					return 3;
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
				public int index() {
					return 1;
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
			public abstract int index();
			public abstract int compareCoordinateDistance(QuaxCoordinate origin, QuaxCoordinate point);
			public abstract QuaxCoordinate wallCoordinate();
			
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

}
