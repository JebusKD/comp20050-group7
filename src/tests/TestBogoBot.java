package tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import controller.QuaxController;
import player.BogoBot;
import player.HumanPlayer;
import types.QuaxTileColour;

public class TestBogoBot {
	
	@Test
	void testBogoBot1() {
		HumanPlayer human = new HumanPlayer(QuaxTileColour.BLACK);
		BogoBot bot = new BogoBot(QuaxTileColour.WHITE);
		
		QuaxController controller = new QuaxController(human, bot);
		
		assertEquals(0, controller.getMoveNumber());
	}
	
	@Test
	void testBogoBot2() {
		HumanPlayer human = new HumanPlayer(QuaxTileColour.WHITE);
		BogoBot bot = new BogoBot(QuaxTileColour.BLACK);
		
		QuaxController controller = new QuaxController(bot, human);
		
		assertEquals(1, controller.getMoveNumber());
	}
	
	@Test
	void testBogoBotPieRule() {
		HumanPlayer human = new HumanPlayer(QuaxTileColour.WHITE);
		BogoBot bot = new BogoBot(QuaxTileColour.BLACK);
		
		QuaxController controller = new QuaxController(bot, human);
		
		controller.doPieRule();
		
		// After the human uses the Pie rule, Bogo bot should immediately make another move.
		assertEquals(3, controller.getMoveNumber());
	}

	@Test
	void testBogoBotVersusBogoBot() {
		BogoBot b1 = new BogoBot(QuaxTileColour.BLACK);
		BogoBot b2 = new BogoBot(QuaxTileColour.WHITE);
		
		/* Creating a QuaxController automatically starts the game - and
		 * in our instance we have two Bogo Bots who will keep making
		 * random moves until the game is over.
		 */
		QuaxController controller = new QuaxController(b1, b2);
		
		// Check to see did the bots complete the game
		assertTrue(controller.getBoard().checkForWinningMove());
	}
	
}
