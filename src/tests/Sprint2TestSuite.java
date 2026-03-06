package tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
	TestBoardCopying.class,
	TestRhombusPlacement.class,
	TestQuaxBoardIteration.class
})
@DisplayName("Sprint 2 Unit Tests")
class Sprint2TestSuite {
	
}
