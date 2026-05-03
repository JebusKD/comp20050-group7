package player;

import java.util.*;

import controller.QuaxController;

import static controller.QuaxController.RNG;
import model.QuaxBoard;
import player.bothelpers.StrategyBuilder;
import player.bothelpers.StrategyValueProbabilities;
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
		strategyGroups = new ArrayList<>(StrategyValue.MAX_STRATEGIES);
        clearAllStrategyGroups();
	}

    private void clearAllStrategyGroups() {
        strategyGroups = new ArrayList<>(StrategyValue.MAX_STRATEGIES);

        for (int i = 0 ; i < StrategyValue.MAX_STRATEGIES; i++) {
        	strategyGroups.add(new LinkedList<>());
        }
    }


    /*
      Given a QuaxBoard b containing strategy values, choose a group of tiles
      with a certain strategy value. If there is a tie, chooses
      one move at random of the group.
     */
	private QuaxCoordinate decideMove(QuaxBoard board) {
        LinkedList<QuaxTile> choice = selectStrategyGroup(board.getMoveNumber());

        ArrayList<QuaxCoordinate> candidateMoves = getPotentialMoves(choice);

        int index = Math.abs(RNG.nextInt()) % candidateMoves.size();
        return candidateMoves.get(index);
	}


    // TODO - Less returns - Remove random group altogether?
    private LinkedList<QuaxTile> selectStrategyGroup(int move) {
        if (move == 0) {
            return getStrategyGroupWithValue(StrategyValue.VERY_LOW);
        }

        if (getStrategyGroupWithValue(StrategyValue.WINNING).size() > 0) {
            return getStrategyGroupWithValue(StrategyValue.WINNING);
        }

        if (getStrategyGroupWithValue(StrategyValue.OPPONENT_WINNING).size() > 0) {
            return getStrategyGroupWithValue(StrategyValue.OPPONENT_WINNING);
        }
        
        if (getStrategyGroupWithValue(StrategyValue.KEY).size() > 0) {
            return getStrategyGroupWithValue(StrategyValue.KEY);
        }

        StrategyValue randStrategyValue = StrategyValueProbabilities.randomStrategyValue();
        LinkedList<QuaxTile> choice = getStrategyGroupWithValue(randStrategyValue);

        while (choice.isEmpty()) {
            randStrategyValue = randStrategyValue.downgradeOne();
            choice = getStrategyGroupWithValue(randStrategyValue);
        }

        return choice;
    }

    private LinkedList<QuaxTile> getStrategyGroupWithValue(int i) {
        assert (i <= StrategyValue.MAX_STRATEGIES && i > 0);
        return strategyGroups.get(i - 1);
    }

    public LinkedList<QuaxTile> getStrategyGroupWithValue(StrategyValue group) {
    	return getStrategyGroupWithValue(group.toInt());
    }

    private ArrayList<QuaxCoordinate> getPotentialMoves(LinkedList<QuaxTile> stratGroup) {
        ArrayList<QuaxCoordinate> moves = new ArrayList<>();

        for (QuaxTile t : stratGroup) {
            moves.add(t.getCoordinates());
        }

        return moves;
    }


    // how the bot decides strategy vals for the tiles
    private void setUpStrategy(QuaxBoard board) {
        clearAllStrategyGroups();
        strategyBuilder.initialiseStrategy(board);
        strategyBuilder.refineStrategy(board);
    }

    public static void enableHaste() {
    	botHaste = true;
    }


	@Override
	public void movePrompt(QuaxBoard board) {
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