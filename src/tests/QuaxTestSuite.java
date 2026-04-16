package tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
	Sprint1TestSuite.class,
	Sprint2TestSuite.class,
	Sprint3TestSuite.class,
	QuaxUITest.class,
	BotUiTest.class
})
@DisplayName("Quax Unit Tests")
class QuaxTestSuite {
	
}