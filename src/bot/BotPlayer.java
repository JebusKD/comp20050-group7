package bot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Set;
import java.util.Vector;
import java.util.function.IntUnaryOperator;

import model.QuaxBoard;
import player.QuaxPlayer;
import types.*;

public abstract class BotPlayer extends QuaxPlayer {
	
	public static final int IGNORE_VALUE = Integer.MIN_VALUE;
	protected static final Random RNG = new Random();
	private static final long MIN_THINKING_TIME = 1000;
	private static final long MAX_THINKING_TIME = 9000;
	
	private boolean interrupt;
	private QuaxBoard submissionBoard;
	private boolean wantsPieRule;
	private long startThinkingTime;
	//private List<BotStrategyElement> strategyElements;

	public BotPlayer() {
		super();
	}
	
	protected abstract void computeMove();
	
	/*
	 * Chooses the best move out of the bot's current submissionBoard
	 * by greatest strategy value and submits it. If there is a tie
	 * between two cells with the greatest strategy values, chooses
	 * one at random.
	 */
	private void decideMove() {
		
		if (wantsPieRule && submissionBoard.isPieRuleValid()) {
			while (!isInterrupted() && thinkingDelayed());
			submitPieRule();
		} else {
			ArrayList<QuaxCoordinate> candidateMoves = new ArrayList<>();
			int maxVal = Integer.MIN_VALUE;
			
			for (QuaxTile tile : submissionBoard) {
				if (tile.isFree() && submissionBoard.validMove(tile.getCoordinates())) {
					int stratVal = tile.getStrategyValue();
					if (stratVal > maxVal) {
						candidateMoves.clear();
						maxVal = stratVal;
					}
					if (stratVal == maxVal) {
						candidateMoves.add(tile.getCoordinates());
					}
				}
			}
			
			int index = Math.abs(RNG.nextInt()) % candidateMoves.size();
			
			while (!isInterrupted() && thinkingDelayed());
			submitMove(candidateMoves.get(index));
		}
			
	}
	
	protected static boolean attemptImmediateWin(QuaxBoard board) {
		List<QuaxCoordinate> winners = findImmediateWins(board);
		if (!winners.isEmpty()) {
			setAll(board, 0);
	        setCoordinatesStrategy(board, winners, 100);
	        return true;
		}
		return false;
	}
	
	protected static LinkedList<QuaxCoordinate> findImmediateWins(QuaxBoard b) {
		LinkedList<QuaxCoordinate> winners = new LinkedList<>();
		for (QuaxTile t : b) {
			if (t.isFree()) {
				QuaxBoard tempCopy = new QuaxBoard(b);
				
				tempCopy.attemptMakeMove(t.getCoordinates());
				
				if (tempCopy.checkForWinningMove())
					winners.add(t.getCoordinates());
			}
		}
		return winners;
	}
	
	protected static void setCoordinatesStrategy(QuaxBoard b, List<QuaxCoordinate> coords, int value) {
		for (QuaxCoordinate c : coords) {
			b.getTile(c).setStrategyValue(value);
		}
	}
	
	protected static void setAll(QuaxBoard board, int value) {
		for (QuaxTile tile : board) {
			if (board.validMove(tile.getCoordinates())) {
                tile.setStrategyValue(value);
			}
			else {
                tile.setStrategyValue(IGNORE_VALUE);
			}
		}
	}
	
	protected void doPieRule() {
		this.wantsPieRule = true;
	}
	
	public void interrupt() {
		this.interrupt = true;
	}
	
	public boolean isInterrupted() {
		return this.interrupt || System.currentTimeMillis() - startThinkingTime >= MAX_THINKING_TIME;
	}
	
	public QuaxBoard getSubmissionBoard() {
		return this.submissionBoard;
	}
	
	private boolean thinkingDelayed() {
		return System.currentTimeMillis() - startThinkingTime < MIN_THINKING_TIME;
	}
	
	@Override
	public void movePrompt(QuaxBoard b) {
		this.startThinkingTime = System.currentTimeMillis();
		this.interrupt = false;
		this.wantsPieRule = false;
		this.submissionBoard = new QuaxBoard(b);
		this.getExecutor().execute(() -> {
			computeMove();
			decideMove();
		});
	}

	public static Vector<QuaxCoordinate> twoOctagonVectorPoints(int x1, int y1, int x2, int y2) {
		Vector<QuaxCoordinate> result = new Vector<>(2);
		result.add(new QuaxCoordinate(x1, y1, true));
		result.add(new QuaxCoordinate(x2, y2, true));
		return result;
	}
	
	protected static int homogeneousNeighbours(QuaxBoard board, QuaxCoordinate center) {
		 int countBlack = 0;
		 int countWhite = 0;
		 
		 for (QuaxTile[] nArray : board.neighbours(center)) {
			 for (QuaxTile n : nArray) {
				 switch (n.getColour()) {
				 case BLACK:
					 countBlack++;
					 break;
				 case WHITE:
					 countWhite++;
					 break;
				 case NONE:
					 break;
				 }
			 }
		 }
		 
		 return Math.max(countWhite, countBlack);
	}
	
	// Adds IGNORE_VALUE to any free rhombus on the board that is useless.
	// "Useless" if surrounded by 3 or 4 tiles of a given colour.
	protected static void avoidUselessRhombuses(QuaxBoard board) {
		List<QuaxCoordinate> uselessRhombuses = new LinkedList<QuaxCoordinate>();
		
		Iterator<QuaxTile> iterator = board.rhombusIterator();
		
		while (iterator.hasNext()) {
			QuaxCoordinate coord = iterator.next().getCoordinates();
			
			if (homogeneousNeighbours(board, coord) >= 3) {
				uselessRhombuses.add(coord);
			}
		}
		
		SimpleStrategyOperation.simpleExecution(
				board,
				uselessRhombuses,
				(_) -> IGNORE_VALUE);
	}
	
	protected static StrategyOperation centerSprawl(int size, IntUnaryOperator operation) {
		return new SprawlStrategyOperation(new QuaxCoordinate(5, 5, true), size, operation);
	}
	
	protected static StrategyOperation boardOperation() {
		return new BoardStrategyOperation(null);
	}
	
	protected static int weakRhombusCount(QuaxBoard board) {
		int count = 0;
		Iterator<QuaxTile> iterator = board.rhombusIterator();
		
		while (iterator.hasNext()) {
			QuaxCoordinate coord = iterator.next().getCoordinates();
			
			if (board.isValidForBoth(coord)) {
				count++;
			}
		}
		return count;
	}
	
	protected static class BoardPermutations implements Iterable<QuaxBoard> {
		private final ArrayList<QuaxBoard> boards;
		
		public BoardPermutations(QuaxBoard original) {
			this.boards = new ArrayList<>(221);
			
			for (QuaxTile t : original) {
				if (original.validMove(t)) {
					QuaxBoard newBoard = new QuaxBoard(original);
					newBoard.makeMove(t);
					boards.add(newBoard);
				}
			}
		}
		
		public int size() {
			return boards.size();
		}
		
		public QuaxBoard get(int i) {
			return boards.get(i);
		};
		
		@Override
		public Iterator<QuaxBoard> iterator() {
			return new BoardPermuter(this);
		}
		
		public static class BoardPermuter implements Iterator<QuaxBoard> {

			private final BoardPermutations permutations;
			private int cursor;
			
			public BoardPermuter(BoardPermutations permutations) {
				if ((this.permutations = permutations) == null) throw new IllegalArgumentException("Cannot be constructed with null permutations.");
				this.cursor = 0;
			}
			
			@Override
			public boolean hasNext() {
				return cursor < permutations.size();
			}

			@Override
			public QuaxBoard next() {
				if (!hasNext()) throw new NoSuchElementException("No more elements in iteration.");
				return permutations.get(cursor++);
			}
			
		}
	}
	
	protected static class TileCounts {
		private final int blackCount;
		private final int whiteCount;
		private final int freeCount;
		
		public TileCounts(QuaxBoard b, Set<QuaxCoordinate> targets) {
			
			int blackCount = 0;
			int whiteCount = 0;
			int freeCount = 0;
			
			for (QuaxCoordinate c : targets) {
				switch (b.getTileColour(c)) {
				case BLACK:
					blackCount++;
					break;
				case WHITE:
					whiteCount++;
					break;
				case NONE:
					freeCount++;
					break;
				}
			}
			
			this.blackCount = blackCount;
			this.whiteCount = whiteCount;
			this.freeCount = freeCount;
		}
		
		public int getBlackCount() {
			return this.blackCount;
		}
		
		public int getWhiteCount() {
			return this.whiteCount;
		}
		
		public int getOccupiedCount() {
			return blackCount + whiteCount;
		}
		
		public int getFreeCount() {
			return this.freeCount;
		}
		
		public int getTotalCount() {
			return getOccupiedCount() + getFreeCount();
		}
	}
	
	protected static interface StrategyOperation {
		public Set<QuaxCoordinate> getTargets();
		public IntUnaryOperator getOperation();
		public void execute(QuaxBoard b);
		public StrategyOperation invertTargets();
		
		public default StrategyOperation auraClone(QuaxCoordinate c) {
			throw new UnsupportedOperationException("Not supported as an aura.");
		}
		
		public default TileCounts contents(QuaxBoard b) {
			return new TileCounts(b, getTargets());
		}
		
		public default int blackContents(QuaxBoard b) {
			return this.contents(b).getBlackCount();
		}
		
		public default int whiteContents(QuaxBoard b) {
			return this.contents(b).getWhiteCount();
		}
		
		public default int occupiedContents(QuaxBoard b) {
			return this.contents(b).getOccupiedCount();
		}
		
		public default int freeContents(QuaxBoard b) {
			return this.contents(b).getFreeCount();
		}
		
		public default int totalContents() {
			return this.contents(new QuaxBoard()).getTotalCount();
		}
		
		// TODO Get rid of unused/unneeded set operations
		public static Set<QuaxCoordinate> unionTargets(Collection<QuaxCoordinate> o1, Collection<QuaxCoordinate> o2) {
			Set<QuaxCoordinate> result = new HashSet<QuaxCoordinate>();
			result.addAll(o1);
			result.addAll(o2);
			return result;
		}
		
		public static Set<QuaxCoordinate> unionTargets(Collection<QuaxCoordinate> o1, StrategyOperation o2) {
			return unionTargets(o1, o2.getTargets());
		}
		
		public static Set<QuaxCoordinate> unionTargets(StrategyOperation o1, Collection<QuaxCoordinate> o2) {
			return unionTargets(o1.getTargets(), o2);
		}
		
		public static Set<QuaxCoordinate> unionTargets(StrategyOperation o1, StrategyOperation o2) {
			return unionTargets(o1.getTargets(), o2.getTargets());
		}
		
		public static Set<QuaxCoordinate> intersectTargets(StrategyOperation o1, StrategyOperation o2) {
			HashSet<QuaxCoordinate> result = new HashSet<QuaxCoordinate>();
			result.addAll(o1.getTargets());
			result.retainAll(o2.getTargets());
			return result;
		}
		
		public static Set<QuaxCoordinate> differenceTargets(StrategyOperation o1, StrategyOperation o2) {
			HashSet<QuaxCoordinate> result = new HashSet<QuaxCoordinate>();
			result.addAll(o1.getTargets());
			result.removeAll(o2.getTargets());
			return result;
		}
		
		public static SimpleStrategyOperation unionTargets(StrategyOperation o1, StrategyOperation o2, IntUnaryOperator op) {
			return new SimpleStrategyOperation(unionTargets(o1, o2), op);
		}
		
		public static SimpleStrategyOperation intersectTargets(StrategyOperation o1, StrategyOperation o2, IntUnaryOperator op) {
			return new SimpleStrategyOperation(intersectTargets(o1, o2), op);
		}
		
		public static SimpleStrategyOperation differenceTargets(StrategyOperation o1, StrategyOperation o2, IntUnaryOperator op) {
			return new SimpleStrategyOperation(differenceTargets(o1, o2), op);
		}
	}
	
	// TODO See if AbstractStrategyOperation can do more.
	protected static abstract class GeneralAbstractStrategyOperation implements StrategyOperation {
		
		private HashSet<QuaxCoordinate> targets;
		private IntUnaryOperator operation;
		
		public GeneralAbstractStrategyOperation() {
			this.targets = new HashSet<QuaxCoordinate>();
			this.operation = null;
		}
		
		public final void execute(QuaxBoard b) {
			if (b == null) throw new IllegalArgumentException("Input board cannot be null.");
			if (getTargets() == null) throw new NullPointerException("Targets set cannot be null at execution.");
			if (getOperation() == null) throw new NullPointerException("Operation cannot be null at execution.");
			
			for (QuaxCoordinate c : getTargets()) {
				QuaxTile t = b.getTile(c);
				if (t.getStrategyValue() != IGNORE_VALUE)
					t.setStrategyValue( getOperation().applyAsInt(t.getStrategyValue()) );
			}
		}
		
		@Override
		public final StrategyOperation invertTargets() {
			return new SimpleStrategyOperation(StrategyOperation.differenceTargets(boardOperation(), this), getOperation());
		}
		
		@Override
		public Set<QuaxCoordinate> getTargets() {
			return this.targets;
		}
		
		protected void targetsAddIfValidOctagon(int x, int y) {
			if (QuaxCoordinate.validOctagonCoordinates(x, y))
				targetsAdd(new QuaxCoordinate(x, y, true));
		}
		
		protected void targetsClear() {
			this.targets.clear();
		}
		
		protected int targetsSize() {
			return targets.size();
		}
			
		protected void targetsAdd(QuaxCoordinate c) {
			targets.add(c);
		}
		
		protected void targetsAddAll(Collection<QuaxCoordinate> c) {
			targets.addAll(c);
		}
		
		@Override
		public IntUnaryOperator getOperation() {
			return this.operation;
		}
		
		protected void setOperation(IntUnaryOperator operation) {
			this.operation = operation;
		}

	}
	
	protected static class SimpleStrategyOperation extends GeneralAbstractStrategyOperation {
		
		public SimpleStrategyOperation(Collection<QuaxCoordinate> targets) {
			super();
			targetsAddAll(targets);
		}
		
		public SimpleStrategyOperation(Collection<QuaxCoordinate> targets, IntUnaryOperator operation) {
			this(targets);
			setOperation(operation);
		}
		
		// Creates and immediately executes a simple strategy operation.
		public static void simpleExecution(QuaxBoard board, Collection<QuaxCoordinate> targets, IntUnaryOperator operation) {
			new SimpleStrategyOperation(
					targets,
					operation
					).execute(board);
		}

	}
	
	protected static class OctagonSquareStrategyOperation extends GeneralAbstractStrategyOperation {
		private int size;
		private QuaxCoordinate center;
		
		public OctagonSquareStrategyOperation(QuaxCoordinate center, int size, IntUnaryOperator operation) {
			super();
			if (size < 0 || size > 10) throw new IllegalArgumentException("Input size must be in the range [0, 10], inclusive.");
			this.size = size;
			setCenter(center);
			setOperation(operation);
		}
		
		@Override
		public StrategyOperation auraClone(QuaxCoordinate c) {
			return new OctagonSquareStrategyOperation(c, size, null);
		}
		
		public void setCenter(QuaxCoordinate center) {
			this.center = center;
			recalculateOperation();
		}
		
		public void setSize(int size) {
			if ((this.size = size) < 0 || (size > 10)) throw new IllegalArgumentException("Input size must be in the range [0, 10], inclusive.");
			recalculateOperation();
		}
		
		private void recalculateOperation() {
			targetsClear();
			if (center.isOctagonMove()) {
				targetsAdd(center);
				for (int i = 1; i < size; i++) {
					addCornersOctagonCenter(i);
					for (int j = -1; j <= 1; j += 2) {
						addHorizontalSideOctagonCenter(i, j);
						addVerticalSideOctagonCenter(i, j);
					}
				}
			}
		}
		
		private void addCornersOctagonCenter(int deviation) {
			for (int i = 0; i < 4; i++) {
				int x = center.x() + ((i % 2) == 0 ?  -deviation : deviation),
					y = center.y() + ((i / 2) == 0 ?  -deviation : deviation);
				targetsAddIfValidOctagon(x, y);
			}
		}
		
		private void addHorizontalSideOctagonCenter(int deviation, int side) {
			int y = center.y() + (side * deviation);
			targetsAddIfValidOctagon(center.x(), y);
			for (int i = 1; i < size; i++) {
				targetsAddIfValidOctagon(center.x() + i, y);
				targetsAddIfValidOctagon(center.x() - i, y);
			}
		}
		
		private void addVerticalSideOctagonCenter(int deviation, int side) {
			int x = center.x() + (side * deviation);
			targetsAddIfValidOctagon(x, center.y());
			for (int i = 1; i < size; i++) {
				targetsAddIfValidOctagon(x, center.y() + i);
				targetsAddIfValidOctagon(x, center.y() - i);
			}
		}
		
	}
	
	protected static class OctagonLineStrategyOperation extends GeneralAbstractStrategyOperation {
		private QuaxCoordinate[] points;
		private int width;
		
		public OctagonLineStrategyOperation(Collection<QuaxCoordinate> points, int width, IntUnaryOperator operation) {
			super();
			this.points = new QuaxCoordinate[2];
			if ((this.width = width) <= 0 || (width > 10)) throw new IllegalArgumentException("Input width must be in the range [0, 10], inclusive.");
			
			setPoints(points);
			setOperation(operation);
		}
		
		public void setPoints(Collection<QuaxCoordinate> points) {
			if (points == null) throw new IllegalArgumentException("Input points array cannot be null.");
			else if (points.size() != 2) throw new IllegalArgumentException("Input points array must contain exactly two coordinates.");
			
			int index = 0;
			for (QuaxCoordinate p : points) {
				if (p.isRhombusMove()) throw new IllegalArgumentException("Input points cannot contain a rhombic tile's coordinate.");
				this.points[index++] = p;
			}
			
			if (this.points[0].equals(this.points[1])) throw new IllegalArgumentException("Input points cannot be the same.");
			else if (arePointsMisaligned()) throw new IllegalArgumentException("Input points must form a vertical or horizontal line on the board.");
			
			recalculateOperation();
		}
		
		public void setWidth(int width) {
			if ((this.width = width) <= 0 || (width > 10)) throw new IllegalArgumentException("Input width must be in the range [0, 10], inclusive.");
			recalculateOperation();
		}
		
		private void recalculateOperation() {
			targetsClear();
			boolean flag = false;
			int direction,
				fixed;
			if (arePointsHorizontal()) {
				direction = points[0].x() < points[1].x() ? 1 : -1;
				fixed = points[0].y();
				for (int i = points[0].x(); !flag; i += direction) {
					if (i == points[1].x()) flag = true;
					buildVerticalRow(i, fixed);
				}
			} else {
				direction = points[0].y() < points[1].y() ? 1 : -1;
				fixed = points[0].x();
				for (int i = points[0].y(); !flag; i += direction) {
					if (i == points[1].y()) flag = true;
					buildHorizontalRow(fixed, i);
				}
			}
		}
		
		private void buildVerticalRow(int x, int y) {
			targetsAdd(new QuaxCoordinate(x, y, true));
			for (int i = 1; i < width; i++) {
				targetsAddIfValidOctagon(x, y+i);
				targetsAddIfValidOctagon(x, y-i);
			}
		}
		
		private void buildHorizontalRow(int x, int y) {
			targetsAdd(new QuaxCoordinate(x, y, true));
			for (int i = 1; i < width; i++) {
				targetsAddIfValidOctagon(x+i, y);
				targetsAddIfValidOctagon(x-i, y);
			}
		}
		
		private boolean arePointsMisaligned() {
			return points[0].x() != points[1].x() && points[0].y() != points[1].y();
		}
		
		public boolean arePointsHorizontal() {
			return points[0].x() != points[1].x();
		}
	}
	
	protected static class SprawlStrategyOperation extends GeneralAbstractStrategyOperation {

		private int size;
		private QuaxCoordinate center;
		
		public SprawlStrategyOperation(QuaxCoordinate center, int size, IntUnaryOperator operation) {
			
			super();
			this.center = center;
			setSize(size);
			setOperation(operation);
			recalculateOperation();
		}
		
		@Override
		public StrategyOperation auraClone(QuaxCoordinate c) {
			return new SprawlStrategyOperation(c, size, null);
		}
		
		public void setSize(int size) {
			if (size < 0) throw new IllegalArgumentException("Size of operation cannot be negative.");
			this.size = size;
			recalculateOperation();
		}
		
		public void setCenter(QuaxCoordinate center) {
			this.center = center;
			recalculateOperation();
		}

		private void recalculateOperation() {
			int prevCount = 1;
			targetsClear();
			targetsAdd(center);
			for (int i = 0; i < size; i++) {
				
				Set<QuaxCoordinate> copy = new HashSet<QuaxCoordinate>(getTargets());
				
				for (QuaxCoordinate c : copy) {
					targetNeighbours(c);
				}
				
				if (prevCount == targetsSize()) break;
				prevCount = targetsSize();
			}
		}
		
		private void targetNeighbours(QuaxCoordinate c) {
			for (QuaxCoordinate n : c.getNeighbouringCoordinates()) {
				targetsAdd(n);
			}
		}
		
	}
	
	protected static class DotStrategyOperation extends GeneralAbstractStrategyOperation {
		private QuaxCoordinate center;
		
		public DotStrategyOperation(QuaxCoordinate center, IntUnaryOperator operation) {
			
			super();
			setCenter(center);
			setOperation(operation);
			
		}
		
		@Override
		public StrategyOperation auraClone(QuaxCoordinate c) {
			return new DotStrategyOperation(c, null);
		}
		
		private void recalculateOperation() {
			targetsClear();
			targetsAdd(center);
		}
		
		public void setCenter(QuaxCoordinate center) {
			this.center = center;
			recalculateOperation();
		}
	}
	
	/*
	 * Aura Strategy Operation takes the basis and runs the 'aura' for
	 * every member of the basis.
	 */
	protected static class AuraStrategyOperation extends GeneralAbstractStrategyOperation {
		private int sprawlSize;
		private HashSet<QuaxCoordinate> basis;
		private StrategyOperation aura;
		
		public AuraStrategyOperation(StrategyOperation basis, StrategyOperation aura, IntUnaryOperator operation) {
			this(basis.getTargets(), aura, operation);
		}
		
		public AuraStrategyOperation(Collection<QuaxCoordinate> basis, StrategyOperation aura, IntUnaryOperator operation) {
			super();
			if ((this.sprawlSize) < 0) throw new IllegalArgumentException("Size of operation cannot be negative.");
			if ((this.aura = aura) == null) throw new IllegalArgumentException("null aura cannot be provided.");
			
			setBasis(basis);
			setOperation(operation);
		}
		
		public void setAura(StrategyOperation aura) {
			if ((this.aura = aura) == null) throw new IllegalArgumentException("null aura cannot be provided.");
			else recalculateOperation();
		}
		
		public void setBasis(StrategyOperation basis) {
			setBasis(basis.getTargets());
		}
		
		public void setBasis(Collection<QuaxCoordinate> basis) {
			if (basis.size() == 0) throw new IllegalArgumentException("Basis of operation cannot be empty.");
			this.basis = new HashSet<>(basis);
			recalculateOperation();
		}
		
		private void recalculateOperation() {
			targetsClear();
			
			Set<QuaxCoordinate> result = new HashSet<QuaxCoordinate>();
			for (QuaxCoordinate c : basis) {
				result = StrategyOperation.unionTargets(result, aura.auraClone(c));
			}
			this.targetsAddAll(result);			
		}
	}
	
	/*
	 * Selects targets from colours on the current board state.
	 */
	protected static class ColourStrategyOperation extends GeneralAbstractStrategyOperation {
		private QuaxTileColour colour;
		private QuaxBoard board;
		
		public ColourStrategyOperation(QuaxBoard board, QuaxTileColour colour, IntUnaryOperator operation) {
			super();
			this.colour = colour;
			setBoard(board);
			setOperation(operation);
			
		}
		
		public void setColour(QuaxTileColour colour) {
			this.colour = colour;
			recalculateOperation();
		}
		
		public void setBoard(QuaxBoard board) {
			if ((this.board = board) == null) throw new IllegalArgumentException("board cannot be null.");
			recalculateOperation();
		}
		
		private void recalculateOperation() {
			targetsClear();
			for (QuaxTile t : board) {
				if (t.getColour() == this.colour) {
					targetsAdd(t.getCoordinates());
				}
			}
		}
	}
	
	protected static class BoardStrategyOperation extends GeneralAbstractStrategyOperation {
		public BoardStrategyOperation(IntUnaryOperator operation) {
			super();
			populateTargets();
			setOperation(operation);
		}
		
		private void populateTargets() {
			populateOctagonRow(0);
			for (int y = 0; y < 10; ) {
				populateRhombusRow(y);
				populateOctagonRow(++y);
			}
		}
		
		private void populateOctagonRow(int y) {
			for (int x = 0; x < 11; x++) {
				targetsAdd(new QuaxCoordinate(x, y, true));
			}
		}
		
		private void populateRhombusRow(int y) {
			for (int x = 0; x < 10; x++) {
				targetsAdd(new QuaxCoordinate(x, y, false));
			}
		}
	}
}
