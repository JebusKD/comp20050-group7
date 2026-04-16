package tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
	TestQuaxCoordinate.class,
	TestOctagon.class,
	TestRhombus.class,
	TestQuaxTileGroup.class,
	TestQuaxBoard.class
})
@DisplayName("Sprint 1 Unit Tests")
class Sprint1TestSuite {
	
}