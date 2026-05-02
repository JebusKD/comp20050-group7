package player;

import java.util.*;

import static controller.QuaxController.RNG;
import model.QuaxBoard;
import player.bothelpers.StrategyBuilder;
import types.*;


public class BotPlayer extends QuaxPlayer {

    public static final int MAX_STRATEGIES = 7;
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
        LinkedList<QuaxTile> choice = selectStrategyGroup(board.getMoveNumber());

        ArrayList<QuaxCoordinate> candidateMoves = getPotentialMoves(choice);

        int index = Math.abs(RNG.nextInt()) % candidateMoves.size();
        return candidateMoves.get(index);
	}


    // TODO - Less returns - Remove random group altogether?
    private LinkedList<QuaxTile> selectStrategyGroup(int move) {
        if (move == 0) {
            return getStrategyGroupWithValue(1);
        }

        if (getStrategyGroupWithValue(MAX_STRATEGIES).size() > 0) {
            return getStrategyGroupWithValue(MAX_STRATEGIES);
        }

        if (getStrategyGroupWithValue(MAX_STRATEGIES-1).size() > 0) {
            return getStrategyGroupWithValue(MAX_STRATEGIES-1);
        }
        
        if (getStrategyGroupWithValue(5).size() > 0) {
            return getStrategyGroupWithValue(5);
        }

        int randStrategyValue = chooseRandomStrategyValue();
        LinkedList<QuaxTile> choice = getStrategyGroupWithValue(randStrategyValue);

        while (choice.isEmpty()) {
            randStrategyValue--;
            choice = getStrategyGroupWithValue(randStrategyValue);
        }

        return choice;
    }


    /* //TODO - Decide on probabilities, are we keeping them as this? ALSO 1 return/method???
    1% chance of strat val 1
    14% chance of strat val 2
    25% chance of strat val 3
    60% chance of strat 4
    */
    private int chooseRandomStrategyValue() {
        SplittableRandom random = new SplittableRandom();
        int probability= random.nextInt(1,101);

        if (probability <= 1) {
            return 1;
        }

        if (probability <= 15) {
            return 2;
        }

        if (probability <= 40) {
            return 3;
        }

        return 4;
    }


    public LinkedList<QuaxTile> getStrategyGroupWithValue(int i) {
        assert (i <= MAX_STRATEGIES && i > 0);
        return strategyGroups.get(i - 1);
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