// UI Tests using TestFX
package tests;

import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import controller.QuaxController;
import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.WindowMatchers.isShowing;
import  org.testfx.api.FxAssert;
import model.QuaxBoard;
import types.QuaxCoordinate;
import types.QuaxTile;
import types.QuaxTileColour;


@ExtendWith(ApplicationExtension.class)
public class QuaxUITest {

    private QuaxController controller;

    @Start
    public void start(Stage stage) throws Exception {
        controller = new QuaxController(stage);
    }

    @Test
    void ButtonExists(FxRobot robot) {
        assertNotNull(robot.lookup("New 2-Player Game").query());
    }

    @Test
    void OctagonClicked(FxRobot robot) {
        robot.clickOn("#octagon5_5");
        assertEquals(QuaxTileColour.BLACK,controller.getBoard().getOctagon(5,5).getColour());
    }

    @Test
    void RhombusClicked(FxRobot robot){
        robot.clickOn("#rhombus5_5");
        assertEquals(QuaxTileColour.BLACK,controller.getBoard().getRhombus(5,5).getColour());
    }

    @Test
    void WinningMove(FxRobot robot){
        QuaxBoard board = controller.getBoard();

        for (int i = 0; i < 10; i++) {
            board.makeMove(new QuaxCoordinate(0, i, true), QuaxTileColour.BLACK);
        }


        robot.clickOn("#octagon0_10");
        assertEquals(true,controller.getBoard().checkForWinningMove());
    }
}