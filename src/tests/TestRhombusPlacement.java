package tests;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.QuaxBoard;
import static types.QuaxCoordinate.*;
import types.QuaxTileColour;


public class TestRhombusPlacement {

	private QuaxBoard placingTestBoard;

	@BeforeEach
	void setUp() throws Exception {
		placingTestBoard = new QuaxBoard();
	}
	
	
	// Tests that a rhombic tile cannot be placed away from other tiles.
	@Test
	void testBlackRhombusPlacement1() {
		assertFalse(placingTestBoard.validMove(newRhombusCoordinate(4, 6), QuaxTileColour.BLACK));
	}
	
	// Tests that a rhombic tile cannot be placed adjacent to exactly one tile of a colour.
	@Test
	void testBlackRhombusPlacement2() {
		placingTestBoard.makeMove(newOctagonCoordinate(4, 6), QuaxTileColour.BLACK);
		
		assertFalse(placingTestBoard.validMove(newRhombusCoordinate(4, 6), QuaxTileColour.BLACK));
	}
	
	// Tests an instance where a downwards diagonal exists and a rhombus can be placed
	@Test
	void testBlackRhombusPlacement3() {
		placingTestBoard.makeMove(newOctagonCoordinate(4, 6), QuaxTileColour.BLACK);
		placingTestBoard.makeMove(newOctagonCoordinate(5, 7), QuaxTileColour.BLACK);
		
		assertTrue(placingTestBoard.validMove(newRhombusCoordinate(4, 6), QuaxTileColour.BLACK));
	}
	
	// Tests an instance where the diagonal is going upwards and a rhombus can be placed.
	@Test
	void testBlackRhombusPlacement4() {
		placingTestBoard.makeMove(newOctagonCoordinate(5, 6), QuaxTileColour.BLACK);
		placingTestBoard.makeMove(newOctagonCoordinate(4, 7), QuaxTileColour.BLACK);
		
		assertTrue(placingTestBoard.validMove(newRhombusCoordinate(4, 6), QuaxTileColour.BLACK));
	}
	
	// Ensures rhombic tiles can be placed in an instance where both players can place a rhombus.
	@Test
	void testBlackRhombusPlacement5() {
		placingTestBoard.makeMove(newOctagonCoordinate(4, 7), QuaxTileColour.BLACK);
		placingTestBoard.makeMove(newOctagonCoordinate(5, 6), QuaxTileColour.BLACK);
		placingTestBoard.makeMove(newOctagonCoordinate(4, 6), QuaxTileColour.WHITE);
		placingTestBoard.makeMove(newOctagonCoordinate(5, 7), QuaxTileColour.WHITE);
		
		assertTrue(placingTestBoard.validMove(newRhombusCoordinate(4, 6), QuaxTileColour.BLACK));
	}
	
	// Ensures rhombic tiles don't depend on the other player's tiles to be placed.
	@Test
	void testBlackRhombusPlacement6() {
		placingTestBoard.makeMove(newOctagonCoordinate(4, 6), QuaxTileColour.WHITE);
		placingTestBoard.makeMove(newOctagonCoordinate(5, 6), QuaxTileColour.WHITE);
		
		assertFalse(placingTestBoard.validMove(newRhombusCoordinate(4, 6), QuaxTileColour.BLACK));
	}



	// Tests that a rhombic tile cant be placed away from other tiles.
	@Test
	void testWhiteRhombusPlacement1() {
		assertFalse(placingTestBoard.validMove(newRhombusCoordinate(4, 6), QuaxTileColour.WHITE));
	}
	
	// Tests that a rhombic tile cannot be placed adjacent to exactly one tile of a colour.
	@Test
	void testWhiteRhombusPlacement2() {
		placingTestBoard.makeMove(newOctagonCoordinate(4, 6), QuaxTileColour.WHITE);
		
		assertFalse(placingTestBoard.validMove(newRhombusCoordinate(4, 6), QuaxTileColour.WHITE));
	}
	
	// Tests an instance where a downwards diagonal exists.
	@Test
	void testWhiteRhombusPlacement3() {
		placingTestBoard.makeMove(newOctagonCoordinate(4, 6), QuaxTileColour.WHITE);
		placingTestBoard.makeMove(newOctagonCoordinate(5, 7), QuaxTileColour.WHITE);
		
		assertTrue(placingTestBoard.validMove(newRhombusCoordinate(4, 6), QuaxTileColour.WHITE));
	}

	// Tests an instance where the diagonal is going upwards.
	@Test
	void testWhiteRhombusPlacement4() {
		placingTestBoard.makeMove(newOctagonCoordinate(5, 6), QuaxTileColour.WHITE);
		placingTestBoard.makeMove(newOctagonCoordinate(4, 7), QuaxTileColour.WHITE);
		
		assertTrue(placingTestBoard.validMove(newRhombusCoordinate(4, 6), QuaxTileColour.WHITE));
	}
	
	// Ensures rhombic tiles can be placed in an instance where both players can place a rhombus.
	@Test
	void testWhiteRhombusPlacement5() {
		placingTestBoard.makeMove(newOctagonCoordinate(4, 7), QuaxTileColour.WHITE);
		placingTestBoard.makeMove(newOctagonCoordinate(5, 6), QuaxTileColour.WHITE);
		placingTestBoard.makeMove(newOctagonCoordinate(4, 6), QuaxTileColour.BLACK);
		placingTestBoard.makeMove(newOctagonCoordinate(5, 7), QuaxTileColour.BLACK);
		
		assertTrue(placingTestBoard.validMove(newRhombusCoordinate(4, 6), QuaxTileColour.WHITE));
	}
	
	// Ensures rhombic tiles don't depend on the other player's tiles to be placed.
	@Test
	void testWhiteRhombusPlacement6() {
		placingTestBoard.makeMove(newOctagonCoordinate(4, 6), QuaxTileColour.BLACK);
		placingTestBoard.makeMove(newOctagonCoordinate(5, 6), QuaxTileColour.BLACK);
		
		assertFalse(placingTestBoard.validMove(newRhombusCoordinate(4, 6), QuaxTileColour.WHITE));
	}
}
