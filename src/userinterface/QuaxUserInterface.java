package userinterface;

import javafx.collections.ObservableList;
import javafx.scene.layout.*;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import model.QuaxBoard;
import player.BotPlayer;
import types.*;
import userinterface.interfacebuilders.*;


public class QuaxUserInterface implements UserInterface {

    public static final double OCTAGON_WIDTH = 40;
    public static final double OCTAGON_GRID_GAP = 1;
    public static final int MAX_STRATEGIES = 6;

    private static final String[] STYLESHEETS = new String[] {
            "/userinterface/stylesheets/tile-styling.css",
            "/userinterface/stylesheets/board-styling.css",
            "/userinterface/stylesheets/ui-styling.css",
            "/userinterface/stylesheets/button-styling.css"
    };

    private Stage quaxUIStage;
    private Scene interfaceScene;
    private Label boardWinLabel;
    private Button pieRuleButton;
    private VBox strategyColourIndicator;

    private UserInterfaceBoard quaxUIBoard;
    private PlayerTurnIndicator turnIndicator;


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


    // TODO - Move all window methods into new class?
    private void initialiseWindow() {

        VBox sideBar = initialiseButtons();

        this.turnIndicator = new PlayerTurnIndicator();

        initialiseWinLabel();
        initialiseStrategyColourCoding();

        sideBar.getChildren().addAll(this.turnIndicator.getTurnTrackerBox(), this.boardWinLabel,this.strategyColourIndicator);
        sideBar.getStyleClass().add("vbox");

        GridPane outer = new GridPane();

        outer.add(createTitle(),0,0);
        outer.add(quaxUIBoard.getStackUIBoard(),0,1);
        outer.add(sideBar,1,1);

        outer.setAlignment(Pos.CENTER);

        this.interfaceScene = new Scene(outer);
    }

    private VBox initialiseButtons() {
        VBox sideBar = new VBox(10);

        Button strat = new Button("Show Strategy");

        strat.setOnMouseClicked(event -> {
            strat.fireEvent(new ButtonClickEvent(ButtonClickEvent.SHOW_STRATEGY_CLICKED_EVENT));
        });

        strat.setId("showStrat");

        Button hideStrat = new Button("Hide Strategy");
        hideStrat.setId("hideStrat");

        hideStrat.setOnMouseClicked(event -> {
            hideStrat.fireEvent(new ButtonClickEvent(ButtonClickEvent.HIDE_STRATEGY_CLICKED_EVENT));
        });

        pieRuleButton = new Button("PieRule");

        pieRuleButton.setOnMouseClicked(event -> {
            pieRuleButton.fireEvent(new ButtonClickEvent(ButtonClickEvent.PIE_RULE_CLICKED_EVENT));
        });

        pieRuleButton.setId("PieRule");
        setPieRuleVisibility(false);

        strat.getStyleClass().add("button3");
        hideStrat.getStyleClass().add("button3");
        pieRuleButton.getStyleClass().add("button3");

        sideBar.getChildren().addAll(strat,hideStrat,pieRuleButton);

        return sideBar;
    }

    private void initialiseWinLabel() {
        this.boardWinLabel = new Label("_ wins");
        this.boardWinLabel.setVisible(false);
        this.boardWinLabel.getStyleClass().add("win-label");
    }

    private void initialiseStrategyColourCoding() {
        Label stratLabel = new Label("Strategy Value - SV");
        stratLabel.getStyleClass().add("stratLabel");

        Label stratTwo = new Label("SV2 - Low priority surrounding tile ");
        stratTwo.getStyleClass().add("stratTwo");

        Label stratThree = new Label("SV3 - Block opponent ");
        stratThree.getStyleClass().add("stratThree");

        Label stratFour = new Label("SV4 - Progress self");
        stratFour.getStyleClass().add("stratFour");

        Label stratFive = new Label("SV5 - Opponent has winning move");
        stratFive.getStyleClass().add("stratFive");

        Label stratSix = new Label("SV6 - Winning move for self");
        stratSix.getStyleClass().add("stratSix");

        //VBox stratColourIndicator = new VBox(10);
        this.strategyColourIndicator = new VBox(10);
        this.strategyColourIndicator.getChildren().addAll(stratLabel,stratTwo,stratThree,stratFour,stratFive,stratSix);
        this.strategyColourIndicator.getStyleClass().add("vbox");
        this.strategyColourIndicator.setVisible(false);
        this.strategyColourIndicator.setId("ColourIndicator");
        //return stratColourIndicator;
    }

    private Label createTitle() {
        Label title = new Label("Quax (Human V Bot)");
        title.getStyleClass().add("custom-title");
        title.setId("Title");
        return title;
    }


    public void showWinLabel(QuaxTileColour c) {
        boardWinLabel.setText(c + " wins");
        boardWinLabel.setVisible(true);
    }

    // TODO - LoD violation
    public void hideTurnTracker() {
        turnIndicator.getTurnTrackerBox().setVisible(false);
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
        pieRuleButton.setDisable(!value);
        pieRuleButton.setVisible(value);
    }


    public void showStrategy(BotPlayer bot) {
        QuaxTileBorder[] colours = new QuaxTileBorder[] {
                QuaxTileBorder.NONE, QuaxTileBorder.BLUE,
                QuaxTileBorder.GREEN, QuaxTileBorder.RED,
                QuaxTileBorder.PURPLE, QuaxTileBorder.PINK
        };

        for (int i = 1 ; i < MAX_STRATEGIES ; i++) {
            for (QuaxTile t : bot.getStrategyValueGroup(i)) {
                quaxUIBoard.setTileBorder(t.getCoordinates(), colours[i - 1]);
            }
        }

        this.strategyColourIndicator.setVisible(true);
    }

    public void hideStrategy(QuaxBoard board) {
        for (QuaxTile t : board) {
            this.quaxUIBoard.setTileBorder(t.getCoordinates(), QuaxTileBorder.NONE);
        }

        this.strategyColourIndicator.setVisible(false);
    }
}