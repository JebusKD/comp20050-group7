package player.bothelpers;

import java.util.*;

import model.QuaxBoard;
import static player.BotPlayer.MAX_STRATEGIES;
import types.*;


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

    private void assignStrategyValue(QuaxTile t, int value) {
        initialStrategy.assignStrategyValue(t, value);
    }



    // TODO - Rename this, or give an explanation
    public void improveStrategy() {
        for (QuaxTile tile : smarterBoard) {
            if (tile.isFree() && isLowPriority(tile)) {

                if (initialStrategy.isLowPriorityRhombus(tile, smarterBoard)) {
                    assignStrategyValue(tile, 0);
                }

                else if (defendsVulnerableRhombuses(tile, smarterBoard)) {
                    upgradeStrategy(tile, 2, 5);
                }

                else if (exploitsVulnerableRhombuses(tile, smarterBoard)) {
                    upgradeStrategy(tile, 2, 5);
                }

                else if (createsOnlyOneVulnerableRhombus(tile, smarterBoard)) {
                    downgradeStrategy(tile, 1, 2);
                }
            }
        }


        PathFinder pf = new PathFinder();
        pf.avoidWeakGroupContributions(2, 2);
        pf.diagonalPathfinding();
    }


    private boolean isLowPriority(QuaxTile t) {
        return t.getStrategyValue() < MAX_STRATEGIES - 2;
    }


    private void upgradeStrategy(QuaxTile t, int increase, int maximum) {
        int strategyValueToIncrease = t.getStrategyValue();
        int limit = Math.max(maximum, MAX_STRATEGIES);

        if (strategyValueToIncrease < limit) {
            if (strategyValueToIncrease + increase > limit) {
                increase = limit - strategyValueToIncrease;
            }

            assignStrategyValue(t, strategyValueToIncrease + increase);
        }
    }

    private void downgradeStrategy(QuaxTile t, int decrease, int minimum) {
        if (isLowPriority(t)) {
            int strategyToDecrease = t.getStrategyValue();
            int limit = Math.max(minimum, 0);

            if (strategyToDecrease > limit) {
                if (strategyToDecrease - decrease < limit) {
                    decrease = strategyToDecrease - limit;
                }
                assignStrategyValue(t, strategyToDecrease - decrease);
            }
        }
    }


    // TODO - Explain these
    private boolean defendsVulnerableRhombuses(QuaxTile t, QuaxBoard b) {
        QuaxBoard copy = new QuaxBoard(b);
        copy.skipTurn();

        return exploitsVulnerableRhombuses(t, copy);
    }

    private boolean exploitsVulnerableRhombuses(QuaxTile t, QuaxBoard b) {
        boolean result = false;

        if (b.validMove(t)) {
            result = changeInVulnerableRhombuses(t, b) >= 2;
        }

        return result;
    }


    private boolean createsOnlyOneVulnerableRhombus(QuaxTile t, QuaxBoard b) {
        return changeInVulnerableRhombuses(t, b) == 1;
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



    private class PathFinder {

        private void avoidWeakGroupContributions(int decrease, int minimum) {
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

        private List<QuaxTileGroup> ownedNearbyGroups(QuaxTile t, QuaxBoard b) {
            return removeOpponentGroups(nearbyTileGroups(t, b));
        }

        // TODO - Correlate with getAdjacentGroups from QuaxBoard?
        private List<QuaxTileGroup> nearbyTileGroups(QuaxTile t, QuaxBoard b) {
            LinkedList<QuaxTileGroup> groups = new LinkedList<>();
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
            LinkedList<QuaxTileGroup> copy = new LinkedList<>(list);
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
            // TODO Octagon iterator
            for (QuaxTile t : smarterBoard) {
                if (t instanceof Octagon center) {
                    if (center.isSameColour(botColour())) {
                        QuaxTile[][] neighbours = smarterBoard.getSquareOctagonNeighbours(center);

                        adjustStrategyIfPathBlocked(neighbours);
                    }
                }
            }
        }

        private void adjustStrategyIfPathBlocked(QuaxTile[][] neighbours) {
            for (int i = -1; i <= 1; i++) {
                if (opponentBlockingPath(neighbours, i)) {
                    for (QuaxTile ahead : neighboursAhead(neighbours, i)) {
                        if (ahead.tileExists() && ahead.getStrategyValue() == 3) {
                            upgradeStrategy(ahead, 1, 4);
                        }
                    }
                }
            }
        }


        private boolean opponentBlockingPath(QuaxTile[][] neighbours, int direction) {
            assert direction == -1 || direction == 1;
            boolean result = false;

            QuaxTile[] pathAhead = neighboursAhead(neighbours, direction);

            if (pathAhead[1].tileExists()) {
                result = pathAhead[1].isOpponentColour(botColour());
            }

            return result;
        }


        private QuaxTile[] neighboursAhead(QuaxTile[][] neighbours, int direction) {
            assert neighbours.length == 3 && neighbours[0].length == 3 && (direction == -1 || direction == 1);

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

