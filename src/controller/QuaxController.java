package controller;

import java.util.Random;
import java.util.concurrent.Executor;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.stage.Stage;
import model.QuaxBoard;
import player.*;
import types.*;
import userinterface.*;

public class QuaxController {
	
	static final Random RNG = new Random();

	private final UserInterface ui;

	private QuaxBoard board;
	
	private final QuaxPlayer[] players;
	
	private final Executor executor;
	
	public QuaxController(QuaxPlayer p1, QuaxPlayer p2) {
		players = new QuaxPlayer[2];
		
		this.ui = new EmptyUserInterface();
		this.executor = new SingleThreadExecutor();
		
		
		startGame(p1, p2);
	}
	
	public QuaxController(Stage stage) {
		this(stage, true);
	}
	
	public QuaxController(Stage stage, boolean againstBot) {
		ui = new QuaxUserInterface(stage);
		
		players = new QuaxPlayer[2];
		
		this.executor = new JavaFXThreadedExecutor();
		
		QuaxEventHandler.setup(this, stage);
		
		if (againstBot) startGameAgainstBot();
		else startTwoPlayerGame();
	}
	
	private void initialiseJavaFX() {
		
	}

    // TODO Keep for testing - Remove on final submission
	public void startTwoPlayerGame() {
		
		QuaxPlayer p1 = new HumanPlayer();
		QuaxPlayer p2 = new HumanPlayer();
		
		startGame(p1, p2);
	}
	
	private void startGame(QuaxPlayer p1, QuaxPlayer p2) {
		this.board = new QuaxBoard();
		
		players[0] = p1;
		players[1] = p2;
		
		p1.setColour(QuaxTileColour.BLACK);
		p2.setColour(QuaxTileColour.WHITE);
		
		p1.setController(this);
		p2.setController(this);
		
		ui.setBoard(board);
	
		curPlayer().movePrompt(board);
	}
	
	public void startGameAgainstBot() {
		QuaxPlayer human = new HumanPlayer();
		QuaxPlayer bot = new BogoBot();
		
		if (RNG.nextInt() % 2 == 0) {
			startGame(human, bot);
		}
		else {
			startGame(bot, human);
		}
	}
	
	public QuaxPlayer curPlayer() {
		return players[getMoveNumber() % 2];
	}
	
	// for testing purposes
	public QuaxTileColour getPlayerColour(int i){
		if (i == 0 || i == 1) return players[i].getColour();
		else throw new IllegalArgumentException("Only players 0 and 1 exist.");
	}
	
	public int getMoveNumber() {
		return board.getMoveNumber();
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
			ui.setPieRuleVisibility(false);
			curPlayer().movePrompt(getBoard());
			return true;
		}
		else return false;
	}
	
	public Executor getExecutor() {
		return this.executor;
	}
	
	public void setPieRuleVisibility(boolean visibility) {
		ui.setPieRuleVisibility(visibility);
	}
	
	public static class SingleThreadExecutor implements Executor {
		public void execute(Runnable r) {
			r.run();
		}
	}
	
	public static class JavaFXThreadedExecutor implements Executor {
		public void execute(Runnable r) {
			new Thread(r).start();
		}
	}

}
