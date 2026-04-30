package userinterface.interfacebuilders;

import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import static player.BotPlayer.MAX_STRATEGIES;
import types.*;

import java.util.ArrayList;


public class WindowManager {

    private Button showStrategyButton;
    private Button hideStrategyButton;
    private Button pieRuleButton;

    private Label boardWinLabel;
    private VBox strategyColourIndicator;


    public Label getWinLabel() {
        return boardWinLabel;
    }

    public VBox getStrategyIndicator() {
        return strategyColourIndicator;
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
        ArrayList<Label> stratLabels = initialiseStrategyLabels();

        strategyColourIndicator = new VBox(10);
        strategyColourIndicator.getChildren().addAll(stratLabels);
        strategyColourIndicator.getStyleClass().add("vbox");
        strategyColourIndicator.setVisible(false);
        strategyColourIndicator.setId("ColourIndicator");
    }

    private ArrayList<Label> initialiseStrategyLabels() {
        ArrayList<Label> labels = new ArrayList<>(MAX_STRATEGIES);

        Label stratLabel = new Label("Strategy Value - SV");
        stratLabel.getStyleClass().add("stratLabel");
        labels.add(stratLabel);

        Label stratTwo = new Label("SV2 - Low priority surrounding tile ");
        stratTwo.getStyleClass().add("stratTwo");
        labels.add(stratTwo);

        Label stratThree = new Label("SV3 - Block opponent ");
        stratThree.getStyleClass().add("stratThree");
        labels.add(stratThree);

        Label stratFour = new Label("SV4 - Progress self");
        stratFour.getStyleClass().add("stratFour");
        labels.add(stratFour);

        Label stratFive = new Label("SV5 - Opponent has winning move");
        stratFive.getStyleClass().add("stratFive");
        labels.add(stratFive);

        Label stratSix = new Label("SV6 - Winning move for self");
        stratSix.getStyleClass().add("stratSix");
        labels.add(stratSix);

        return labels;
    }


    public Label createTitle() {
        Label title = new Label("Quax (Human V Bot)");
        title.getStyleClass().add("custom-title");
        title.setId("Title");
        return title;
    }


    public void setPieRuleVisibility(boolean value) {
        pieRuleButton.setDisable(!value);
        pieRuleButton.setVisible(value);
    }

    public void setStrategyVisibility(boolean visibility) {
        this.strategyColourIndicator.setVisible(visibility);
    }

    public void showWinLabel(QuaxTileColour c) {
        boardWinLabel.setText(c + " wins");
        boardWinLabel.setVisible(true);
    }
}
