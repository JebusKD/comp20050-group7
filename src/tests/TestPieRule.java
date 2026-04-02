package tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import controller.QuaxController;
import player.HumanPlayer;
import player.QuaxPlayer;
import types.QuaxCoordinate;
import types.QuaxTileColour;

class TestPieRule {

	QuaxController controller;
	QuaxPlayer p1;
	QuaxPlayer p2;
	
	@BeforeEach
	void setUp() throws Exception {
		p1 = new HumanPlayer("Player 1", QuaxTileColour.BLACK);
		p2 = new HumanPlayer("Player 2", QuaxTileColour.WHITE);
		controller = new QuaxController(p1, p2);
	}
	
	@Test
	void testPieRuleSuccess() {
		controller.makeMove(new QuaxCoordinate(3, 3, true));
		controller.doPieRule();
		assertEquals(QuaxTileColour.WHITE, controller.curPlayer().getColour());
		assertEquals(p1, controller.curPlayer());
	}
	
	@Test
	void testPieRuleFailure1() {
		controller.doPieRule();
		assertEquals(QuaxTileColour.BLACK, controller.curPlayer().getColour());
		assertEquals(p1, controller.curPlayer());
	}
	
	@Test
	void testPieRuleFailure2() {
		controller.makeMove(new QuaxCoordinate(3, 3, true));
		controller.makeMove(new QuaxCoordinate(3, 4, true));
		controller.doPieRule();
		assertEquals(QuaxTileColour.BLACK, controller.curPlayer().getColour());
		assertEquals(p1, controller.curPlayer());
	}
	
	@Test
	void testPieRuleFailure3() {
		controller.makeMove(new QuaxCoordinate(3, 3, true));
		controller.makeMove(new QuaxCoordinate(3, 4, true));
		controller.makeMove(new QuaxCoordinate(3, 5, true));
		controller.doPieRule();
		assertEquals(QuaxTileColour.WHITE, controller.curPlayer().getColour());
		assertEquals(p2, controller.curPlayer());
	}

}
