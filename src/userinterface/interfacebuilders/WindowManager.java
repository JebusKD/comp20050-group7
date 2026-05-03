package userinterface.interfacebuilders;


import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import static types.StrategyValue.MAX_STRATEGIES;
import types.*;


public class WindowManager {

    private Button showStrategyButton;
    private Button hideStrategyButton;
    private Button pieRuleButton;

    private Label boardWinLabel;
    private VBox strategyColourIndicator;
    private VBox strategyDescription;


    public Label getWinLabel() {
        return boardWinLabel;
    }

    public VBox getStrategyIndicator() {
        return strategyColourIndicator;
    }

    public VBox getStrategyDescription() {
        return strategyDescription;
    }


    public VBox initialiseButtons() {
        VBox sideBar = new VBox(10);

        initialiseShowStrategyButton();
        initialiseHideStrategyButton();
        initialisePieRuleButton();

        sideBar.getChildren().addAll(showStrategyButton, hideStrategyButton,pieRuleButton);

        return sideBar;
    }

    private void initialiseShowStrategyButton() {
        showStrategyButton = new Button("Show Strategy");

        showStrategyButton.setOnMouseClicked((_) -> {
            showStrategyButton.fireEvent(new ButtonClickEvent(ButtonClickEvent.SHOW_STRATEGY_CLICKED_EVENT));
        });

        showStrategyButton.setId("showStrat");
        showStrategyButton.getStyleClass().add("button3");
    }

    private void initialiseHideStrategyButton() {
        hideStrategyButton = new Button("Hide Strategy");

        hideStrategyButton.setOnMouseClicked((_) -> {
            hideStrategyButton.fireEvent(new ButtonClickEvent(ButtonClickEvent.HIDE_STRATEGY_CLICKED_EVENT));
        });

        hideStrategyButton.setId("hideStrat");
        hideStrategyButton.getStyleClass().add("button3");
    }

    private void initialisePieRuleButton() {
        pieRuleButton = new Button("PieRule");

        pieRuleButton.setOnMouseClicked((_) -> {
            pieRuleButton.fireEvent(new ButtonClickEvent(ButtonClickEvent.PIE_RULE_CLICKED_EVENT));
        });

        pieRuleButton.setId("PieRule");
        pieRuleButton.getStyleClass().add("button3");

        setPieRuleVisibility(false);
    }


    public void initialiseWinLabel() {
        boardWinLabel = new Label("_ wins");
        boardWinLabel.setVisible(false);
        boardWinLabel.getStyleClass().add("win-label");
    }

    public void initialiseStrategyColourCoding() {
        Label[] stratLabels = initialiseStrategyLabels();

        strategyColourIndicator = new VBox(10);
        strategyColourIndicator.getChildren().addAll(stratLabels);
        strategyColourIndicator.getStyleClass().add("vbox");
        strategyColourIndicator.setVisible(false);
        strategyColourIndicator.setId("ColourIndicator");
    }

    private Label[] initialiseStrategyLabels() {
        Label[] labels = new Label[MAX_STRATEGIES];

        labels[0] = new Label("Strategy Value - SV");
        labels[0].getStyleClass().add("stratLabel");

        labels[1] = new Label("SV2 - Low priority surrounding tile ");
        labels[1].getStyleClass().add("stratTwo");

        labels[2] = new Label("SV3 - Block opponent ");
        labels[2].getStyleClass().add("stratThree");

        labels[3] = new Label("SV4 - Progress self");
        labels[3].getStyleClass().add("stratFour");

        labels[4] = new Label("SV5 - Key Move");
        labels[4].getStyleClass().add("stratFive");

        labels[5] = new Label("SV6 - Opponent has winning move");
        labels[5].getStyleClass().add("stratSix");

        labels[6] = new Label("SV7 - Winning move for self");
        labels[6].getStyleClass().add("stratSeven");

        return labels;
    }

    //TODO - Add test & fix
    public void initialiseStrategyDescription() {
        strategyDescription = new VBox(5);

        Label simpleStrategy = describeSimpleStrategy();
        Label complexStrategy = describeImprovedStrategy();

        strategyDescription.getChildren().addAll(simpleStrategy, complexStrategy);
        strategyDescription.getStyleClass().add("vbox");
        strategyDescription.setVisible(false);
        strategyDescription.setId("StrategyDescriptionBox");
    }

    private Label describeSimpleStrategy() {
        Label description = new Label("Bot Strategy Description:\n" +
                "Starts Simple: Checks each tile on the board, setting each to minimum SV1\n" +
                "All surrounding tiles of owned tiles have SV = 2\n" +
                "If tile would directly impede opponent, SV = 3\n" +
                "If tile would directly progress bot, SV = 4\n" +
                "If tile would intercept one of opponent's group, SV = 5 - \"Key Move\"\n" +
                "If the opponent would win with tile, SV = 6\n" +
                "If the bot would win with tile, SV = 7, max value\n");

        description.getStyleClass().add("stratLabel");
        return description;

    }

    private Label describeImprovedStrategy() {
        Label description = new Label("\nBuilds on Simple Strategy\n" +
                "Evaluates tiles based on potential future value, for example taking vulnerable rhombus tiles\n" +
                "Analyse a tile based on how much it would progress the bot, \n\teither by merging groups or " +
                "avoiding the human player's groups\n"
                );

        description.getStyleClass().add("stratLabel");
        return description;

    }



    public Label createTitle() {
        Label title = new Label("Quax (Human V Bot)");
        title.getStyleClass().add("custom-title");
        title.setId("Title");
        return title;
    }


    public void setPieRuleVisibility(boolean visibility) {
        pieRuleButton.setDisable(!visibility);
        pieRuleButton.setVisible(visibility);
    }

    public void setStrategyVisibility(boolean visibility) {
        this.strategyColourIndicator.setVisible(visibility);
        this.strategyDescription.setVisible(visibility);
    }

    public void showWinLabel(QuaxTileColour c) {
        boardWinLabel.setText(c + " wins");
        boardWinLabel.setVisible(true);
    }
}