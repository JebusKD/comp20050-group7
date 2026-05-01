package tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import types.Octagon;
import types.QuaxCoordinate;
import types.QuaxTile;
import types.QuaxTileColour;

class TestOctagon {

	@Test
	void testOctagonCoordinates1() {
		QuaxTile t = new Octagon(5, 2);
		QuaxCoordinate q = t.getCoordinates();
		assertEquals(5, q.x());
	}
	
	@Test
	void testOctagonCoordinates2() {
		QuaxTile t = new Octagon(1, 0);
		QuaxCoordinate q = t.getCoordinates();
		assertEquals(1, q.x());
	}
	
	@Test
	void testOctagonCoordinates3() {
		QuaxTile t = new Octagon(9, 10);
		QuaxCoordinate q = t.getCoordinates();
		assertEquals(9, q.x());
	}
	
	@Test
	void testOctagonCoordinates4() {
		QuaxTile t = new Octagon(0, 7);
		QuaxCoordinate q = t.getCoordinates();
		assertEquals(0, q.x());
	}
	
	@Test
	void testOctagonCoordinates5() {
		QuaxTile t = new Octagon(5, 2);
		QuaxCoordinate q = t.getCoordinates();
		assertEquals(2, q.y());
	}
	
	@Test
	void testOctagonCoordinates6() {
		QuaxTile t = new Octagon(1, 0);
		QuaxCoordinate q = t.getCoordinates();
		assertEquals(0, q.y());
	}
	
	@Test
	void testOctagonCoordinates7() {
		QuaxTile t = new Octagon(9, 10);
		QuaxCoordinate q = t.getCoordinates();
		assertEquals(10, q.y());
	}
	
	@Test
	void testOctagonCoordinates8() {
		QuaxTile t = new Octagon(0, 7);
		QuaxCoordinate q = t.getCoordinates();
		assertEquals(7, q.y());
	}


	@Test
	void testDistanceToLowWall1() {
		Octagon t = new Octagon(3, 10);
		t.setTileColour(QuaxTileColour.BLACK);
		assertEquals(10, t.distanceToLowWall());
	}

	@Test
	void testDistanceToLowWall2() {
		Octagon t = new Octagon(6, 2);
		t.setTileColour(QuaxTileColour.BLACK);
		assertEquals(2, t.distanceToLowWall());
	}

	@Test
	void testDistanceToLowWall3() {
		Octagon t = new Octagon(3, 10);
		t.setTileColour(QuaxTileColour.WHITE);
		assertEquals(3, t.distanceToLowWall());
	}

	@Test
	void testDistanceToLowWall4() {
		Octagon t = new Octagon(6, 2);
		t.setTileColour(QuaxTileColour.WHITE);
		assertEquals(6, t.distanceToLowWall());
	}

	@Test
	void testDistanceToHighWall1() {
		Octagon t = new Octagon(3, 10);
		t.setTileColour(QuaxTileColour.BLACK);
		assertEquals(0, t.distanceToHighWall());
	}

	@Test
	void testDistanceToHighWall2() {
		Octagon t = new Octagon(6, 2);
		t.setTileColour(QuaxTileColour.BLACK);
		assertEquals(8, t.distanceToHighWall());
	}

	@Test
	void testDistanceToHighWall3() {
		Octagon t = new Octagon(3, 10);
		t.setTileColour(QuaxTileColour.WHITE);
		assertEquals(7, t.distanceToHighWall());
	}

	@Test
	void testDistanceToHighWall4() {
		Octagon t = new Octagon(6, 2);
		t.setTileColour(QuaxTileColour.WHITE);
		assertEquals(4, t.distanceToHighWall());
	}


	@Test
	void testIsFree1() {
		QuaxTile t = new Octagon(5, 5);
		t.setTileColour(QuaxTileColour.BLACK);
		assertFalse(t.isFree());
	}
	
	@Test
	void testIsFree2() {
		QuaxTile t = new Octagon(5, 5);
		t.setTileColour(QuaxTileColour.WHITE);
		assertFalse(t.isFree());
	}
	
	@Test
	void testIsFree3() {
		QuaxTile t = new Octagon(5, 5);
		t.setTileColour(QuaxTileColour.NONE);
		assertTrue(t.isFree());
	}
	
	@Test
	void testIsFree4() {
		QuaxTile t = new Octagon(5, 5);
		assertTrue(t.isFree());
	}

	@Test
	void testIsBlack1() {
		QuaxTile t = new Octagon(5, 5);
		t.setTileColour(QuaxTileColour.BLACK);
		assertTrue(t.isBlack());
	}

	@Test
	void testIsBlack2() {
		QuaxTile t = new Octagon(5, 5);
		t.setTileColour(QuaxTileColour.WHITE);
		assertFalse(t.isBlack());
	}

	@Test
	void testIsBlack3() {
		QuaxTile t = new Octagon(5, 5);
		t.setTileColour(QuaxTileColour.NONE);
		assertFalse(t.isBlack());
	}

	@Test
	void testIsBlack4() {
		QuaxTile t = new Octagon(5, 5);
		assertFalse(t.isBlack());
	}

	@Test
	void testIsWhite1() {
		QuaxTile t = new Octagon(5, 5);
		t.setTileColour(QuaxTileColour.BLACK);
		assertFalse(t.isWhite());
	}

	@Test
	void testIsWhite2() {
		QuaxTile t = new Octagon(5, 5);
		t.setTileColour(QuaxTileColour.WHITE);
		assertTrue(t.isWhite());
	}

	@Test
	void testIsWhite3() {
		QuaxTile t = new Octagon(5, 5);
		t.setTileColour(QuaxTileColour.NONE);
		assertFalse(t.isWhite());
	}


	@Test
	void testIsWhite4() {
		QuaxTile t = new Octagon(5, 5);
		assertFalse(t.isWhite());
	}

	
	@Test
	void testUnassignedGroup() {
		QuaxTile t = new Octagon(5, 5);
		assertNull(t.getTileGroup());
	}
	
	@Test
	void testStrategyValue() {
		QuaxTile t = new Octagon(3, 2);
		t.setStrategyValue(5);
		assertEquals(5, t.getStrategyValue());
	}
}
