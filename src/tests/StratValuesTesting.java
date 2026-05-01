package tests;


import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import controller.QuaxController;
import static org.junit.jupiter.api.Assertions.*;
import player.*;
import types.*;

import java.util.concurrent.*;

@ExtendWith(ApplicationExtension.class)
public class StratValuesTesting {

    private QuaxController controller;

    @Start
    public void start(Stage stage) throws Exception {
        controller = new QuaxController(stage,true,true);//human v bot game now
    }

    @Test
    void stratValsUpdate(FxRobot robot){
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        robot.clickOn("#octagon5-5");
        robot.clickOn("#showStrat");

        scheduler.schedule(() ->{
            assertTrue(!robot.lookup(".tileoutline-base.tileoutline-1").queryAll().isEmpty());
            assertTrue(!robot.lookup(".tileoutline-base.tileoutline-4").queryAll().isEmpty());
        },3, TimeUnit.SECONDS);
    }
}