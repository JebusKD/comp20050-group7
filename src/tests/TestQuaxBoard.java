package tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.QuaxBoard;
import types.QuaxCoordinate;
import types.QuaxTile;
import types.QuaxTileColour;

class TestQuaxBoard {

	private QuaxBoard b;

	@BeforeEach
	void setUp() throws Exception {
		b = new QuaxBoard();
	}

	@Test
	void testWinningBoard1() {
		for (int i = 0; i < 11; i++) {
			b.makeMove(new QuaxCoordinate(i, 3, true), QuaxTileColour.WHITE);
		}
		assertTrue(b.checkForWinningMove());
	}
	
	@Test
	void testWinningBoard2() {
		for (int i = 0; i < 11; i++) {
			b.makeMove(new QuaxCoordinate(6, i, true), QuaxTileColour.BLACK);
		}
		assertTrue(b.checkForWinningMove());
	}
	
	// Not going the full way should result in no win
	@Test
	void testWinningBoard3() {
		for (int i = 0; i < 10; i++) {
			b.makeMove(new QuaxCoordinate(i, 3, true), QuaxTileColour.WHITE);
		}
		assertFalse(b.checkForWinningMove());
	}
	
	@Test
	void testWinningBoard4() {
		for (int i = 0; i < 10; i++) {
			b.makeMove(new QuaxCoordinate(6, i, true), QuaxTileColour.BLACK);
		}
		assertFalse(b.checkForWinningMove());
	}
	
	// Feature that prevents moves after the game is won.
	@Test
	void testWinningBoardInvalidMove() {
		for (int i = 0; i < 11; i++) {
			b.makeMove(new QuaxCoordinate(6, i, true), QuaxTileColour.BLACK);
		}
		assertFalse(b.validMove(new QuaxCoordinate(8, 8, true), QuaxTileColour.BLACK));
	}
	
	// Checks if a correctly-sized array is created for octagon's neighbours.
	@Test
	void testNeighboursArraySize1() {
		QuaxTile[][] neighbours = b.neighbours(new QuaxCoordinate(5, 5, true));
		assertEquals(3, neighbours.length);
		assertEquals(3, neighbours[0].length);
		assertEquals(3, neighbours[1].length);
		assertEquals(3, neighbours[2].length);
	}
	
	// Checks if a correctly-sized array is created for rhombus's neighbours.
	@Test
	void testNeighboursArraySize2() {
		QuaxTile[][] neighbours = b.neighbours(new QuaxCoordinate(4, 3, false));
		assertEquals(2, neighbours.length);
		assertEquals(2, neighbours[0].length);
		assertEquals(2, neighbours[1].length);
	}

}
