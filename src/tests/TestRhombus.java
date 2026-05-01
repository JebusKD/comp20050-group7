package tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import types.Rhombus;
import types.QuaxCoordinate;
import types.QuaxTile;
import types.QuaxTileColour;

class TestRhombus {

	@Test
	void testOctagonCoordinates1() {
		QuaxTile t = new Rhombus(5, 2);
		QuaxCoordinate q = t.getCoordinates();
		assertEquals(5, q.x());
	}
	
	@Test
	void testOctagonCoordinates2() {
		QuaxTile t = new Rhombus(1, 0);
		QuaxCoordinate q = t.getCoordinates();
		assertEquals(1, q.x());
	}
	
	@Test
	void testOctagonCoordinates3() {
		QuaxTile t = new Rhombus(8, 7);
		QuaxCoordinate q = t.getCoordinates();
		assertEquals(8, q.x());
	}
	
	@Test
	void testOctagonCoordinates4() {
		QuaxTile t = new Rhombus(0, 7);
		QuaxCoordinate q = t.getCoordinates();
		assertEquals(0, q.x());
	}
	
	@Test
	void testOctagonCoordinates5() {
		QuaxTile t = new Rhombus(5, 2);
		QuaxCoordinate q = t.getCoordinates();
		assertEquals(2, q.y());
	}
	
	@Test
	void testOctagonCoordinates6() {
		QuaxTile t = new Rhombus(1, 0);
		QuaxCoordinate q = t.getCoordinates();
		assertEquals(0, q.y());
	}
	
	@Test
	void testOctagonCoordinates7() {
		QuaxTile t = new Rhombus(8, 7);
		QuaxCoordinate q = t.getCoordinates();
		assertEquals(7, q.y());
	}
	
	@Test
	void testOctagonCoordinates8() {
		QuaxTile t = new Rhombus(0, 7);
		QuaxCoordinate q = t.getCoordinates();
		assertEquals(7, q.y());
	}


	@Test
	void testIsFree1() {
		QuaxTile t = new Rhombus(5, 5);
		t.setTileColour(QuaxTileColour.BLACK);
		assertFalse(t.isFree());
	}
	
	@Test
	void testIsFree2() {
		QuaxTile t = new Rhombus(5, 5);
		t.setTileColour(QuaxTileColour.WHITE);
		assertFalse(t.isFree());
	}
	
	@Test
	void testIsFree3() {
		QuaxTile t = new Rhombus(5, 5);
		t.setTileColour(QuaxTileColour.NONE);
		assertTrue(t.isFree());
	}
	
	@Test
	void testIsFree4() {
		QuaxTile t = new Rhombus(5, 5);
		assertTrue(t.isFree());
	}
	
	@Test
	void testUnassignedGroup() {
		QuaxTile t = new Rhombus(5, 5);
		assertNull(t.getTileGroup());
	}
	
	@Test
	void testStrategyValue() {
		QuaxTile t = new Rhombus(3, 2);
		t.setStrategyValue(6);
		assertEquals(6, t.getStrategyValue());
	}
}
