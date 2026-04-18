package bot;

import java.util.List;

import model.QuaxBoard;
import types.QuaxCoordinate;

public class BogoBot extends BotPlayer {

	public BogoBot() {
		super();
	}

	@Override
	protected void computeMove() {
		setAll(getSubmissionBoard(), 1);
		
		List<QuaxCoordinate> winners = findImmediateWins(getSubmissionBoard());
		if (!winners.isEmpty())
        	setCoordinatesStrategy(getSubmissionBoard(), winners, 10);
    }
}
