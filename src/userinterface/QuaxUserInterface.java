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
    
    private static final QuaxTileBorder[] STRATEGY_GROUP_BORDERS = new QuaxTileBorder[] {
            QuaxTileBorder.NONE, QuaxTileBorder.BLUE,
            QuaxTileBorder.GREEN, QuaxTileBorder.RED,
            QuaxTileBorder.PURPLE, QuaxTileBorder.PINK
    };

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

    private BotPlayer linkedBot;
    private QuaxCoordinate chosenMove;
    private boolean showingStrategy;


    public QuaxUserInterface(Stage stage) {
        this.quaxUIStage = stage;
        this.quaxUIBoard = new UserInterfaceBoard();
        this.showingStrategy = false;

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


        this.interfaceScene = new Scene(initialiseOuterGrid(sideBar));
    }

    private GridPane initialiseOuterGrid(VBox extrasBar) {
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
        updateStrategy();
        if (previousMove == null) {
            this.turnIndicator.setIndicatorColour(QuaxTileColour.BLACK);
        }

        else {
            QuaxTileColour colour = board.getTileColour(previousMove);
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


    public void showStrategy() {
    	showingStrategy = true;
        updateStrategy();
        quaxUIWindow.setStrategyVisibility(true);
    }
    
    private void updateStrategy() {
    	if (linkedBot != null && showingStrategy) {
    		quaxUIBoard.clearTileBorders();
    		quaxUIBoard.setBotChosenCell(chosenMove);
	        for (int i = 1 ; i <= MAX_STRATEGIES ; i++) {
	            for (QuaxTile t : linkedBot.getStrategyGroupWithValue(i)) {
	                quaxUIBoard.setTileBorder(t.getCoordinates(), STRATEGY_GROUP_BORDERS[i - 1]);
	            }
	        }
    	}
    }

    @Override
    public void hideStrategy(QuaxBoard board) {
    	this.showingStrategy = false;
    	this.quaxUIBoard.clearBotChosenMove();
        for (QuaxTile t : board) {
            this.quaxUIBoard.setTileBorder(t.getCoordinates(), QuaxTileBorder.NONE);
        }

        quaxUIWindow.setStrategyVisibility(false);
    }

    @Override
    public void setLinkedBot(BotPlayer bot) {
        this.linkedBot = bot;
    }

    @Override
    public void setBotChosenMove(QuaxCoordinate c) {
        this.chosenMove = c;
    }
}