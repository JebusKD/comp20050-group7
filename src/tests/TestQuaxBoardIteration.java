package tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.QuaxBoard;
import types.QuaxCoordinate;
import types.QuaxTile;

public class TestQuaxBoardIteration {

	private QuaxBoard b;

	@BeforeEach
	void setUp() throws Exception {
		b = new QuaxBoard();
	}
	
	@Test
	void testIterationSize() {
		int count = 0;
		for (QuaxTile t : b) {
			count++;
		}
		assertEquals(221, count);
	}
	
	@Test
	void testIterationOctagonCount() {
		int count = 0;
		for (QuaxTile t : b) {
			if (t.getCoordinates().isOctagon()) {
				count++;
			}
		}
		assertEquals(121, count);
	}
	
	@Test
	void testIterationRhombusCount() {
		int count = 0;
		for (QuaxTile t : b) {
			if (t.getCoordinates().isRhombus()) {
				count++;
			}
		}
		assertEquals(100, count);
	}
	
	@Test
	void testIterateStrategyValues() {
		int count = 0;
		for (QuaxTile t : b) {
			t.setStrategyValue(21);
		}
		assertEquals(21, b.getTile(new QuaxCoordinate(1, 9, true)).getStrategyValue());
	}
}
