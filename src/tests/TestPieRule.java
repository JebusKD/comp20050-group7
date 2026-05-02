package tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import controller.QuaxController;
import player.*;
import static types.QuaxCoordinate.*;
import types.QuaxTileColour;


class TestPieRule {

	QuaxController testQuaxController;
	QuaxPlayer tester1;
	QuaxPlayer tester2;
	
	@BeforeEach
	void setUp() throws Exception {
		tester1 = new HumanPlayer();
		tester2 = new HumanPlayer();
		testQuaxController = new QuaxController(tester1, tester2);
	}


	@Test
	void testPieRuleSuccess() {
		testQuaxController.attemptMove(newOctagonCoordinate(3, 3));
		testQuaxController.doPieRule();

		assertEquals(QuaxTileColour.WHITE, testQuaxController.curPlayer().getPlayerColour());
		assertEquals(tester1, testQuaxController.curPlayer());
	}


	@Test
	void testPieRuleFailure1() {
		testQuaxController.doPieRule();

		assertEquals(QuaxTileColour.BLACK, testQuaxController.curPlayer().getPlayerColour());
		assertEquals(tester1, testQuaxController.curPlayer());
	}
	
	@Test
	void testPieRuleFailure2() {
		testQuaxController.attemptMove(newOctagonCoordinate(3, 3));
		testQuaxController.attemptMove(newOctagonCoordinate(3, 4));
		testQuaxController.doPieRule();

		assertEquals(QuaxTileColour.BLACK, testQuaxController.curPlayer().getPlayerColour());
		assertEquals(tester1, testQuaxController.curPlayer());
	}
	
	@Test
	void testPieRuleFailure3() {
		testQuaxController.attemptMove(newOctagonCoordinate(3, 3));
		testQuaxController.attemptMove(newOctagonCoordinate(3, 4));
		testQuaxController.attemptMove(newOctagonCoordinate(3, 5));
		testQuaxController.doPieRule();

		assertEquals(QuaxTileColour.WHITE, testQuaxController.curPlayer().getPlayerColour());
		assertEquals(tester2, testQuaxController.curPlayer());
	}
}
