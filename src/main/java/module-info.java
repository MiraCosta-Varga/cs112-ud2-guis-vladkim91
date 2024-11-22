module poker {
    requires javafx.controls;
    requires javafx.fxml;

    
    exports poker;
    opens poker to javafx.fxml;
}