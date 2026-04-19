package bot;

import java.util.ArrayList;
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
		int maxVal = submissionBoard.getOctagon(0, 0).getStrategyValue();
		
		for (QuaxTile tile : submissionBoard) {
			if (!tile.isFree()) {
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
		public Set<QuaxTile> getTargets();
		public IntUnaryOperator getOperation();
		public void execute(QuaxBoard b);
	}
	
	protected static abstract class AbstractStrategyOperation implements StrategyOperation {
		
		public void execute(QuaxBoard b) {
			for (QuaxTile t : getTargets()) {
				t.setStrategyValue( getOperation().applyAsInt(t.getStrategyValue()) );
			}
		}
		
	}
}
