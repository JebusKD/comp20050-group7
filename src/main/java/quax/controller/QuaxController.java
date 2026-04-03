package quax.controller;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.stage.Stage;
import quax.model.QuaxBoard;
import quax.player.BogoBot;
import quax.player.HumanPlayer;
import quax.player.QuaxPlayer;
import quax.types.QuaxCoordinate;
import quax.types.QuaxCoordinateEvent;
import quax.types.QuaxTileColour;
import quax.userinterface.QuaxUserInterface;
import quax.types.ButtonClickEvent;

import java.util.Random;

public class QuaxController {

    static final Random RNG = new Random();

    public static final EventType<QuaxCoordinateEvent> MOVE_SUBMITTED_EVENT = new EventType<>("quaxMoveSubmittedEvent");
    public static final EventType<QuaxCoordinateEvent> TILE_CLICKED_EVENT = new EventType<>("tileClickedEvent");
    public static final EventType<ButtonClickEvent> PIE_RULE_CLICKED_EVENT = new EventType<>("pieRuleClickedEvent");

    private Stage stage;

    private QuaxUserInterface ui;

    private QuaxBoard board;

    private QuaxPlayer[] players;

    public QuaxController(Stage stage) {
        this(stage,true); //by default, bot game is always true
    }

    public QuaxController(Stage stage,boolean checkBotGame){
        this.stage = stage;
        ui = new QuaxUserInterface(stage);

        players = new QuaxPlayer[2];

        stage.addEventHandler(QuaxController.TILE_CLICKED_EVENT, new EventHandler<>() {
            @Override
            public void handle(QuaxCoordinateEvent coords) {

                if (curPlayer() instanceof HumanPlayer) {
                    makeMove(coords.coords());
                }
            }
        });

        stage.addEventHandler(QuaxController.MOVE_SUBMITTED_EVENT, new EventHandler<>() {
            @Override
            public void handle(QuaxCoordinateEvent coords) {
                makeMove(coords.coords());
            }
        });

        stage.addEventHandler(QuaxController.PIE_RULE_CLICKED_EVENT, new EventHandler<ButtonClickEvent>() {
            @Override
            public void handle(ButtonClickEvent event) {
                if (curPlayer() instanceof HumanPlayer && doPieRule()) {
                    ui.setPieRuleVisibility(false);

                    curPlayer().movePrompt(board);
                }
            }
        });

        if(checkBotGame){
            startGameAgainstBot();
        }
        else{
            startTwoPlayerGame();
        }

    }

    // TODO Keep for testing - Remove on final submission
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

        ui.setPieRuleVisibility(true);
        curPlayer().movePrompt(board);
    }

    public void startGameAgainstBot() {

        if (RNG.nextInt() % 2 == 0) {
            QuaxPlayer human = new HumanPlayer("Player", QuaxTileColour.BLACK, stage);
            QuaxPlayer bot = new BogoBot(QuaxTileColour.WHITE, stage);

            startGame(human, bot);
        }
        else {
            QuaxPlayer human = new HumanPlayer("Player", QuaxTileColour.WHITE, stage);
            QuaxPlayer bot = new BogoBot(QuaxTileColour.BLACK, stage);

            startGame(bot, human);
        }
    }

    public QuaxPlayer curPlayer() {
        return players[board.getMoveNumber() % 2];
    }

    //for testing purposes
    public QuaxTileColour getPlayerColour(int i){
        if(i == 0){
            return players[0].getColour();
        }
        else if(i == 1){
            return players[1].getColour();
        }
        return null;
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

}