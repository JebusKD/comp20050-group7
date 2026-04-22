package tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.QuaxBoard;
import types.QuaxCoordinate;
import types.QuaxTile;
import types.QuaxTileColour;

class TestQuaxBoard {

	private QuaxBoard board;

	@BeforeEach
	void setUp() throws Exception {
		board = new QuaxBoard();
	}
	
	@Test
	void testMakeMove1() {
		board.makeMove(new QuaxCoordinate(4, 7, true), QuaxTileColour.BLACK);
		
		assertEquals(board.getTile(new QuaxCoordinate(4, 7, true)).getColour(), QuaxTileColour.BLACK);
	}
	
	@Test
	void testMakeMove2() {
		board.makeMove(new QuaxCoordinate(2, 3, true), QuaxTileColour.WHITE);
		
		assertEquals(board.getTile(new QuaxCoordinate(2, 3, true)).getColour(), QuaxTileColour.WHITE);
	}
	
	@Test
	void testOccupiedTile1() {
		board.makeMove(new QuaxCoordinate(2, 3, true), QuaxTileColour.WHITE);
		
		assertFalse(board.validMove(new QuaxCoordinate(2, 3, true), QuaxTileColour.WHITE));
	}
	
	@Test
	void testOccupiedTile2() {
		board.makeMove(new QuaxCoordinate(2, 3, true), QuaxTileColour.WHITE);
		
		assertFalse(board.validMove(new QuaxCoordinate(2, 3, true), QuaxTileColour.BLACK));
	}
	
	@Test
	void testOccupiedTile3() {
		board.makeMove(new QuaxCoordinate(2, 3, true), QuaxTileColour.WHITE);
		board.makeMove(new QuaxCoordinate(3, 4, true), QuaxTileColour.WHITE);
		board.makeMove(new QuaxCoordinate(2, 3, false), QuaxTileColour.WHITE);
		
		assertFalse(board.validMove(new QuaxCoordinate(2, 3, false), QuaxTileColour.WHITE));
	}
	
	@Test
	void testOccupiedTile4() {
		board.makeMove(new QuaxCoordinate(2, 3, true), QuaxTileColour.BLACK);
		
		assertFalse(board.validMove(new QuaxCoordinate(2, 3, true), QuaxTileColour.BLACK));
	}
	
	@Test
	void testOccupiedTile5() {
		board.makeMove(new QuaxCoordinate(2, 3, true), QuaxTileColour.BLACK);
		
		assertFalse(board.validMove(new QuaxCoordinate(2, 3, true), QuaxTileColour.WHITE));
	}
	
	@Test
	void testOccupiedTile6() {
		board.makeMove(new QuaxCoordinate(2, 3, true), QuaxTileColour.BLACK);
		board.makeMove(new QuaxCoordinate(3, 4, true), QuaxTileColour.BLACK);
		board.makeMove(new QuaxCoordinate(2, 3, false), QuaxTileColour.BLACK);
		
		assertFalse(board.validMove(new QuaxCoordinate(2, 3, false), QuaxTileColour.BLACK));
	}

	@Test
	void testWinningBoard1() {
		for (int i = 0; i < 11; i++) {
			board.makeMove(new QuaxCoordinate(i, 3, true), QuaxTileColour.WHITE);
		}
		assertTrue(board.checkForWinningMove());
	}
	
	@Test
	void testWinningBoard2() {
		for (int i = 0; i < 11; i++) {
			board.makeMove(new QuaxCoordinate(6, i, true), QuaxTileColour.BLACK);
		}
		assertTrue(board.checkForWinningMove());
	}
	
	// Not going the full way should result in no win
	@Test
	void testWinningBoard3() {
		for (int i = 0; i < 10; i++) {
			board.makeMove(new QuaxCoordinate(i, 3, true), QuaxTileColour.WHITE);
		}
		assertFalse(board.checkForWinningMove());
	}
	
	@Test
	void testWinningBoard4() {
		for (int i = 0; i < 10; i++) {
			board.makeMove(new QuaxCoordinate(6, i, true), QuaxTileColour.BLACK);
		}
		assertFalse(board.checkForWinningMove());
	}
	
	// Feature that prevents moves after the game is won.
	@Test
	void testWinningBoardInvalidMove() {
		for (int i = 0; i < 11; i++) {
			board.makeMove(new QuaxCoordinate(6, i, true), QuaxTileColour.BLACK);
		}
		assertFalse(board.validMove(new QuaxCoordinate(8, 8, true), QuaxTileColour.BLACK));
	}
	
	// Checks if a correctly-sized array is created for octagon's neighbours.
	@Test
	void testNeighboursArraySize1() {
		QuaxTile[][] neighbours = board.getNeighbours(new QuaxCoordinate(5, 5, true));
		assertEquals(3, neighbours.length);
		assertEquals(3, neighbours[0].length);
		assertEquals(3, neighbours[1].length);
		assertEquals(3, neighbours[2].length);
	}
	
	// Checks if a correctly-sized array is created for rhombus's neighbours.
	@Test
	void testNeighboursArraySize2() {
		QuaxTile[][] neighbours = board.getNeighbours(new QuaxCoordinate(4, 3, false));
		assertEquals(2, neighbours.length);
		assertEquals(2, neighbours[0].length);
		assertEquals(2, neighbours[1].length);
	}

	// Checks if the colour of neighbouring tiles are correctly read.
	@Test
	void testNeighboursColours1() {
		QuaxTile[][] neighbours = board.getNeighbours(new QuaxCoordinate(3, 4, true));
		
		board.makeMove(new QuaxCoordinate(3, 3, true), QuaxTileColour.BLACK);
		
		assertEquals(QuaxTileColour.BLACK, neighbours[1][0].getColour());
	}
	
	// Checks if the colour of neighbouring tiles are correctly read.
	@Test
	void testNeighboursColours2() {
		QuaxTile[][] neighbours = board.getNeighbours(new QuaxCoordinate(3, 4, false));
		
		board.makeMove(new QuaxCoordinate(4, 5, true), QuaxTileColour.WHITE);
		
		assertEquals(QuaxTileColour.WHITE, neighbours[1][1].getColour());
	}
	
	// Ensures the correct entries in neighbours are indeed null/not null
	@Test
	void testNeighboursStructure1() {
		QuaxTile[][] neighbours = board.getNeighbours(new QuaxCoordinate(0, 0, true));
		// Octagon in top-left corner
		/*
		  		null | null | null
		  		-----+------+-----
		  		null | null | tile
		  		-----+------+-----
		  		null | tile | tile
		 */
		
		assertNull(neighbours[0][0]);
		assertNull(neighbours[0][1]);
		assertNull(neighbours[0][2]);
		assertNull(neighbours[1][0]);
		assertNull(neighbours[1][1]);
		assertNotNull(neighbours[1][2]);
		assertNull(neighbours[2][0]);
		assertNotNull(neighbours[2][1]);
		assertNotNull(neighbours[2][2]);
		
	}
	
	// Ensures the correct entries in neighbours are indeed null/not null
	// Rhombus against top-left (A rhombus should see four adjacent octagons always)
	/*
	  		tile | tile
	  		-----+-----
	  		tile | tile
	 */
	@Test
	void testNeighboursStructure2() {
		QuaxTile[][] neighbours = board.getNeighbours(new QuaxCoordinate(0, 0, false));
		
		assertNotNull(neighbours[0][0]);
		assertNotNull(neighbours[0][1]);
		assertNotNull(neighbours[1][0]);
		assertNotNull(neighbours[1][1]);
		
	}
	
	// Tests that the coordinates of neighbours match up with how
	// they'd be expected to be viewed on the board.
	/*
			r3,3 | o3,3 | r4,3
			-----+------+-----
			o2,4 | o3,4 | o4,4
			-----+------+-----
			r3,4 | o3,5 | r4,4
	 */
	@Test
	void testNeighboursCoordinates1() {
		QuaxTile[][] neighbours = board.getNeighbours(new QuaxCoordinate(3, 4, true));
		
		assertEquals(3, neighbours[1][2].getCoordinates().x());
		assertEquals(5, neighbours[1][2].getCoordinates().y());
	}
}
