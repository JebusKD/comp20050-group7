package userinterface.interfacebuilders;


import javafx.scene.control.*;
import javafx.scene.layout.*;

import static types.StrategyValue.MAX_STRATEGIES;
import types.*;


public class WindowManager {

    private Button showStrategyButton;
    private Button hideStrategyButton;
    private Button pieRuleButton;

    private Label boardWinLabel;
    private VBox strategyColourIndicator;

    private VBox[] strategyDescription;


    public Label getWinLabel() {
        return boardWinLabel;
    }

    public VBox getStrategyIndicator() {
        return strategyColourIndicator;
    }

    public VBox[] getStrategyDescription() {
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
        strategyDescription = new VBox[2];

        strategyDescription[0] = describeSimpleStrategy();
        strategyDescription[1] = describeImprovedStrategy();

        strategyDescription[0].getStyleClass().add("vbox");
        strategyDescription[1].getStyleClass().add("vbox");

        strategyDescription[0].setVisible(false);
        strategyDescription[1].setVisible(false);

        strategyDescription[0].setId("SimpleStrategyDescription");
        strategyDescription[1].setId("ComplexStrategyDescription");
    }

    private VBox describeSimpleStrategy() {
        Label[] simpleDes = new Label[MAX_STRATEGIES + 1];

        simpleDes[0] = new Label("Bot Strategy Description - Simple Start");
        simpleDes[0].getStyleClass().add("stratDescription");

        simpleDes[1] = new Label("Checks each tile on the board, setting each to minimum SV1");
        simpleDes[1].getStyleClass().add("stratDescription");

        simpleDes[2] = new Label("All surrounding tiles of owned tiles have SV = 2");
        simpleDes[2].getStyleClass().add("stratTwo");

        simpleDes[3] = new Label("Tile would directly impede opponent: SV = 3");
        simpleDes[3].getStyleClass().add("stratThree");

        simpleDes[4] = new Label("Tile would directly progress bot: SV = 4");
        simpleDes[4].getStyleClass().add("stratFour");

        simpleDes[5] = new Label("Tile intercepts or disrupts human massively: SV = 5 - \"Key Move\"");
        simpleDes[5].getStyleClass().add("stratFive");

        simpleDes[6] = new Label("Tile is winning move for human, SV = 6");
        simpleDes[6].getStyleClass().add("stratSix");

        simpleDes[7] = new Label("Tile is winning move for bot, SV = 7, max value");
        simpleDes[7].getStyleClass().add("stratSeven");

        return new VBox(simpleDes);
    }

    private VBox describeImprovedStrategy() {
        Label[] complexDes = new Label[MAX_STRATEGIES];

        complexDes[0] = new Label("Bot then builds on Simple Strategy");
        complexDes[0].getStyleClass().add("stratDescription");

        complexDes[1] = new Label("Evaluates tiles based on potential future value");
        complexDes[1].getStyleClass().add("stratDescription");

        complexDes[2] = new Label("- taking vulnerable rhombus tiles");
        complexDes[2].getStyleClass().add("stratDescription");

        complexDes[3] = new Label("- cutting off opponent");
        complexDes[3].getStyleClass().add("stratDescription");

        complexDes[4] = new Label("Adjust simple tile SV based on actual value");
        complexDes[4].getStyleClass().add("stratDescription");

        complexDes[5] = new Label(" - merging groups");
        complexDes[5].getStyleClass().add("stratDescription");

        complexDes[6] = new Label(" - avoiding the human player's groups");
        complexDes[6].getStyleClass().add("stratDescription");

        return new VBox(complexDes);
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
        this.strategyDescription[0].setVisible(visibility);
        this.strategyDescription[1].setVisible(visibility);
    }

    public void showWinLabel(QuaxTileColour winnerColour) {
        boardWinLabel.setText(winnerColour + " wins");
        boardWinLabel.setVisible(true);
    }
}