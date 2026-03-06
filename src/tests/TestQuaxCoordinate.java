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

	@BeforeEach
	void setUp() throws Exception {
		b = new QuaxBoard();
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
	
	@Test
	void testX() {
		QuaxTile t = b.getOctagon(3,  6);
		assertEquals(3, t.getCoordinates().x());
	}
	
	@Test
	void testY() {
		QuaxTile t = b.getOctagon(3,  6);
		assertEquals(6, t.getCoordinates().y());
	}

}
