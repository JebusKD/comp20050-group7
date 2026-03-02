package player;

import java.util.ArrayList;
import java.util.Random;

import javafx.stage.Stage;
import model.QuaxBoard;
import types.QuaxCoordinate;
import types.QuaxTile;
import types.QuaxTileColour;

public abstract class BotPlayer extends QuaxPlayer {
	
	static final Random RNG = new Random();

	public BotPlayer(QuaxTileColour colour, Stage stage) {
		super(colour, stage);
	}
	
	protected abstract QuaxCoordinate computeMove(QuaxBoard b);
	
	/* Given a QuaxBoard b containing strategy values, chooses the move with
	 	the highest strategy value and returns it. If there is a tie, chooses
	 	one move at random of the highest strategy values.
	 */
	public static QuaxCoordinate decideMove(QuaxBoard b) {
		ArrayList<QuaxCoordinate> candidateMoves = new ArrayList<QuaxCoordinate>();
		int maxVal = b.getOctagon(0, 0).getStrategyValue();
		
		for (QuaxTile t : b) {
			int stratVal = t.getStrategyValue();
			if (stratVal > maxVal) {
				candidateMoves.clear();
				maxVal = stratVal;
			}
			if (stratVal == maxVal) {
				candidateMoves.add(t.getCoordinates());
			}
		}
		
		int index = Math.abs(RNG.nextInt()) % candidateMoves.size();
		return candidateMoves.get(index);
		
	}
	
	@Override
	public void movePrompt(QuaxBoard b) {
		Thread t = new Thread(() -> {
			submitMove(computeMove(new QuaxBoard(b)));
		});
		t.start();
	}
	
}
