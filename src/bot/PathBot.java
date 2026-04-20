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
 * If path is empty, tries to place centrally.
 * Tries to expand close to existing path.
 * If opponent is near the path, it will reinforce it.
 * If opponent blocks the path, it redirects itself.
 * 
 *
 */
public class PathBot extends BotPlayer {

	protected int pathLength;
	protected ArrayList<QuaxCoordinate> desiredPath;
	
	public PathBot() {
		super();
		
		//pathLength = 0;
	}

	@Override
	protected void computeMove() {
		setAll(getSubmissionBoard(), 1);
		StrategyOperation op = new SprawlStrategyOperation(new QuaxCoordinate(5, 5, true), 1, (prevValue) -> 3);
		op.execute(getSubmissionBoard());
	}
}
