package tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.QuaxBoard;
import types.QuaxTile;

class TestQuaxCoordinate {
	
	private QuaxBoard b;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
		b = new QuaxBoard();
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testIsOctagonMove() {
		QuaxTile t = b.getOctagon(5, 1);
		assertTrue(t.getCoordinates().isOctagonMove());
	}
	
	@Test
	void testIsRhombusMove() {
		QuaxTile t = b.getRhombus(6, 4);
		assertTrue(t.getCoordinates().isRhombusMove());
	}

}
