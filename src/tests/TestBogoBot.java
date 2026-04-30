package tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import controller.QuaxController;
import player.BotPlayer;
import player.HumanPlayer;
import types.QuaxTile;
import types.QuaxTileColour;

public class TestBogoBot {
	
	@Test
	void testBogoBot1() {
		HumanPlayer human = new HumanPlayer();
		BotPlayer bot = new BotPlayer();
		
		QuaxController controller = new QuaxController(human, bot);
		
		assertEquals(0, controller.getMoveNumber());
	}
	
	@Test
	void testBogoBot2() {
		HumanPlayer human = new HumanPlayer();
		BotPlayer bot = new BotPlayer();
		
		QuaxController controller = new QuaxController(bot, human);
		
		assertEquals(1, controller.getMoveNumber());
	}
	
	@Test
	void testBogoBot3() {
		HumanPlayer human = new HumanPlayer();
		BotPlayer bot = new BotPlayer();
		
		QuaxController controller = new QuaxController(bot, human);
		
		int count = 0;
		for (QuaxTile t : controller.getQuaxBoard()) {
			if (t.getTileColour() == QuaxTileColour.BLACK) {
				count++;
			}
		}
		assertEquals(1, count);
	}
	
	@Test
	void testBogoBotPieRule() {
		HumanPlayer human = new HumanPlayer();
		BotPlayer bot = new BotPlayer();
		
		QuaxController controller = new QuaxController(bot, human);

		controller.doPieRule();
		
		// After the human uses the Pie rule, Bogo bot should immediately make another move.
		assertEquals(3, controller.getMoveNumber());
	}

	@Test
	void testBogoBotVersusBogoBot() {
		BotPlayer b1 = new BotPlayer();
		BotPlayer b2 = new BotPlayer();
		
		/* Creating a QuaxController automatically starts the game - and
		 * in our instance we have two Bogo Bots who will keep making
		 * random moves until the game is over.
		 */
		QuaxController controller = new QuaxController(b1, b2);
		
		// Check to see did the bots complete the game
		assertTrue(controller.getQuaxBoard().checkForWinningMove());
	}
	
	
	
}
