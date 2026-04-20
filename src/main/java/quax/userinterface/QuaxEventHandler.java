package quax.userinterface;

import quax.controller.QuaxController;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import quax.player.HumanPlayer;
import quax.types.ButtonClickEvent;
import quax.types.QuaxCoordinateEvent;

public class QuaxEventHandler {
    public static void setup(QuaxController controller, Stage stage) {

        stage.addEventHandler(QuaxCoordinateEvent.TILE_CLICKED_EVENT, new EventHandler<>() {
            @Override
            public void handle(QuaxCoordinateEvent coords) {

                if (controller.curPlayer() instanceof HumanPlayer) {
                    controller.makeMove(coords.coords());
                    controller.redoStrategy();
                }
            }
        });

        stage.addEventHandler(ButtonClickEvent.PIE_RULE_CLICKED_EVENT, new EventHandler<ButtonClickEvent>() {
            @Override
            public void handle(ButtonClickEvent event) {
                if (controller.curPlayer() instanceof HumanPlayer) {
                    controller.doPieRule();
                }
            }
        });

        stage.addEventHandler(ButtonClickEvent.SHOW_STRATEGY_CLICKED_EVENT, new EventHandler<ButtonClickEvent>() {
            @Override
            public void handle(ButtonClickEvent event) {
                controller.showStrategy();
            }
        });

        stage.addEventHandler(ButtonClickEvent.HIDE_STRATEGY_CLICKED_EVENT, new EventHandler<ButtonClickEvent>() {
            @Override
            public void handle(ButtonClickEvent event) {
                controller.hideStrategy();
            }
        });
    }
}
