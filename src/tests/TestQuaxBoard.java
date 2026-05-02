package tests;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.QuaxBoard;
import static types.QuaxCoordinate.*;
import types.*;


class TestQuaxBoard {

	private QuaxBoard testQuaxBoard;

	@BeforeEach
	void setUp() throws Exception {
		testQuaxBoard = new QuaxBoard();
	}
	
	@Test
	void testMakeMove1() {
		testQuaxBoard.makeMove(newOctagonCoordinate(4, 7), QuaxTileColour.BLACK);
		
		assertEquals(QuaxTileColour.BLACK, testQuaxBoard.getTile(newOctagonCoordinate(4, 7)).getTileColour());
	}
	
	@Test
	void testMakeMove2() {
		testQuaxBoard.makeMove(newOctagonCoordinate(2, 3), QuaxTileColour.WHITE);
		
		assertEquals(QuaxTileColour.WHITE, testQuaxBoard.getTile(newOctagonCoordinate(2, 3)).getTileColour());
	}


	@Test
	void testOccupiedTile1() {
		testQuaxBoard.makeMove(newOctagonCoordinate(2, 3), QuaxTileColour.WHITE);
		
		assertFalse(testQuaxBoard.validMove(newOctagonCoordinate(2, 3), QuaxTileColour.BLACK));
	}
	
	@Test
	void testOccupiedTile2() {
		testQuaxBoard.makeMove(newOctagonCoordinate(2,3), QuaxTileColour.WHITE);
		
		assertFalse(testQuaxBoard.validMove(newOctagonCoordinate(2, 3), QuaxTileColour.WHITE));
	}
	
	@Test
	void testOccupiedTile3() {
		testQuaxBoard.makeMove(newOctagonCoordinate(2, 3), QuaxTileColour.WHITE);
		testQuaxBoard.makeMove(newOctagonCoordinate(3, 4), QuaxTileColour.WHITE);
		testQuaxBoard.makeMove(newRhombusCoordinate(2, 3), QuaxTileColour.WHITE);
		
		assertFalse(testQuaxBoard.validMove(newRhombusCoordinate(2, 3), QuaxTileColour.BLACK));
	}
	
	@Test
	void testOccupiedTile4() {
		testQuaxBoard.makeMove(newOctagonCoordinate(2, 3), QuaxTileColour.BLACK);
		
		assertFalse(testQuaxBoard.validMove(newOctagonCoordinate(2, 3), QuaxTileColour.BLACK));
	}
	
	@Test
	void testOccupiedTile5() {
		testQuaxBoard.makeMove(newOctagonCoordinate(2, 3), QuaxTileColour.BLACK);
		
		assertFalse(testQuaxBoard.validMove(newOctagonCoordinate(2, 3), QuaxTileColour.WHITE));
	}
	
	@Test
	void testOccupiedTile6() {
		testQuaxBoard.makeMove(newOctagonCoordinate(2, 3), QuaxTileColour.BLACK);
		testQuaxBoard.makeMove(newOctagonCoordinate(3, 4), QuaxTileColour.BLACK);
		testQuaxBoard.makeMove(newRhombusCoordinate(2, 3), QuaxTileColour.BLACK);
		
		assertFalse(testQuaxBoard.validMove(newRhombusCoordinate(2, 3), QuaxTileColour.BLACK));
	}


	@Test
	void testWinningBoard1() {
		for (int i = 0; i < 11; i++) {
			testQuaxBoard.makeMove(newOctagonCoordinate(i, 3), QuaxTileColour.WHITE);
		}
		assertTrue(testQuaxBoard.checkForWinningMove());
	}
	
	@Test
	void testWinningBoard2() {
		for (int i = 0; i < 11; i++) {
			testQuaxBoard.makeMove(newOctagonCoordinate(6, i), QuaxTileColour.BLACK);
		}
		assertTrue(testQuaxBoard.checkForWinningMove());
	}
	
	// Not going the full way should result in no win
	@Test
	void testWinningBoard3() {
		for (int i = 0; i < 10; i++) {
			testQuaxBoard.makeMove(newOctagonCoordinate(i, 3), QuaxTileColour.WHITE);
		}
		assertFalse(testQuaxBoard.checkForWinningMove());
	}
	
	@Test
	void testWinningBoard4() {
		for (int i = 0; i < 10; i++) {
			testQuaxBoard.makeMove(newOctagonCoordinate(6, i), QuaxTileColour.BLACK);
		}
		assertFalse(testQuaxBoard.checkForWinningMove());
	}


	// Feature that prevents moves after the game is won.
	@Test
	void testWinningBoardInvalidMove() {
		for (int i = 0; i < 11; i++) {
			testQuaxBoard.makeMove(newOctagonCoordinate(6, i), QuaxTileColour.BLACK);
		}
		assertFalse(testQuaxBoard.validMove(newOctagonCoordinate(8, 8), QuaxTileColour.WHITE));
	}


	// Checks if a correctly-sized array is created for octagon's neighbours. TODO - 1 assert/test?
	@Test
	void testNeighboursArraySize1() {
		QuaxTile[][] neighbours = testQuaxBoard.getNeighbours(newOctagonCoordinate(5, 5));
		assertEquals(3, neighbours.length);
		assertEquals(3, neighbours[0].length);
		assertEquals(3, neighbours[1].length);
		assertEquals(3, neighbours[2].length);
	}
	
	// Checks if a correctly-sized array is created for rhombus's neighbours.
	@Test
	void testNeighboursArraySize2() {
		QuaxTile[][] neighbours = testQuaxBoard.getNeighbours(newRhombusCoordinate(4,3));
		assertEquals(2, neighbours.length);
		assertEquals(2, neighbours[0].length);
		assertEquals(2, neighbours[1].length);
	}


	// Checks if the colour of neighbouring tiles are correctly read.
	@Test
	void testNeighboursColours1() {
		QuaxTile[][] neighbours = testQuaxBoard.getNeighbours(newOctagonCoordinate(3, 4));
		
		testQuaxBoard.makeMove(newOctagonCoordinate(3, 3), QuaxTileColour.BLACK);
		
		assertEquals(QuaxTileColour.BLACK, neighbours[1][0].getTileColour());
	}
	
	// Checks if the colour of neighbouring tiles are correctly read.
	@Test
	void testNeighboursColours2() {
		QuaxTile[][] neighbours = testQuaxBoard.getNeighbours(newRhombusCoordinate(3, 4));
		
		testQuaxBoard.makeMove(newOctagonCoordinate(4, 5), QuaxTileColour.WHITE);
		
		assertEquals(QuaxTileColour.WHITE, neighbours[1][1].getTileColour());
	}


	// Ensures the correct entries in neighbours are indeed out of bounds/in bounds (oob)
	@Test
	void testNeighboursStructure1() {
		QuaxTile[][] neighbours = testQuaxBoard.getNeighbours(newOctagonCoordinate(0, 0));
		// Octagon in top-left corner
		/*
		  		oob  | oob  | null
		  		-----+------+-----
		  		oob  | hide | tile
		  		-----+------+-----
		  		oob  | oob  | tile
		 */
		
		assertEquals(QuaxTile.OUT_OF_BOUNDS_TILE, neighbours[0][0]);
		assertEquals(QuaxTile.OUT_OF_BOUNDS_TILE, neighbours[0][1]);
		assertEquals(QuaxTile.OUT_OF_BOUNDS_TILE, neighbours[0][2]);
		assertEquals(QuaxTile.OUT_OF_BOUNDS_TILE, neighbours[1][0]);
		assertEquals(QuaxTile.HIDDEN_TILE, neighbours[1][1]);

		assertNotEquals(QuaxTile.OUT_OF_BOUNDS_TILE, neighbours[1][2]);
		assertEquals(QuaxTile.OUT_OF_BOUNDS_TILE, neighbours[2][0]);
		assertNotEquals(QuaxTile.OUT_OF_BOUNDS_TILE, neighbours[2][1]);
		assertNotEquals(QuaxTile.OUT_OF_BOUNDS_TILE, neighbours[2][2]);
		
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
		QuaxTile[][] neighbours = testQuaxBoard.getNeighbours(newRhombusCoordinate(0, 0));
		
		assertNotEquals(QuaxTile.OUT_OF_BOUNDS_TILE, neighbours[0][0]);
		assertNotEquals(QuaxTile.OUT_OF_BOUNDS_TILE, neighbours[0][1]);
		assertNotEquals(QuaxTile.OUT_OF_BOUNDS_TILE, neighbours[1][0]);
		assertNotEquals(QuaxTile.OUT_OF_BOUNDS_TILE, neighbours[1][1]);
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
		QuaxTile[][] neighbours = testQuaxBoard.getNeighbours(newOctagonCoordinate(3, 4));
		
		assertEquals(3, neighbours[1][2].getCoordinates().x());
		assertEquals(5, neighbours[1][2].getCoordinates().y());
	}
}
