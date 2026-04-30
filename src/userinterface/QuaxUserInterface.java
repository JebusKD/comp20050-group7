package userinterface;

import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.geometry.Pos;
import javafx.stage.Stage;

import model.QuaxBoard;
import player.BotPlayer;
import static player.BotPlayer.MAX_STRATEGIES;
import types.*;
import userinterface.interfacebuilders.*;


public class QuaxUserInterface implements UserInterface {

    public static final double OCTAGON_WIDTH = 40;
    public static final double OCTAGON_GRID_GAP = 1;

    private static final String[] STYLESHEETS = new String[] {
            "/userinterface/stylesheets/tile-styling.css",
            "/userinterface/stylesheets/board-styling.css",
            "/userinterface/stylesheets/ui-styling.css",
            "/userinterface/stylesheets/button-styling.css"
    };

    private Stage quaxUIStage;
    private Scene interfaceScene;

    private UserInterfaceBoard quaxUIBoard;
    private PlayerTurnIndicator turnIndicator;
    private WindowManager quaxUIWindow;


    public QuaxUserInterface(Stage stage) {
        this.quaxUIStage = stage;
        this.quaxUIBoard = new UserInterfaceBoard();

        initialiseWindow();
        initialiseStylesheets();

        setupStage();
    }


    private void setupStage() {
        this.quaxUIStage.setScene(this.interfaceScene);
        this.quaxUIStage.setMaximized(true);
        this.quaxUIStage.show();
    }

    private void initialiseStylesheets() {
        ObservableList<String> sheets = this.interfaceScene.getStylesheets();
        for (String stylesheet : STYLESHEETS) {
            sheets.add(getClass().getResource(stylesheet).toExternalForm());
        }
    }


    private void initialiseWindow() {
        quaxUIWindow = new WindowManager();

        VBox sideBar = quaxUIWindow.initialiseButtons();
        quaxUIWindow.initialiseWinLabel();
        quaxUIWindow.initialiseStrategyColourCoding();

        this.turnIndicator = new PlayerTurnIndicator();

        sideBar.getChildren().addAll(turnIndicator.getTurnTrackerBox(),
                                        quaxUIWindow.getWinLabel(),
                                        quaxUIWindow.getStrategyIndicator());
        sideBar.getStyleClass().add("vbox");


        this.interfaceScene = new Scene(setOuterGrid(sideBar));
    }

    private GridPane setOuterGrid(VBox extrasBar) {
        GridPane outer = new GridPane();

        outer.add(quaxUIWindow.createTitle(),0,0);
        outer.add(quaxUIBoard.getStackUIBoard(),0,1);
        outer.add(extrasBar,1,1);

        outer.setAlignment(Pos.CENTER);
        return outer;
    }


    public void showWinLabel(QuaxTileColour c) {
        quaxUIWindow.showWinLabel(c);
    }

    public void hideTurnTracker() {
        turnIndicator.hideTurnTrackerBox();
    }


    public void updateFromPreviousMove(QuaxBoard board) {
        QuaxCoordinate previousMove = board.previousMove();
        if (previousMove == null) {
            this.turnIndicator.setIndicatorColour(QuaxTileColour.BLACK);
        }

        else {
            // TODO - LoD violation
            QuaxTileColour colour = board.getTile(previousMove).getTileColour();
            this.setTile(previousMove, colour);
            this.turnIndicator.setIndicatorColour(colour.flip());

            setPieRuleVisibility(board.isPieRuleValid());
        }
    }


    public void setTile(QuaxCoordinate q, QuaxTileColour c) {
        quaxUIBoard.setTile(q, c);
    }

    public void setQuaxUIBoard(QuaxBoard b) {
        quaxUIBoard.setStackUIBoard(b);
        setPieRuleVisibility(b.isPieRuleValid());
    }

    public void setPieRuleVisibility(boolean value) {
        quaxUIWindow.setPieRuleVisibility(value);
    }


    public void showStrategy(BotPlayer bot) {
        QuaxTileBorder[] colours = new QuaxTileBorder[] {
                QuaxTileBorder.NONE, QuaxTileBorder.BLUE,
                QuaxTileBorder.GREEN, QuaxTileBorder.RED,
                QuaxTileBorder.PURPLE, QuaxTileBorder.PINK
        };

        //QuaxTileStrategyGroup[] allStrategyGroups = bot.getStrategyGroups();

        for (int i = 1 ; i <= MAX_STRATEGIES ; i++) {
            for (QuaxTile t : bot.getStrategyGroupWithValue(i)) {
                quaxUIBoard.setTileBorder(t.getCoordinates(), colours[i - 1]);
            }
        }

        quaxUIWindow.setStrategyVisibility(true);
    }

    public void hideStrategy(QuaxBoard board) {
        for (QuaxTile t : board) {
            this.quaxUIBoard.setTileBorder(t.getCoordinates(), QuaxTileBorder.NONE);
        }

        quaxUIWindow.setStrategyVisibility(false);
    }
}