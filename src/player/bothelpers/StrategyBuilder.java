package player.bothelpers;

import java.util.*;

import model.QuaxBoard;
import player.BotPlayer;
import types.*;
import static types.StrategyValue.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static types.QuaxTileColour.*;


public class StrategyBuilder {

    private final BotPlayer linkedBot;

    
    public StrategyBuilder(BotPlayer bot) {
    	if (bot == null) {
    		throw new IllegalArgumentException("BotPlayer cannot be null.");
    	}
    	
        this.linkedBot = bot;
    }


    public QuaxTileColour botColour() {
    	assert linkedBot != null;
    	
        return linkedBot.getPlayerColour();
    }

    public StrategyValue getRandomStrategyValue() {
        return StrategyValueProbabilities.randomStrategyValue();
    }


    public void initialiseStrategy(QuaxBoard b) {
    	assert b != null;
    	
        initialiseAllStrategyGroups(b);
        BasicBotStrategist simpleBot = new BasicBotStrategist(b);

        simpleBot.createSimpleStrategy();
    }


    private void initialiseAllStrategyGroups(QuaxBoard board) {
    	assert board != null;
    	
        for (QuaxTile t : board) {
            t.setStrategyValue(IGNORE);
            if (isValidStrategicMove(t, board, botColour())) {
                assignStrategyValue(t, VERY_LOW);
            }
        }
    }


    void assignStrategyValue(QuaxTile t, StrategyValue value) {
    	assert t != null && t.tileExists() && value != null;
    	
        t.setStrategyValue(value);
        assignTileToStrategyGroup(t);
    }

    private void assignTileToStrategyGroup(QuaxTile newTile) {
    	assert newTile != null && newTile.tileExists() && linkedBot != null;
    	
        removeTileFromAllStrategyGroups(newTile);
        StrategyValue strategyValue = newTile.getStrategyValue();

        if (strategyValue != IGNORE) {
            LinkedList<QuaxTile> stratGroup = linkedBot.getStrategyGroupWithValue(strategyValue);
            stratGroup.add(newTile);
        }
    }

    private void removeTileFromAllStrategyGroups(QuaxTile targetTile) {
    	assert targetTile != null && targetTile.tileExists();
    	
        for (LinkedList<QuaxTile> g : getBotStrategyGroups()) {
            g.remove(targetTile);
        }
    }

    private ArrayList<LinkedList<QuaxTile>> getBotStrategyGroups() {
    	assert linkedBot != null;
    	
        ArrayList<LinkedList<QuaxTile>> strategyGroups = new ArrayList<>(MAX_STRATEGIES);

        for (int i = 1 ; i <= MAX_STRATEGIES ; i++) {
            strategyGroups.add(linkedBot.getStrategyGroupWithValue(fromInt(i)));
        }

        return strategyGroups;
    }



    private boolean isValidStrategicMove(QuaxTile t, QuaxBoard b, QuaxTileColour c) {
    	assert t != null && t.tileExists() && b != null && c != QuaxTileColour.NONE;
    	
        return b.validMove(t.getCoordinates(), c);
    }


    /* "Low Priority Rhombus" is defined as having at most 1
     * nearby enemy tile, so the bot may take it whenever needed without
     * needing to worry about the human placing a tile there
     */
    boolean isLowPriorityRhombus(QuaxTile t, QuaxBoard b) {
    	assert t != null && t.tileExists() && b != null;
    	
        boolean result = false;

        if (t instanceof Rhombus) {
            int countOpponentTiles = 0;
            for (QuaxTile[] row : b.getNeighbours(t.getCoordinates())) {
                for (QuaxTile n : row) {
                    if (n.getTileColour() == botColour().flip()) {
                        countOpponentTiles++;
                    }
                }
            }
            result = countOpponentTiles <= 1;
        }
        return result;
    }


    private class BasicBotStrategist {

        private final QuaxBoard simpleBoard;


        private BasicBotStrategist(QuaxBoard b) {
        	assert b != null;
        	
            this.simpleBoard = b;
        }


        private void createSimpleStrategy() {
        	assert simpleBoard != null;
        	
            for (QuaxTile t : simpleBoard) {
                // If the tile being checked is an already owned Octagon tile,
                //      set the strategy value of the tiles around it
                if (!isValidStrategicMove(t, simpleBoard, botColour())) {
                    if (t instanceof Octagon o) {
                        setOctagonStrategyValues(o);
                    }
                }

                // If the tile being checked is valid, ignoring unplaced Octagon tiles,
                //      set values depending on the board status
                else {
                    if (t instanceof Rhombus r) {
                        setRhombusStrategyValue(r);
                    }

                    setHighPriorityStrategyGroups(t);
                }
            }
        }


        private void setOctagonStrategyValues(Octagon o) {
        	assert o != null && o.tileExists();
        	
            QuaxTile[][] neighbours = simpleBoard.getNeighbours(o.getCoordinates());

            for (QuaxTile[] row : neighbours) {
                for (QuaxTile neighbour : row) {
                    // If tile is adjacent to an already owned one, it has a base strategy value of 2
                    if (neighbour.tileExists() && isValidStrategicMove(neighbour, simpleBoard, botColour())) {
                        assignStrategyValueIfLess(neighbour, LOW);

                        // If the move is not game-changing, set progressing tile strategy values
                        if (PROGRESS.compareTo( neighbour.getStrategyValue() ) > 0) {
                            setProgressStrategy(o, neighbour, neighbours);
                        }
                    }
                }
            }
        }

        private void setProgressStrategy(Octagon o, QuaxTile n, QuaxTile[][] neighbours) {
        	assert o != null && o.tileExists() && n != null && n.tileExists() && neighbours.length == 3 && neighbours[0].length == 3;
        	
            if (o.isBlack()) {
                progressVertically(n, neighbours);
            }
            else {
                progressHorizontally(n, neighbours);
            }
        }

        private void progressVertically(QuaxTile n, QuaxTile[][] neighbours) {
        	assert n != null && n.tileExists() && neighbours.length == 3 && neighbours[0].length == 3;
        	
            if (n == neighbours[1][0] || n == neighbours[1][2]) {
                if (botColour() == BLACK) {
                    assignStrategyValueIfLess(n, PROGRESS);
                }
                else {
                    assignStrategyValueIfLess(n, BLOCKING);
                }
            }
        }

        private void progressHorizontally(QuaxTile n, QuaxTile[][] neighbours) {
        	assert n != null && n.tileExists() && neighbours.length == 3 && neighbours[0].length == 3;

            if (n == neighbours[0][1] || n == neighbours[2][1]) {
                if (botColour() == WHITE) {
                    assignStrategyValueIfLess(n, PROGRESS);
                }
                else {
                    assignStrategyValueIfLess(n, BLOCKING);
                }
            }
        }


        private void setRhombusStrategyValue(Rhombus r) {
        	assert r != null && r.tileExists() && simpleBoard != null;
        	
            if (isLowPriorityRhombus(r, simpleBoard)) {
                assignStrategyValue(r, IGNORE);
            }
            else {
                // If not a low-priority rhombus, the human player can place the rhombus as well,
                //      so assign it the second-highest priority, to block it
                if (isValidStrategicMove(r, simpleBoard, botColour().flip())) {
                    assignStrategyValue(r, KEY);
                }
            }
        }

        private void setHighPriorityStrategyGroups(QuaxTile t) {
        	assert t != null && t.tileExists();
        	
            // If human player can win, try to block the win
            if (checkForWin(t.getCoordinates(), botColour().flip())) {
                assignStrategyValue(t, OPPONENT_WINNING);
            }

            // If the bot can place any winning tile, assign the highest priority
            if (checkForWin(t.getCoordinates(), botColour())) {
                assignStrategyValue(t, WINNING);
            }
        }


        private void assignStrategyValueIfLess(QuaxTile t, StrategyValue value) {
        	assert t != null && t.tileExists() && value != null;
        	
            if (value.compareTo( t.getStrategyValue() ) > 0) {
                assignStrategyValue(t, value);
            }
        }

        private boolean checkForWin(QuaxCoordinate coord, QuaxTileColour colour) {
        	assert coord != null && simpleBoard != null && (colour == QuaxTileColour.BLACK || colour == QuaxTileColour.WHITE);
        	
            QuaxBoard copyBoard = new QuaxBoard(simpleBoard);
            copyBoard.makeMove(coord, colour);

            return copyBoard.checkForWinningMove();
        }
    }


    public void refineStrategy(QuaxBoard b) {
    	assert b != null;
    	
        BotStrategyImprover smarterBot = new BotStrategyImprover(b, this);

        smarterBot.improveStrategy();
    }
}
