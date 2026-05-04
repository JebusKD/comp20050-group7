package userinterface;

import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.geometry.Pos;
import javafx.stage.Stage;

import model.QuaxBoard;
import player.BotPlayer;
import static types.StrategyValue.MAX_STRATEGIES;
import types.*;
import userinterface.interfacebuilders.*;


public class QuaxUserInterface implements UserInterface {

    public static final QuaxTileBorder[] STRATEGY_GROUP_BORDERS = new QuaxTileBorder[] {
            QuaxTileBorder.NONE, QuaxTileBorder.BLUE,
            QuaxTileBorder.GREEN, QuaxTileBorder.RED,
            QuaxTileBorder.CYAN, QuaxTileBorder.PURPLE,
            QuaxTileBorder.PINK
    };

    private static final String[] STYLESHEETS = new String[] {
            "/userinterface/stylesheets/tile-styling.css",
            "/userinterface/stylesheets/board-styling.css",
            "/userinterface/stylesheets/ui-styling.css",
            "/userinterface/stylesheets/button-styling.css"
    };

    private final Stage quaxUIStage;
    private Scene interfaceScene;

    private final UserInterfaceBoard quaxUIBoard;
    private final PlayerTurnIndicator turnIndicator;
    private final SideBarUtilityManager quaxUISideBarManager;

    private BotPlayer linkedBot;
    private QuaxCoordinate chosenMove;

    private boolean hasLinkedBot;
    private boolean showingStrategy;


    public QuaxUserInterface(Stage stage) {
    	if (stage == null) {
    		throw new IllegalArgumentException("QuaxUserInterface cannot be initialised for null Stage.");
    	}
    	
        this.quaxUIStage = stage;

        this.quaxUIBoard = new UserInterfaceBoard();
        this.turnIndicator = new PlayerTurnIndicator();
        this.quaxUISideBarManager = new SideBarUtilityManager();

        this.showingStrategy = false;
        this.hasLinkedBot = false;

        initialiseSideBar();
        initialiseStylesheets();

        setupStage();
    }


    private void setupStage() {
    	assert quaxUIStage != null && interfaceScene != null;
    	
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


    private void initialiseSideBar() {
    	assert quaxUISideBarManager != null && turnIndicator != null;
    	
        VBox sideBar = quaxUISideBarManager.initialiseButtons();
        quaxUISideBarManager.initialiseWinLabel();
        quaxUISideBarManager.initialiseStrategyColourCoding();

        sideBar.getChildren().addAll(turnIndicator.getTurnTrackerBox(),
                                        quaxUISideBarManager.getWinLabel(),
                                        quaxUISideBarManager.getStrategyIndicator()
                                    );
        sideBar.getStyleClass().add("vbox");


        interfaceScene = new Scene(createOuterGrid(sideBar));
    }

    /* Create Stage layout - Title, Board and SideBar */
    private GridPane createOuterGrid(VBox extrasBar) {
    	assert extrasBar != null;
    	
        GridPane outer = new GridPane();

        outer.add(quaxUISideBarManager.createTitle(),0,0);
        outer.add(quaxUIBoard.getStackUIBoard(),0,1);
        outer.add(extrasBar,1,1);

        outer.setAlignment(Pos.CENTER);
        return outer;
    }


    public void showWinLabel(QuaxTileColour winnerColour) {
    	if (winnerColour != QuaxTileColour.BLACK || winnerColour != QuaxTileColour.WHITE) {
    		throw new IllegalArgumentException("Cannot showWinLabel for " + winnerColour + " colour.");
    	}
    	assert quaxUISideBarManager != null;
    	
        quaxUISideBarManager.showWinLabel(winnerColour);
    }

    public void hideTurnTracker() {
    	assert turnIndicator != null;
    	
        turnIndicator.hideTurnTrackerBox();
    }


    public void updateFromPreviousMove(QuaxBoard board) {
    	if (board == null) {
    		throw new IllegalArgumentException("Cannot update from null board.");
    	}
    	
    	assert turnIndicator != null;
    	
        updateStrategy();

        if (board.isStartingMove()) {
            this.turnIndicator.setIndicatorColour(QuaxTileColour.BLACK);
        }

        else {
            QuaxCoordinate previousMove = board.previousMove();
            QuaxTileColour colour = board.getTileColour(previousMove);

            this.setTile(previousMove, colour);
            this.turnIndicator.setIndicatorColour(colour.flip());

            setPieRuleVisibility(board.isPieRuleValid());
        }
    }


    public void setTile(QuaxCoordinate tileCoord, QuaxTileColour colour) {
    	if (tileCoord == null) {
    		throw new IllegalArgumentException("Cannot set colour of null coordinate.");
    	}
    	if (colour == null) {
    		throw new IllegalArgumentException("Cannot set colour of tile to null.");
    	}
    	assert quaxUIBoard != null;
    	
        quaxUIBoard.setTile(tileCoord, colour);
    }

    public void setQuaxUIBoard(QuaxBoard board) {
    	if (board == null) {
    		throw new IllegalArgumentException("Cannot set user interface board to null.");
    	}
    	assert quaxUIBoard != null;
    	
        quaxUIBoard.setStackUIBoard(board);
        setPieRuleVisibility(board.isPieRuleValid());
    }

    public void setPieRuleVisibility(boolean value) {
    	assert quaxUISideBarManager != null;
    	
        quaxUISideBarManager.setPieRuleVisibility(value);
    }


    public void showStrategy() {
    	assert quaxUISideBarManager != null;
    	
    	showingStrategy = true;
        updateStrategy();
        quaxUISideBarManager.setStrategyVisibility(true);
    }
    
    private void updateStrategy() {
    	if (hasLinkedBot && showingStrategy) {
    		assert quaxUIBoard != null && chosenMove != null
    				&& linkedBot != null;
    		
    		quaxUIBoard.clearTileBorders();
    		quaxUIBoard.setBotChosenCell(chosenMove);

	        for (int i = 1 ; i <= MAX_STRATEGIES ; i++) {
	            for (QuaxTile t : linkedBot.getStrategyGroupWithValue(StrategyValue.fromInt(i))) {
	                assert t.tileExists();
	            	quaxUIBoard.setTileBorder(t.getCoordinates(), STRATEGY_GROUP_BORDERS[i - 1]);
	            }
	        }
    	}
    }

    @Override
    public void hideStrategy(QuaxBoard board) {
    	if (board == null) {
    		throw new IllegalArgumentException("Cannot hide strategy for a null board.");
    	}
    	
    	this.showingStrategy = false;
    	this.quaxUIBoard.clearBotChosenMove();

        for (QuaxTile t : board) {
            this.quaxUIBoard.setTileBorder(t.getCoordinates(), QuaxTileBorder.NONE);
        }

        quaxUISideBarManager.setStrategyVisibility(false);
    }

    @Override
    public void setLinkedBot(BotPlayer bot) {
    	if (bot == null) {
    		throw new IllegalArgumentException("Cannot link user interface to null BotPlayer.");
    	}
    	
    	this.hasLinkedBot = true;
        this.linkedBot = bot;
    }

    @Override
    public void setBotChosenMove(QuaxCoordinate botCoord) {
    	if (botCoord == null) {
    		throw new IllegalArgumentException("Cannot setBotChosenMove for null QuaxCoordinate.");
    	}
    	
        this.chosenMove = botCoord;
    }
}