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
        controller = new QuaxController(stage,false);//human v human game for testing
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

        for(int i = 0; i < 10;i++){
            robot.clickOn("#octagon5-" + i);
            robot.clickOn("#octagon1-" + i);
        }

        robot.clickOn("#octagon5-10");
        assertTrue(controller.getBoard().checkForWinningMove());
    }

    @Test
    void InvalidRhombusPlacement(FxRobot robot){
        QuaxBoard board = controller.getBoard();
        robot.clickOn("#rhombus5-5");
        assertEquals(QuaxTileColour.NONE,board.getRhombus(5,5).getColour());
    }

    //this is passing when it shouldn't
    @Test
    void validRhombusPlacement(FxRobot robot){
        QuaxBoard board = controller.getBoard();

        robot.clickOn("#octagon5-5");//Black goes first
        robot.clickOn("#octagon0-0");//just have white turn click somehwere else
        robot.clickOn("#octagon6-6");
        robot.clickOn("#octagon0-1");

        robot.clickOn("#rhombus5-5");
        assertEquals(QuaxTileColour.BLACK,board.getRhombus(5,5).getColour());
    }

    @Test
    void OctagonObjectDisplayExists(FxRobot robot){
        Node turnOct = robot.lookup("#Octagon-object").query();
        assertTrue(turnOct.isVisible());
    }

    @Test
    void RhombusObjectDisplayExists(FxRobot robot){
        Node turnRhom = robot.lookup("#Rhombus-object").query();
        assertTrue(turnRhom.isVisible());
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

    @Test
    void PieRuleButtonInvisibleOnceClicked(FxRobot robot){
        robot.clickOn("#octagon5-5");
        robot.clickOn("#PieRule");
        assertFalse(robot.lookup("#PieRule").query().isVisible());
    }

    @Test
    void PieRuleButtonLocked(FxRobot robot){
        robot.clickOn("#octagon5-5");
        robot.clickOn("#octagon0-0");
        robot.clickOn("#PieRule");
        assertTrue(robot.lookup("#PieRule").query().isVisible());//Pie rule should not be activated

    }

    @Test
    void PieRuleSwapsPlayerColours(FxRobot robot){
        robot.clickOn("#octagon5-5"); //player one makes move
        robot.clickOn("#PieRule"); //player two clicks PieRule

        //player one should now be the colour player two started out as
        assertEquals(QuaxTileColour.WHITE,controller.getPlayerColour(0));
        //player two should now be the colour player one started out as
        assertEquals(QuaxTileColour.BLACK,controller.getPlayerColour(1));

    }

    @Test
    void NumberCoordsExist(FxRobot robot){
        assertEquals(22, robot.lookup(".coordinate-number-style").queryAll().size()); //theres 22 of each coordinate type, all with the same styling
    }


    @Test
    void LetterCoordsExist(FxRobot robot){
        assertEquals(22, robot.lookup(".coordinate-letter-style").queryAll().size());
    }

    @Test
    void WinLabelIsDisplayed(FxRobot robot){
        QuaxBoard board = controller.getBoard();

        for(int i = 0; i < 10;i++){
            robot.clickOn("#octagon5-" + i);
            robot.clickOn("#octagon1-" + i);
        }

        robot.clickOn("#octagon5-10");
        Label winLabel = robot.lookup(".win-label").queryAs(Label.class);
        assertEquals("BLACK wins",winLabel.getText());
    }

}