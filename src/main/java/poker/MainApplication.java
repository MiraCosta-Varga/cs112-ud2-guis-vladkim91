package poker;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import poker.controllers.PokerTableManualDesign;

public class MainApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the FXML file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/poker/poker-view.fxml"));


        AnchorPane root = loader.load(); // Ensure the root matches your FXML layout

        // Add the manually designed poker table (background)
        PokerTableManualDesign pokerTable = new PokerTableManualDesign();
        root.getChildren().add(0, pokerTable.getTableGroup()); // Add as the first element to act as the background

        // Set up the scene and stage
        Scene scene = new Scene(root, 800, 600); // Match dimensions to your game design
        primaryStage.setTitle("Poker Game");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
