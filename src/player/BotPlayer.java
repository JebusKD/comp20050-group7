package player;

import java.util.*;

import static controller.QuaxController.RNG;
import model.QuaxBoard;
import types.*;


public class BotPlayer extends QuaxPlayer {
	private static boolean botHaste = false;

    public static final int MAX_STRATEGIES = 6;
	private static final long MIN_THINKING_TIME = 1000;

    private QuaxTileStrategyGroup[] strategyGroups;
    
    private long startThinkingTime;


	public BotPlayer() {
		super();
		strategyGroups = new QuaxTileStrategyGroup[MAX_STRATEGIES];
        clearAllStrategyGroups();
	}

    private void clearAllStrategyGroups() {
        for (int i = 0; i < MAX_STRATEGIES; i++) {
        	strategyGroups[i] = new QuaxTileStrategyGroup();
        }
    }


    /*
     * TODO Outdated comment?
      Given a QuaxBoard b containing strategy values, chooses the move with
      the highest strategy value and returns it. If there is a tie, chooses
      one move at random of the highest strategy values.
     */
	private QuaxCoordinate decideMove(QuaxBoard b) {
        int randStrategyValue = chooseStrategyValue();
        QuaxTileStrategyGroup choice = getStrategyGroupWithValue(randStrategyValue);

        while (choice.size() == 0) {
            randStrategyValue--;
            choice = getStrategyGroupWithValue(randStrategyValue);
        }


        ArrayList<QuaxCoordinate> candidateMoves = getPotentialMoves(b, choice);

        int index = Math.abs(RNG.nextInt()) % candidateMoves.size();
        return candidateMoves.get(index);
	}


    /* 5% chance of strat val 1
    20% chance of strat val 2
    25% chance of strat val 3
    50% chance of strat 4
    */
    private int chooseStrategyValue() {
        SplittableRandom random = new SplittableRandom();
        int probability= random.nextInt(1,101);

        if (probability <= 3) {
            return 1;
        }

        if (probability <= 15) {
            return 2;
        }

        if (probability <= 45) {
            return 3;
        }

        return 4;
    }

    public QuaxTileStrategyGroup getStrategyGroupWithValue(int i) {
        assert (i <= MAX_STRATEGIES && i > 0);
        return strategyGroups[i-1];
    }

    private ArrayList<QuaxCoordinate> getPotentialMoves(QuaxBoard b, QuaxTileStrategyGroup tsg) {
        ArrayList<QuaxCoordinate> moves = new ArrayList<>();

        for (QuaxTile t : tsg) {
            moves.add(t.getCoordinates());
        }

        if (moves.isEmpty()) { // just in case candidateMoves is somehow empty
            for (QuaxTile t : b) {
                if (isValidMove(t, b)) {
                    moves.add(t.getCoordinates());
                }
            }
        }

        return moves;
    }


    private void assignTileToStrategyGroup(QuaxTile newTile) {
        QuaxTileStrategyGroup tileSGroup = getStrategyGroupWithValue(newTile.getStrategyValue());
        removeTileFromAllStrategyGroups(newTile);
        tileSGroup.addTile(newTile);
    }

    private void removeTileFromAllStrategyGroups(QuaxTile targetTile) {
        for (QuaxTileStrategyGroup g : strategyGroups) {
        	g.removeTile(targetTile);
        }
    }


    // how the bot decides strategy vals for the tiles
    private void setUpStrategy(QuaxBoard b) {
        StrategyBuilder sb = new StrategyBuilder();
        clearAllStrategyGroups();
        sb.initialiseAllStrategyGroups(b);

        for (QuaxTile t : b) {
            if (!isValidMove(t, b)) {
                if (t instanceof Octagon) {
                    sb.setOctagonStrategyValues(t, b);
                }
            }

            else {
                if (t instanceof Rhombus) {
                    sb.setRhombusStrategyValue(t, b);
                }

                sb.setHighPriorityStrategyGroups(t, b);
            }
        }
    }

    private class StrategyBuilder {
        private void initialiseAllStrategyGroups(QuaxBoard b) {

            for (QuaxTile t : b) {
                t.setStrategyValue(0);
                if (!isValidMove(t, b)) {
                    continue;
                }
                t.setStrategyValue(1);
                assignTileToStrategyGroup(t);
            }
        }

        private void setOctagonStrategyValues(QuaxTile t, QuaxBoard b) {
            QuaxTile[][] neighbours = b.getNeighbours(t.getCoordinates());

            for (QuaxTile[] row : neighbours) {
                for (QuaxTile neighbour : row) {
                    if (neighbour == null) {
                        continue;
                    }

                    if (isValidMove(neighbour, b)) {
                        neighbour.setStrategyValue(2);
                        assignTileToStrategyGroup(neighbour);

                        setProgressStrategy(t, neighbour, neighbours);
                    }
                }
            }
        }

        private void setProgressStrategy(QuaxTile t, QuaxTile n, QuaxTile[][] neighbours) {
            if (t.getTileColour() == QuaxTileColour.BLACK) {
                if (n == neighbours[1][0] || n == neighbours[1][2]) {
                    if (getPlayerColour() == QuaxTileColour.BLACK) {
                        n.setStrategyValue(4);
                        assignTileToStrategyGroup(n);
                    }
                    else {
                        n.setStrategyValue(3);
                        assignTileToStrategyGroup(n);
                    }
                }
            }

            else if (t.getTileColour() == QuaxTileColour.WHITE) {
                if (n == neighbours[0][1] || n == neighbours[2][1]) {
                    if (getPlayerColour() == QuaxTileColour.WHITE) {
                        n.setStrategyValue(4);
                        assignTileToStrategyGroup(n);
                    }
                    else {
                        n.setStrategyValue(3);
                        assignTileToStrategyGroup(n);
                    }
                }
            }
        }


        private void setRhombusStrategyValue(QuaxTile t, QuaxBoard b) {
            t.setStrategyValue(4);
            assignTileToStrategyGroup(t);
            if (isValidMove(t, b)) {
                t.setStrategyValue(5);
                assignTileToStrategyGroup(t);
            }
        }


        private void setHighPriorityStrategyGroups(QuaxTile t, QuaxBoard b) {
            if (checkForWin(t.getCoordinates(), b, getPlayerColour().flip())) {
                t.setStrategyValue(5); // Block opponent
                assignTileToStrategyGroup(t);
            }

            if (checkForWin(t.getCoordinates(), b, getPlayerColour())) {
                t.setStrategyValue(6); // bot winning takes priority
                assignTileToStrategyGroup(t);
            }
        }


        private boolean checkForWin(QuaxCoordinate coord, QuaxBoard b, QuaxTileColour colour) {
            QuaxBoard copyBoard = new QuaxBoard(b);
            copyBoard.makeMove(coord, colour);
            return copyBoard.checkForWinningMove();
        }
    }


    private boolean isValidMove(QuaxTile t, QuaxBoard b) {
        return b.validMove(t.getCoordinates());
    }

    public static void enableHaste() {
    	botHaste = true;
    }


	@Override
	public void movePrompt(QuaxBoard b) {
		startThinkingTime = System.currentTimeMillis();
		
        this.getExecutor().execute(() -> {
            setUpStrategy(b);
        	QuaxCoordinate move = decideMove(b);
        	
        	while (!botHaste && System.currentTimeMillis() - startThinkingTime < MIN_THINKING_TIME) {
                ;
            }
            submitMove(move);
        });
	}
}