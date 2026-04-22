package tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import model.QuaxBoard;
import types.Octagon;
import types.Rhombus;
import types.QuaxCoordinate;
import types.QuaxTileColour;

class TestBoardCopying {

	@Test
	void testOctagonCopying1() {
		Octagon o = new Octagon(3, 2);
		
		Octagon c = new Octagon(o);
		assertEquals(3, c.getCoordinates().x());
		
	}
	
	@Test
	void testOctagonCopying2() {
		Octagon o = new Octagon(3, 2);
		
		Octagon c = new Octagon(o);
		assertEquals(2, c.getCoordinates().y());
		
	}
	
	@Test
	void testOctagonCopying3() {
		Octagon o = new Octagon(3, 2);
		o.setTileColour(QuaxTileColour.WHITE);
		
		Octagon c = new Octagon(o);
		assertEquals(QuaxTileColour.WHITE, c.getTileColour());
	}
	
	@Test
	void testOctagonCopying4() {
		
		QuaxBoard b = new QuaxBoard();
		
		b.makeMove(new QuaxCoordinate(5, 4, true), QuaxTileColour.BLACK);
		
		Octagon c = new Octagon(b.getOctagon(5, 4));
		assertNull(c.getGroup());
	}
	
	@Test
	void testRhombusCopying1() {
		Rhombus o = new Rhombus(3, 2);
		
		Rhombus c = new Rhombus(o);
		assertEquals(3, c.getCoordinates().x());
		
	}
	
	@Test
	void testRhombusCopying2() {
		Rhombus o = new Rhombus(3, 2);
		
		Rhombus c = new Rhombus(o);
		assertEquals(2, c.getCoordinates().y());
		
	}
	
	@Test
	void testRhombusCopying3() {
		Rhombus o = new Rhombus(3, 2);
		o.setTileColour(QuaxTileColour.WHITE);
		
		Rhombus c = new Rhombus(o);
		assertEquals(QuaxTileColour.WHITE, c.getTileColour());
	}
	
	@Test
	void testRhombusCopying4() {
		
		QuaxBoard b = new QuaxBoard();
		
		// Add octagons to permit the rhombus move
		b.makeMove(new QuaxCoordinate(5, 4, true), QuaxTileColour.BLACK);
		b.makeMove(new QuaxCoordinate(6, 5, true), QuaxTileColour.BLACK);
		
		b.makeMove(new QuaxCoordinate(5, 4, false), QuaxTileColour.BLACK);
		
		Rhombus c = new Rhombus(b.getRhombus(5, 4));
		assertNull(c.getGroup());
	}

}
