package bot;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntUnaryOperator;

import model.QuaxBoard;
import types.QuaxCoordinate;


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
	
	public PathBot() {
		super();
		
		//pathLength = 0;
	}

	@Override
	protected void computeMove() {
	
		if (getSubmissionBoard().getMoveNumber() == 0) {
			// Starting as black and need to consider starting location.
			setAll(getSubmissionBoard(), 0);
			// Place centrally.
			centerSprawl(2, (prevValue) -> 1).execute(getSubmissionBoard());
		}
		else if (getSubmissionBoard().getMoveNumber() == 1) {
			// Starting as white and need to consider the pie rule.
			
			// If opponent has placed centrally, use the pie rule.
			if (centerSprawl(2, null).blackContents(getSubmissionBoard()) == 1) {
				doPieRule();
			}
		}
		
	}
}
