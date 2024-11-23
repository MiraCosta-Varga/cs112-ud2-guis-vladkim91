package poker.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import poker.models.PokerGame;
import poker.models.PokerAI;

import java.util.List;

public class PokerController {

    @FXML
    private Label potLabel, dealerLabel, roundLabel;
    @FXML
    private HBox communityCards; // Dynamic card display
    @FXML
    private VBox playerDetails;  // Player info
    @FXML
    private Slider aggressionSlider, tightnessSlider, bluffSlider;
    @FXML
    private Button startSimulation, pauseSimulation, resetGame;

    private PokerGame pokerGame; // Backend game logic

    @FXML
    public void initialize() {
        // Initialize game logic with dummy data
        PokerAI mainAI = new PokerAI("AI_Main", 1000, 50, 50, 50, 50);
        PokerAI opponent1 = new PokerAI("AI_1", 1000, 50, 50, 50, 50);
        pokerGame = new PokerGame(
                List.of(opponent1), mainAI, 10, 20
        );

        updateUI();
    }

    @FXML
    private void startSimulation() {
        pokerGame.playHand(); // Starts a single game round (can be looped for simulations)
        updateUI();
    }

    @FXML
    private void pauseSimulation() {
        System.out.println("Simulation Paused"); // Add logic for pausing game if needed
    }

    @FXML
    private void resetGame() {
        pokerGame = new PokerGame(
                pokerGame.getPlayers(),
                pokerGame.getMainAI(),
                pokerGame.getSmallBlind(),
                pokerGame.getBigBlind()
        );
        updateUI();
    }

    private void updateUI() {
        // Update game status labels
        potLabel.setText("$" + pokerGame.getPot());
        dealerLabel.setText(String.valueOf(pokerGame.getDealerPosition()));
        roundLabel.setText(pokerGame.getCurrentRound());

        // Update community cards
        communityCards.getChildren().clear();
        pokerGame.getCommunityCards().forEach(card -> {
            Label cardLabel = new Label(card.toString());
            cardLabel.setStyle("-fx-border-color: black; -fx-padding: 5;");
            communityCards.getChildren().add(cardLabel);
        });

        // Update player details
        playerDetails.getChildren().clear();
        pokerGame.getPlayers().forEach(player -> {
            Label playerLabel = new Label(player.toString());
            playerDetails.getChildren().add(playerLabel);
        });
    }
}
