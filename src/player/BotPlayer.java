package player;

import java.util.*;

import static controller.QuaxController.RNG;
import model.QuaxBoard;
import types.*;


public class BotPlayer extends QuaxPlayer {

    public static final int MAX_STRATEGIES = 7;
    // TODO for final submission MIN_THINKING_TIME will need to be upped to the 3-5 second range (Confirm)
	private static final long MIN_THINKING_TIME = 1000;
    private static boolean botHaste = false;

    private final StrategyBuilder sb;
    private final QuaxTileStrategyGroup[] strategyGroups;


	public BotPlayer() {
		super();
		sb = new StrategyBuilder();
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

        ArrayList<QuaxCoordinate> candidateMoves = getPotentialMoves(choice);

        int index = Math.abs(RNG.nextInt()) % candidateMoves.size();
        return candidateMoves.get(index);
	}


    private QuaxTileStrategyGroup selectStrategyGroup(int move) {
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

        int randStrategyValue = chooseStrategyValue();
        QuaxTileStrategyGroup choice = getStrategyGroupWithValue(randStrategyValue);

        while (choice.isEmpty()) {
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

    private ArrayList<QuaxCoordinate> getPotentialMoves(QuaxTileStrategyGroup tsg) {
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
                    if (neighbour.tileExists() && isValidStrategicMove(neighbour, b, getPlayerColour())) {
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

        // TODO not sure if I can use these
        /*
        private static QuaxTile[][] rotateOctagonNeighbours(QuaxTile[][] neighbours) {
        	assert neighbours[0].length == 3;
        	return new QuaxTile[][]
        			{ { neighbours[2][0], neighbours[1][0], neighbours[0][0] },
        			  { neighbours[2][1], neighbours[1][1], neighbours[0][1] },
        			  { neighbours[2][2], neighbours[1][2], neighbours[0][2] } };
        }*/

        private void setRhombusStrategyValue(QuaxTile t, QuaxBoard b) {
        	if (isUselessRhombus(t, b)) {
        		assignStrategyValue(t, 0);
        	}
            else {
                // TODO - This means every Rhombus has either SV 0 or 5. It only places Rhombuses on critical moves then
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
                assignStrategyValue(t, MAX_STRATEGIES-1);
            }

            // If the bot can place any winning tile, assign the highest priority
            if (checkForWin(t.getCoordinates(), b, getPlayerColour())) {
                assignStrategyValue(t, MAX_STRATEGIES);
            }
        }

        private static boolean isLowPriority(QuaxTile t) {
        	return t.getStrategyValue() < MAX_STRATEGIES-1;
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
        		if (t.isFree() && isLowPriority(t)) {
        			if (isUselessRhombus(t, b)) {
	        			assignStrategyValue(t, 0);
	        		}
	                else if (exploitsVulnerableRhombuses(t, b)) {
	        			upgradeStrategy(t, 1, 5);
	        		}
	                else if (defendsVulnerableRhombuses(t, b)) {
	        			upgradeStrategy(t, 2, 5);
	        		}
	                else if (createsOnlyOneVulnerableRhombus(t, b)) {
	                	downgradeStrategy(t, 1, 2);
	                }
        		}
        	}
        	
        	avoidWeakGroupContributions(b, 2, 2);
        }
        
        private void upgradeStrategy(QuaxTile t, int increase, int maximum) {
        	int prevValue = t.getStrategyValue(),
        			limit = Math.max(maximum, MAX_STRATEGIES);
        	if (prevValue < limit) {
	        	if (prevValue + increase > limit) {
	        		increase = limit - prevValue;
	        	}
	        	assignStrategyValue(t, prevValue + increase);
        	}
        }
        
        private void downgradeStrategy(QuaxTile t, int decrease, int minimum) {
        	int prevValue = t.getStrategyValue(),
        			limit = Math.max(minimum, 0);
        	if (prevValue > limit) {
	        	if (prevValue - decrease < limit) {
	        		decrease = prevValue - limit;
	        	}
	        	assignStrategyValue(t, prevValue - decrease);
        	}
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
        
        private int changeInVulnerableRhombuses(QuaxTile t, QuaxBoard b) {
        	int result = 0;
        	if (b.validMove(t)) {
	        	QuaxBoard copy = new QuaxBoard(b);
	        	int vulnerableCountBefore = vulnerableRhombusCount(copy);
	        	copy.makeMove(t);
	        	result = vulnerableRhombusCount(copy) - vulnerableCountBefore;
        	}
        	return result;
        }
        
        private boolean exploitsVulnerableRhombuses(QuaxTile t, QuaxBoard b) {
        	boolean result = false;
        	if (b.validMove(t)) {
	        	result = changeInVulnerableRhombuses(t, b) >= 2;
        	}
        	return result;
        }
        
        private boolean defendsVulnerableRhombuses(QuaxTile t, QuaxBoard b) {
        	QuaxBoard copy = new QuaxBoard(b);
        	copy.skipTurn();
        	return exploitsVulnerableRhombuses(t, copy);
        }
        
        private boolean createsOnlyOneVulnerableRhombus(QuaxTile t, QuaxBoard b) {
        	return changeInVulnerableRhombuses(t, b) == 1;
	    }
        
        private static List<QuaxTileGroup> nearbyTileGroups(QuaxTile t, QuaxBoard b) {
        	LinkedList<QuaxTileGroup> groups = new LinkedList<QuaxTileGroup>();
        	for (QuaxTile n : b.getNeighboursList(t)) {
        		if (n.isOccupied()) {
        			QuaxTileGroup tileGroup = n.getTileGroup();
        			if (!groups.contains(tileGroup)) {
        				groups.add(tileGroup);
        			}
        		}
        	}
        	return groups;
        }
        
        private List<QuaxTileGroup> removeOpponentGroups(List<QuaxTileGroup> list) {
        	LinkedList<QuaxTileGroup> copy = new LinkedList<QuaxTileGroup>(list),
        							 found = new LinkedList<QuaxTileGroup>();
        	for (QuaxTileGroup g : copy) {
        		if (g.getGroupColour() != getPlayerColour()) {
        			found.add(g);
        		}
        	}
        	copy.removeAll(found);
        	return copy;
        }
        
        private List<QuaxTileGroup> ownedNearbyGroups(QuaxTile t, QuaxBoard b) {
        	return removeOpponentGroups(nearbyTileGroups(t, b));
        }
        
        private void avoidWeakGroupContributions(QuaxBoard board, int decrease, int minimum) {
        	for (QuaxTile tile : board) {
        		List<QuaxTileGroup> nearbyGroupsBefore = ownedNearbyGroups(tile, board);
        		// TODO better comment - Joining two groups together is fine
        		if (tile.isFree() && nearbyGroupsBefore.size() == 1) {
        			QuaxTileGroup groupBefore = nearbyGroupsBefore.getFirst();
        			
        			QuaxBoard copy = new QuaxBoard(board);
        			copy.makeMove(tile);
        			
        			QuaxTileGroup groupAfter = ownedNearbyGroups(tile, copy).getFirst();
        			
        			if (groupBefore.distanceToWalls() == groupAfter.distanceToWalls()) {
        				downgradeStrategy(tile, decrease, minimum);
        			}
        		}
        	}
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