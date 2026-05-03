package userinterface;

import javafx.event.EventHandler;
import javafx.stage.Stage;

import controller.QuaxController;
import player.HumanPlayer;
import types.*;
import static types.QuaxCoordinateEvent.*;
import static types.ButtonClickEvent.*;


public class QuaxEventHandler {

    public static void setupTileClickEvents(QuaxController controller, Stage stage) {
        stage.addEventHandler(TILE_CLICKED_EVENT, new EventHandler<>() {
            @Override
            public void handle(QuaxCoordinateEvent coords) {
                if (controller.currentPlayer() instanceof HumanPlayer) {
                    controller.attemptMove(coords.coordinate());
                }
            }
        });
    }

    public static void setupButtonEvents(QuaxController controller, Stage stage) {
        stage.addEventHandler(PIE_RULE_CLICKED_EVENT, new EventHandler<>() {
            @Override
            public void handle(ButtonClickEvent event) {
                if (controller.currentPlayer() instanceof HumanPlayer) {
                    controller.doPieRule();
                }
            }
        });

        stage.addEventHandler(SHOW_STRATEGY_CLICKED_EVENT, new EventHandler<>() {
            @Override
            public void handle(ButtonClickEvent event) {
                controller.showStrategy();
            }
        });

        stage.addEventHandler(HIDE_STRATEGY_CLICKED_EVENT, new EventHandler<>() {
            @Override
            public void handle(ButtonClickEvent event) {
                controller.hideStrategy();
            }
        });
    }
}