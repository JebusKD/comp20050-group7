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
public class StratValuesTesting {

    private QuaxController controller;
    @Start
    public void start(Stage stage) throws Exception {
        controller = new QuaxController(stage,true,true);//human v bot game now
    }

    @Test
    void stratValsUpdate(FxRobot robot){
        robot.clickOn("#octagon5-5");
        robot.clickOn("#showStrat");

        assertTrue(!robot.lookup(".tileoutline-base.tileoutline-0").queryAll().isEmpty());
        assertTrue(!robot.lookup(".tileoutline-base.tileoutline-2").queryAll().isEmpty());
    }
}
