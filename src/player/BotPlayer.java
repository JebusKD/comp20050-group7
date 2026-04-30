package player;

import java.util.*;

import static controller.QuaxController.RNG;
import model.QuaxBoard;
import types.*;


public class BotPlayer extends QuaxPlayer {

    public static final int MAX_STRATEGIES = 6;
    // TODO for final submission MIN_THINKING_TIME will need to be upped to the 3-5 second range (Confirm)
	private static final long MIN_THINKING_TIME = 1000;
    private static boolean botHaste = false;

    private final QuaxTileStrategyGroup[] strategyGroups;


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
      Given a QuaxBoard b containing strategy values, choose a group of tiles
      with a certain strategy value. If there is a tie, chooses
      one move at random of the group.
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
        return strategyGroups[i - 1];
    }

    private ArrayList<QuaxCoordinate> getPotentialMoves(QuaxBoard b, QuaxTileStrategyGroup tsg) {
        ArrayList<QuaxCoordinate> moves = new ArrayList<>();

        for (QuaxTile t : tsg) {
            moves.add(t.getCoordinates());
        }

        return moves;
    }


    private void assignTileToStrategyGroup(QuaxTile newTile) {
        removeTileFromAllStrategyGroups(newTile);

        int strategyValue = newTile.getStrategyValue();
        if (strategyValue > 0 && strategyValue <= MAX_STRATEGIES) {
	        QuaxTileStrategyGroup tileSGroup = getStrategyGroupWithValue(strategyValue);
	        tileSGroup.addTile(newTile);
        }
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
        sb.postStrategy(b);
    }


    private class StrategyBuilder {

        private void initialiseAllStrategyGroups(QuaxBoard b) {
            for (QuaxTile t : b) {
                t.setStrategyValue(0);
                if (isValidStrategicMove(t, b, getPlayerColour())) {
                    assignStrategyValue(t, 1);
                }
            }
        }

        private void initialiseStrategy(QuaxBoard b) {
            initialiseAllStrategyGroups(b);

            for (QuaxTile t : b) {
                // If the tile being checked is not valid, i.e. already owned,
                //      set the strategy value of the tiles around it
                if (!isValidStrategicMove(t, b, getPlayerColour())) {
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

                    setHighPriorityStrategyGroups(t, b);
                }
            }
        }


        private void setOctagonStrategyValues(QuaxTile t, QuaxBoard b) {
            QuaxTile[][] neighbours = b.getNeighbours(t.getCoordinates());

            for (QuaxTile[] row : neighbours) {
                for (QuaxTile neighbour : row) {
                    if (neighbour != null && isValidStrategicMove(neighbour, b, getPlayerColour())) {
                        if (neighbour.getStrategyValue() <= 2) {
                            neighbour.setStrategyValue(2);
                        }
                        assignTileToStrategyGroup(neighbour);

                        if (neighbour.getStrategyValue() <= 4) {
                            setProgressStrategy(t, neighbour, neighbours);
                        }
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

        // TODO - Clean these
        private void progressVertically(QuaxTile n, QuaxTile[][] neighbours) {
            if (n == neighbours[1][0] || n == neighbours[1][2]) {
                if (getPlayerColour() == QuaxTileColour.BLACK) {
                    assignStrategyValueIfLess(n, 4);
                }
                else {
                    assignStrategyValueIfLess(n, 3);
                }
            }
        }

        private void progressHorizontally(QuaxTile n, QuaxTile[][] neighbours) {
            if (n == neighbours[0][1] || n == neighbours[2][1]) {
                if (getPlayerColour() == QuaxTileColour.WHITE) {
                    assignStrategyValueIfLess(n, 4);
                }
                else {
                    assignStrategyValueIfLess(n, 3);
                }
            }
        }


        private void setRhombusStrategyValue(QuaxTile t, QuaxBoard b) {
        	if (isUselessRhombus(t, b)) {
        		assignStrategyValue(t, 0);
        	}
            else {
                // All placeable Rhombus tiles have a base strategy value of 4
                assignStrategyValue(t, 4);

                // If the human player can place the tile as well,
                //      assign it the second-highest priority, to block it
	            if (isValidStrategicMove(t, b, getPlayerColour().flip())){
	                assignStrategyValue(t, 5);
	            }
        	}
        }

        /* "Useless Rhombus" is defined as having at most 1
         * nearby enemy tile.
         */
        private boolean isUselessRhombus(QuaxTile t, QuaxBoard b) {
        	boolean result = false;
        	if (t instanceof Rhombus) {
	        	int countOpponentTiles = 0;
	        	for (QuaxTile[] row : b.getNeighbours(t)) {
	        		for (QuaxTile n : row) {
	        			if (n.getTileColour() == getPlayerColour().flip()) {
	        				countOpponentTiles++;
	        			}
	        		}
	        	}
	        	result = countOpponentTiles <= 1;
        	}
        	return result;
        }

        private void setHighPriorityStrategyGroups(QuaxTile t, QuaxBoard b) {
            // If human player can win, try to block the win
            if (checkForWin(t.getCoordinates(), b, getPlayerColour().flip())) {
                assignStrategyValue(t, 5);
            }

            // If the bot can place any winning tile, assign the highest priority
            if (checkForWin(t.getCoordinates(), b, getPlayerColour())) {
                assignStrategyValue(t, 6);
            }
        }

        private void assignStrategyValueIfLess(QuaxTile t, int value) {
        	if (t.getStrategyValue() < value) {
        		assignStrategyValue(t, value);
        	}
        }

        private boolean checkForWin(QuaxCoordinate coord, QuaxBoard b, QuaxTileColour colour) {
            QuaxBoard copyBoard = new QuaxBoard(b);
            copyBoard.makeMove(coord, colour);

            return copyBoard.checkForWinningMove();
        }
        
        private void postStrategy(QuaxBoard b) {
        	for (QuaxTile t : b) {
        		if (isUselessRhombus(t, b)) {
        			assignStrategyValue(t, 0);
        		}
                else if (exploitsVulnerableRhombuses(t, b)) {
        			upgradeStrategy(t, 1, 5);
        		}
                else if (defendsVulnerableRhombuses(t, b)) {
        			upgradeStrategy(t, 2, 5);
        		}
        	}
        }
        
        private void upgradeStrategy(QuaxTile t, int increase, int maximum) {
        	int prevValue = t.getStrategyValue(),
        			limit = Math.max(maximum, MAX_STRATEGIES);
        	if (prevValue + increase > limit) {
        		increase = limit - prevValue;
        	}
        	assignStrategyValue(t, prevValue + increase);
        }
        
        private int vulnerableRhombusCount(QuaxBoard b) {
        	Iterator<QuaxCoordinate> iterator = QuaxBoard.rhombusCoordinateIterator();
        	int count = 0;
        	while (iterator.hasNext()) {
        		if (b.isValidRhombusForBoth(iterator.next())) {
        			count++;
        		}
        	}
        	return count;
        }
        
        private boolean exploitsVulnerableRhombuses(QuaxTile t, QuaxBoard b) {
        	boolean result = false;
        	if (b.validMove(t)) {
	        	QuaxBoard copy = new QuaxBoard(b);
	        	int vulnerableCountBefore = vulnerableRhombusCount(copy);
	        	copy.makeMove(t);
	        	if (vulnerableCountBefore + 2 <= vulnerableRhombusCount(copy)) {
	        		result = true;
	        	}
        	}
        	return result;
        }
        
        private boolean defendsVulnerableRhombuses(QuaxTile t, QuaxBoard b) {
        	QuaxBoard copy = new QuaxBoard(b);
        	copy.skipTurn();
        	return exploitsVulnerableRhombuses(t, copy);
        }


        private void assignStrategyValue(QuaxTile t, int value) {
            t.setStrategyValue(value);
            assignTileToStrategyGroup(t);
        }
    }

    private boolean isValidStrategicMove(QuaxTile t, QuaxBoard b, QuaxTileColour c) {
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