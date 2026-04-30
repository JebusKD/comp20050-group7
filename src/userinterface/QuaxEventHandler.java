package userinterface;

import javafx.event.EventHandler;
import javafx.stage.Stage;

import controller.QuaxController;
import player.HumanPlayer;
import types.*;


public class QuaxEventHandler {

    public static void setup(QuaxController controller, Stage stage) {

        stage.addEventHandler(QuaxCoordinateEvent.TILE_CLICKED_EVENT, new EventHandler<>() {
            @Override
            public void handle(QuaxCoordinateEvent coords) {

                if (controller.curPlayer() instanceof HumanPlayer) {
                    controller.tryMove(coords.coordinate());
                }
            }
        });

        stage.addEventHandler(ButtonClickEvent.PIE_RULE_CLICKED_EVENT, new EventHandler<>() {
            @Override
            public void handle(ButtonClickEvent event) {
                if (controller.curPlayer() instanceof HumanPlayer) {
                    controller.doPieRule();
                }
            }
        });

        stage.addEventHandler(ButtonClickEvent.SHOW_STRATEGY_CLICKED_EVENT, new EventHandler<>() {
            @Override
            public void handle(ButtonClickEvent event) {
                controller.showStrategy();
            }
        });

        stage.addEventHandler(ButtonClickEvent.HIDE_STRATEGY_CLICKED_EVENT, new EventHandler<>() {
            @Override
            public void handle(ButtonClickEvent event) {
                controller.hideStrategy();
            }
        });
    }
}