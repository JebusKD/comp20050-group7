package player.bothelpers;

import java.util.*;

import model.QuaxBoard;
import types.*;


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

    private void assignStrategyValue(QuaxTile t, StrategyValue value) {
    	assert t != null && t.tileExists() && value != null;
        initialStrategy.assignStrategyValue(t, value);
    }



    /* Given the initial strategy from BasicBotStrategist,
     *  adjust the current strategy values on the board depending on how
     *  important a move would be
     */
    public void improveStrategy() {
    	assert smarterBoard != null && initialStrategy != null;
    	
        for (QuaxTile tile : smarterBoard) {
            if (tile.isFree() && isLowPriority(tile)) {

                if (initialStrategy.isLowPriorityRhombus(tile, smarterBoard)) {
                    assignStrategyValue(tile, StrategyValue.IGNORE);
                }

                else if (defendsVulnerableRhombuses(tile, smarterBoard)) {
                    upgradeStrategy(tile, 2, StrategyValue.KEY);
                }

                else if (exploitsVulnerableRhombuses(tile, smarterBoard)) {
                    upgradeStrategy(tile, 2, StrategyValue.KEY);
                }

                else if (createsOnlyOneVulnerableRhombus(tile, smarterBoard)) {
                    downgradeStrategy(tile, 1, StrategyValue.KEY); // TODO - This was SV2 before
                }
            }
        }


        PathFinder pf = new PathFinder();
        pf.avoidWeakGroupContributions(2, StrategyValue.LOW);
        pf.diagonalPathfinding();
    }

    // TODO Move into QuaxTile? - Senan) Also LoD?
    private static boolean isLowPriority(QuaxTile t) {
    	assert t != null && t.tileExists();
        return t.getStrategyValue().isLowPriority();
    }


    private void upgradeStrategy(QuaxTile t, int increase, StrategyValue max) {
    	assert t != null && t.tileExists() && increase > 0 && max != null;
    	
        int strategyValueToIncrease = t.getStrategyValue().toInt();
        int maximum = max.toInt();
        int limit = Math.max(maximum, StrategyValue.MAX_STRATEGIES);

        if (strategyValueToIncrease < limit) {
            if (strategyValueToIncrease + increase > limit) {
                increase = limit - strategyValueToIncrease;
            }

            assignStrategyValue(t, StrategyValue.fromInt(strategyValueToIncrease + increase));
        }
    }

    private void downgradeStrategy(QuaxTile t, int decrease, StrategyValue min) {
    	assert t != null && t.tileExists() && decrease > 0 && min != null;
    	
        if (isLowPriority(t)) {
            int strategyToDecrease = t.getStrategyValue().toInt();
            int minimum = min.toInt();
            int limit = Math.max(minimum, 0);

            if (strategyToDecrease > limit) {
                if (strategyToDecrease - decrease < limit) {
                    decrease = strategyToDecrease - limit;
                }
                assignStrategyValue(t, StrategyValue.fromInt(strategyToDecrease - decrease));
            }
        }
    }


    // TODO - Explain these
    private boolean defendsVulnerableRhombuses(QuaxTile t, QuaxBoard b) {
    	assert t != null && t.tileExists() && b != null;
    	
        QuaxBoard copy = new QuaxBoard(b);
        copy.skipTurn();

        return exploitsVulnerableRhombuses(t, copy);
    }

    private boolean exploitsVulnerableRhombuses(QuaxTile t, QuaxBoard b) {
    	assert t != null && t.tileExists() && b != null;
    	
        boolean result = false;

        if (b.validMove(t)) {
            result = changeInVulnerableRhombuses(t, b) >= 2;
        }

        return result;
    }


    private boolean createsOnlyOneVulnerableRhombus(QuaxTile t, QuaxBoard b) {
    	assert t != null && t.tileExists() && b != null;
        return changeInVulnerableRhombuses(t, b) == 1;
    }


    private int changeInVulnerableRhombuses(QuaxTile t, QuaxBoard b) {
    	assert t != null && t.tileExists() && b != null;
    	
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
    	assert b != null;
    	
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

        private void avoidWeakGroupContributions(int decrease, StrategyValue minimum) {
        	assert smarterBoard != null && decrease > 0 && minimum != null;
        	
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
        	assert t != null && t.tileExists() && b != null;
        	
            return removeOpponentGroups(nearbyTileGroups(t, b));
        }

        // TODO - Correlate with getAdjacentGroups from QuaxBoard?
        private List<QuaxTileGroup> nearbyTileGroups(QuaxTile t, QuaxBoard b) {
        	assert t != null && t.tileExists() && b != null;
        	
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
        	assert list != null;
        	
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
        	assert smarterBoard != null;
        	
        	Iterator<Octagon> iterator = smarterBoard.octagonIterator();

        	while (iterator.hasNext()) {
        		Octagon center = iterator.next();
                if (center.isSameColour(botColour())) {
                    QuaxTile[][] neighbours = smarterBoard.getSquareOctagonNeighbours(center);

                    adjustStrategyIfPathBlocked(neighbours);
                }
            }
        }

        private void adjustStrategyIfPathBlocked(QuaxTile[][] neighbours) {
        	assert neighbours.length == 3 && neighbours[0].length == 3;
        	
            for (int i = -1; i <= 1; i++) {
                if (opponentBlockingPath(neighbours, i)) {
                    for (QuaxTile ahead : neighboursAhead(neighbours, i)) {
                        if (ahead.tileExists() && ahead.getStrategyValue() == StrategyValue.BLOCKING) {
                            upgradeStrategy(ahead, 1, StrategyValue.PROGRESS);
                        }
                    }
                }
            }
        }


        private boolean opponentBlockingPath(QuaxTile[][] neighbours, int direction) {
            assert ( direction == -1 || direction == 1 ) && neighbours != null;
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
        	assert neighbours.length > index && index > 0;
        	
            return neighbours[index];
        }
        // TODO maybe "3" is a magic number here?
        private QuaxTile[] getNeighboursColumn(QuaxTile[][] neighbours, int index) {
        	assert neighbours.length == 3 && index > 0;
        	
            QuaxTile[] column = new QuaxTile[3];
            for (int i = 0; i < 3; i++) {
                column[i] = neighbours[i][index];
            }
            return column;
        }

    }
}

