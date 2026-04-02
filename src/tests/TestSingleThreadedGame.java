package tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import controller.QuaxController;
import player.HumanPlayer;
import player.QuaxPlayer;
import types.QuaxCoordinate;
import types.QuaxTileColour;

class TestSingleThreadedGame {

	QuaxController controller;
	
	@BeforeEach
	void setUp() throws Exception {
		QuaxPlayer p1 = new HumanPlayer("Player 1", QuaxTileColour.BLACK);
		QuaxPlayer p2 = new HumanPlayer("Player 2", QuaxTileColour.WHITE);
		controller = new QuaxController(p1, p2);
	}
	
	@Test
	void testPieRule() {
		controller.makeMove(new QuaxCoordinate(3, 3, true));
		controller.doPieRule();
		assertEquals(2, controller.getBoard().getMoveNumber());
		assertEquals(QuaxTileColour.WHITE, controller.curPlayer().getColour());
	}
}
