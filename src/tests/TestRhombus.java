package tests;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import types.*;
import static types.StrategyValue.*;


class TestRhombus {

	@Test
	void testRhombusCoordinates1() {
		QuaxTile t = new Rhombus(5, 2);
		QuaxCoordinate q = t.getCoordinates();
		assertEquals(5, q.x());
	}
	
	@Test
	void testRhombusCoordinates2() {
		QuaxTile t = new Rhombus(1, 0);
		QuaxCoordinate q = t.getCoordinates();
		assertEquals(1, q.x());
	}
	
	@Test
	void testRhombusCoordinates3() {
		QuaxTile t = new Rhombus(8, 7);
		QuaxCoordinate q = t.getCoordinates();
		assertEquals(8, q.x());
	}
	
	@Test
	void testRhombusCoordinates4() {
		QuaxTile t = new Rhombus(0, 7);
		QuaxCoordinate q = t.getCoordinates();
		assertEquals(0, q.x());
	}
	
	@Test
	void testRhombusCoordinates5() {
		QuaxTile t = new Rhombus(5, 2);
		QuaxCoordinate q = t.getCoordinates();
		assertEquals(2, q.y());
	}
	
	@Test
	void testRhombusCoordinates6() {
		QuaxTile t = new Rhombus(1, 0);
		QuaxCoordinate q = t.getCoordinates();
		assertEquals(0, q.y());
	}
	
	@Test
	void testRhombusCoordinates7() {
		QuaxTile t = new Rhombus(8, 7);
		QuaxCoordinate q = t.getCoordinates();
		assertEquals(7, q.y());
	}
	
	@Test
	void testRhombusCoordinates8() {
		QuaxTile t = new Rhombus(0, 7);
		QuaxCoordinate q = t.getCoordinates();
		assertEquals(7, q.y());
	}


	@Test
	void testRhombusIsFree1() {
		QuaxTile t = new Rhombus(5, 5);
		t.setTileColour(QuaxTileColour.BLACK);
		assertFalse(t.isFree());
	}
	
	@Test
	void testRhombusIsFree2() {
		QuaxTile t = new Rhombus(5, 5);
		t.setTileColour(QuaxTileColour.WHITE);
		assertFalse(t.isFree());
	}
	
	@Test
	void testRhombusIsFree3() {
		QuaxTile t = new Rhombus(5, 5);
		t.setTileColour(QuaxTileColour.NONE);
		assertTrue(t.isFree());
	}
	
	@Test
	void testRhombusIsFree4() {
		QuaxTile t = new Rhombus(5, 5);
		assertTrue(t.isFree());
	}


	@Test
	void testRhombusIsBlack1() {
		QuaxTile t = new Rhombus(5, 5);
		t.setTileColour(QuaxTileColour.BLACK);
		assertTrue(t.isBlack());
	}

	@Test
	void testRhombusIsBlack2() {
		QuaxTile t = new Rhombus(5, 5);
		t.setTileColour(QuaxTileColour.WHITE);
		assertFalse(t.isBlack());
	}

	@Test
	void testRhombusIsBlack3() {
		QuaxTile t = new Rhombus(5, 5);
		t.setTileColour(QuaxTileColour.NONE);
		assertFalse(t.isBlack());
	}

	@Test
	void testRhombusIsBlack4() {
		QuaxTile t = new Rhombus(5, 5);
		assertFalse(t.isBlack());
	}

	@Test
	void testRhombusIsWhite1() {
		QuaxTile t = new Rhombus(5, 5);
		t.setTileColour(QuaxTileColour.BLACK);
		assertFalse(t.isWhite());
	}

	@Test
	void testRhombusIsWhite2() {
		QuaxTile t = new Rhombus(5, 5);
		t.setTileColour(QuaxTileColour.WHITE);
		assertTrue(t.isWhite());
	}

	@Test
	void testRhombusIsWhite3() {
		QuaxTile t = new Rhombus(5, 5);
		t.setTileColour(QuaxTileColour.NONE);
		assertFalse(t.isWhite());
	}

	@Test
	void testRhombusIsWhite4() {
		QuaxTile t = new Rhombus(5, 5);
		assertFalse(t.isWhite());
	}



	@Test
	void testRhombusUnassignedGroup() {
		QuaxTile t = new Rhombus(5, 5);

		IllegalStateException exception = assertThrows(IllegalStateException.class,
				() -> { t.getTileGroup(); } );

		String expectedMessage = "TileGroup not initialised for tile.";
		String actualMessage = exception.getMessage();

		assertEquals(0, actualMessage.compareTo(expectedMessage));
	}
	
	@Test
	void testRhombusStrategyValue() {
		QuaxTile t = new Rhombus(3, 2);
		t.setStrategyValue(OPPONENT_WINNING);
		assertEquals(fromInt(6), t.getStrategyValue());
	}
}
