package controller;

import java.util.concurrent.Executor;
import java.util.Random;
import javafx.application.Platform;
import javafx.stage.Stage;

import model.QuaxBoard;
import player.*;
import types.*;
import userinterface.*;


public class QuaxController {

    static final Random RNG = new Random();
    private final Executor executor;
    private final Executor submitter;

    private final UserInterface quaxUserInterface;
    private QuaxBoard quaxBoard;
    private final QuaxPlayer[] quaxPlayers;

    private boolean showingStrategy = false;


    public QuaxController(QuaxPlayer p1, QuaxPlayer p2) {
        this.executor = new SingleThreadExecutor();
        this.submitter = new SingleThreadExecutor();

        this.quaxPlayers = new QuaxPlayer[2];
        this.quaxUserInterface = new TestingEmptyInterface();

        BotPlayer.enableHaste();
        
        startGame(p1, p2);
    }

    // Testing constructor
    public QuaxController(Stage stage) {
        this(stage, true, false);
    }

    public QuaxController(Stage stage, boolean againstBot,boolean humanPlaysFirst) {
        this.executor = new JavaFXThreadedExecutor();
        this.submitter = new JavaFXPlatformRunner();

        this.quaxUserInterface = new QuaxUserInterface(stage);
        this.quaxPlayers = new QuaxPlayer[2];

        QuaxEventHandler.setup(this, stage);

        if (humanPlaysFirst) {
            startGame(new HumanPlayer(),new BogoBot());
        }
        else {
            // TODO - Remove on final submission
            //startGame(new BogoBot(), new BogoBot());
            if (againstBot) {
                startGameAgainstBot();
            }
            else {
                startTwoPlayerGame();
            }
        }
    }


    // TODO - Remove on final submission
    public void startTwoPlayerGame() {
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
        return quaxPlayers[getMoveNumber() % 2];
    }

    // for testing purposes
    public QuaxTileColour getPlayerColour(int i) {
        assert i == 0 || i == 1;
        return quaxPlayers[i].getPlayerColour();
    }

    public int getMoveNumber() {
        return quaxBoard.getMoveNumber();
    }

    public QuaxBoard getQuaxBoard() {
        return this.quaxBoard;
    }

    public Executor getExecutor() {
        return this.executor;
    }

    public Executor getSubmitter() {
        return this.submitter;
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
        QuaxTileColour c = curPlayer().getPlayerColour();

        if (quaxBoard.validMove(coords, c)) {
            quaxBoard.makeMove(coords, c);

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


    // TODO - Bot cleanup
    public BotPlayer getBot() {
        for (QuaxPlayer p : quaxPlayers) {
            if (p instanceof BotPlayer) return (BotPlayer) p;
        }
        return null;
    }

    public void showStrategy() {
        showingStrategy = true;
        BotPlayer bot = getBot();
        if (bot != null) {
            bot.setUpStrategy(quaxBoard);
            quaxUserInterface.showStrategy(bot);
        }
    }

    public void redoStrategy() {
        if (showingStrategy) {
            BotPlayer bot = getBot();
            if (bot != null) {
                quaxUserInterface.hideStrategy(quaxBoard);
                bot.setUpStrategy(quaxBoard);
                quaxUserInterface.showStrategy(bot);
            }
        }
    }

    public void hideStrategy() {
        showingStrategy = false;
        quaxUserInterface.hideStrategy(quaxBoard);
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
    
    public static class JavaFXPlatformRunner implements Executor {
    	public void execute(Runnable r) {
    		Platform.runLater(r);
    	}
    }
}