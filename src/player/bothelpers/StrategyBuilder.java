package player.bothelpers;

import java.util.*;

import model.QuaxBoard;
import player.BotPlayer;
import types.*;


public class StrategyBuilder {

    private final BotPlayer linkedBot;

    
    public StrategyBuilder(BotPlayer bot) {
        this.linkedBot = bot;
    }


    public QuaxTileColour botColour() {
        return linkedBot.getPlayerColour();
    }

    public StrategyValue getRandomStrategyValue() {
        return StrategyValueProbabilities.randomStrategyValue();
    }

    private ArrayList<LinkedList<QuaxTile>> getBotStrategyGroups() {
        ArrayList<LinkedList<QuaxTile>> strategyGroups = new ArrayList<>(StrategyValue.MAX_STRATEGIES);

        for (int i = 1 ; i <= StrategyValue.MAX_STRATEGIES ; i++) {
            strategyGroups.add(linkedBot.getStrategyGroupWithValue(StrategyValue.fromInt(i)));
        }

        return strategyGroups;
    }


    public void initialiseStrategy(QuaxBoard b) {
        initialiseAllStrategyGroups(b);
        BasicBotStrategist simpleBot = new BasicBotStrategist(b);

        simpleBot.createSimpleStrategy();
    }


    private void initialiseAllStrategyGroups(QuaxBoard board) {
        for (QuaxTile t : board) {
            t.setStrategyValue(StrategyValue.IGNORE);
            if (isValidStrategicMove(t, board, botColour())) {
                assignStrategyValue(t, StrategyValue.VERY_LOW);
            }
        }
    }

    void assignStrategyValue(QuaxTile t, StrategyValue value) {
        t.setStrategyValue(value);
        assignTileToStrategyGroup(t);
    }

    private void assignTileToStrategyGroup(QuaxTile newTile) {
        removeTileFromAllStrategyGroups(newTile);
        StrategyValue strategyValue = newTile.getStrategyValue();

        if (strategyValue != StrategyValue.IGNORE) {
            LinkedList<QuaxTile> stratGroup = linkedBot.getStrategyGroupWithValue(strategyValue);
            stratGroup.add(newTile);
        }
    }

    private void removeTileFromAllStrategyGroups(QuaxTile targetTile) {
        for (LinkedList<QuaxTile> g : getBotStrategyGroups()) {
            g.remove(targetTile);
        }
    }


    private boolean isValidStrategicMove(QuaxTile t, QuaxBoard b, QuaxTileColour c) {
        return b.validMove(t.getCoordinates(), c);
    }


    /* "Low Priority Rhombus" is defined as having at most 1
     * nearby enemy tile, so the bot may take it whenever needed without
     * needing to worry about the human placing a tile there
     */
    boolean isLowPriorityRhombus(QuaxTile t, QuaxBoard b) {
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
            this.simpleBoard = b;
        }


        private void createSimpleStrategy() {
            for (QuaxTile t : simpleBoard) {
                // If the tile being checked is an already owned Octagon tile,
                //      set the strategy value of the tiles around it
                if (!isValidStrategicMove(t, simpleBoard, botColour())) {
                    if (t instanceof Octagon) {
                        setOctagonStrategyValues(t);
                    }
                }

                // If the tile being checked is valid, ignoring unplaced Octagon tiles,
                //      set values depending on the board status
                else {
                    if (t instanceof Rhombus) {
                        setRhombusStrategyValue(t);
                    }

                    setHighPriorityStrategyGroups(t);
                }
            }
        }


        private void setOctagonStrategyValues(QuaxTile t) {
            QuaxTile[][] neighbours = simpleBoard.getNeighbours(t.getCoordinates());

            for (QuaxTile[] row : neighbours) {
                for (QuaxTile neighbour : row) {
                    // If tile is adjacent to an already owned one, it has a base strategy value of 2
                    if (neighbour.tileExists() && isValidStrategicMove(neighbour, simpleBoard, botColour())) {
                        assignStrategyValueIfLess(neighbour, StrategyValue.LOW);

                        // If the move is not game-changing, set progressing tile strategy values
                        if (StrategyValue.PROGRESS.compareTo( neighbour.getStrategyValue() ) > 0) {
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

        private void progressVertically(QuaxTile n, QuaxTile[][] neighbours) {
            if (n == neighbours[1][0] || n == neighbours[1][2]) {
                if (botColour() == QuaxTileColour.BLACK) {
                    assignStrategyValueIfLess(n, StrategyValue.PROGRESS);
                }
                else {
                    assignStrategyValueIfLess(n, StrategyValue.BLOCKING);
                }
            }
        }

        private void progressHorizontally(QuaxTile n, QuaxTile[][] neighbours) {
            if (n == neighbours[0][1] || n == neighbours[2][1]) {
                if (botColour() == QuaxTileColour.WHITE) {
                    assignStrategyValueIfLess(n, StrategyValue.PROGRESS);
                }
                else {
                    assignStrategyValueIfLess(n, StrategyValue.BLOCKING);
                }
            }
        }


        private void setRhombusStrategyValue(QuaxTile t) {
            if (isLowPriorityRhombus(t, simpleBoard)) {
                assignStrategyValue(t, StrategyValue.IGNORE);
            }
            else {
                // If not a low-priority rhombus, the human player can place the rhombus as well,
                //      so assign it the second-highest priority, to block it
                if (isValidStrategicMove(t, simpleBoard, botColour().flip())) {
                    assignStrategyValue(t, StrategyValue.KEY);
                }
            }
        }

        private void setHighPriorityStrategyGroups(QuaxTile t) {
            // If human player can win, try to block the win
            if (checkForWin(t.getCoordinates(), botColour().flip())) {
                assignStrategyValue(t, StrategyValue.OPPONENT_WINNING);
            }

            // If the bot can place any winning tile, assign the highest priority
            if (checkForWin(t.getCoordinates(), botColour())) {
                assignStrategyValue(t, StrategyValue.WINNING);
            }
        }


        private void assignStrategyValueIfLess(QuaxTile t, StrategyValue value) {
            if (value.compareTo( t.getStrategyValue() ) > 0) {
                assignStrategyValue(t, value);
            }
        }

        private boolean checkForWin(QuaxCoordinate coord, QuaxTileColour colour) {
            QuaxBoard copyBoard = new QuaxBoard(simpleBoard);
            copyBoard.makeMove(coord, colour);

            return copyBoard.checkForWinningMove();
        }
    }


    public void refineStrategy(QuaxBoard b) {
        BotStrategyImprover smarterBot = new BotStrategyImprover(b, this);

        smarterBot.improveStrategy();
    }
}
