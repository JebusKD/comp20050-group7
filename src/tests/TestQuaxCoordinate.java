package tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.QuaxBoard;
import types.QuaxTile;

class TestQuaxCoordinate {
	
	private QuaxBoard coordTestBoard;

	@BeforeEach
	void setUp() throws Exception {
		coordTestBoard = new QuaxBoard();
	}

	@Test
	void testIsOctagon() {
		QuaxTile t = coordTestBoard.getOctagon(5, 1);
		assertTrue(t.getCoordinates().isOctagon());
	}
	
	@Test
	void testIsRhombus() {
		QuaxTile t = coordTestBoard.getRhombus(6, 4);
		assertTrue(t.getCoordinates().isRhombus());
	}


	@Test
	void testX() {
		QuaxTile t = coordTestBoard.getOctagon(3,  6);
		assertEquals(3, t.getCoordinates().x());
	}
	
	@Test
	void testY() {
		QuaxTile t = coordTestBoard.getOctagon(3,  6);
		assertEquals(6, t.getCoordinates().y());
	}
}
