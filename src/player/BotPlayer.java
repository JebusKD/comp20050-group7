package player;

import java.util.*;

import static controller.QuaxController.RNG;
import model.QuaxBoard;
import types.*;


public class BotPlayer extends QuaxPlayer {
	private static boolean botHaste = false;

    public static final int MAX_STRATEGIES = 6;
    // TODO for final submission MIN_THINKING_TIME will
    // need to be upped to the 3-5 second range (Confirm)
	private static final long MIN_THINKING_TIME = 1000;

    private QuaxTileStrategyGroup[] strategyGroups;


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
     * TODO Outdated comment? - Fixed (ish)
      Given a QuaxBoard b containing strategy values, chooses a move with
      a random strategy value and returns it. If there is a tie, chooses
      one move at random of the highest strategy values.
     */
	private QuaxCoordinate decideMove(QuaxBoard b) {
        QuaxTileStrategyGroup choice = selectStrategyGroup(b.getMoveNumber());

        ArrayList<QuaxCoordinate> candidateMoves = getPotentialMoves(b, choice);

        int index = Math.abs(RNG.nextInt()) % candidateMoves.size();
        return candidateMoves.get(index);
	}

    private QuaxTileStrategyGroup selectStrategyGroup(int move) {
        if (move == 0) {
            return getStrategyGroupWithValue(1);
        }

        if (getStrategyGroupWithValue(6).size() > 0) {
            return getStrategyGroupWithValue(6);
        }

        if (getStrategyGroupWithValue(5).size() > 0) {
            return getStrategyGroupWithValue(5);
        }

        int randStrategyValue = chooseStrategyValue();
        QuaxTileStrategyGroup choice = getStrategyGroupWithValue(randStrategyValue);

        while (choice.size() == 0) {
            randStrategyValue--;
            choice = getStrategyGroupWithValue(randStrategyValue);
        }

        return choice;
    }

    /* //TODO - Decide on probabilities, are we keeping them as this?
    1% chance of strat val 1
    14% chance of strat val 2
    25% chance of strat val 3
    60% chance of strat 4
    */
    private int chooseStrategyValue() {
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
                if (isValidMove(t, b, t.getTileColour())) {
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
        sb.initialiseStrategy(b);
    }


    private class StrategyBuilder {

        private void initialiseAllStrategyGroups(QuaxBoard b) {
            for (QuaxTile t : b) {
                t.setStrategyValue(0);
                if (isValidMove(t, b, getPlayerColour())) {
                    assignStrategyValue(t, 1);
                }
            }
        }


        private void initialiseStrategy(QuaxBoard b) {
            initialiseAllStrategyGroups(b);

            for (QuaxTile t : b) {
                // If the tile being checked is not valid, i.e. already owned,
                //      set the strategy value of the tiles around it
                if (!isValidMove(t, b, getPlayerColour())) {
                    if (t instanceof Octagon) {
                        setOctagonStrategyValues(t, b);
                    }
                }

                // If the tile being checked is valid, ignoring unplaced Octagons,
                //      set values depending on the board status
                else {
                    if (t instanceof Rhombus) {
                        setRhombusStrategyValue(t, b);
                    }

                    else {
                        setHighPriorityStrategyGroups(t, b);
                    }
                }
            }
        }


        private void setOctagonStrategyValues(QuaxTile t, QuaxBoard b) {
            QuaxTile[][] neighbours = b.getNeighbours(t.getCoordinates());

            for (QuaxTile[] row : neighbours) {
                for (QuaxTile neighbour : row) {
                    if (neighbour != null && isValidMove(neighbour, b, getPlayerColour())) {
                        if (neighbour.getStrategyValue() <= 2) {
                            neighbour.setStrategyValue(2);
                        }
                        assignTileToStrategyGroup(neighbour);

                        setProgressStrategy(t, neighbour, neighbours);
                    }
                }
            }
        }

        private void setProgressStrategy(QuaxTile t, QuaxTile n, QuaxTile[][] neighbours) {
            if (t.isBlack()) {
                progressVertically(n, neighbours);
            }

            else {
                progressHorizontally(n, neighbours);
            }
        }

        private void progressVertically(QuaxTile n, QuaxTile[][] neighbours) {
            if (n == neighbours[1][0] || n == neighbours[1][2]) {
                if (getPlayerColour() == QuaxTileColour.BLACK) {
                    assignStrategyValue(n, 4);
                }
                else {
                    assignStrategyValue(n, 3);
                }
            }
        }

        private void progressHorizontally(QuaxTile n, QuaxTile[][] neighbours) {
            if (n == neighbours[0][1] || n == neighbours[2][1]) {
                if (getPlayerColour() == QuaxTileColour.WHITE) {
                    assignStrategyValue(n, 4);
                }
                else {
                    assignStrategyValue(n, 3);
                }
            }
        }


        private void setRhombusStrategyValue(QuaxTile t, QuaxBoard b) {
        	if (isUselessRhombus(t, b)) {
        		assignStrategyValue(t, 0);
        	} else {
        	
	            assignStrategyValue(t, 4);
	
	            if (b.validMove(t.getCoordinates(), getPlayerColour().flip())){
	                assignStrategyValue(t, 5);
	            }
	
	            if (checkForWin(t.getCoordinates(), b, getPlayerColour())) {
	                assignStrategyValue(t, 6);
	            }
	            
        	}
        }
        
        /* "Useless Rhombus" is defined as having at most 1
         * nearby enemy tile.
         */
        private boolean isUselessRhombus(QuaxTile t, QuaxBoard b) {
        	assert t instanceof Rhombus;
        	int countOpponentTiles = 0;
        	for (QuaxTile[] row : b.getNeighbours(t)) {
        		for (QuaxTile n : row) {
        			if (n.getTileColour() == getPlayerColour().flip()) {
        				countOpponentTiles++;
        			}
        		}
        	}
        	return countOpponentTiles <= 1;
        }

        private void setHighPriorityStrategyGroups(QuaxTile t, QuaxBoard b) {
            // If human player can win, block the win
            if (checkForWin(t.getCoordinates(), b, getPlayerColour().flip())) {
                assignStrategyValue(t, 5);
            }

            // If the bot can win, set tile priority to max
            if (checkForWin(t.getCoordinates(), b, getPlayerColour())) {
                assignStrategyValue(t, 6);
            }
        }


        private void assignStrategyValue(QuaxTile t, int value) {
            t.setStrategyValue(value);
            assignTileToStrategyGroup(t);
        }

        private boolean checkForWin(QuaxCoordinate coord, QuaxBoard b, QuaxTileColour colour) {
            QuaxBoard copyBoard = new QuaxBoard(b);

            copyBoard.makeMove(coord, colour);

            return copyBoard.checkForWinningMove();
        }
    }


    private boolean isValidMove(QuaxTile t, QuaxBoard b, QuaxTileColour c) {
        return b.validMove(t.getCoordinates(), c);
    }

    public static void enableHaste() {
    	botHaste = true;
    }


	@Override
	public void movePrompt(QuaxBoard b) {
        long startThinkingTime = System.currentTimeMillis();
		
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