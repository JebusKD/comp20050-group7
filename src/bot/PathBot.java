package bot;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.IntUnaryOperator;

import model.QuaxBoard;
import types.QuaxCoordinate;
import types.QuaxTile;
import types.QuaxTileColour;


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
	
	}

	@Override
	protected void computeMove() {

		if (!attemptImmediateWin(getSubmissionBoard())) {
	
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
				else {
					setAll(getSubmissionBoard(), 1);
					// Otherwise, place centrally but away from opponent.
					new AuraStrategyOperation(
							new ColourStrategyOperation(getSubmissionBoard(), QuaxTileColour.BLACK, null),
							centerSprawl(3, null),
							(prevValue) -> -1)
					.execute(getSubmissionBoard());
					centerSprawl(2, (prevValue) -> prevValue + 1)
					.execute(getSubmissionBoard());
				}
			} else {
				
				standardTurn();
				
			}
		}
	}
	
	private void standardTurn() {
		setAll(getSubmissionBoard(), 0);
		defendRhombuses(10);
		exploitWeakRhombuses(40);
		avoidDefendableRhombuses(20);
	}

	private void defendRhombuses(int strength) {
		List<QuaxCoordinate> endangeredRhombuses = new LinkedList<QuaxCoordinate>();
		
		Iterator<QuaxTile> iterator = getSubmissionBoard().rhombusIterator();
		
		while (iterator.hasNext()) {
			QuaxCoordinate coord = iterator.next().getCoordinates();
			
			if (getSubmissionBoard().isValidForBoth(coord)) {
				endangeredRhombuses.add(coord);
			}
		}
		
		SimpleStrategyOperation.simpleExecution(
				getSubmissionBoard(),
				endangeredRhombuses,
				(prevValue) -> prevValue + strength);
	}
	
	private void avoidDefendableRhombuses(int strength) {
		List<QuaxCoordinate> strongTiles = new LinkedList<QuaxCoordinate>();
		int currentWeaknesses = weakRhombusCount( getSubmissionBoard() );
		
		for ( QuaxBoard b : new BoardPermutations(getSubmissionBoard()) ) {
			if (weakRhombusCount( b ) == currentWeaknesses + 1) {
				strongTiles.add(b.previousMove());
			}
		}
		
		SimpleStrategyOperation.simpleExecution(
				getSubmissionBoard(),
				strongTiles,
				(prevValue) -> prevValue - strength);
	}
	
	private void exploitWeakRhombuses(int strength) {
		List<QuaxCoordinate> weakTiles = new LinkedList<QuaxCoordinate>();
		int currentWeaknesses = weakRhombusCount( getSubmissionBoard() );
		
		for ( QuaxBoard b : new BoardPermutations(getSubmissionBoard()) ) {
			if (weakRhombusCount( b ) > currentWeaknesses + 1) {
				weakTiles.add(b.previousMove());
			}
		}
		
		SimpleStrategyOperation.simpleExecution(
				getSubmissionBoard(),
				weakTiles,
				(prevValue) -> prevValue - strength);
	}

}
