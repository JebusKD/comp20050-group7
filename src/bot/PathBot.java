package bot;

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

	protected boolean opening() {
		int moveNumber = getSubmissionBoard().getMoveNumber();
		if (moveNumber == 0) {
			// Starting as black and need to consider starting location.
			setAll(getSubmissionBoard(), 0);
			// Place centrally.
			centerSprawl(2, (_) -> 1).execute(getSubmissionBoard());
		}
		else if (moveNumber == 1) {
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
						(_) -> -1)
				.execute(getSubmissionBoard());
				centerSprawl(2, (prevValue) -> prevValue + 1)
				.execute(getSubmissionBoard());
			}
		
		} else if (moveNumber == 2 && getColour() == QuaxTileColour.WHITE) {
			// We started as black, opponent used pie rule, starting location.
			
		}
		else return false;
		
		return true;
	}
	
	@Override
	protected void computeMove() {

		if (!attemptImmediateWin(getSubmissionBoard()) && !opening()) {
				standardTurn();
		}
	}
	
	private void standardTurn() {
		setAll(getSubmissionBoard(), 5);
		avoidUselessRhombuses(getSubmissionBoard());
		
		modifyVulnerableRhombuses((prevValue) -> prevValue + 10);
		modifyWeakRhombuses((prevValue) -> prevValue + 40);
		modifyDefendableRhombuses((prevValue) -> prevValue - 20);
		modifyUnnecesaryRhombuses((prevValue) -> prevValue - 2);
		modifyReinforceWeakness((prevValue) -> prevValue + 100);
		
		paths.rebuild();
		
		modifyForwardSnares((prevValue) -> prevValue * 5);
		modifyForwardHops((prevValue) -> prevValue * 4);
		modifyForwardSteps((prevValue) -> prevValue * 2);
	}

	private void modifyVulnerableRhombuses(IntUnaryOperator op) {
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
				op);
	}
	
	private void modifyDefendableRhombuses(IntUnaryOperator op) {
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
				op);
	}
	
	private void modifyWeakRhombuses(IntUnaryOperator op) {
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
				op);
	}
	
	// "Unnecessary rhombus" is one that isn't being threatened.
	// It'd be better to place an adjacent octagonal tile instead.
	private void modifyUnnecesaryRhombuses(IntUnaryOperator op) {
		List<QuaxCoordinate> unnecessaryRhombuses = new LinkedList<QuaxCoordinate>();
		
		Iterator<QuaxTile> iterator = getSubmissionBoard().rhombusIterator();
		
		while (iterator.hasNext()) {
			QuaxCoordinate coord = iterator.next().getCoordinates();
			
			if (!getSubmissionBoard().isValidForBoth(coord)) {
				unnecessaryRhombuses.add(coord);
			}
		}
		
		SimpleStrategyOperation.simpleExecution(
				getSubmissionBoard(),
				unnecessaryRhombuses,
				op);
	}
	
	// Assuming we skip our turn, could our opponent make a move that
	// creates 2+ weaknesses? If so, try prevent that.
	private void modifyReinforceWeakness(IntUnaryOperator op) {
		List<QuaxCoordinate> reinforceTiles = new LinkedList<QuaxCoordinate>();
		
		QuaxBoard skippedBoard = new QuaxBoard(getSubmissionBoard());
		skippedBoard.skipTurn();
		int currentWeaknesses = weakRhombusCount( skippedBoard );
		
		for ( QuaxBoard b : new BoardPermutations(skippedBoard) ) {
			if (weakRhombusCount( b ) > currentWeaknesses + 1) {
				reinforceTiles.add(b.previousMove());
			}
		}
		
		SimpleStrategyOperation.simpleExecution(
				getSubmissionBoard(),
				reinforceTiles,
				op);
	}
	
	private static class Path {
		
		
		private static enum Direction {
			UP {
				@Override
				public Direction flip() {
					return DOWN;
				}
				
				@Override
				public Direction rotateClockwise() {
					return RIGHT;
				}
				
				@Override
				public Direction rotateCounterClockwise() {
					return LEFT;
				}
			},
			DOWN {
				@Override
				public Direction flip() {
					return UP;
				}
				
				@Override
				public Direction rotateClockwise() {
					return LEFT;
				}
				
				@Override
				public Direction rotateCounterClockwise() {
					return RIGHT;
				}
			},
			LEFT {
				@Override
				public Direction flip() {
					return RIGHT;
				}
				
				@Override
				public Direction rotateClockwise() {
					return UP;
				}
				
				@Override
				public Direction rotateCounterClockwise() {
					return DOWN;
				}
			},
			RIGHT {
				@Override
				public Direction flip() {
					return LEFT;
				}
				
				@Override
				public Direction rotateClockwise() {
					return DOWN;
				}
				
				@Override
				public Direction rotateCounterClockwise() {
					return UP;
				}
			};
			public abstract Direction flip();
			public abstract Direction rotateClockwise();
			public abstract Direction rotateCounterClockwise();
		}
	}

}
