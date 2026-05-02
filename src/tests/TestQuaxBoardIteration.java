package tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.QuaxBoard;
import static types.QuaxCoordinate.*;
import types.QuaxTile;

public class TestQuaxBoardIteration {

	private QuaxBoard iteratorTestBoard;

	@BeforeEach
	void setUp() throws Exception {
		iteratorTestBoard = new QuaxBoard();
	}


	@Test
	void testIterationSize() {
		int count = 0;
		for (QuaxTile t : iteratorTestBoard) {
			count++;
		}
		assertEquals(221, count);
	}
	
	@Test
	void testIterationOctagonCount() {
		int count = 0;
		for (QuaxTile t : iteratorTestBoard) {
			if (t.getCoordinates().isOctagon()) {
				count++;
			}
		}
		assertEquals(121, count);
	}
	
	@Test
	void testIterationRhombusCount() {
		int count = 0;
		for (QuaxTile t : iteratorTestBoard) {
			if (t.getCoordinates().isRhombus()) {
				count++;
			}
		}
		assertEquals(100, count);
	}


	@Test
	void testIterateStrategyValues() {
		for (QuaxTile t : iteratorTestBoard) {
			t.setStrategyValue(21);
		}
		assertEquals(21, iteratorTestBoard.getTile(newOctagonCoordinate(1, 9)).getStrategyValue());
	}
}
