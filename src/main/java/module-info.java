module poker {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.json; // Add this line to require the JSON API module

    exports poker;
    exports poker.api;
    exports poker.models;
    exports poker.controllers;
    opens poker.controllers to javafx.fxml;
    opens poker.models to javafx.fxml;
}