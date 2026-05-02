package tests;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

import controller.QuaxController;
import static org.junit.jupiter.api.Assertions.*;
import model.QuaxBoard;
import types.QuaxTileColour;


@ExtendWith(ApplicationExtension.class)
public class QuaxUITest {

	private QuaxController testUIController;

    @Start
    public void start(Stage stage) throws Exception {
        // human v human game for testing
        testUIController = new QuaxController(stage,false, false);
    }


    @Test
    void testShowStrategyButtonExists(FxRobot robot) {
        assertNotNull(robot.lookup("Show Strategy").query());
    }

    @Test
    void testHideStrategyButtonExists(FxRobot robot) {
        assertNotNull(robot.lookup("Hide Strategy").query());
    }

    @Test
    void testPieRuleButtonExists(FxRobot robot){
        assertNotNull(robot.lookup("PieRule").query());
    }


    @Test
    void testOctagonClicked(FxRobot robot) {
        robot.clickOn("#octagon5-5");

        assertEquals(QuaxTileColour.BLACK, testUIController.getQuaxBoard().getOctagon(5,5).getTileColour());
    }

    @Test
    void testWinningMove(FxRobot robot) {

        for (int i = 0 ; i < 10 ; i++) {
            robot.clickOn("#octagon5-" + i);
            robot.clickOn("#octagon1-" + i);
        }

        robot.clickOn("#octagon5-10");

        assertTrue(testUIController.getQuaxBoard().checkForWinningMove());
    }

    @Test
    void testInvalidRhombusPlacement(FxRobot robot) {
        QuaxBoard board = testUIController.getQuaxBoard();
        robot.clickOn("#rhombus5-5");

        assertEquals(QuaxTileColour.NONE,board.getRhombus(5,5).getTileColour());
    }

    @Test
    void validRhombusPlacement(FxRobot robot) {
        QuaxBoard board = testUIController.getQuaxBoard();

        robot.clickOn("#octagon5-5"); //Black goes first
        robot.clickOn("#octagon0-0"); //just have white turn click somewhere else
        robot.clickOn("#octagon6-6");
        robot.clickOn("#octagon0-1");

        robot.clickOn("#rhombus5-5");

        assertEquals(QuaxTileColour.BLACK, board.getRhombus(5,5).getTileColour());
    }


    @Test
    void testOctagonTurnIndicatorExists(FxRobot robot) {
        Node turnOct = robot.lookup("#Octagon-Turn-Indicator").query();

        assertTrue(turnOct.isVisible());
    }

    @Test
    void testRhombusTurnIndicatorDisplayExists(FxRobot robot) {
        Node turnRhombus = robot.lookup("#Rhombus-Turn-Indicator").query();

        assertTrue(turnRhombus.isVisible());
    }

    @Test
    void testOctagonTurnIndicatorChangesColour(FxRobot robot) {
        robot.clickOn("#octagon5-5");
        Node turnOct = robot.lookup("#Octagon-Turn-Indicator").query();

        assertTrue(turnOct.getStyleClass().contains("tilecolour-white"));
    }

    @Test
    void testRhombusTurnIndicatorChangesColour(FxRobot robot) {
        robot.clickOn("#octagon5-5");
        Node turnRhombus = robot.lookup("#Rhombus-Turn-Indicator").query();

        assertTrue(turnRhombus.getStyleClass().contains("tilecolour-white"));
    }


    @Test
    void testPieRuleButtonInvisibleOnceClicked(FxRobot robot) {
        robot.clickOn("#octagon5-5");
        robot.clickOn("#PieRule");
        WaitForAsyncUtils.waitForFxEvents();

        assertFalse(robot.lookup("#PieRule").query().isVisible());
    }

    @Test
    void testPieRuleButtonLocked(FxRobot robot) {
        robot.clickOn("#octagon5-5");
        robot.clickOn("#octagon0-0");

        assertFalse(robot.lookup("#PieRule").query().isVisible());//Pie rule should disappear
    }

    @Test
    void testPieRuleSwapsPlayerColours(FxRobot robot) {
        robot.clickOn("#octagon5-5"); //player one makes move
        robot.clickOn("#PieRule"); //player two clicks PieRule

        //player one should now be the colour player two started out as
        assertEquals(QuaxTileColour.WHITE, testUIController.getFirstPlayerColour());
        //player two should now be the colour player one started out as
        assertEquals(QuaxTileColour.BLACK, testUIController.getSecondPlayerColour());

    }


    @Test
    void testNumberCoordinatesExist(FxRobot robot) {
        //there is 22 of each coordinate type, all with the same styling
        assertEquals(22, robot.lookup(".coordinate-number-style").queryAll().size());
    }

    @Test
    void testLetterCoordinatesExist(FxRobot robot) {
        assertEquals(22, robot.lookup(".coordinate-letter-style").queryAll().size());
    }

    @Test
    void testWinLabelIsDisplayedBlack(FxRobot robot) {
        for (int i = 0 ; i < 10 ; i++) {
            robot.clickOn("#octagon5-" + i);
            robot.clickOn("#octagon1-" + i);
        }

        robot.clickOn("#octagon5-10");
        Label winLabel = robot.lookup(".win-label").queryAs(Label.class);

        assertEquals("BLACK wins", winLabel.getText());
    }
    
    @Test
    void testWinLabelIsDisplayedWhite(FxRobot robot){
    	robot.clickOn("#octagon8-0"); // Waste Black's first move

        for (int i = 0 ; i < 10 ; i++) {
            robot.clickOn("#octagon" + i + "-3");
            robot.clickOn("#octagon" + i + "-6");
        }

        robot.clickOn("#octagon10-3");
        Label winLabel = robot.lookup(".win-label").queryAs(Label.class);

        assertEquals("WHITE wins", winLabel.getText());
    }
    
    @Test
    void testWinLabelIsDisplayed(FxRobot robot) { //TODO - Why do we have a duplicate test?
        for (int i = 0 ; i < 10 ; i++) {
            robot.clickOn("#octagon5-" + i);
            robot.clickOn("#octagon1-" + i);
        }

        robot.clickOn("#octagon5-10");
        Label winLabel = robot.lookup(".win-label").queryAs(Label.class);

        assertEquals("BLACK wins",winLabel.getText());
    }


    @Test
    void testShowTitleExists(FxRobot robot) {
        assertNotNull(robot.lookup("#Title").query());
    }

    @Test
    void testShowTitleTextCorrect(FxRobot robot) {
        Label title = robot.lookup("#Title").queryAs(Label.class);

        assertEquals("Quax (Human V Bot)",title.getText());
    }
}