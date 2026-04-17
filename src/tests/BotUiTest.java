package tests;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;
import controller.QuaxController;
import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.WindowMatchers.isShowing;
import  org.testfx.api.FxAssert;
import model.QuaxBoard;
import player.BotPlayer;
import player.HumanPlayer;
import types.QuaxCoordinate;
import types.QuaxTile;
import types.QuaxTileColour;


@ExtendWith(ApplicationExtension.class)
public class BotUiTest {

    private QuaxController controller;

    @Start
    public void start(Stage stage) throws Exception {
        controller = new QuaxController(stage,true);//human v bot game now
    }

    @Test
    public void BotMovesIfFirstOrNotIfSecond(FxRobot robot)  {
    	// Wait for the bot to make a move, after that, ensure it's no longer the bot's turn.
    	WaitForAsyncUtils.waitForFxEvents();
    	// Bot should automatically make a move if possible, so should always be the human's turn.
    	assertFalse(controller.curPlayer() instanceof BotPlayer);
    	if (controller.curPlayer().getColour() == QuaxTileColour.BLACK) { // Human goes first
    		assertEquals(0,controller.getBoard().getMoveNumber()); //robot has not moved
    	}
    	else { // Otherwise, bot moves first and makes exactly one move.
    		assertEquals(1,controller.getBoard().getMoveNumber());
    	}
  
    }

    //https://testfx.github.io/TestFX/docs/javadoc/testfx-core/javadoc/org.testfx/org/testfx/util/WaitForAsyncUtils.html

    @Test
    public void BotAlwaysMakesMove(FxRobot robot){
    	WaitForAsyncUtils.waitForFxEvents();
    	if (controller.curPlayer().getColour() == QuaxTileColour.BLACK) { // Human goes first
    		robot.clickOn("#octagon5-5");
    		WaitForAsyncUtils.waitForFxEvents();
    		assertEquals(2,controller.getBoard().getMoveNumber()); //robot went after human
    	}
		else {
            assertEquals(1,controller.getBoard().getMoveNumber()); //robot is BLACK so has moved
        }
    }
}