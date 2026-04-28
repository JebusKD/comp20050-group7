package controller;

import javafx.application.Application;
import javafx.stage.Stage;

/* Launch Game
 *
 */
public class Main extends Application {

	@Override
	public void start(Stage stage) throws Exception {
		new QuaxController(stage);
	}

	public static void main(String[] args) {
		launch();
	}
}
