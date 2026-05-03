package controller;

import java.util.concurrent.Executor;
import java.util.Random;
import javafx.application.Platform;
import javafx.stage.Stage;

import model.QuaxBoard;
import player.*;
import types.*;
import userinterface.*;


/*
 * Handle all technical aspects of the game
 */
public class QuaxController {

    public static final Random RNG = new Random();
    private final Executor quaxExecutor;
    private final Executor quaxMoveSubmitter;

    private final UserInterface quaxUserInterface;
    private QuaxBoard quaxBoard;
    private final QuaxPlayer[] quaxPlayers;


    // Start Game against Bot
    public QuaxController(Stage stage) {
        this(stage, true, false);
    }

    public QuaxController(Stage stage, boolean againstBot, boolean humanPlaysFirst) {
        this.quaxExecutor = new MultithreadedExecutor();
        this.quaxMoveSubmitter = new JavaFXPlatformExecutor();

        this.quaxUserInterface = new QuaxUserInterface(stage);
        this.quaxPlayers = new QuaxPlayer[2];

        QuaxEventHandler.setup(this, stage);

        if (humanPlaysFirst) {
            startGame(new HumanPlayer(), new BotPlayer());
        }
        else {
            // TODO - Remove on final submission
            if (againstBot) {
                startGameAgainstBot();
            }
            else {
                startTwoPlayerGame();
            }
        }
    }

    // Testing Constructor
    public QuaxController(QuaxPlayer p1, QuaxPlayer p2) {
        this.quaxExecutor = new SingleThreadExecutor();
        this.quaxMoveSubmitter = new SingleThreadExecutor();

        this.quaxPlayers = new QuaxPlayer[2];
        this.quaxUserInterface = new TestingEmptyInterface();

        startGame(p1, p2);
    }


    // TODO - Remove on final submission
    private void startTwoPlayerGame() {
        QuaxPlayer p1 = new HumanPlayer();
        QuaxPlayer p2 = new HumanPlayer();

        startGame(p1, p2);
    }

    private void startGameAgainstBot() {
        QuaxPlayer human = new HumanPlayer();
        QuaxPlayer bot = new BotPlayer();

        if (RNG.nextInt() % 2 == 0) {
            startGame(human, bot);
        }
        else {
            startGame(bot, human);
        }
    }


    private void startGame(QuaxPlayer p1, QuaxPlayer p2) {
        this.quaxBoard = new QuaxBoard();
        setQuaxPlayers(p1, p2);

        this.quaxUserInterface.setQuaxUIBoard(quaxBoard);

        currentPlayer().movePrompt(quaxBoard);
    }

    private void setQuaxPlayers(QuaxPlayer p1, QuaxPlayer p2) {
        this.quaxPlayers[0] = p1;
        this.quaxPlayers[1] = p2;

        quaxPlayers[0].setPlayerColour(QuaxTileColour.BLACK);
        quaxPlayers[1].setPlayerColour(QuaxTileColour.WHITE);

        quaxPlayers[0].setPlayerController(this);
        quaxPlayers[1].setPlayerController(this);
    }


    public QuaxPlayer currentPlayer() {
        return quaxPlayers[getMoveNumber() % 2];
    }

    public int getMoveNumber() {
        return quaxBoard.getMoveNumber();
    }

    public QuaxBoard getQuaxBoard() {
        return this.quaxBoard;
    }

    public Executor getQuaxExecutor() {
        return this.quaxExecutor;
    }

    public Executor getQuaxMoveSubmitter() {
        return this.quaxMoveSubmitter;
    }

    // for testing purposes
    public QuaxTileColour getFirstPlayerColour() {
        assert quaxPlayers[0] != null;
        return quaxPlayers[0].getPlayerColour();
    }

    // for testing purposes
    public QuaxTileColour getSecondPlayerColour() {
        assert quaxPlayers[1] != null;
        return quaxPlayers[1].getPlayerColour();
    }



    public void doPieRule() {
        if (quaxBoard.attemptPieRule()) {
            quaxPlayers[0].setPlayerColour(QuaxTileColour.WHITE);
            quaxPlayers[1].setPlayerColour(QuaxTileColour.BLACK);

            quaxUserInterface.setPieRuleVisibility(false);
            currentPlayer().movePrompt(getQuaxBoard());
        }
    }

    public void attemptMove(QuaxCoordinate coords) {
    	QuaxPlayer moveSubmitter = currentPlayer();
        QuaxTileColour moveColour = moveSubmitter.getPlayerColour();

        if (quaxBoard.validMove(coords, moveColour)) {
            quaxBoard.makeMove(coords, moveColour);

            if (moveSubmitter instanceof BotPlayer bot) {
            	quaxUserInterface.setBotChosenMove(coords);
            	quaxUserInterface.setLinkedBot(bot);
            }

            quaxUserInterface.updateFromPreviousMove(quaxBoard);

            if (quaxBoard.checkForWinningMove()) {
                quaxUserInterface.showWinLabel(moveColour);
                quaxUserInterface.hideTurnTracker();
            }
            else {
                currentPlayer().movePrompt(quaxBoard);
            }
        }
    }



    public void showStrategy() {
        quaxUserInterface.showStrategy();
    }

    public void hideStrategy() {
        quaxUserInterface.hideStrategy(quaxBoard);
    }



    /*
     * These three executor classes allow us to choose how
     * players (particularly bots) interact with the threads
     * in the JVM. Without these, there will be situations
     * where test cases finish before bots can make their
     * moves and JavaFX will freeze or crash.
     */

    /*
     * The SingleThreadedExecutor will run on the main thread,
     * blocking JavaFX and any other operations until the
     * execution is done.
     * 
     * This is used in integration tests not involving the UI.
     */
    private static class SingleThreadExecutor implements Executor {
        public void execute(Runnable r) {
            r.run();
        }
    }

    /*
     * The MultithreadedExecutor will run on a separate thread
     * than the JavaFX Application, which means the window won't
     * freeze while a long operation is happening.
     * 
     * This is used for bots to compute their moves.
     */
    private static class MultithreadedExecutor implements Executor {
    	public void execute(Runnable r) {
    		// TODO add daemon to thread
			new Thread(r).start();
		}
    }
    
    /*
     * The JavaFXPlatformExecutor allows us to rejoin operations
     * made off the main JavaFX application thread by queuing them
     * for when JavaFX is idle.
     * 
     * This is used for bots to submit computed moves.
     */
    private static class JavaFXPlatformExecutor implements Executor {
    	public void execute(Runnable r) {
    		Platform.runLater(r);
    	}
    }
}