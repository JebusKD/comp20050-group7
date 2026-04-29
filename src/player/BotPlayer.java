package player;

import java.util.*;

import model.QuaxBoard;
import types.*;


// TODO - Remove abstract
public abstract class BotPlayer extends QuaxPlayer {
	// TODO - rename variable
	private static boolean botHaste = false;

    public static final int MAX_STRATEGIES = 6;
	private static final long MIN_THINKING_TIME = 2500;
	static final int IGNORE_VALUE = Integer.MIN_VALUE;
	static final Random RNG = new Random();

    private QuaxTileStrategyGroup stratOne;
    private QuaxTileStrategyGroup stratTwo;
    private QuaxTileStrategyGroup stratThree;
    private QuaxTileStrategyGroup stratFour;
    private QuaxTileStrategyGroup stratFive;
    private QuaxTileStrategyGroup stratSix;

    private QuaxTileStrategyGroup[] strategyGroups;
    
    private long startThinkingTime;


	public BotPlayer() {
		super();
        clearAllStrategyGroups();
	}


	protected abstract QuaxCoordinate computeMove(QuaxBoard b);


    public QuaxTileStrategyGroup[] getStrategyGroups() {
        strategyGroups = new QuaxTileStrategyGroup[7];
        addStrategyGroupsToArray();
        return strategyGroups;
    }

    private void addStrategyGroupsToArray() {
        strategyGroups[0] = new QuaxTileStrategyGroup();
        strategyGroups[1] = stratOne;
        strategyGroups[2] = stratTwo;
        strategyGroups[3] = stratThree;
        strategyGroups[4] = stratFour;
        strategyGroups[5] = stratFive;
        strategyGroups[6] = stratSix;
    }


    /*
      Given a QuaxBoard b containing strategy values, chooses the move with
      the highest strategy value and returns it. If there is a tie, chooses
      one move at random of the highest strategy values.
     */
	public QuaxCoordinate decideMove(QuaxBoard b) {
        int randStrategyValue = 6;
        QuaxTileStrategyGroup choice = getStrategyGroupWithValue(randStrategyValue);

        while (choice.size() <= 0) {
            randStrategyValue--;
            choice = getStrategyGroupWithValue(randStrategyValue);
        }


        ArrayList<QuaxCoordinate> candidateMoves = getPotentialMoves(b, choice);

        int index = Math.abs(RNG.nextInt()) % candidateMoves.size();
        return candidateMoves.get(index);
	}

    /*
      5% chance of strat val 1
      20% chance of strat val 2
      25% chance of strat val 3
      50% chance of strat 4
     *)/
    private int chooseStrategyValue() {
        SplittableRandom random = new SplittableRandom();
        int probability= random.nextInt(1, 101);

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

    private QuaxTileStrategyGroup getHigherStrategyGroup() {
        if (stratSix != null && stratSix.size() != 0) { //if bot can win, that is highest priority
            return getStrategyGroupWithValue(6);
        }

        else if (stratFive != null && stratFive.size() != 0) { //else block the opponents win
            return getStrategyGroupWithValue(5);
        }

        else {
            return new QuaxTileStrategyGroup();
        }
    }
    */

    private QuaxTileStrategyGroup getStrategyGroupWithValue(int i) {
        assert (i < 7 && i > 0);
        switch (i) {
            case 1:
                return this.stratOne;
            case 2:
                return this.stratTwo;
            case 3:
                return this.stratThree;
            case 4:
                return this.stratFour;
            case 5:
                return this.stratFive;
            case 6:
                return this.stratSix;
            default:
                return new QuaxTileStrategyGroup();
        }
    }

    private ArrayList<QuaxCoordinate> getPotentialMoves(QuaxBoard b, QuaxTileStrategyGroup tsg) {
        ArrayList<QuaxCoordinate> moves = new ArrayList<>();

        for (QuaxTile t : tsg) {
            moves.add(t.getCoordinates());
        }

        if (moves.isEmpty()) { //just in case candidateMoves is somehow empty
            for (QuaxTile t : b) {
                if (b.validMove(t.getCoordinates(), this.getPlayerColour())) {
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

    private void removeTileFromAllStrategyGroups(QuaxTile newTile) {
        this.stratOne.removeTile(newTile);
        this.stratTwo.removeTile(newTile);
        this.stratThree.removeTile(newTile);
        this.stratFour.removeTile(newTile);
        this.stratFive.removeTile(newTile);
        this.stratSix.removeTile(newTile);
    }

    private void clearAllStrategyGroups() {
        this.stratOne = new QuaxTileStrategyGroup();
        this.stratTwo = new QuaxTileStrategyGroup();
        this.stratThree = new QuaxTileStrategyGroup();
        this.stratFour = new QuaxTileStrategyGroup();
        this.stratFive = new QuaxTileStrategyGroup();
        this.stratSix = new QuaxTileStrategyGroup();
    }


    // how the bot decides strategy vals for the tiles
    // TODO - please help
    public void setUpStrategy(QuaxBoard b) {
        clearAllStrategyGroups();
        initialiseAllStrategyGroups(b);

        for (QuaxTile t : b) {
            // TODO - Fix octagons
            if (t instanceof Octagon && !b.validMove(t.getCoordinates(), this.getPlayerColour())) {
                QuaxTile[][] neighbours = b.getNeighbours(t.getCoordinates());

                for (QuaxTile[] row : neighbours) {
                    for (QuaxTile neighbour : row) {
                        if (neighbour instanceof Octagon
                                && b.validMove(neighbour.getCoordinates(), this.getPlayerColour())) {
                            neighbour.setStrategyValue(2);
                            assignTileToStrategyGroup(neighbour);
                            if (t.getTileColour() == QuaxTileColour.BLACK) {
                                if (neighbour == neighbours[1][0] || neighbour == neighbours[1][2]) {
                                    if (this.getPlayerColour() == QuaxTileColour.BLACK) {
                                        neighbour.setStrategyValue(4);
                                        assignTileToStrategyGroup(neighbour);
                                    }
                                    else {
                                        neighbour.setStrategyValue(3);
                                        assignTileToStrategyGroup(neighbour);
                                    }
                                }
                            }

                            else if (t.getTileColour() == QuaxTileColour.WHITE) {
                                if (neighbour == neighbours[0][1] || neighbour == neighbours[2][1]) {
                                    if (this.getPlayerColour() == QuaxTileColour.WHITE) {
                                        neighbour.setStrategyValue(4);
                                        assignTileToStrategyGroup(neighbour);
                                    }
                                    else {
                                        neighbour.setStrategyValue(3);
                                        assignTileToStrategyGroup(neighbour);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // TODO - Fix set high strat vals
            if (b.validMove(t.getCoordinates(), this.getPlayerColour())) {
                if (t instanceof Rhombus) {
                    setRhombusStrategyValue(t, b);
                }

                if (checkForWin(t.getCoordinates(),b,this.getPlayerColour().flip())) {
                    t.setStrategyValue(5);
                    assignTileToStrategyGroup(t);
                }

                if (checkForWin(t.getCoordinates(), b, this.getPlayerColour())) {
                    t.setStrategyValue(6); // bot winning takes priority
                    assignTileToStrategyGroup(t);
                }
            }
        }
    }

    private void initialiseAllStrategyGroups(QuaxBoard b) {
        for (QuaxTile t : b) {
            t.setStrategyValue(IGNORE_VALUE);
            if (!b.validMove(t.getCoordinates(), this.getPlayerColour())) {
                continue;
            }
            t.setStrategyValue(1);
            assignTileToStrategyGroup(t);
        }
    }

    private void setRhombusStrategyValue(QuaxTile t, QuaxBoard b) {
        t.setStrategyValue(4);
        assignTileToStrategyGroup(t);
        if (b.validMove(t.getCoordinates(), this.getPlayerColour().flip())) {
            t.setStrategyValue(5);
            assignTileToStrategyGroup(t);
        }
    }


    private boolean checkForWin(QuaxCoordinate coord, QuaxBoard b, QuaxTileColour colour) {
        QuaxBoard copyBoard = new QuaxBoard(b);
        copyBoard.makeMove(coord, colour);
        return copyBoard.checkForWinningMove();
    }

    public static void enableHaste() {
    	botHaste = true;
    }


	@Override
	public void movePrompt(QuaxBoard b) {
		startThinkingTime = System.currentTimeMillis();
		
        this.getExecutor().execute(() -> {
        	QuaxCoordinate move = computeMove(b);
        	
        	while (!botHaste && System.currentTimeMillis() - startThinkingTime < MIN_THINKING_TIME) {
                ;
            }
            submitMove(move);
        });
	}
}