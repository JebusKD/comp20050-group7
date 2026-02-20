package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Method;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import controller.QuaxController;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import player.NullPlayer;
import player.QuaxPlayer;
import types.QuaxTileColour;

class TestGameWin {

	static QuaxController controller;
	static Stage stage;
	
	static Method startGame;
	
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		
		// Code for creating a stage that doesn't appear.
		
		// Source - https://stackoverflow.com/a/25561862
		// Posted by dejuknow
		// Retrieved 2026-02-20, License - CC BY-SA 3.0

		    stage = new Stage();

		    stage.initStyle(StageStyle.UTILITY);
		    stage.setMaxHeight(0);
		    stage.setMaxWidth(0);
		    stage.setX(Double.MAX_VALUE);

		
		controller = new QuaxController(stage);

		startGame = controller.getClass().getMethod("startGame", QuaxPlayer.class, QuaxPlayer.class);
		startGame.setAccessible(true);
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
		startGame.setAccessible(false);
	}

	@BeforeEach
	void setUp() throws Exception {
		startGame.invoke(controller, new NullPlayer(QuaxTileColour.BLACK, stage), new NullPlayer(QuaxTileColour.WHITE, stage));
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void test() {
		assertEquals(3, 3);
	}

}
