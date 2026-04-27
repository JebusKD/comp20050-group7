package player;

import java.util.ArrayList;
import java.util.Random;
import java.util.SplittableRandom;

import model.QuaxBoard;
import types.*;

public abstract class BotPlayer extends QuaxPlayer {
	
	private static boolean botHaste = false;
	
	private static final long MIN_THINKING_TIME = 1000;
	static final int IGNORE_VALUE = Integer.MIN_VALUE;
	
	static final Random RNG = new Random();
    private QuaxTileStrategyGroup stratOne;
    private QuaxTileStrategyGroup stratTwo;
    private QuaxTileStrategyGroup stratThree;
    private QuaxTileStrategyGroup stratFour;
    private QuaxTileStrategyGroup stratFive;
    private QuaxTileStrategyGroup stratSix;

    private QuaxTileStrategyGroup[] stratGroups;
    
    private long startThinkingTime;

	public BotPlayer() {
		super();
        this.stratOne = new QuaxTileStrategyGroup();
        this.stratTwo = new QuaxTileStrategyGroup();
        this.stratThree = new QuaxTileStrategyGroup();
        this.stratFour = new QuaxTileStrategyGroup();
        this.stratFive = new QuaxTileStrategyGroup();
        this.stratSix = new QuaxTileStrategyGroup();
	}
	
	protected abstract QuaxCoordinate computeMove(QuaxBoard b);
	
	/* Given a QuaxBoard b containing strategy values, chooses the move with
	 	the highest strategy value and returns it. If there is a tie, chooses
	 	one move at random of the highest strategy values.
	 */
	public  QuaxCoordinate decideMove(QuaxBoard b) {
        int val = chooseStrategyValue();
        QuaxTileStrategyGroup choice = getStratGroup(val);

        if(b.getMoveNumber() == 0){// if theres no moves, all strat vals are 1, so choose from this group
            choice = getStratGroup(1);
        }
        if(choice == getStratGroup(4) && choice.size() == 0){
            choice = getStratGroup(3);
         
        }
        if(choice == getStratGroup(3) && choice.size() == 0){
            choice = getStratGroup(2);
           
        }

        if(stratSix != null && stratSix.size() != 0){ //if bot can win, that is highest priority
            choice = getStratGroup(6);
        }
        else if(stratFive != null && stratFive.size() != 0){ //else block the opponents win
            choice = getStratGroup(5);
        }

		ArrayList<QuaxCoordinate> candidateMoves = new ArrayList<QuaxCoordinate>();

        for (QuaxTile t : choice) {
            candidateMoves.add(t.getCoordinates());
        }

        if(candidateMoves.isEmpty()){ //just in case candiateMoves is somehow empty
            for (QuaxTile t : b) {
            	if (b.validMove(t.getCoordinates(), this.getPlayerColour())) {
            		candidateMoves.add(t.getCoordinates());
            	}
            }
        }

		int index = Math.abs(RNG.nextInt()) % candidateMoves.size();
		return candidateMoves.get(index);
		
	}
    /* 5% chance of strat val 1
        20% chance of strat val 2
        25% chance of strat val 3
        50% chance of strat 4
        */
    public int chooseStrategyValue(){
        SplittableRandom random = new SplittableRandom();
        int probability= random.nextInt(1,101);
        if(probability <= 3) return 1;
        if(probability <= 15) return 2;
        if(probability <= 45) return 3;
        return 4;
    }
    public void assignStratGroup(QuaxTile newTile) {
        if(newTile.getStrategyValue() == 1){
            this.stratOne.addTile(newTile);
        }
        else if(newTile.getStrategyValue() == 2){
            removeFromAllGroups(newTile);
            this.stratTwo.addTile(newTile);
        }
        else if(newTile.getStrategyValue() == 3){
            removeFromAllGroups(newTile);
            this.stratThree.addTile(newTile);
        }
        else if(newTile.getStrategyValue() == 4){
            removeFromAllGroups(newTile);
            this.stratFour.addTile(newTile);
        }
        else if(newTile.getStrategyValue() == 5){
            removeFromAllGroups(newTile);
            this.stratFive.addTile(newTile);
        }
        else if(newTile.getStrategyValue() == 6){
            removeFromAllGroups(newTile);
            this.stratSix.addTile(newTile);
        }
    }

    public void removeFromAllGroups(QuaxTile newTile) {
        this.stratOne.removeTile(newTile);
        this.stratTwo.removeTile(newTile);
        this.stratThree.removeTile(newTile);
        this.stratFour.removeTile(newTile);
        this.stratFive.removeTile(newTile);
        this.stratSix.removeTile(newTile);
    }

    public QuaxTileStrategyGroup getStratGroup(int i){
        if(i == 1) return this.stratOne;
        if(i == 2) return this.stratTwo;
        if(i == 3) return this.stratThree;
        if(i == 4) return this.stratFour;
        if(i == 5) return this.stratFive;
        if(i == 6) return this.stratSix;
        return null;
    }

    public void clearStratGroups() {
        this.stratOne = new QuaxTileStrategyGroup();
        this.stratTwo = new QuaxTileStrategyGroup();
        this.stratThree = new QuaxTileStrategyGroup();
        this.stratFour = new QuaxTileStrategyGroup();
        this.stratFive = new QuaxTileStrategyGroup();
        this.stratSix = new QuaxTileStrategyGroup();
    }
	
	public void setAll(QuaxBoard b, int val) {
		for (QuaxTile t : b) {
			if (b.validMove(t.getCoordinates(), this.getPlayerColour())) {
				t.setStrategyValue(val);
			}
			else {
				t.setStrategyValue(IGNORE_VALUE);
			}
		}
	}

    // how the bot decides strat vals for the tiles
    public void setUpStrategy(QuaxBoard b) {
        clearStratGroups();

        for (QuaxTile t : b) {
            t.setStrategyValue(IGNORE_VALUE);
            if (!b.validMove(t.getCoordinates(), this.getPlayerColour())) {
                continue;
            }
            t.setStrategyValue(1);
            assignStratGroup(t);
        }

        for (QuaxTile t : b) {
            if (t instanceof Octagon && !b.validMove(t.getCoordinates(), this.getPlayerColour())) {
                QuaxTile[][] neighbours = b.getNeighbours(t.getCoordinates());
                for (QuaxTile[] row : neighbours) {
                    for (QuaxTile neighbour : row) {
                        if (neighbour instanceof Octagon
                                && b.validMove(neighbour.getCoordinates(), this.getPlayerColour())) {
                            neighbour.setStrategyValue(2);
                            assignStratGroup(neighbour);
                            if (t.getTileColour() == QuaxTileColour.BLACK) {
                                if (neighbour == neighbours[1][0] || neighbour == neighbours[1][2]) {
                                    if(this.getPlayerColour() == QuaxTileColour.BLACK){
                                        neighbour.setStrategyValue(4);
                                        assignStratGroup(neighbour);
                                    }else{
                                        neighbour.setStrategyValue(3);
                                        assignStratGroup(neighbour);
                                    }
                                }
                            } else if (t.getTileColour() == QuaxTileColour.WHITE) {
                                if (neighbour == neighbours[0][1] || neighbour == neighbours[2][1]) {
                                    if(this.getPlayerColour() == QuaxTileColour.WHITE){
                                        neighbour.setStrategyValue(4);
                                        assignStratGroup(neighbour);
                                    }else {
                                        neighbour.setStrategyValue(3);
                                        assignStratGroup(neighbour);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (t instanceof Rhombus && b.validMove(t.getCoordinates(), this.getPlayerColour())) {
                t.setStrategyValue(4);
                assignStratGroup(t);
                if (b.validMove(t.getCoordinates(), this.getPlayerColour().flip())){
                    t.setStrategyValue(4);
                    assignStratGroup(t);
                }

               QuaxTileColour humanCol =  this.getPlayerColour().flip();
                if(b.validMove(t.getCoordinates(), humanCol)){
                    if(checkForWin(t.getCoordinates(),b,humanCol)){
                        t.setStrategyValue(5);
                        assignStratGroup(t);
                    }
                }
               if(checkForWin(t.getCoordinates(),b,this.getPlayerColour())){
                    t.setStrategyValue(6);
                    assignStratGroup(t);
                }

            }
        }
        for(QuaxTile t: b){
            if(b.validMove(t.getCoordinates(), this.getPlayerColour())){
                    QuaxTileColour humanCol =  this.getPlayerColour().flip();
                    if(checkForWin(t.getCoordinates(),b,humanCol)){
                        t.setStrategyValue(5);
                        assignStratGroup(t);
                    }

                if(checkForWin(t.getCoordinates(),b,this.getPlayerColour())){
                    t.setStrategyValue(6); // bot winning takes priority
                    assignStratGroup(t);
                }

            }
        }

    }

    public boolean checkForWin(QuaxCoordinate c, QuaxBoard b,QuaxTileColour colour) {
        QuaxBoard copyBoard = new QuaxBoard(b);
        copyBoard.makeMove(c,colour);
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
        	
        	while (!botHaste && System.currentTimeMillis() - startThinkingTime < MIN_THINKING_TIME);
            submitMove(move);
        });
	}
	
}
