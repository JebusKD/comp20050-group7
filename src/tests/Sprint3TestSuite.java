package tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
	TestPieRule.class
})
@DisplayName("Sprint 3 Unit Tests")
class Sprint3TestSuite {
	
}
