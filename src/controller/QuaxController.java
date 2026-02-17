package controller;

import javafx.stage.Stage;
import model.QuaxBoard;
import userinterface.QuaxUserInterface;

public class QuaxController {
	
	private QuaxUserInterface ui;

	private QuaxBoard board;
	
	public QuaxController(Stage stage) {
		ui = new QuaxUserInterface(stage);
		
		startTwoPlayerGame();
	}
	
	public void startTwoPlayerGame() {
		this.board = new QuaxBoard();
		
		ui.setBoard(board);
	}
	
}
