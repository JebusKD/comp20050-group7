package tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import types.Octagon;
import types.QuaxTile;
import types.QuaxTileColour;
import types.QuaxTileGroup;
import types.Rhombus;

class TestQuaxTileGroup {

	@Test
	void testIsTileMember1() {
		QuaxTileGroup g = new QuaxTileGroup();
		QuaxTile t = new Octagon(3, 6);
		g.addTile(t);
		assertTrue(g == t.getGroup());
	}
	
	@Test
	void testIsTileMember2() {
		QuaxTileGroup g = new QuaxTileGroup();
		QuaxTile t = new Rhombus(2, 0);
		g.addTile(t);
		assertTrue(g == t.getGroup());
	}
	
	@Test
	void testIsTileMember3() {
		QuaxTile t = new Octagon(3, 6);
		QuaxTileGroup g = new QuaxTileGroup(t);
		assertTrue(g == t.getGroup());
	}
	
	@Test
	void testIsTileMember4() {
		QuaxTile t = new Rhombus(2, 0);
		QuaxTileGroup g = new QuaxTileGroup(t);
		assertTrue(g == t.getGroup());
	}
	
	@Test
	void testSize1() {
		QuaxTileGroup g = new QuaxTileGroup();
		assertEquals(0, g.size());
	}
	
	@Test
	void testSize2() {
		QuaxTileGroup g = new QuaxTileGroup();
		g.addTile(new Octagon(3, 2));
		assertEquals(1, g.size());
	}
	
	@Test
	void testSize3() {
		QuaxTileGroup g = new QuaxTileGroup(new Rhombus(0, 0));
		assertEquals(1, g.size());
	}
	
	@Test
	void testSize4() {
		QuaxTileGroup g = new QuaxTileGroup();
		g.addTile(new Octagon(3, 2));
		g.addTile(new Octagon(4, 2));
		g.addTile(new Rhombus(5, 2));
		g.addTile(new Octagon(8, 9));
		g.addTile(new Rhombus(4, 2));
		assertEquals(5, g.size());
	}

	@Test
	void testIsWinningGroup1() {
		QuaxTileGroup g = new QuaxTileGroup();
		
		for (int i = 0; i < 11; i++) {
			QuaxTile t = new Octagon(5, i);
			t.setColour(QuaxTileColour.BLACK);
			g.addTile(t);
		}
		
		assertTrue(g.isWinningGroup());
	}
	
	@Test
	void testIsWinningGroup2() {
		QuaxTileGroup g = new QuaxTileGroup();
		
		for (int i = 0; i < 11; i++) {
			QuaxTile t = new Octagon(5, i);
			t.setColour(QuaxTileColour.WHITE);
			g.addTile(t);
		}
		
		assertFalse(g.isWinningGroup());
	}
	
	@Test
	void testIsWinningGroup3() {
		QuaxTileGroup g = new QuaxTileGroup();
		
		for (int i = 0; i < 11; i++) {
			QuaxTile t = new Octagon(i, 8);
			t.setColour(QuaxTileColour.BLACK);
			g.addTile(t);
		}
		
		assertFalse(g.isWinningGroup());
	}
	
	@Test
	void testIsWinningGroup4() {
		QuaxTileGroup g = new QuaxTileGroup();
		
		for (int i = 0; i < 11; i++) {
			QuaxTile t = new Octagon(i, 8);
			t.setColour(QuaxTileColour.WHITE);
			g.addTile(t);
		}
		
		assertTrue(g.isWinningGroup());
	}
	
	@Test
	void testMerge1() {
		QuaxTileGroup g1 = new QuaxTileGroup(new Octagon(0, 1));
		QuaxTileGroup g2 = new QuaxTileGroup();
		
		g1.merge(g2);
		
		assertEquals(1, g1.size());
	}
	
	void testMerge2() {
		QuaxTileGroup g1 = new QuaxTileGroup(new Octagon(0, 1));
		QuaxTileGroup g2 = new QuaxTileGroup(new Octagon(5, 6));
		
		g1.merge(g2);
		
		assertEquals(2, g1.size());
	}
	
	@Test
	void testMerge3() {
		QuaxTileGroup g1 = new QuaxTileGroup();
		QuaxTileGroup g2 = new QuaxTileGroup();
		
		g1.addTile(new Rhombus(3, 5));
		g1.addTile(new Octagon(6, 6));
		g1.addTile(new Octagon(5, 5));
		g1.addTile(new Rhombus(3, 3));
		g1.addTile(new Octagon(9, 10));
		g1.addTile(new Octagon(2, 4));
		g1.addTile(new Octagon(1, 6));
		
		g1.merge(g2);
		
		assertEquals(7, g1.size());
	}
	
	@Test
	void testMergeWinningGroup1() {
		QuaxTileGroup g1 = new QuaxTileGroup();
		QuaxTileGroup g2 = new QuaxTileGroup();
		
		for (int i = 0; i <= 6; i++) {
			QuaxTile t = new Octagon(5, i);
			t.setColour(QuaxTileColour.BLACK);
			g1.addTile(t);
		}
		
		for (int i = 7; i < 11; i++) {
			QuaxTile t = new Octagon(5, i);
			t.setColour(QuaxTileColour.BLACK);
			g2.addTile(t);
		}
		
		g2.merge(g1);
		assertTrue(g2.isWinningGroup());
	}
	
	@Test
	void testMergeWinningGroup2() {
		QuaxTileGroup g1 = new QuaxTileGroup();
		QuaxTileGroup g2 = new QuaxTileGroup();
		
		for (int i = 0; i <= 7; i++) {
			QuaxTile t = new Octagon(i, 3);
			t.setColour(QuaxTileColour.WHITE);
			g1.addTile(t);
		}
		
		for (int i = 8; i < 11; i++) {
			QuaxTile t = new Octagon(i, 3);
			t.setColour(QuaxTileColour.WHITE);
			g2.addTile(t);
		}
		
		g1.merge(g2);
		assertTrue(g1.isWinningGroup());
	}
}
