package tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import types.Octagon;
import types.QuaxCoordinate;

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
		Octagon o = new Octagon(5, 2);
		QuaxCoordinate q = o.getCoordinates();
		assertEquals(5, q.x());
	}
	
	@Test
	void testOctagonCoordinates2() {
		Octagon o = new Octagon(1, 0);
		QuaxCoordinate q = o.getCoordinates();
		assertEquals(1, q.x());
	}
	
	@Test
	void testOctagonCoordinates3() {
		Octagon o = new Octagon(9, 10);
		QuaxCoordinate q = o.getCoordinates();
		assertEquals(9, q.x());
	}
	
	@Test
	void testOctagonCoordinates4() {
		Octagon o = new Octagon(0, 7);
		QuaxCoordinate q = o.getCoordinates();
		assertEquals(0, q.x());
	}
	
	@Test
	void testOctagonCoordinates5() {
		Octagon o = new Octagon(5, 2);
		QuaxCoordinate q = o.getCoordinates();
		assertEquals(2, q.y());
	}
	
	@Test
	void testOctagonCoordinates6() {
		Octagon o = new Octagon(1, 0);
		QuaxCoordinate q = o.getCoordinates();
		assertEquals(0, q.y());
	}
	
	@Test
	void testOctagonCoordinates7() {
		Octagon o = new Octagon(9, 10);
		QuaxCoordinate q = o.getCoordinates();
		assertEquals(10, q.y());
	}
	
	@Test
	void testOctagonCoordinates8() {
		Octagon o = new Octagon(0, 7);
		QuaxCoordinate q = o.getCoordinates();
		assertEquals(7, q.y());
	}

}
