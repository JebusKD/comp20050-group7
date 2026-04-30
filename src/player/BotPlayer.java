package player;

import java.util.*;

import model.QuaxBoard;
import types.*;


public class BotPlayer extends QuaxPlayer {
	private static boolean botHaste = false;

    public static final int MAX_STRATEGIES = 6;
	private static final long MIN_THINKING_TIME = 1000;
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

    private void clearAllStrategyGroups() {
        this.stratOne = new QuaxTileStrategyGroup();
        this.stratTwo = new QuaxTileStrategyGroup();
        this.stratThree = new QuaxTileStrategyGroup();
        this.stratFour = new QuaxTileStrategyGroup();
        this.stratFive = new QuaxTileStrategyGroup();
        this.stratSix = new QuaxTileStrategyGroup();
    }


    /*
      Given a QuaxBoard b containing strategy values, chooses the move with
      the highest strategy value and returns it. If there is a tie, chooses
      one move at random of the highest strategy values.
     */
	private QuaxCoordinate decideMove(QuaxBoard b) {
        int randStrategyValue = 6;
        QuaxTileStrategyGroup choice = getStrategyGroupWithValue(randStrategyValue);

        while (choice.size() == 0) {
            randStrategyValue--;
            choice = getStrategyGroupWithValue(randStrategyValue);
        }


        ArrayList<QuaxCoordinate> candidateMoves = getPotentialMoves(b, choice);

        int index = Math.abs(RNG.nextInt()) % candidateMoves.size();
        return candidateMoves.get(index);
	}

    public QuaxTileStrategyGroup getStrategyGroupWithValue(int i) {
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


    // how the bot decides strategy vals for the tiles
    public void setUpStrategy(QuaxBoard b) {
        StrategyBuilder sb = new StrategyBuilder();
        clearAllStrategyGroups();
        sb.initialiseAllStrategyGroups(b);

        for (QuaxTile t : b) {
            if (!b.validMove(t.getCoordinates(), this.getPlayerColour())) {
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
                t.setStrategyValue(IGNORE_VALUE);
                if (!b.validMove(t.getCoordinates(), getPlayerColour())) {
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

                    if (b.validMove(neighbour.getCoordinates(), getPlayerColour())) {
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
            if (b.validMove(t.getCoordinates(), getPlayerColour().flip())) {
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