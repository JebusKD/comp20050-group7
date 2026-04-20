package bot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
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
	
	public void interrupt() {
		this.interrupt = true;
	}
	
	public boolean isInterrupted() {
		return this.interrupt || System.currentTimeMillis() - startThinkingTime >= MAX_THINKING_TIME;
	}
	
	protected QuaxBoard getSubmissionBoard() {
		return this.submissionBoard;
	}
	
	protected void setSubmissionBoard(QuaxBoard b) {
		this.submissionBoard = b;
	}
	
	private boolean thinkingDelayed() {
		return System.currentTimeMillis() - startThinkingTime < MIN_THINKING_TIME;
	}
	
	@Override
	public void movePrompt(QuaxBoard b) {
		this.startThinkingTime = System.currentTimeMillis();
		this.interrupt = false;
		this.submissionBoard = new QuaxBoard(b);
		this.getExecutor().execute(() -> {
			computeMove();
			decideMove();
		});
	}
	
	protected static interface StrategyOperation {
		public Set<QuaxCoordinate> getTargets();
		public IntUnaryOperator getOperation();
		public void execute(QuaxBoard b);
		
		public default Set<QuaxCoordinate> unionTargets(StrategyOperation o1, StrategyOperation o2) {
			Set<QuaxCoordinate> result = new HashSet<QuaxCoordinate>();
			result.addAll(o1.getTargets());
			result.addAll(o2.getTargets());
			return result;
		}
		
		public default Set<QuaxCoordinate> intersectTargets(StrategyOperation o1, StrategyOperation o2) {
			HashSet<QuaxCoordinate> result = new HashSet<QuaxCoordinate>();
			result.addAll(o1.getTargets());
			result.retainAll(o2.getTargets());
			return result;
		}
		
		public default Set<QuaxCoordinate> differenceTargets(StrategyOperation o1, StrategyOperation o2) {
			HashSet<QuaxCoordinate> result = new HashSet<QuaxCoordinate>();
			result.addAll(o1.getTargets());
			result.removeAll(o2.getTargets());
			return result;
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
