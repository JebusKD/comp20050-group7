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


    public void initialiseStrategy(QuaxBoard currentBoard) {
        assert currentBoard != null;

        initialiseAllStrategyGroups(currentBoard);
        BasicBotStrategist simpleBot = new BasicBotStrategist(currentBoard);

        simpleBot.createSimpleStrategy();
    }


    private void initialiseAllStrategyGroups(QuaxBoard currBoard) {
        assert currBoard != null;

        for (QuaxTile t : currBoard) {
            t.setStrategyValue(IGNORE);
            if (isValidStrategicMove(t, currBoard, botColour())) {
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



    private boolean isValidStrategicMove(QuaxTile triedTile, QuaxBoard board, QuaxTileColour playerColour) {
        assert triedTile != null && triedTile.tileExists() && board != null
                && playerColour != QuaxTileColour.NONE;

        return board.validMove(triedTile.getCoordinates(), playerColour);
    }


    /* "Low Priority Rhombus" is defined as having at most 1
     * nearby enemy tile, so the bot may take it whenever needed without
     * needing to worry about the human placing a tile there
     */
    boolean isLowPriorityRhombus(QuaxTile checkedRhombus, QuaxBoard board) {
        assert checkedRhombus != null && checkedRhombus.tileExists() && board != null;

        boolean result = false;

        if (checkedRhombus instanceof Rhombus) {
            int countOpponentTiles = 0;
            for (QuaxTile[] row : board.getNeighbours(checkedRhombus.getCoordinates())) {
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


        private BasicBotStrategist(QuaxBoard botBoard) {
            assert botBoard != null;
            this.simpleBoard = botBoard;
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

        private void setHighPriorityStrategyGroups(QuaxTile validTile) {
            assert validTile != null && validTile.tileExists();

            // If human player can win, try to block the win
            if (checkForWin(validTile.getCoordinates(), botColour().flip())) {
                assignStrategyValue(validTile, OPPONENT_WINNING);
            }

            // If the bot can place any winning tile, assign the highest priority
            if (checkForWin(validTile.getCoordinates(), botColour())) {
                assignStrategyValue(validTile, WINNING);
            }
        }


        private void assignStrategyValueIfLess(QuaxTile strategyTile, StrategyValue value) {
            assert strategyTile != null && strategyTile.tileExists() && value != null;

            if (value.compareTo(strategyTile.getStrategyValue()) > 0) {
                assignStrategyValue(strategyTile, value);
            }
        }

        private boolean checkForWin(QuaxCoordinate coord, QuaxTileColour colour) {
        	assert coord != null && simpleBoard != null && (colour == QuaxTileColour.BLACK || colour == QuaxTileColour.WHITE);

            QuaxBoard copyBoard = new QuaxBoard(simpleBoard);
            copyBoard.makeMove(coord, colour);

            return copyBoard.checkForWinningMove();
        }
    }


    public void refineStrategy(QuaxBoard strategyRefiningBoard) {
        assert strategyRefiningBoard != null;
        BotStrategyImprover smarterBot = new BotStrategyImprover(strategyRefiningBoard, this);

        smarterBot.improveStrategy();
    }
}
