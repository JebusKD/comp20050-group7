package controller;

import javafx.event.Event;
import javafx.event.EventType;
import javafx.stage.Stage;
import model.QuaxBoard;
import player.HumanPlayer;
import player.QuaxPlayer;
import types.QuaxCoordinateEvent;
import types.QuaxTileColour;
import userinterface.QuaxUserInterface;

public class QuaxController {
	
	public static final EventType<QuaxCoordinateEvent> MOVE_SUBMITTED_EVENT = new EventType<QuaxCoordinateEvent>("quaxMoveSubmittedEvent");
	
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
		
		players[0] = new HumanPlayer("Player 1", QuaxTileColour.BLACK, ui.getScene());
		players[1] = new HumanPlayer("Player 2", QuaxTileColour.WHITE, ui.getScene());
		moveNumber = 0;
		
		curPlayer().movePrompt();
	}
	
	public QuaxPlayer curPlayer() {
		return players[moveNumber % 2];
	}
	
	private void makeMove(int[] move) {
		board.getOctagon(move[0], move[1]).setColour(curPlayer().getColour());
	}
	
	
}
