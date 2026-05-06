package tests;


import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import controller.QuaxController;
import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.*;


@ExtendWith(ApplicationExtension.class)
public class TestStrategyValues {
    @Start
    public void start(Stage stage) throws Exception {
        new QuaxController(stage,true,true);//human v bot game now
    }

    @Test
    void testStrategyValuesUpdate(FxRobot robot){
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        robot.clickOn("#octagon5-5");
        robot.clickOn("#showStrat");

        scheduler.schedule(() ->{
            assertFalse(robot.lookup(".tileoutline-base.tileoutline-1").queryAll().isEmpty());
            assertFalse(robot.lookup(".tileoutline-base.tileoutline-4").queryAll().isEmpty());
        },5, TimeUnit.SECONDS);
    }
}