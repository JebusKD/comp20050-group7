package player;

import java.util.ArrayList;
import java.util.Random;

import model.QuaxBoard;
import types.*;

public abstract class BotPlayer extends QuaxPlayer {

	static final int IGNORE_VALUE = Integer.MIN_VALUE;
	static final Random RNG = new Random();


	public BotPlayer() {
		super();
	}
	
	protected abstract QuaxCoordinate computeMove(QuaxBoard b);
	
	/*
	    Given a QuaxBoard b containing strategy values, chooses the move with
	    the highest strategy value and returns it. If there is a tie, chooses
	 	one move at random of the highest strategy values.
	 */
	public static QuaxCoordinate decideMove(QuaxBoard board) {
		ArrayList<QuaxCoordinate> candidateMoves = getMaxStrategyValue(board);
		
		int index = Math.abs(RNG.nextInt()) % candidateMoves.size();
		return candidateMoves.get(index);
	}

	private static ArrayList<QuaxCoordinate> getMaxStrategyValue(QuaxBoard board) {
		ArrayList<QuaxCoordinate> candidateMoves = new ArrayList<>();
		int maxVal = board.getTile(new QuaxCoordinate(0, 0, true)).getStrategyValue();

		for (QuaxTile tile : board) {
			int stratVal = tile.getStrategyValue();
			if (stratVal > maxVal) {
				candidateMoves.clear();
				maxVal = stratVal;
			}
			if (stratVal == maxVal) {
				candidateMoves.add(tile.getCoordinates());
			}
		}

		return candidateMoves;
	}
	
	public void setAll(QuaxBoard board, int value) {
		for (QuaxTile tile : board) {
			if (board.validMove(tile.getCoordinates(), this.getPlayerColour())) {
                tile.setStrategyValue(value);
			}
			else {
                tile.setStrategyValue(IGNORE_VALUE);
			}
		}
	}
	
	@Override
	public void movePrompt(QuaxBoard b) {
		this.getExecutor().execute(() -> {
			submitMove(computeMove(b));
		});
	}
}
