package tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import controller.QuaxController;
import player.BotPlayer;
import player.HumanPlayer;
import types.QuaxTile;
import types.QuaxTileColour;

public class TestBot {
	
	@Test
	void testBot1() {
		HumanPlayer human = new HumanPlayer();
		BotPlayer bot = new BotPlayer();
		
		QuaxController controller = new QuaxController(human, bot);
		
		assertEquals(0, controller.getMoveNumber());
	}
	
	@Test
	void testBot2() {
		HumanPlayer human = new HumanPlayer();
		BotPlayer bot = new BotPlayer();
		
		QuaxController controller = new QuaxController(bot, human);
		
		assertEquals(1, controller.getMoveNumber());
	}
	
	@Test
	void testBot3() {
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
	void testBotPieRule() {
		HumanPlayer human = new HumanPlayer();
		BotPlayer bot = new BotPlayer();
		
		QuaxController controller = new QuaxController(bot, human);

		controller.doPieRule();

		// After the human uses the Pie rule, Bot should immediately make another move.
		assertEquals(3, controller.getMoveNumber());
	}

	// NOTE - May take a few minutes
	@Test
	void testBotVersusBot() {
		BotPlayer b1 = new BotPlayer();
		BotPlayer b2 = new BotPlayer();
		
		/* Creating a QuaxController automatically starts the game - and
		 * in our instance we have two Bots who will keep making
		 * moves until the game is over.
		 */
		QuaxController controller = new QuaxController(b1, b2);
		
		// Check to see did the bots complete the game
		assertTrue(controller.getQuaxBoard().checkForWinningMove());
	}
}
