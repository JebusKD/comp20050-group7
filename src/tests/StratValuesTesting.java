package tests;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;
import controller.QuaxController;
import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.WindowMatchers.isShowing;
import  org.testfx.api.FxAssert;
import model.QuaxBoard;
import player.*;
import types.*;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
            assertTrue(!robot.lookup(".tileoutline-base.tileoutline-0").queryAll().isEmpty());
            assertTrue(!robot.lookup(".tileoutline-base.tileoutline-2").queryAll().isEmpty());
        },3, TimeUnit.SECONDS);
    }
}