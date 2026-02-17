package controller;

import javafx.stage.Stage;
import model.QuaxBoard;
import player.HumanPlayer;
import player.QuaxPlayer;
import types.QuaxTileColour;
import userinterface.QuaxUserInterface;

public class QuaxController {
	
	private QuaxUserInterface ui;

	private QuaxBoard board;
	
	private QuaxPlayer[] players;
	int moveNumber;
	
	public QuaxController(Stage stage) {
		ui = new QuaxUserInterface(stage);
		
		players = new QuaxPlayer[2];
		
		startTwoPlayerGame();
	}
	
	public void startTwoPlayerGame() {
		this.board = new QuaxBoard();
		ui.setBoard(board);
		
		players[0] = new HumanPlayer("Player 1", QuaxTileColour.BLACK);
		players[1] = new HumanPlayer("Player 2", QuaxTileColour.WHITE);
		moveNumber = 0;
		
		newTurn();
	}
	
	public QuaxPlayer curPlayer() {
		return players[moveNumber % 2];
	}
	
	public void makeMove(int[] move) {
		board.getOctagon(move[0], move[1]).setColour(curPlayer().getColour());
	}
	
	public void newTurn() {
		
		int[] move = curPlayer().movePrompt();
		moveNumber++;
		
		makeMove(move);
		
		newTurn();
	}
}
