package controller;

import javafx.stage.Stage;
import model.QuaxBoard;
import player.QuaxPlayer;
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
	}
	
	public QuaxPlayer curPlayer() {
		return players[moveNumber % 2];
	}
	
}
