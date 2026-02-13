package quax.ui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class ShowBoard extends Application {
    @Override
    public void start(Stage stage){
        BoardMaking board  = new BoardMaking();
        BorderPane borderPane = new BorderPane();
        VBox vBox = new VBox();
        //StackPane  stackPane = new StackPane(board.getBoard());
        //stackPane.setPadding(new Insets(5,5,5,10));

        Label title = new Label("Game");
        Font font =  Font.font("Arial", FontWeight.EXTRA_BOLD, 20);
        title.setFont(font);

        Button PieRule = new Button("Pie Rule");
        Button DevMode =  new Button("Dev Mode");
        vBox.getChildren().addAll(PieRule,DevMode);

        borderPane.setTop(title);
        borderPane.setCenter(board.getBoard());
        borderPane.setRight(vBox);

          Scene scene = new Scene(borderPane,800,700,false, SceneAntialiasing.DISABLED);

        stage.setTitle("Quax");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
