package player;

import java.util.*;

import static controller.QuaxController.RNG;
import model.QuaxBoard;
import player.bothelpers.*;
import static types.StrategyValue.*;
import types.*;


public class BotPlayer extends QuaxPlayer {

	// TODO for final submission MIN_THINKING_TIME will need to be upped to the 3-5 second range (Confirm)
	private static final long MIN_THINKING_TIME = 1000;
	private static boolean botHaste = false;

    private final StrategyBuilder strategyBuilder;
    private ArrayList<LinkedList<QuaxTile>> strategyGroups;


	public BotPlayer() {
		super();
		strategyBuilder = new StrategyBuilder(this);
		strategyGroups = new ArrayList<>(MAX_STRATEGIES);
        clearAllStrategyGroups();
	}

    private void clearAllStrategyGroups() {
        strategyGroups = new ArrayList<>(MAX_STRATEGIES);

        for (int i = 0 ; i < MAX_STRATEGIES; i++) {
        	strategyGroups.add(new LinkedList<>());
        }
    }


    /*
      Given a QuaxBoard b containing strategy values, choose a group of tiles
      with a certain strategy value. If there is a tie, chooses
      one move at random of the group.
     */
	private QuaxCoordinate decideMove(QuaxBoard board) {
        assert board != null && strategyGroups != null;
        LinkedList<QuaxTile> choice = selectStrategyGroup(board);

        ArrayList<QuaxCoordinate> candidateMoves = getPotentialMoves(choice);

        int index = Math.abs(RNG.nextInt()) % candidateMoves.size();
        return candidateMoves.get(index);
	}


    private LinkedList<QuaxTile> selectStrategyGroup(QuaxBoard board) {
        assert strategyGroups != null && strategyBuilder != null;
        LinkedList<QuaxTile> choice = checkPrioritisedStrategyGroups(board);

        if (choice.isEmpty()) {
            StrategyValue randStrategyValue = strategyBuilder.getRandomStrategyValue();
            choice = getStrategyGroupWithValue(randStrategyValue);

            while (choice.isEmpty()) {
                randStrategyValue = randStrategyValue.downgradeOne();
                choice = getStrategyGroupWithValue(randStrategyValue);
            }
        }

        return choice;
    }

    private LinkedList<QuaxTile> checkPrioritisedStrategyGroups(QuaxBoard board) {
        LinkedList<QuaxTile> strategyGroup = new LinkedList<>();

        if (board.isStartingMove()) {
            strategyGroup = getStrategyGroupWithValue(VERY_LOW);
        }

        if (getStrategyGroupWithValue(WINNING).size() > 0) {
            strategyGroup = getStrategyGroupWithValue(WINNING);
        }

        if (getStrategyGroupWithValue(OPPONENT_WINNING).size() > 0) {
            strategyGroup = getStrategyGroupWithValue(OPPONENT_WINNING);
        }

        if (getStrategyGroupWithValue(KEY).size() > 0) {
            strategyGroup = getStrategyGroupWithValue(KEY);
        }

        return strategyGroup;
    }


    public LinkedList<QuaxTile> getStrategyGroupWithValue(StrategyValue group) {
       	if (group == null) {
       		throw new IllegalArgumentException("null cannot be passed as StrategyValue parameter.");
       	}
       	if (group == StrategyValue.IGNORE) {
       		throw new IllegalArgumentException("Cannot get strategy group for the IGNORE values.");
       	}

        assert (group.toInt() <= MAX_STRATEGIES && group.toInt() > 0);
        return strategyGroups.get(group.toInt() - 1);
    }


    private ArrayList<QuaxCoordinate> getPotentialMoves(LinkedList<QuaxTile> stratGroup) {
    	assert stratGroup != null;

        ArrayList<QuaxCoordinate> moves = new ArrayList<>();

        for (QuaxTile t : stratGroup) {
            moves.add(t.getCoordinates());
        }

        return moves;
    }


    // how the bot decides strategy vals for the tiles
    private void setUpStrategy(QuaxBoard board) {
    	assert board != null && strategyBuilder != null;

        clearAllStrategyGroups();
        strategyBuilder.initialiseStrategy(board);
        strategyBuilder.refineStrategy(board);
    }


    public static void enableHaste() {
    	botHaste = true;
    }


	@Override
	public void movePrompt(QuaxBoard board) {
		if (board == null) {
			throw new IllegalArgumentException("Passed board cannot be null.");
		}
		if (board.checkForWinningMove()) {
			throw new IllegalStateException("Bot shouldn't be prompted for move after the game has ended.");
		}

        long startThinkingTime = System.currentTimeMillis();
		
        this.getExecutor().execute(() -> {
            setUpStrategy(board);
        	QuaxCoordinate move = decideMove(board);
        	
        	while (!botHaste && System.currentTimeMillis() - startThinkingTime < MIN_THINKING_TIME) {
                ;
            }
            submitMove(move);
        });
	}
}