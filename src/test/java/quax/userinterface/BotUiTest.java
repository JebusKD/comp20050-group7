package quax.userinterface;

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
import quax.controller.QuaxController;
import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.WindowMatchers.isShowing;
import  org.testfx.api.FxAssert;
import quax.model.QuaxBoard;
import quax.player.BotPlayer;
import quax.player.HumanPlayer;
import quax.types.QuaxCoordinate;
import quax.types.QuaxTile;
import quax.types.QuaxTileColour;



@ExtendWith(ApplicationExtension.class)
public class BotUiTest {

    private QuaxController controller;
    @Start
    public void start(Stage stage) throws Exception {
        controller = new QuaxController(stage,true,false);//human v bot game now
    }

    @Test
    public void BotMovesIfFirst_AND_DoesNotMoveIfSecond(FxRobot robot)  {
        //if bot goes first, then its move number should be one upon opening the stage (cos it started the game)
        if(controller.curPlayer() instanceof BotPlayer){
            assertEquals(1,controller.getBoard().getMoveNumber());
        }
        else{
            assertEquals(0,controller.getBoard().getMoveNumber());
        }
    }

    //https://testfx.github.io/TestFX/docs/javadoc/testfx-core/javadoc/org.testfx/org/testfx/util/WaitForAsyncUtils.html

    @Test
    public void BotAlwaysMakesMove(FxRobot robot){
        if(controller.curPlayer() instanceof HumanPlayer){
            robot.clickOn("#octagon5-5");
            WaitForAsyncUtils.waitForFxEvents();
            assertEquals(2,controller.getBoard().getMoveNumber()); //robot went after human
        }
        else{
            assertEquals(1,controller.getBoard().getMoveNumber()); //robot is BLACK so has moved
        }
    }

    @Test
    void StratColourIndicatorAppearsWhenShowStratActive(FxRobot robot){
        robot.clickOn("#showStrat");
        assertTrue(robot.lookup("#ColourIndicator").query().isVisible());
    }

    @Test
    void StratColourIndicatorDisappearsWhenShowStratInactive(FxRobot robot){
        robot.clickOn("#showStrat");
        robot.clickOn("#hideStrat");
        assertFalse(robot.lookup("#ColourIndicator").query().isVisible());

    }

    @Test
    void hideStratRemovesAllBorders(FxRobot robot){
        robot.clickOn("#showStrat");
        robot.clickOn("#hideStrat");
        assertEquals(221, robot.lookup(".tileoutline-base").queryAll().size());
    }
}
