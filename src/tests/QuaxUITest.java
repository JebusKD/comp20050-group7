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
import controller.QuaxController;
import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.WindowMatchers.isShowing;
import org.testfx.api.FxAssert;
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
    void ShowStrategyButtonExists(FxRobot robot) {
        assertNotNull(robot.lookup("Show Strategy").query());
    }

    @Test
    void HideStrategyButtonExists(FxRobot robot) {
        assertNotNull(robot.lookup("Hide Strategy").query());
    }

    @Test
    void PieRuleButtonExists(FxRobot robot){
        assertNotNull(robot.lookup("PieRule").query());
    }

    @Test
    void OctagonClicked(FxRobot robot) {
        robot.clickOn("#octagon5-5");
        assertEquals(QuaxTileColour.BLACK,controller.getBoard().getOctagon(5,5).getColour());
    }

    @Test
    void WinningMove(FxRobot robot){
        QuaxBoard board = controller.getBoard();

        for (int i = 0; i < 10; i++) {
            board.makeMove(new QuaxCoordinate(0, i, true), QuaxTileColour.BLACK);
        }


        robot.clickOn("#octagon0-10");
        assertEquals(true,controller.getBoard().checkForWinningMove());
    }

    @Test
    void InvalidRhombusPlacement(FxRobot robot){
        QuaxBoard board = controller.getBoard();
        robot.clickOn("#rhombus5-5");
        assertEquals(QuaxTileColour.NONE,board.getRhombus(5,5).getColour());
    }

    @Test
    void validRhombusPlacement(FxRobot robot){
        QuaxBoard board = controller.getBoard();
        board.makeMove(new QuaxCoordinate(5, 5, true), QuaxTileColour.BLACK);
        board.makeMove(new QuaxCoordinate(5,6, true), QuaxTileColour.BLACK);
        board.makeMove(new QuaxCoordinate(6, 6, true), QuaxTileColour.BLACK);
        board.makeMove(new QuaxCoordinate(1, 6, true), QuaxTileColour.BLACK);
        robot.clickOn("#rhombus5-5");
        assertEquals(QuaxTileColour.BLACK,board.getRhombus(5,5).getColour());
    }

    @Test
    void OctagonObjectDisplayExists(FxRobot robot){
        Node turnOct = robot.lookup("#Octagon-object").query();
        assertEquals(true,robot.lookup("#Octagon-object").query().isVisible());
    }

    @Test
    void RhombusObjectDisplayExists(FxRobot robot){
        Node turnRhom = robot.lookup("#Rhombus-object").query();
        assertEquals(true,robot.lookup("#Rhombus-object").query().isVisible());
    }

    @Test
    void TurnOctagonObject_ChangesColour(FxRobot robot){
        robot.clickOn("#octagon5-5");
        Node turnOct = robot.lookup("#Octagon-object").query();
        assertTrue(turnOct.getStyleClass().contains("tilecolour-white"));
    }

    @Test
    void TurnRhombusObject_ChangesColour(FxRobot robot){
        robot.clickOn("#octagon5-5");
        Node turnRhombus = robot.lookup("#Rhombus-object").query();
        assertTrue(turnRhombus.getStyleClass().contains("tilecolour-white"));
    }

}