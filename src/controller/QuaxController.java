package controller;

import java.util.concurrent.Executor;
import java.util.Random;
import javafx.application.Platform;
import javafx.stage.Stage;

import model.QuaxBoard;
import player.*;
import types.*;
import userinterface.*;


/** Handle all technical aspects of the game */
public class QuaxController {

    static final Random RNG = new Random();
    private final Executor quaxExecutor;
    private final Executor quaxMoveSubmitter;

    private final UserInterface quaxUserInterface;
    private QuaxBoard quaxBoard;
    private final QuaxPlayer[] quaxPlayers;

    public QuaxController(QuaxPlayer p1, QuaxPlayer p2) {
        this.quaxExecutor = new SingleThreadExecutor();
        this.quaxMoveSubmitter = new SingleThreadExecutor();

        this.quaxPlayers = new QuaxPlayer[2];
        this.quaxUserInterface = new TestingEmptyInterface();

        BotPlayer.enableHaste();
        
        startGame(p1, p2);
    }

    // Testing constructor
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


    // TODO - Remove on final submission
    private void startTwoPlayerGame() {
        QuaxPlayer p1 = new HumanPlayer();
        QuaxPlayer p2 = new HumanPlayer();

        startGame(p1, p2);
    }

    private void startGame(QuaxPlayer p1, QuaxPlayer p2) {
        this.quaxBoard = new QuaxBoard();

        this.quaxPlayers[0] = p1;
        this.quaxPlayers[1] = p2;

        p1.setPlayerColour(QuaxTileColour.BLACK);
        p2.setPlayerColour(QuaxTileColour.WHITE);

        p1.setPlayerController(this);
        p2.setPlayerController(this);

        this.quaxUserInterface.setQuaxUIBoard(quaxBoard);

        curPlayer().movePrompt(quaxBoard);
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



    public QuaxPlayer curPlayer() {
        return quaxPlayers[getMoveNumber() % 2];
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


    public void doPieRule() {
        if (quaxBoard.attemptPieRule()) {
            quaxPlayers[0].setPlayerColour(QuaxTileColour.WHITE);
            quaxPlayers[1].setPlayerColour(QuaxTileColour.BLACK);
            quaxUserInterface.setPieRuleVisibility(false);
            curPlayer().movePrompt(getQuaxBoard());
        }
    }

    public boolean tryMove(QuaxCoordinate coords) {
    	QuaxPlayer moveSubmitter = curPlayer();
        QuaxTileColour c = moveSubmitter.getPlayerColour();

        if (quaxBoard.validMove(coords, c)) {
            quaxBoard.makeMove(coords, c);
            if (moveSubmitter instanceof BotPlayer bot) {
            	quaxUserInterface.setLinkedBot(bot);
            }
            quaxUserInterface.updateFromPreviousMove(quaxBoard);

            if (quaxBoard.checkForWinningMove()) {
                quaxUserInterface.showWinLabel(c);
                quaxUserInterface.hideTurnTracker();
            }
            else {
                curPlayer().movePrompt(quaxBoard);
            }

            return true;
        }

        return false;
    }


    /**  Methods for handling the strategic bot
     *  Retrieve bot, manage showing/hiding strategy
     */
    // TODO - Bot Cleanup - assertions break tests, but too many null checks --> DON'T RETURN NULL
    public BotPlayer getBot() {
        for (QuaxPlayer p : quaxPlayers) {
            if (p instanceof BotPlayer) return (BotPlayer) p;
        }
        return null;
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
    public static class SingleThreadExecutor implements Executor {
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
    public static class MultithreadedExecutor implements Executor {
    	public void execute(Runnable r) {
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
    public static class JavaFXPlatformExecutor implements Executor {
    	public void execute(Runnable r) {
    		Platform.runLater(r);
    	}
    }
}