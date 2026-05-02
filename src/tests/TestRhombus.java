package tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import types.Rhombus;
import types.QuaxCoordinate;
import types.QuaxTile;
import types.QuaxTileColour;

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
	void testRhombusUnassignedGroup() {
		QuaxTile t = new Rhombus(5, 5);
		assertNull(t.getTileGroup());
	}
	
	@Test
	void testRhombusStrategyValue() {
		QuaxTile t = new Rhombus(3, 2);
		t.setStrategyValue(6);
		assertEquals(6, t.getStrategyValue());
	}
}
