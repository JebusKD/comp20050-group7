package userinterface;

import controller.QuaxController;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import player.HumanPlayer;
import types.ButtonClickEvent;
import types.QuaxCoordinateEvent;

public class QuaxEventHandler {
	public static void setup(QuaxController controller, Stage stage) {
		
		stage.addEventHandler(QuaxCoordinateEvent.TILE_CLICKED_EVENT, new EventHandler<>() {
			@Override
			public void handle(QuaxCoordinateEvent coords) {
				
				if (controller.curPlayer() instanceof HumanPlayer) {
					controller.makeMove(coords.coords());
				}
			}
		});
		
		stage.addEventHandler(ButtonClickEvent.PIE_RULE_CLICKED_EVENT, new EventHandler<ButtonClickEvent>() {
			@Override
			public void handle(ButtonClickEvent event) {
				if (controller.curPlayer() instanceof HumanPlayer && controller.doPieRule()) {
					controller.setPieRuleVisibility(false);
				}
			}
		});
		
	}
	
}
