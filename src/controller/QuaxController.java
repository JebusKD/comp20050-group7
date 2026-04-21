package controller;

import java.util.Random;
import java.util.concurrent.Executor;

import javafx.application.Platform;
import javafx.stage.Stage;

import model.QuaxBoard;
import player.*;
import types.*;
import userinterface.*;

public class QuaxController {

	static final Random RNG = new Random();
	private final Executor executor;

	private final UserInterface userInterface;
	private QuaxBoard board;
	private final QuaxPlayer[] players;


	public QuaxController(QuaxPlayer p1, QuaxPlayer p2) {
		this.players = new QuaxPlayer[2];
		this.userInterface = new EmptyUserInterface();
		this.executor = new SingleThreadExecutor();
		
		startGame(p1, p2);
	}
	
	public QuaxController(Stage stage) {
		this(stage, true);
	}
	
	public QuaxController(Stage stage, boolean againstBot) {
		this.players = new QuaxPlayer[2];
		this.userInterface = new QuaxUserInterface(stage);
		this.executor = new JavaFXThreadedExecutor();
		
		QuaxEventHandler.setup(this, stage);
		if (againstBot) {
			startGameAgainstBot();
		}
		else {
			startTwoPlayerGame();
		}
	}

    // TODO Keep for testing - Remove on final submission
	public void startTwoPlayerGame() {
		QuaxPlayer p1 = new HumanPlayer();
		QuaxPlayer p2 = new HumanPlayer();
		
		startGame(p1, p2);
	}
	
	private void startGame(QuaxPlayer p1, QuaxPlayer p2) {
		this.board = new QuaxBoard();
		this.players[0] = p1;
		this.players[1] = p2;
		
		p1.setColour(QuaxTileColour.BLACK);
		p2.setColour(QuaxTileColour.WHITE);
		
		p1.setController(this);
		p2.setController(this);
		
		userInterface.setBoard(board);
	
		curPlayer().movePrompt(board);
	}

	// TODO - Update new BotPlayer()
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
		assert i == 0 || i == 1;
		return players[i].getColour();
	}
	
	public int getMoveNumber() {
		return board.getMoveNumber();
	}
	
	public void makeMove(QuaxCoordinate coords) {
		QuaxTileColour c = curPlayer().getColour();
		if (board.validMove(coords, c)) {
			board.makeMove(coords, c);

			userInterface.updateFromPreviousMove(board);

			didMoveWin(c);
		}
	}

	// TODO - Rename method?
	private void didMoveWin(QuaxTileColour c) {
		if (board.checkForWinningMove()) {
			userInterface.showWinLabel(c);
			userInterface.hideTurnTracker();
		}
		else {
			curPlayer().movePrompt(board);
		}
	}

	public QuaxBoard getBoard() {
		return this.board;
	}

	public void doPieRule() {
		if (board.attemptPieRule()) {
			players[0].setColour(QuaxTileColour.WHITE);
			players[1].setColour(QuaxTileColour.BLACK);

			userInterface.setPieRuleVisibility(false);
			curPlayer().movePrompt(getBoard());
		}
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
