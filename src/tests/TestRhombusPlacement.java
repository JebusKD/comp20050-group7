package tests;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.QuaxBoard;
import static types.QuaxCoordinate.*;
import types.QuaxTileColour;


public class TestRhombusPlacement {

	private QuaxBoard b;

	@BeforeEach
	void setUp() throws Exception {
		b = new QuaxBoard();
	}
	
	
	// Tests that a rhombic tile cannot be placed away from other tiles.
	@Test
	void testBlackRhombusPlacement1() {
		assertFalse(b.validMove(newRhombusCoordinate(4, 6), QuaxTileColour.BLACK));
	}
	
	// Tests that a rhombic tile cannot be placed adjacent to exactly one tile of a colour.
	@Test
	void testBlackRhombusPlacement2() {
		b.makeMove(newOctagonCoordinate(4, 6), QuaxTileColour.BLACK);
		
		assertFalse(b.validMove(newRhombusCoordinate(4, 6), QuaxTileColour.BLACK));
	}
	
	// Tests an instance where a downwards diagonal exists and a rhombus can be placed
	@Test
	void testBlackRhombusPlacement3() {
		b.makeMove(newOctagonCoordinate(4, 6), QuaxTileColour.BLACK);
		b.makeMove(newOctagonCoordinate(5, 7), QuaxTileColour.BLACK);
		
		assertTrue(b.validMove(newRhombusCoordinate(4, 6), QuaxTileColour.BLACK));
	}
	
	// Tests an instance where the diagonal is going upwards and a rhombus can be placed.
	@Test
	void testBlackRhombusPlacement4() {
		b.makeMove(newOctagonCoordinate(5, 6), QuaxTileColour.BLACK);
		b.makeMove(newOctagonCoordinate(4, 7), QuaxTileColour.BLACK);
		
		assertTrue(b.validMove(newRhombusCoordinate(4, 6), QuaxTileColour.BLACK));
	}
	
	// Ensures rhombic tiles can be placed in an instance where both players can place a rhombus.
	@Test
	void testBlackRhombusPlacement5() {
		b.makeMove(newOctagonCoordinate(4, 7), QuaxTileColour.BLACK);
		b.makeMove(newOctagonCoordinate(5, 6), QuaxTileColour.BLACK);
		b.makeMove(newOctagonCoordinate(4, 6), QuaxTileColour.WHITE);
		b.makeMove(newOctagonCoordinate(5, 7), QuaxTileColour.WHITE);
		
		assertTrue(b.validMove(newRhombusCoordinate(4, 6), QuaxTileColour.BLACK));
	}
	
	// Ensures rhombic tiles don't depend on the other player's tiles to be placed.
	@Test
	void testBlackRhombusPlacement6() {
		b.makeMove(newOctagonCoordinate(4, 6), QuaxTileColour.WHITE);
		b.makeMove(newOctagonCoordinate(5, 6), QuaxTileColour.WHITE);
		
		assertFalse(b.validMove(newRhombusCoordinate(4, 6), QuaxTileColour.BLACK));
	}



	// Tests that a rhombic tile cant be placed away from other tiles.
	@Test
	void testWhiteRhombusPlacement1() {
		assertFalse(b.validMove(newRhombusCoordinate(4, 6), QuaxTileColour.WHITE));
	}
	
	// Tests that a rhombic tile cannot be placed adjacent to exactly one tile of a colour.
	@Test
	void testWhiteRhombusPlacement2() {
		b.makeMove(newOctagonCoordinate(4, 6), QuaxTileColour.WHITE);
		
		assertFalse(b.validMove(newRhombusCoordinate(4, 6), QuaxTileColour.WHITE));
	}
	
	// Tests an instance where a downwards diagonal exists.
	@Test
	void testWhiteRhombusPlacement3() {
		b.makeMove(newOctagonCoordinate(4, 6), QuaxTileColour.WHITE);
		b.makeMove(newOctagonCoordinate(5, 7), QuaxTileColour.WHITE);
		
		assertTrue(b.validMove(newRhombusCoordinate(4, 6), QuaxTileColour.WHITE));
	}

	// Tests an instance where the diagonal is going upwards.
	@Test
	void testWhiteRhombusPlacement4() {
		b.makeMove(newOctagonCoordinate(5, 6), QuaxTileColour.WHITE);
		b.makeMove(newOctagonCoordinate(4, 7), QuaxTileColour.WHITE);
		
		assertTrue(b.validMove(newRhombusCoordinate(4, 6), QuaxTileColour.WHITE));
	}
	
	// Ensures rhombic tiles can be placed in an instance where both players can place a rhombus.
	@Test
	void testWhiteRhombusPlacement5() {
		b.makeMove(newOctagonCoordinate(4, 7), QuaxTileColour.WHITE);
		b.makeMove(newOctagonCoordinate(5, 6), QuaxTileColour.WHITE);
		b.makeMove(newOctagonCoordinate(4, 6), QuaxTileColour.BLACK);
		b.makeMove(newOctagonCoordinate(5, 7), QuaxTileColour.BLACK);
		
		assertTrue(b.validMove(newRhombusCoordinate(4, 6), QuaxTileColour.WHITE));
	}
	
	// Ensures rhombic tiles don't depend on the other player's tiles to be placed.
	@Test
	void testWhiteRhombusPlacement6() {
		b.makeMove(newOctagonCoordinate(4, 6), QuaxTileColour.BLACK);
		b.makeMove(newOctagonCoordinate(5, 6), QuaxTileColour.BLACK);
		
		assertFalse(b.validMove(newRhombusCoordinate(4, 6), QuaxTileColour.WHITE));
	}
}
