module com.example.octagongame {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.graphics;


    opens com.example.octagongame to javafx.fxml;
    exports com.example.octagongame;
    exports quax.ui;
}