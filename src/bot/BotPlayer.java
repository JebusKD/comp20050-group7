package bot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Vector;
import java.util.function.IntUnaryOperator;

import javafx.application.Platform;
import model.QuaxBoard;
import player.QuaxPlayer;
import types.*;

public abstract class BotPlayer extends QuaxPlayer {
	
	protected static final int IGNORE_VALUE = Integer.MIN_VALUE;
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
	
	protected static LinkedList<QuaxCoordinate> findImmediateWins(QuaxBoard b) {
		LinkedList<QuaxCoordinate> winners = new LinkedList<>();
		for (QuaxTile t : b) {
			if (t.isFree()) {
				QuaxBoard tempCopy = new QuaxBoard(b);
				
				tempCopy.makeMove(t.getCoordinates());
				
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
	
	protected void setAll(QuaxBoard board, int value) {
		for (QuaxTile tile : board) {
			if (board.validMove(tile.getCoordinates(), this.getColour())) {
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
	
	protected QuaxBoard getSubmissionBoard() {
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
	
	protected static StrategyOperation centerSprawl(int size, IntUnaryOperator operation) {
		return new SprawlStrategyOperation(new QuaxCoordinate(5, 5, true), size, operation);
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
		
		public static Set<QuaxCoordinate> unionTargets(StrategyOperation o1, StrategyOperation o2) {
			Set<QuaxCoordinate> result = new HashSet<QuaxCoordinate>();
			result.addAll(o1.getTargets());
			result.addAll(o2.getTargets());
			return result;
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
	
	protected static abstract class AbstractStrategyOperation implements StrategyOperation {
		
		public void execute(QuaxBoard b) {
			if (b == null) throw new IllegalArgumentException("Input board cannot be null.");
			if (getTargets() == null) throw new NullPointerException("Targets set cannot be null at execution.");
			if (getOperation() == null) throw new NullPointerException("Operation cannot be null at execution.");
			
			for (QuaxCoordinate c : getTargets()) {
				QuaxTile t = b.getTile(c);
				t.setStrategyValue( getOperation().applyAsInt(t.getStrategyValue()) );
			}
		}

	}
	
	protected static class SimpleStrategyOperation extends AbstractStrategyOperation {
		private HashSet<QuaxCoordinate> targets;
		private IntUnaryOperator operation;
		
		public SimpleStrategyOperation(Collection<QuaxCoordinate> targets) {
			this.targets = new HashSet<QuaxCoordinate>();
			this.targets.addAll(targets);
		}
		
		public SimpleStrategyOperation(Collection<QuaxCoordinate> targets, IntUnaryOperator operation) {
			this(targets);
			this.operation = operation;
		}

		@Override
		public Set<QuaxCoordinate> getTargets() {
			return targets;
		}

		@Override
		public IntUnaryOperator getOperation() {
			return operation;
		}
	}
	
	protected static class OctagonLineStrategyOperation extends AbstractStrategyOperation {
		private HashSet<QuaxCoordinate> targets;
		private QuaxCoordinate[] points;
		private int width;
		private IntUnaryOperator operation;
		
		public OctagonLineStrategyOperation(Collection<QuaxCoordinate> points, int width, IntUnaryOperator operation) {
			this.targets = new HashSet<QuaxCoordinate>();
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
			targets.clear();
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
			targets.add(new QuaxCoordinate(x, y, true));
			for (int i = 1; i < width; i++) {
				if (QuaxCoordinate.validOctagonCoordinates(x, y+i))
						targets.add(new QuaxCoordinate(x, y+i, true));
				if (QuaxCoordinate.validOctagonCoordinates(x, y-i))
					targets.add(new QuaxCoordinate(x, y-i, true));
			}
		}
		
		private void buildHorizontalRow(int x, int y) {
			targets.add(new QuaxCoordinate(x, y, true));
			for (int i = 1; i < width; i++) {
				if (QuaxCoordinate.validOctagonCoordinates(x+i, y))
						targets.add(new QuaxCoordinate(x+i, y, true));
				if (QuaxCoordinate.validOctagonCoordinates(x-i, y))
					targets.add(new QuaxCoordinate(x-i, y, true));
			}
		}
		
		private boolean arePointsMisaligned() {
			return points[0].x() != points[1].x() && points[0].y() != points[1].y();
		}
		
		public boolean arePointsHorizontal() {
			return points[0].x() != points[1].x();
		}
		
		public Set<QuaxCoordinate> getTargets() {
			return this.targets;
		}
		
		public IntUnaryOperator getOperation() {
			return this.operation;
		}
		
		public void setOperation(IntUnaryOperator operation) {
			this.operation = operation;
		}
	}
	
	protected static class SprawlStrategyOperation extends AbstractStrategyOperation {

		private HashSet<QuaxCoordinate> targets;
		private IntUnaryOperator operation;
		private int size;
		private QuaxCoordinate center;
		
		public SprawlStrategyOperation(QuaxCoordinate center, int size, IntUnaryOperator operation) {
			
			this.targets = new HashSet<QuaxCoordinate>();
			this.center = center;
			setSize(size);
			setOperation(operation);
			recalculateOperation();
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
		
		public void setOperation(IntUnaryOperator operation) {
			this.operation = operation;
		}
		
		private void recalculateOperation() {
			int prevCount = 1;
			targets.clear();
			targets.add(center);
			for (int i = 0; i < size; i++) {
				
				Set<QuaxCoordinate> copy = new HashSet<QuaxCoordinate>(targets);
				
				for (QuaxCoordinate c : copy) {
					targetNeighbours(c);
				}
				
				if (prevCount == targets.size()) break;
				prevCount = targets.size();
			}
		}
		
		private void targetNeighbours(QuaxCoordinate c) {
			for (QuaxCoordinate n : c.getNeighbouringCoordinates()) {
				targets.add(n);
			}
		}
		
		@Override
		public Set<QuaxCoordinate> getTargets() {
			return this.targets;
		}

		@Override
		public IntUnaryOperator getOperation() {
			return this.operation;
		}
	}
	
	protected static class DotStrategyOperation extends AbstractStrategyOperation {
		private HashSet<QuaxCoordinate> targets;
		private IntUnaryOperator operation;
		private QuaxCoordinate center;
		
		public DotStrategyOperation(QuaxCoordinate center, IntUnaryOperator operation) {
			
			this.targets = new HashSet<QuaxCoordinate>();
			setCenter(center);
			setOperation(operation);
			
		}
		
		private void recalculateOperation() {
			targets.clear();
			targets.add(center);
		}
		
		public void setOperation(IntUnaryOperator operation) {
			this.operation = operation;
		}
		
		public void setCenter(QuaxCoordinate center) {
			this.center = center;
			recalculateOperation();
		}
		
		@Override
		public Set<QuaxCoordinate> getTargets() {
			return this.targets;
		}

		@Override
		public IntUnaryOperator getOperation() {
			return this.operation;
		}
	}
}
