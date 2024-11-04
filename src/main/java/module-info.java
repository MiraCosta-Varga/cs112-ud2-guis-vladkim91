module cs112.ud2 {
    requires javafx.controls;
    requires javafx.fxml;


    opens cs112.ud2 to javafx.fxml;
    exports cs112.ud2;
}