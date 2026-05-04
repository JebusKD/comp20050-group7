package tests;

import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;
import controller.QuaxController;
import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.*;

import player.BotPlayer;
import types.QuaxTileColour;


@ExtendWith(ApplicationExtension.class)
public class BotUiTest {

    private QuaxController controller;

    @Start
    public void start(Stage stage) throws Exception {
        //human v bot game now
        controller = new QuaxController(stage,true, false);
    }

    @Test
    public void testBotMovesIfFirstOrNotIfSecond(FxRobot robot)  {
    	ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    	// Wait for the bot to make a move, after that, ensure it's no longer the bot's turn.
    	WaitForAsyncUtils.waitForFxEvents();
    	// Bot should automatically make a move if possible, so should always be the human's turn.
    	scheduler.schedule(() -> {
            
    		assertFalse(controller.currentPlayer() instanceof BotPlayer);

        	if (controller.currentPlayer().getPlayerColour() == QuaxTileColour.BLACK) { // Human goes first
        		assertEquals(0,controller.getQuaxBoard().getMoveNumber()); //robot has not moved
        	}

        	else { // Otherwise, bot moves first and makes exactly one move.
        		assertEquals(1,controller.getQuaxBoard().getMoveNumber());
        	}

        },3, TimeUnit.SECONDS);
	}

    @Test
    public void testBotAlwaysMakesMove(FxRobot robot) {
    	ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    	WaitForAsyncUtils.waitForFxEvents();

        // Human goes first
    	if (controller.currentPlayer().getPlayerColour() == QuaxTileColour.BLACK) {
    		robot.clickOn("#octagon5-5");
    		WaitForAsyncUtils.waitForFxEvents();
    		
    		scheduler.schedule(() ->{
                assertEquals(2,controller.getQuaxBoard().getMoveNumber());
            },3, TimeUnit.SECONDS);

             //robot went after human
    	}
		else {
			scheduler.schedule(() ->{
                assertEquals(1,controller.getQuaxBoard().getMoveNumber());;
            },3, TimeUnit.SECONDS);

            //robot is BLACK so has moved
        }
    }


    @Test
    void testStrategyColourIndicatorInvisibleOnGameStart(FxRobot robot){
        assertFalse(robot.lookup("#StrategyIndicator").query().isVisible());
    }

    @Test
    void testStrategyColourIndicatorAppearsWhenShowStrategyActive(FxRobot robot) {
        robot.clickOn("#showStrat");

        assertTrue(robot.lookup("#StrategyIndicator").query().isVisible());
    }

    @Test
    void testStrategyColourIndicatorDisappearsWhenShowStrategyInactive(FxRobot robot) {
        robot.clickOn("#showStrat");
        robot.clickOn("#hideStrat");

        assertFalse(robot.lookup("#StrategyIndicator").query().isVisible());
    }

    @Test
    void testHideStrategyRemovesAllBorders(FxRobot robot){
        robot.clickOn("#showStrat");
        robot.clickOn("#hideStrat");

        assertEquals(221, robot.lookup(".tileoutline-base").queryAll().size());
    }
}