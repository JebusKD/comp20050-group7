module com.example.gameofquax {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.desktop;


    opens com.example.gameofquax to javafx.fxml;
    exports com.example.gameofquax;
    exports quax.controller;
    exports quax.userinterface;
    exports quax.types;
}