package tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import types.Octagon;
import types.QuaxCoordinate;
import types.QuaxTile;
import types.QuaxTileColour;

class TestOctagon {

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

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
	void testOnLow1() {
		QuaxTile t = new Octagon(3, 10);
		t.setColour(QuaxTileColour.BLACK);
		assertTrue(t.onLow());
	}
	
	@Test
	void testOnLow2() {
		QuaxTile t = new Octagon(3, 10);
		t.setColour(QuaxTileColour.WHITE);
		assertFalse(t.onLow());
	}
	
	@Test
	void testOnLow3() {
		QuaxTile t = new Octagon(3, 10);
		t.setColour(QuaxTileColour.NONE);
		assertFalse(t.onLow());
	}
	
	@Test
	void testOnLow4() {
		QuaxTile t = new Octagon(0, 4);
		t.setColour(QuaxTileColour.BLACK);
		assertFalse(t.onLow());
	}
	
	@Test
	void testOnLow5() {
		QuaxTile t = new Octagon(0, 4);
		t.setColour(QuaxTileColour.WHITE);
		assertTrue(t.onLow());
	}
	
	@Test
	void testOnLow6() {
		QuaxTile t = new Octagon(0, 4);
		t.setColour(QuaxTileColour.NONE);
		assertFalse(t.onLow());
	}
	
	@Test
	void testOnLow7() {
		QuaxTile t = new Octagon(4, 6);
		t.setColour(QuaxTileColour.BLACK);
		assertFalse(t.onLow());
	}
	
	@Test
	void testOnLow8() {
		QuaxTile t = new Octagon(4, 6);
		t.setColour(QuaxTileColour.WHITE);
		assertFalse(t.onLow());
	}
	
	@Test
	void testOnLow9() {
		QuaxTile t = new Octagon(4, 6);
		t.setColour(QuaxTileColour.NONE);
		assertFalse(t.onLow());
	}
	
	@Test
	void testOnHigh1() {
		QuaxTile t = new Octagon(3, 0);
		t.setColour(QuaxTileColour.BLACK);
		assertTrue(t.onHigh());
	}
	
	@Test
	void testOnHigh2() {
		QuaxTile t = new Octagon(3, 0);
		t.setColour(QuaxTileColour.WHITE);
		assertFalse(t.onHigh());
	}
	
	@Test
	void testOnHigh3() {
		QuaxTile t = new Octagon(3, 0);
		t.setColour(QuaxTileColour.NONE);
		assertFalse(t.onHigh());
	}
	
	@Test
	void testOnHigh4() {
		QuaxTile t = new Octagon(10, 4);
		t.setColour(QuaxTileColour.BLACK);
		assertFalse(t.onHigh());
	}
	
	@Test
	void testOnHigh5() {
		QuaxTile t = new Octagon(10, 4);
		t.setColour(QuaxTileColour.WHITE);
		assertTrue(t.onHigh());
	}
	
	@Test
	void testOnHigh6() {
		QuaxTile t = new Octagon(10, 4);
		t.setColour(QuaxTileColour.NONE);
		assertFalse(t.onHigh());
	}
	
	@Test
	void testOnHigh7() {
		QuaxTile t = new Octagon(4, 6);
		t.setColour(QuaxTileColour.BLACK);
		assertFalse(t.onHigh());
	}
	
	@Test
	void testOnHigh8() {
		QuaxTile t = new Octagon(4, 6);
		t.setColour(QuaxTileColour.WHITE);
		assertFalse(t.onHigh());
	}
	
	@Test
	void testOnHigh9() {
		QuaxTile t = new Octagon(4, 6);
		t.setColour(QuaxTileColour.NONE);
		assertFalse(t.onHigh());
	}
}
