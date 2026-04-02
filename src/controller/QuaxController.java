package controller;

import java.util.Random;
import java.util.concurrent.Executor;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.stage.Stage;
import model.QuaxBoard;
import player.BogoBot;
import player.HumanPlayer;
import player.QuaxPlayer;
import types.ButtonClickEvent;
import types.QuaxCoordinate;
import types.QuaxCoordinateEvent;
import types.QuaxTileColour;
import userinterface.QuaxUserInterface;

public class QuaxController {
	
	static final Random RNG = new Random();

	private final QuaxUserInterface ui;

	private QuaxBoard board;
	
	private final QuaxPlayer[] players;
	
	private final Executor executor;
	
	public QuaxController(QuaxPlayer p1, QuaxPlayer p2) {
		players = new QuaxPlayer[2];
		
		this.ui = null;
		
		this.executor = new SingleThreadExecutor();
		
		startGame(p1, p2);
	}
	
	public QuaxController(Stage stage) {
		ui = new QuaxUserInterface(stage);
		
		players = new QuaxPlayer[2];
		
		this.executor = new JavaFXThreadedExecutor();
		
		stage.addEventHandler(QuaxCoordinateEvent.TILE_CLICKED_EVENT, new EventHandler<>() {
			@Override
			public void handle(QuaxCoordinateEvent coords) {
				
				if (curPlayer() instanceof HumanPlayer) {
					makeMove(coords.coords());
				}
			}
		});
		
		stage.addEventHandler(ButtonClickEvent.PIE_RULE_CLICKED_EVENT, new EventHandler<ButtonClickEvent>() {
			@Override
			public void handle(ButtonClickEvent event) {
				if (curPlayer() instanceof HumanPlayer && doPieRule()) {
					ui.setPieRuleVisibility(false);
					
					curPlayer().movePrompt(board);
				}
			}
		});
		
		startGameAgainstBot();
	}

    // TODO Keep for testing - Remove on final submission
	public void startTwoPlayerGame() {
		
		QuaxPlayer p1 = new HumanPlayer("Player 1", QuaxTileColour.BLACK);
		QuaxPlayer p2 = new HumanPlayer("Player 2", QuaxTileColour.WHITE);
		
		startGame(p1, p2);
	}
	
	private void startGame(QuaxPlayer p1, QuaxPlayer p2) {
		this.board = new QuaxBoard();
		
		players[0] = p1;
		players[1] = p2;
		
		p1.setController(this);
		p2.setController(this);
		
		if (ui != null) {
			ui.setBoard(board);
			ui.setPieRuleVisibility(true);
		}
	
		curPlayer().movePrompt(board);
	}
	
	public void startGameAgainstBot() {
		if (RNG.nextInt() % 2 == 0) {
			QuaxPlayer human = new HumanPlayer("Player", QuaxTileColour.BLACK);
			QuaxPlayer bot = new BogoBot(QuaxTileColour.WHITE);
			
			startGame(human, bot);
		}
		else {
			QuaxPlayer human = new HumanPlayer("Player", QuaxTileColour.WHITE);
			QuaxPlayer bot = new BogoBot(QuaxTileColour.BLACK);
			
			startGame(bot, human);
		}
	}
	
	public QuaxPlayer curPlayer() {
		return players[board.getMoveNumber() % 2];
	}
	
	public void makeMove(QuaxCoordinate coords) {
		QuaxTileColour c = curPlayer().getColour();
		if (board.validMove(coords, c)) {
			board.makeMove(coords, c);

			ui.updateFromPreviousMove(board);
			
			if (board.checkForWinningMove()) {
				ui.showWinLabel(c);
				ui.hideTurnTracker();
			}
			else {
				curPlayer().movePrompt(board);
			}
		}
	}

	public QuaxBoard getBoard() {
		return this.board;
	}

	public boolean doPieRule() {
		if (board.attemptPieRule()) {
			players[0].setColour(QuaxTileColour.WHITE);
			players[1].setColour(QuaxTileColour.BLACK);
			return true;
		}
		else return false;
	}
	
	public Executor getExecutor() {
		return this.executor;
	}
	
	public static class SingleThreadExecutor implements Executor {
		public void execute(Runnable r) {
			r.run();
		}
	}
	
	public static class JavaFXThreadedExecutor implements Executor {
		public void execute(Runnable r) {
			Platform.runLater(r);
		}
	}

}
