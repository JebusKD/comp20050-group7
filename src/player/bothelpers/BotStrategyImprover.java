package player.bothelpers;

import java.util.*;

import model.QuaxBoard;
import types.*;
import static types.StrategyValue.*;


class BotStrategyImprover {

    private final QuaxBoard smarterBoard;
    private final StrategyBuilder initialStrategy;


    BotStrategyImprover(QuaxBoard board, StrategyBuilder startingStrategy) {
        this.smarterBoard = board;
        this.initialStrategy = startingStrategy;
    }


    private QuaxTileColour botColour() {
        return initialStrategy.botColour();
    }

    private void assignStrategyValue(QuaxTile tile, StrategyValue value) {
        initialStrategy.assignStrategyValue(tile, value);
    }



    /* Given the initial strategy from BasicBotStrategist,
     *  adjust the current strategy values on the board depending on how
     *  important a move would be.
     */
    public void improveStrategy() {
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
                    downgradeStrategy(tile, 1, KEY); // TODO - This was SV2 before, mistake?
                }
            }
        }


        PathFinder pf = new PathFinder();
        pf.avoidWeakGroupContributions(2, LOW);
        pf.diagonalPathfinding();
    }

    // TODO Move into QuaxTile? - Senan) Also LoD?
    private static boolean isLowPriority(QuaxTile tile) {
        return tile.getStrategyValue().isLowPriority();
    }


    private void upgradeStrategy(QuaxTile valuableTile, int increase, StrategyValue max) {
        int strategyValueToIncrease = valuableTile.getStrategyValue().toInt();
        int maximum = max.toInt();
        int limit = Math.max(maximum, MAX_STRATEGIES);

        if (strategyValueToIncrease < limit) {
            if (strategyValueToIncrease + increase > limit) {
                increase = limit - strategyValueToIncrease;
            }

            assignStrategyValue(valuableTile, fromInt(strategyValueToIncrease + increase));
        }
    }

    private void downgradeStrategy(QuaxTile lowPriorityTile, int decrease, StrategyValue min) {
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


    // TODO - Explain these
    private boolean defendsVulnerableRhombuses(QuaxTile nextTileMove, QuaxBoard board) {
        QuaxBoard copy = new QuaxBoard(board);
        copy.skipTurn();

        return exploitsVulnerableRhombuses(nextTileMove, copy);
    }

    private boolean exploitsVulnerableRhombuses(QuaxTile nextTileMove, QuaxBoard board) {
        boolean result = false;

        if (board.validMove(nextTileMove)) {
            result = changeInVulnerableRhombuses(nextTileMove, board) >= 2;
        }

        return result;
    }


    private boolean createsOnlyOneVulnerableRhombus(QuaxTile nextTileMove, QuaxBoard board) {
        return changeInVulnerableRhombuses(nextTileMove, board) == 1;
    }


    private int changeInVulnerableRhombuses(QuaxTile nextTileMove, QuaxBoard board) {
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
        Iterator<QuaxCoordinate> iterator = QuaxBoard.rhombusCoordinateIterator();
        int count = 0;

        while (iterator.hasNext()) {
            if (board.isValidRhombusForBoth(iterator.next())) {
                count++;
            }
        }

        return count;
    }



    private class PathFinder {

        private void avoidWeakGroupContributions(int decrease, StrategyValue minimum) {
            for (QuaxTile tile : smarterBoard) {
                List<QuaxTileGroup> nearbyGroupsBefore = ownedNearbyGroups(tile, smarterBoard);

                // TODO better comment - Joining two groups together is fine
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
            return removeOpponentGroups(nearbyTileGroups(centre, board));
        }

        // TODO - Correlate with getAdjacentGroups from QuaxBoard?
        private List<QuaxTileGroup> nearbyTileGroups(QuaxTile centreTile, QuaxBoard board) {
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
        	Iterator<Octagon> iterator = smarterBoard.octagonIterator();

        	while (iterator.hasNext()) {
        		Octagon centre = iterator.next();
                if (centre.isSameColour(botColour())) {
                    QuaxTile[][] neighbours = smarterBoard.getSquareOctagonNeighbours(centre);

                    adjustStrategyIfPathBlocked(neighbours);
                }
            }
        }

        private void adjustStrategyIfPathBlocked(QuaxTile[][] neighbours) {
            for (int i = -1; i <= 1; i++) {
                if (opponentBlockingPath(neighbours, i)) {
                    for (QuaxTile ahead : neighboursAhead(neighbours, i)) {
                        if (ahead.tileExists() && ahead.getStrategyValue() == BLOCKING) {
                            upgradeStrategy(ahead, 1, PROGRESS);
                        }
                    }
                }
            }
        }


        private boolean opponentBlockingPath(QuaxTile[][] neighbours, int direction) {
            assert direction >= -1 && direction <= 1;
            boolean result = false;

            QuaxTile[] pathAhead = neighboursAhead(neighbours, direction);

            if (pathAhead[1].tileExists()) {
                result = pathAhead[1].isOpponentColour(botColour());
            }

            return result;
        }


        private QuaxTile[] neighboursAhead(QuaxTile[][] neighbours, int direction) {
            assert neighbours.length == 3 && neighbours[0].length == 3 && (direction >= -1 && direction <= 1);

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
            return neighbours[index];
        }

        private QuaxTile[] getNeighboursColumn(QuaxTile[][] neighbours, int index) {
            QuaxTile[] column = new QuaxTile[3];
            for (int i = 0; i < 3; i++) {
                column[i] = neighbours[i][index];
            }
            return column;
        }

    }
}

