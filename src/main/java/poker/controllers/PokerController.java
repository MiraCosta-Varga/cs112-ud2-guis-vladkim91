package poker.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import poker.models.PokerGame;
import poker.models.PokerAI;
import poker.models.Player;

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
    private Button foldButton, callButton, raiseButton;
    @FXML
    private TextField raiseAmountField; // For player raise amount

    private PokerGame pokerGame; // Backend game logic
    private Player humanPlayer;  // Reference to the human player

    @FXML
    public void initialize() {
        // Initialize the human player and AI players
        humanPlayer = new Player("Human", 1000);
        PokerAI mainAI = new PokerAI("AI_Main", 1000, 50, 50, 50, 50) {
            @Override
            public String makeDecision(double handStrength, double potOdds) {
                if (handStrength > 0.8) return "Raise";
                else if (handStrength > 0.5) return "Call";
                return "Fold";
            }
        };
        PokerAI opponent1 = new PokerAI("AI_1", 1000, 60, 40, 30, 70) {
            @Override
            public String makeDecision(double handStrength, double potOdds) {
                if (handStrength > 0.7) return "Raise";
                else if (handStrength > 0.4) return "Call";
                return "Fold";
            }
        };

        // Initialize the game with players and blinds
        pokerGame = new PokerGame(humanPlayer, List.of(opponent1, mainAI), 10, 20);

        // Set up button actions
        foldButton.setOnAction(e -> handlePlayerAction("Fold"));
        callButton.setOnAction(e -> handlePlayerAction("Call"));
        raiseButton.setOnAction(e -> handlePlayerAction("Raise"));

        updateUI();
    }

    @FXML
    private void handlePlayerAction(String action) {
        switch (action) {
            case "Fold" -> {
                humanPlayer.fold();
                System.out.println("Human folds.");
            }
            case "Call" -> {
                int amountToCall = pokerGame.getSmallBlind();
                humanPlayer.call(amountToCall);
                pokerGame.addToPot(amountToCall);
                System.out.println("Human calls $" + amountToCall);
            }
            case "Raise" -> {
                try {
                    int raiseAmount = Integer.parseInt(raiseAmountField.getText());
                    humanPlayer.raise(raiseAmount);
                    pokerGame.addToPot(raiseAmount);
                    System.out.println("Human raises $" + raiseAmount);
                } catch (NumberFormatException e) {
                    System.err.println("Invalid raise amount. Please enter a number.");
                }
            }
        }
        updateUI();
        pokerGame.playBettingRound();
        updateUI();
    }

    private void updateUI() {
        potLabel.setText("Pot: $" + pokerGame.getPot());
        dealerLabel.setText("Dealer: Player " + pokerGame.getDealerPosition());
        roundLabel.setText("Round: " + pokerGame.getCurrentRound());

        communityCards.getChildren().clear();
        pokerGame.getCommunityCards().forEach(card -> {
            Label cardLabel = new Label(card.toString());
            cardLabel.setStyle("-fx-border-color: black; -fx-padding: 5;");
            communityCards.getChildren().add(cardLabel);
        });

        playerDetails.getChildren().clear();
        pokerGame.getPlayers().forEach(player -> {
            Label playerLabel = new Label(player.getId() + ": $" + player.getChips());
            if (player instanceof PokerAI) playerLabel.setText(playerLabel.getText() + " (AI)");
            playerDetails.getChildren().add(playerLabel);
        });
    }
}
