package player.bothelpers;

import java.util.*;

import model.QuaxBoard;
import types.*;
import static types.StrategyValue.*;


class BotStrategyImprover {

    private final QuaxBoard smarterBoard;
    private final StrategyBuilder initialStrategy;


    BotStrategyImprover(QuaxBoard board, StrategyBuilder startingStrategy) {
    	assert board != null && startingStrategy != null;
        this.smarterBoard = board;
        this.initialStrategy = startingStrategy;
    }


    private QuaxTileColour botColour() {
    	assert initialStrategy != null;
        return initialStrategy.botColour();
    }

    private void assignStrategyValue(QuaxTile tile, StrategyValue value) {
        assert tile != null && tile.tileExists() && value != null;
        initialStrategy.assignStrategyValue(tile, value);
    }



    /* Given the initial strategy from BasicBotStrategist,
     *  adjust the current strategy values on the board depending on how
     *  important a move would be.
     */
    public void improveStrategy() {
    	assert smarterBoard != null && initialStrategy != null;

        for (QuaxTile tile : smarterBoard) {
            if (tile.isFree() && isLowPriority(tile)) {

                if (initialStrategy.isLowPriorityRhombus(tile, smarterBoard)) {
                    assignStrategyValue(tile, IGNORE);
                }

                else if (defendsVulnerableRhombuses(tile, smarterBoard)) {
                    upgradeStrategy(tile, 2, KEY);
                }

                else if (exploitsVulnerableRhombuses(tile, smarterBoard)) {
                    upgradeStrategy(tile, 2, KEY);
                }

                else if (createsOnlyOneVulnerableRhombus(tile, smarterBoard)) {
                    downgradeStrategy(tile, 1, LOW);
                }
            }
        }


        PathFinder pf = new PathFinder();
        pf.avoidWeakGroupContributions(2, LOW);
        pf.diagonalPathfinding();
    }


    private boolean isLowPriority(QuaxTile tile) {
        assert tile != null && tile.tileExists();
        return tile.isLowPriority();
    }


    private void upgradeStrategy(QuaxTile valuableTile, int increase, StrategyValue maxSV) {
        assert valuableTile != null && valuableTile.tileExists()
                && increase > 0 && maxSV != null;

        int strategyValueToIncrease = valuableTile.getStrategyValue().toInt();
        int limit = Math.max(maxSV.toInt(), MAX_STRATEGIES);

        if (strategyValueToIncrease < limit) {
            if (strategyValueToIncrease + increase > limit) {
                increase = limit - strategyValueToIncrease;
            }

            assignStrategyValue(valuableTile, fromInt(strategyValueToIncrease + increase));
        }
    }

    private void downgradeStrategy(QuaxTile lowPriorityTile, int decrease, StrategyValue min) {
        assert lowPriorityTile != null && lowPriorityTile.tileExists()
                && decrease > 0 && min != null;

        if (isLowPriority(lowPriorityTile)) {
            int strategyValueToDecrease = lowPriorityTile.getStrategyValue().toInt();
            int minimum = min.toInt();
            int limit = Math.max(minimum, 0);

            if (strategyValueToDecrease > limit) {
                if (strategyValueToDecrease - decrease < limit) {
                    decrease = strategyValueToDecrease - limit;
                }
                assignStrategyValue(lowPriorityTile, fromInt(strategyValueToDecrease - decrease));
            }
        }
    }


    /* Check what would happen if the other player made the same move, and how it would
     *  affect the bot's strategy. A "Vulnerable Rhombus" is one that both players are able to take, as so;
     *      BLACK_OCT |  WHITE_OCT |
     *          |  VUL_RHOM  |
     *      WHITE_OCT |  BLACK_OCT |
     *
     */
    private boolean defendsVulnerableRhombuses(QuaxTile nextTileMove, QuaxBoard board) {
        assert nextTileMove != null && nextTileMove.tileExists() && board != null;

        QuaxBoard copy = new QuaxBoard(board);
        copy.skipTurn();

        return exploitsVulnerableRhombuses(nextTileMove, copy);
    }

    /* Exploiting a vulnerable rhombus would be something like:
     *
     *      HUMAN_OCT |   FREE_OCT   |  HUMAN_OCT       HUMAN_OCT | *BOT_OCT* | HUMAN_OCT
     *            FREE_RHOM   |  FREE_RHOM       ===>       FREE_RHOM   |  FREE_RHOM
     *       BOT_OCT  |   HUMAN_OCT  |  BOT_OCT         BOT_OCT   | HUMAN_OC  |  BOT_OCT
     *
     * This forces the human player to take only one of the rhombus tiles,
     * and on the next move the bot may take the other. This increases the strategy value of the tile
     */
    private boolean exploitsVulnerableRhombuses(QuaxTile nextTileMove, QuaxBoard board) {
        assert nextTileMove != null && nextTileMove.tileExists() && board != null;

        boolean result = false;

        if (board.validMove(nextTileMove)) {
            result = changeInVulnerableRhombuses(nextTileMove, board) >= 2;
        }

        return result;
    }


    /* Creating only one vulnerable rhombus would be something like:
     *
     *      HUMAN_OCT |  FREE_OCT  |          HUMAN_OCT |  *BOT_OCT* |
     *          |  VUL_RHOM  |       ===>         |  VUL_RHOM  |
     *       BOT_OCT  | HUMAN_OCT |           BOT_OCT  |  HUMAN_OC  |
     *
     * The human player can then take the rhombus tile, wasting the bot's move, so
     *  this decreases the strategy value of the tile
     */
    private boolean createsOnlyOneVulnerableRhombus(QuaxTile nextTileMove, QuaxBoard board) {
        assert nextTileMove != null && nextTileMove.tileExists() && board != null;
        return changeInVulnerableRhombuses(nextTileMove, board) == 1;
    }

    /* Check what would happen if the other player made the same move, and how it would
     *  affect the bot's strategy
     */
    private int changeInVulnerableRhombuses(QuaxTile nextTileMove, QuaxBoard board) {
        assert nextTileMove != null && nextTileMove.tileExists() && board != null;

        int result = 0;

        if (board.validMove(nextTileMove)) {
            QuaxBoard copy = new QuaxBoard(board);
            int vulnerableCountBefore = vulnerableRhombusCount(copy);

            copy.makeMove(nextTileMove);
            result = vulnerableRhombusCount(copy) - vulnerableCountBefore;
        }

        return result;
    }


    private int vulnerableRhombusCount(QuaxBoard board) {
        assert board != null;

        Iterator<QuaxCoordinate> iterator = QuaxBoard.rhombusCoordinateIterator();
        int count = 0;

        while (iterator.hasNext()) {
            if (board.isValidRhombusPlacementForBothPlayers(iterator.next())) {
                count++;
            }
        }

        return count;
    }



    private class PathFinder {

        private static final int NEIGHBOURING_OCTAGON_SQUARE_LENGTH = 3;


        private void avoidWeakGroupContributions(int decrease, StrategyValue minimum) {
        	assert smarterBoard != null && decrease > 0 && minimum != null;

            for (QuaxTile tile : smarterBoard) {
                List<QuaxTileGroup> nearbyGroupsBefore = ownedNearbyGroups(tile, smarterBoard);

                /* If a tile can be placed that will expand a group, check to see if placing the tile
                 *  will result in actual progress (i.e. the distance to the borders decreases).
                 *  This also takes into account actual joining two tile groups together
                 *
                 * If not, decrease the tiles strategy value
                 */
                if (tile.isFree() && nearbyGroupsBefore.size() == 1) {
                    QuaxTileGroup groupBefore = nearbyGroupsBefore.getFirst();

                    QuaxBoard copy = new QuaxBoard(smarterBoard);
                    copy.makeMove(tile);

                    QuaxTileGroup groupAfter = ownedNearbyGroups(tile, copy).getFirst();

                    if (groupBefore.distanceToWalls() == groupAfter.distanceToWalls()) {
                        downgradeStrategy(tile, decrease, minimum);
                    }
                }
            }
        }

        private List<QuaxTileGroup> ownedNearbyGroups(QuaxTile centre, QuaxBoard board) {
            assert centre != null && centre.tileExists() && board != null;

            return removeOpponentGroups(nearbyTileGroups(centre, board));
        }

        private List<QuaxTileGroup> nearbyTileGroups(QuaxTile centreTile, QuaxBoard board) {
            assert centreTile != null && centreTile.tileExists() && board != null;

            LinkedList<QuaxTileGroup> groups = new LinkedList<>();

            for (QuaxTile n : board.getNeighboursList(centreTile)) {
                if (n.isOccupied()) {
                    QuaxTileGroup tileGroup = n.getTileGroup();
                    if (!groups.contains(tileGroup)) {
                        groups.add(tileGroup);
                    }
                }
            }

            return groups;
        }

        private List<QuaxTileGroup> removeOpponentGroups(List<QuaxTileGroup> neighbourList) {
            assert neighbourList != null;

            LinkedList<QuaxTileGroup> copy = new LinkedList<>(neighbourList);
            LinkedList<QuaxTileGroup> found = new LinkedList<>();

            for (QuaxTileGroup g : copy) {
                if (g.getGroupColour() != botColour()) {
                    found.add(g);
                }
            }
            copy.removeAll(found);
            return copy;
        }



        private void diagonalPathfinding() {
        	assert smarterBoard != null;

        	Iterator<Octagon> iterator = smarterBoard.octagonIterator();

        	while (iterator.hasNext()) {
        		Octagon centre = iterator.next();
                if (centre.isSameColour(botColour())) {
                    QuaxTile[][] neighbours = smarterBoard.getSquareOctagonNeighbours(centre);

                    checkIfPathBlocked(neighbours);
                }
            }
        }

        private void checkIfPathBlocked(QuaxTile[][] neighbours) {
        	assert neighbours.length == 3 && neighbours[0].length == 3;

            for (int i = -1; i <= 1; i++) {
                if (opponentBlockingPath(neighbours, i)) {
                    adjustStrategyIfPathBlocked(neighbours, i);
                }
            }
        }

        private void adjustStrategyIfPathBlocked(QuaxTile[][] neighbours, int direction) {
            assert neighbours.length == 3 && neighbours[0].length == 3;

            for (QuaxTile ahead : neighboursAhead(neighbours, direction)) {
                if (ahead.tileExists() && ahead.getStrategyValue() == BLOCKING) {
                    upgradeStrategy(ahead, 1, PROGRESS);
                }
            }
        }




        private boolean opponentBlockingPath(QuaxTile[][] neighbours, int direction) {
            assert (direction >= -1 && direction <= 1) && neighbours != null;
            boolean result = false;

            QuaxTile[] pathAhead = neighboursAhead(neighbours, direction);

            if (pathAhead[1].tileExists()) {
                result = pathAhead[1].isOpponentColour(botColour());
            }

            return result;
        }


        private QuaxTile[] neighboursAhead(QuaxTile[][] neighbours, int direction) {
            assert neighbours.length == NEIGHBOURING_OCTAGON_SQUARE_LENGTH
                    && neighbours[0].length == NEIGHBOURING_OCTAGON_SQUARE_LENGTH
                    && (direction >= -1 && direction <= 1);

            QuaxTile[] neighboursAhead;
            if (botColour() == QuaxTileColour.BLACK) {
                neighboursAhead = getNeighboursRow(neighbours, 1 + direction);
            }
            else {
                neighboursAhead = getNeighboursColumn(neighbours, 1 + direction);
            }
            return neighboursAhead;
        }

        private QuaxTile[] getNeighboursRow(QuaxTile[][] neighbours, int index) {
        	assert neighbours.length > index && index >= 0;

            return neighbours[index];
        }

        private QuaxTile[] getNeighboursColumn(QuaxTile[][] neighbours, int index) {
        	assert neighbours.length == NEIGHBOURING_OCTAGON_SQUARE_LENGTH && index >= 0;

            QuaxTile[] column = new QuaxTile[NEIGHBOURING_OCTAGON_SQUARE_LENGTH];
            for (int i = 0; i < NEIGHBOURING_OCTAGON_SQUARE_LENGTH; i++) {
                column[i] = neighbours[i][index];
            }
            return column;
        }

    }
}

