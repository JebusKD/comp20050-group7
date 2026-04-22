package bot;

import model.QuaxBoard;
import types.*;


public class FrankenBot extends PathBot {
	private QuaxTileStrategyGroup[] stratGroups;
	
	public FrankenBot() {
		super();
		stratGroups = new QuaxTileStrategyGroup[6];
		clearStratGroups();
	}
	
	@Override
	protected void subclassAfterTurnHook() {
		betterBogoFallback(getSubmissionBoard());
	}
	
	public void removeFromAllGroups(QuaxTile newTile) {
        for (QuaxTileStrategyGroup g : stratGroups) {
        	g.removeTile(newTile);
        }
    }

    public QuaxTileStrategyGroup getStratGroup(int i){
        return stratGroups[i+i];
    }
	
	public void clearStratGroups() {
		for (int i = 0; i < stratGroups.length; i++) {
			
			stratGroups[i] = new QuaxTileStrategyGroup();

		}
    }
	
	public void assignStratGroup(QuaxTile newTile) {
		stratGroups[newTile.getStrategyValue() + 1].addTile(newTile);
    }
	
	public void betterBogoFallback(QuaxBoard b) {
		clearStratGroups();
        
        for (QuaxTile t : b) {
            t.setStrategyValue(IGNORE_VALUE);
            if (!b.validMove(t.getCoordinates(), this.getColour())) {
                continue; // TODO no continues or breaks in loops
            }
            t.setStrategyValue(1);
            assignStratGroup(t);
        }

        for (QuaxTile t : b) {
            if (t instanceof Octagon && !b.validMove(t.getCoordinates(), this.getColour())) {
                QuaxTile[][] neighbours = b.neighbours(t.getCoordinates());
                for (QuaxTile[] row : neighbours) {
                    for (QuaxTile neighbour : row) {
                        if (neighbour instanceof Octagon
                                && b.validMove(neighbour.getCoordinates(), this.getColour())) {
                            neighbour.setStrategyValue(2);
                            assignStratGroup(neighbour);
                            if (t.getColour() == QuaxTileColour.BLACK) {
                                if (neighbour == neighbours[1][0] || neighbour == neighbours[1][2]) {
                                    if(this.getColour() == QuaxTileColour.BLACK){
                                        neighbour.setStrategyValue(4);
                                        assignStratGroup(neighbour);
                                    }else{
                                        neighbour.setStrategyValue(3);
                                        assignStratGroup(neighbour);
                                    }
                                }
                            } else if (t.getColour() == QuaxTileColour.WHITE) {
                                if (neighbour == neighbours[0][1] || neighbour == neighbours[2][1]) {
                                    if(this.getColour() == QuaxTileColour.WHITE){
                                        neighbour.setStrategyValue(4);
                                        assignStratGroup(neighbour);
                                    }
                                    neighbour.setStrategyValue(3);
                                    assignStratGroup(neighbour);
                                }
                            }
                        }
                    }
                }
            }
            if (t instanceof Rhombus && b.validMove(t.getCoordinates(), this.getColour())) {
                t.setStrategyValue(3);
                assignStratGroup(t);
                if (b.validMove(t.getCoordinates(), this.getColour().flip())){
                    t.setStrategyValue(4);
                    assignStratGroup(t);
                }

                QuaxTileColour humanCol =  this.getColour().flip();
                if(b.validMove(t.getCoordinates(), humanCol)){
                    if(checkForWin(t.getCoordinates(),b,humanCol)){
                        t.setStrategyValue(5);
                        assignStratGroup(t);
                    }
                }
                if(checkForWin(t.getCoordinates(),b,this.getColour())){
                    t.setStrategyValue(6);
                    assignStratGroup(t);
                }

            }
        }
        for(QuaxTile t: b){
            if(b.validMove(t.getCoordinates(), this.getColour())){
                    QuaxTileColour humanCol =  this.getColour().flip();
                    if(checkForWin(t.getCoordinates(),b,humanCol)){
                        t.setStrategyValue(5);
                        assignStratGroup(t);
                    }

                if(checkForWin(t.getCoordinates(),b,this.getColour())){
                    t.setStrategyValue(6); // bot winning takes priority
                    assignStratGroup(t);
                }

            }
        }

    }
	
    public boolean checkForWin(QuaxCoordinate c, QuaxBoard b,QuaxTileColour colour) {
        QuaxBoard copyBoard = new QuaxBoard(b);
        copyBoard.attemptMakeMove(c,colour);
        return copyBoard.checkForWinningMove();
    }
}
