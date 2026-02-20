package quax.controller;

import javafx.application.Application;
import javafx.stage.Stage;
import quax.userinterface.QuaxUserInterface;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        QuaxController controller = new QuaxController(stage);
    }

    public static void main(String[] args) {
        //System.setProperty("prism.allowhidpi", "false");
        launch();
    }
}
