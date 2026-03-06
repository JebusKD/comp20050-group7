package tests;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.QuaxBoard;
import types.QuaxCoordinate;
import types.QuaxTileColour;

public class TestRhombusPlacement {

	private QuaxBoard b;

	@BeforeEach
	void setUp() throws Exception {
		b = new QuaxBoard();
	}
	
	
	// Tests that a rhombic tile cant be placed away from other tiles.
	@Test
	void testRhombusPlacement1() {
		assertFalse(b.validMove(new QuaxCoordinate(4, 6, false), QuaxTileColour.BLACK));
	}
	
	// Tests that a rhombic tile cannot be placed adjacent to exactly one tile of a colour.
	@Test
	void testRhombusPlacement2() {
		b.makeMove(new QuaxCoordinate(4, 6, true), QuaxTileColour.BLACK);
		
		assertFalse(b.validMove(new QuaxCoordinate(4, 6, false), QuaxTileColour.BLACK));
	}
	
	// Tests an instance where a downwards diagonal exists.
	@Test
	void testRhombusPlacement3() {
		b.makeMove(new QuaxCoordinate(4, 6, true), QuaxTileColour.BLACK);
		b.makeMove(new QuaxCoordinate(5, 7, true), QuaxTileColour.BLACK);
		
		assertTrue(b.validMove(new QuaxCoordinate(4, 6, false), QuaxTileColour.BLACK));
	}
	
	// Tests an instance where the diagonal is going upwards.
	@Test
	void testRhombusPlacement4() {
		b.makeMove(new QuaxCoordinate(5, 6, true), QuaxTileColour.BLACK);
		b.makeMove(new QuaxCoordinate(4, 7, true), QuaxTileColour.BLACK);
		
		assertTrue(b.validMove(new QuaxCoordinate(4, 6, false), QuaxTileColour.BLACK));
	}
	
	// Ensures rhombic tiles can be placed in an instance where both players can place a rhombus.
	@Test
	void testRhombusPlacement5() {
		b.makeMove(new QuaxCoordinate(4, 7, true), QuaxTileColour.BLACK);
		b.makeMove(new QuaxCoordinate(5, 6, true), QuaxTileColour.BLACK);
		b.makeMove(new QuaxCoordinate(4, 6, true), QuaxTileColour.WHITE);
		b.makeMove(new QuaxCoordinate(5, 7, true), QuaxTileColour.WHITE);
		
		assertTrue(b.validMove(new QuaxCoordinate(4, 6, false), QuaxTileColour.BLACK));
	}
	
	// Ensures rhombic tiles don't depend on the other player's tiles to be placed.
	@Test
	void testRhombusPlacement6() {
		b.makeMove(new QuaxCoordinate(4, 6, true), QuaxTileColour.WHITE);
		b.makeMove(new QuaxCoordinate(5, 6, true), QuaxTileColour.WHITE);
		
		assertFalse(b.validMove(new QuaxCoordinate(4, 6, false), QuaxTileColour.BLACK));
	}
	
	// Tests that a rhombic tile cant be placed away from other tiles.
	@Test
	void testRhombusPlacement7() {
		assertFalse(b.validMove(new QuaxCoordinate(4, 6, false), QuaxTileColour.WHITE));
	}
	
	// Tests that a rhombic tile cannot be placed adjacent to exactly one tile of a colour.
	@Test
	void testRhombusPlacement8() {
		b.makeMove(new QuaxCoordinate(4, 6, true), QuaxTileColour.WHITE);
		
		assertFalse(b.validMove(new QuaxCoordinate(4, 6, false), QuaxTileColour.WHITE));
	}
	
	// Tests an instance where a downwards diagonal exists.
	@Test
	void testRhombusPlacement9() {
		b.makeMove(new QuaxCoordinate(4, 6, true), QuaxTileColour.WHITE);
		b.makeMove(new QuaxCoordinate(5, 7, true), QuaxTileColour.WHITE);
		
		assertTrue(b.validMove(new QuaxCoordinate(4, 6, false), QuaxTileColour.WHITE));
	}
	
	// Tests an instance where the diagonal is going upwards.
	@Test
	void testRhombusPlacement10() {
		b.makeMove(new QuaxCoordinate(5, 6, true), QuaxTileColour.WHITE);
		b.makeMove(new QuaxCoordinate(4, 7, true), QuaxTileColour.WHITE);
		
		assertTrue(b.validMove(new QuaxCoordinate(4, 6, false), QuaxTileColour.WHITE));
	}
	
	// Ensures rhombic tiles can be placed in an instance where both players can place a rhombus.
	@Test
	void testRhombusPlacement11() {
		b.makeMove(new QuaxCoordinate(4, 7, true), QuaxTileColour.WHITE);
		b.makeMove(new QuaxCoordinate(5, 6, true), QuaxTileColour.WHITE);
		b.makeMove(new QuaxCoordinate(4, 6, true), QuaxTileColour.BLACK);
		b.makeMove(new QuaxCoordinate(5, 7, true), QuaxTileColour.BLACK);
		
		assertTrue(b.validMove(new QuaxCoordinate(4, 6, false), QuaxTileColour.WHITE));
	}
	
	// Ensures rhombic tiles don't depend on the other player's tiles to be placed.
	@Test
	void testRhombusPlacement12() {
		b.makeMove(new QuaxCoordinate(4, 6, true), QuaxTileColour.BLACK);
		b.makeMove(new QuaxCoordinate(5, 6, true), QuaxTileColour.BLACK);
		
		assertFalse(b.validMove(new QuaxCoordinate(4, 6, false), QuaxTileColour.WHITE));
	}
}
