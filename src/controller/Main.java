package controller;

import javafx.application.Application;
import javafx.stage.Stage;
import userinterface.QuaxUserInterface;

public class Main extends Application {

	@Override
	public void start(Stage stage) throws Exception {
		QuaxController controller = new QuaxController(stage);

		stage.show();
	}

	public static void main(String[] args) {
		launch();
	}
}
