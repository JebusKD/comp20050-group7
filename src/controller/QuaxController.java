package controller;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.stage.Stage;
import model.QuaxBoard;
import player.HumanPlayer;
import player.QuaxPlayer;
import types.QuaxCoordinate;
import types.QuaxCoordinateEvent;
import types.QuaxTileColour;
import userinterface.QuaxUserInterface;

public class QuaxController {
	
	public static final EventType<QuaxCoordinateEvent> MOVE_SUBMITTED_EVENT = new EventType<QuaxCoordinateEvent>("quaxMoveSubmittedEvent");
	public static final EventType<QuaxCoordinateEvent> TILE_CLICKED_EVENT = new EventType<QuaxCoordinateEvent>("tileClickedEvent");
	
	private Stage stage;
	
	private QuaxUserInterface ui;

	private QuaxBoard board;
	
	private QuaxPlayer[] players;
	int moveNumber;
	
	public QuaxController(Stage stage) {
		this.stage = stage;
		ui = new QuaxUserInterface(stage);
		
		players = new QuaxPlayer[2];
		
		stage.addEventHandler(QuaxController.TILE_CLICKED_EVENT, new EventHandler<QuaxCoordinateEvent>() {
			@Override
			public void handle(QuaxCoordinateEvent coords) {
				
				if (curPlayer() instanceof HumanPlayer) {
					makeMove(coords.coords());
				}
				
			}
			
		});
		
		stage.addEventHandler(QuaxController.MOVE_SUBMITTED_EVENT, new EventHandler<QuaxCoordinateEvent>() {
			@Override
			public void handle(QuaxCoordinateEvent coords) {
					makeMove(coords.coords());
			}
		});
		
		startTwoPlayerGame();
	}
	
	public void startTwoPlayerGame() {
		
		QuaxPlayer p1 = new HumanPlayer("Player 1", QuaxTileColour.BLACK, stage);
		QuaxPlayer p2 = new HumanPlayer("Player 2", QuaxTileColour.WHITE, stage);
		
		startGame(p1, p2);
	}
	
	private void startGame(QuaxPlayer p1, QuaxPlayer p2) {
		this.board = new QuaxBoard();
		ui.setBoard(board);
		
		players[0] = p1;
		players[1] = p2;
		moveNumber = 0;
		
		curPlayer().movePrompt(board);
	}
	
	public QuaxPlayer curPlayer() {
		return players[moveNumber % 2];
	}
	
	public void makeMove(QuaxCoordinate coords) {
		QuaxTileColour c = curPlayer().getColour();
		if (board.validMove(coords, c)) {
			board.makeMove(coords, c);
			moveNumber++;

			ui.fetchPreviousMove(board);
			
			curPlayer().movePrompt(board);
		}
	}
	
}
