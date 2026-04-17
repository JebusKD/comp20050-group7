package player;

import java.util.ArrayList;
import java.util.Random;

import model.QuaxBoard;
import types.*;

public abstract class BotPlayer extends QuaxPlayer {
	
	static final int IGNORE_VALUE = Integer.MIN_VALUE;
	static final Random RNG = new Random();
	private static final long MIN_THINKING_TIME = 1000;
	private static final long MAX_THINKING_TIME = 9000;
	
	private boolean interrupt;
	private QuaxBoard submissionBoard;
	private long startThinkingTime;

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
	public void decideMove() {
		ArrayList<QuaxCoordinate> candidateMoves = new ArrayList<>();
		int maxVal = submissionBoard.getOctagon(0, 0).getStrategyValue();
		
		for (QuaxTile tile : submissionBoard) {
			int stratVal = tile.getStrategyValue();
			if (stratVal > maxVal) {
				candidateMoves.clear();
				maxVal = stratVal;
			}
			if (stratVal == maxVal) {
				candidateMoves.add(tile.getCoordinates());
			}
		}
		
		int index = Math.abs(RNG.nextInt()) % candidateMoves.size();
		
		while (!isInterrupted() && thinkingDelayed());
		submitMove(candidateMoves.get(index));
	}
	
	public void setAll(QuaxBoard board, int value) {
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
		return this.interrupt;
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
		});
	}
}
